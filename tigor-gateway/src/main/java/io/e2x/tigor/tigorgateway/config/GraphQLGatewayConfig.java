package io.e2x.tigor.tigorgateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

import static io.e2x.tigor.frameworks.common.graph.config.GraphRouteConfigConstants.*;

@Configuration
public class GraphQLGatewayConfig {

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
            String version = request.getHeaders().getFirst(VERSION_HEADER_NAME);
            String module = request.getHeaders().getFirst(MODULE_HEADER_NAME);
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
            return routeLocator.getRoutes()
                    .filter(r -> r.getId().equals(moduleId))
                    .next()
                    .flatMap(route -> {
                        exchange.getAttributes().put(GatewayFilterFactory.class.getName() + "_route", route);
                        return chain.filter(exchange);
                    })
                    .switchIfEmpty(Mono.defer(() -> chain.filter(exchange))); // 如果没有匹配的路由，继续执行后续过滤器
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
        return builder.routes()
                .route("graphql_route_v1",
                        r -> r.path("/graphql")
                                .and().query("version=v1")
                                .filters(f -> f.stripPrefix(1))
                                .uri("lb://graphql-service-v1"))
                .route("graphql_route_v2",
                        r -> r.path("/graphql")
                                .and().query("version=v2")
                                .filters(f -> f.stripPrefix(1))
                                .uri("lb://graphql-service-v2"))
                .build();
    }
}
