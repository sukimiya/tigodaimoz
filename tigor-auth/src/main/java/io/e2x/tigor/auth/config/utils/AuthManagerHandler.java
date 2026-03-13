package io.e2x.tigor.auth.config.utils;

import io.e2x.tigor.auth.dal.vo.UserInfo;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
public class AuthManagerHandler implements ReactiveAuthorizationManager<AuthorizationContext> {

    private static final Logger logger = Logger.getLogger(AuthManagerHandler.class.getName());

    private static AuthorizationResult createAuthorizationResult(boolean granted) {
        return new AuthorizationResult() {
            @Override
            public boolean isGranted() {
                return granted;
            }
        };
    }

    @Override
    public Mono<AuthorizationResult> authorize(Mono<Authentication> authentication, AuthorizationContext context) {
        ServerHttpRequest request = context.getExchange().getRequest();
        String requestUrl = request.getPath().pathWithinApplication().value();
        // String requestUrl = request.getPath().pathWithinApplication().value();
        return authentication
                .filter(Authentication::isAuthenticated)
                .map(auth -> {
                    List<String> roles = getRoles(auth);
                    // 这里可以添加具体的权限检查逻辑
                    boolean granted = !roles.isEmpty();
                    return createAuthorizationResult(granted);
                })
                .defaultIfEmpty(createAuthorizationResult(false));
    }

    private List<String> getRoles(Authentication authentication) {
        if (authentication.getPrincipal() instanceof UserInfo) {
            UserInfo userInfo = (UserInfo) authentication.getPrincipal();
            return userInfo.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
