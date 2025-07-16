package io.e2x.tigor.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class UserApiWebFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // 检查是否是 /api/users/login 或 /api/users/register 路径下的请求
        String path = request.getURI().getPath();
        if (path.startsWith("/login") || path.startsWith("/register")) {
            // 放行请求
            return chain.filter(exchange);
        }
        
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // 设置过滤器的优先级，数字越小优先级越高
        return -100;
    }
}