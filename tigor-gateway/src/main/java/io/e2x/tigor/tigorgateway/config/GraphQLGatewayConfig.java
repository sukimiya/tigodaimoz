package io.e2x.tigor.tigorgateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static io.e2x.tigor.frameworks.common.graph.config.RouteConfigConstants.*;

@Configuration
public class GraphQLGatewayConfig {
    private static final Logger logger = Logger.getLogger(GraphQLGatewayConfig.class.getName());
    public static class GraphQLVersionRoutingFilter extends AbstractGatewayFilterFactory<GraphQLRouteConfig> {

        private final RouteLocator routeLocator;
        private final GraphQLRouteConfig config;
        public GraphQLVersionRoutingFilter(RouteLocator routeLocator, GraphQLRouteConfig config) {
            super(GraphQLRouteConfig.class);
            this.routeLocator = routeLocator;
            this.config = config;
        }
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            ServerHttpRequest request = exchange.getRequest();
            String version = request.getHeaders().getFirst(HEADER_VERSION_NAME);
            String module = request.getHeaders().getFirst(HEADER_MODULE_NAME);
            // 如果缺少参数，返回404
            if (module != null && version != null
                    && (module.isEmpty() || version.isEmpty())) {
                exchange.getResponse().setStatusCode(HttpStatusCode.valueOf(404));
            }
            // 获取模块ID
            String moduleId = Arrays.stream(GRAPHQL_MODULE_ID_PREFIXES)
                    .map(prefix -> {
                        if (module != null) {
                            return module.startsWith(prefix) ? module : null;
                        }else{
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(module) + (version != null ? version : "");
            if (moduleId.isEmpty() && Arrays.stream(AUTH_MODULE_IDS).noneMatch(module::equals)) {
                // 如果没有token，返回401
                if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    ServerHttpResponse response = exchange.getResponse();
                    response.setStatusCode(HttpStatusCode.valueOf(401));
                    return response.setComplete();
                }
            }
            return routeLocator.getRoutes()
                    .filter(r -> r.getId().equals(moduleId))
                    .next()
                    .flatMap(route -> {
                        // 设置路由
                        exchange.getAttributes().put(GatewayFilterFactory.class.getName() + "_route", route);
                        return chain.filter(exchange);
                    })
                    .switchIfEmpty(Mono.defer(() -> chain.filter(exchange))).onErrorContinue((ex, error) -> {
                        // 如果没有匹配的路由，返回404
                        logger.log(Level.WARNING, "No route found for module: " + moduleId);
                        exchange.getResponse().setStatusCode(HttpStatusCode.valueOf(404));
                    }); // 如果没有匹配的路由，继续执行后续过滤器
        }

        @Override
        public GatewayFilter apply(String routeId, Consumer<GraphQLRouteConfig> consumer) {
            consumer.accept(config);
            return this::filter;
        }

        @Override
        public GatewayFilter apply(Consumer<GraphQLRouteConfig> consumer) {
            consumer.accept(config);
            return this::filter;
        }
        @Override
        public GatewayFilter apply(GraphQLRouteConfig config) {
            return this::filter;
        }

        @Override
        public GatewayFilter apply(String routeId, GraphQLRouteConfig config) {
            return this::filter;
        }

        @Override
        public String name() {
            return super.name();
        }
    }
    // 路由定位器配置
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        logger.log(Level.INFO, "Custom route locator initialized");
        return builder.routes()
                .route("auth_login_route_v1", r -> r.path("/login")
                        .and().query("username", "^[A-Za-z@_.]+$")
                        .and().query("password", "^[A-Za-z!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]+$")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://auth-service/auth/login"))
                .route("register_route_v1", r -> r.path("/register")
                        .and().query("username", "^[A-Za-z@_.]+$")
                        .and().query("password", "^[A-Za-z!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]+$")
                        .and().query("email", "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://user-service/api/users/register"))
                .route("auth_logout_route_v1", r -> r.path("/logout")
                        .and().query(HttpHeaders.AUTHORIZATION, "^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?[A-Za-z0-9-_.+/ =]*$")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://auth-service/auth/logout"))
                .build();
    }
}
