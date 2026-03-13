package io.e2x.tigor.auth.data;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Aspect
@Component
public class CustomTenantIdentifierResolver implements WebFilter {

    public static final String TENANT_CONTEXT_KEY = "X-Tenant-ID";
    private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String tenantId = exchange.getRequest().getHeaders().getFirst(TENANT_CONTEXT_KEY);
        if (tenantId == null) {
            tenantId = "public"; // 默认租户
        }
        final String finalTenantId = tenantId;
        return chain.filter(exchange)
                .doOnSubscribe(s -> setCurrentTenant(finalTenantId))
                .doFinally(signal -> clearCurrentTenant());
    }

    public static void setCurrentTenant(String tenantId) {
        currentTenant.set(tenantId);
    }

    public static String getCurrentTenant() {
        return Optional.ofNullable(currentTenant.get()).orElse("public");
    }

    public static void clearCurrentTenant() {
        currentTenant.remove();
    }
}
