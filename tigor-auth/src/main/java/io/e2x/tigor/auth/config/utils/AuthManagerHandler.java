package io.e2x.tigor.auth.config.utils;

import io.e2x.tigor.auth.dal.vo.UserInfo;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
public class AuthManagerHandler implements ReactiveAuthorizationManager<AuthorizationContext> {

    private static final Logger logger = Logger.getLogger(AuthManagerHandler.class.getName());

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext object) {
        ServerHttpRequest request = object.getExchange().getRequest();
        // TODO: URL 匹配
        // String requestUrl = request.getPath().pathWithinApplication().value();
        return authentication
                .filter(Authentication::isAuthenticated)
                .flatMapIterable(Authentication::getAuthorities)
                .map(GrantedAuthority::getAuthority)
                .filter(c -> hasRole(authentication, c))
                .then(Mono.just(new AuthorizationDecision(true)))
                .defaultIfEmpty(new AuthorizationDecision(false));
    }
    private Boolean hasRole(Mono<Authentication> authentication, String role) {
        return getRoles(authentication).contains(role);
    }
    private List<String> getRoles(Mono<Authentication> authentication) {
        return authentication
                .flatMap(a -> {
                    logger.log(Level.INFO, "getRoles: " + a.getPrincipal());
                    if(a.getPrincipal() instanceof UserInfo)
                        return Mono.just((UserInfo)a);
                    return null;
                })
                .flatMap(userInfo -> {
                    List<String> roles = userInfo.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
                    return Mono.just(roles);
                }).block();
    }
    @Override
    public Mono<Void> verify(Mono<Authentication> authentication, AuthorizationContext object) {
        return check(authentication, object)
                .filter(AuthorizationDecision::isGranted)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new AccessDeniedException("Access Denied"))))
                .flatMap((decision) -> Mono.empty());
    }
}

