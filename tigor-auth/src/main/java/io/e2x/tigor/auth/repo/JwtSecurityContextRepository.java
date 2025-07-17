package io.e2x.tigor.auth.repo;

import io.e2x.tigor.auth.config.JwtTool;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Component
public class JwtSecurityContextRepository implements ServerSecurityContextRepository {

//    @Autowired
//    UserInfoAuthManager authManager;

    @Autowired
    JwtTool jwtTool;

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        return Mono.empty();
    }

    Collection<GrantedAuthority> getAnonymousRoles() {
        ArrayList<GrantedAuthority> list = new ArrayList<GrantedAuthority>();
        list.add(new SimpleGrantedAuthority("ROLE_ANONYMOUS"));
        return list;
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        String path = exchange.getRequest().getPath().toString();
        // 过滤路径
        if ("/auth/login".equals(path)) {
            return Mono.empty();
//            return Mono.just(
//                    new SecurityContextImpl(
//                            new AnonymousAuthenticationToken("anonymous", "anonymous", getAnonymousRoles()
//                            )
//                    )
//            );
        }
        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isBlank(token)) {
            throw new DisabledException("登录失效！");
        }
        token = token.substring("Bearer ".length(), token.length());
        boolean isold = jwtTool.VerityToken(token);
        if (!isold) {
            throw new AccessDeniedException("登录失效！");
        }
        String username = jwtTool.getUserName(token);
        if (StringUtils.isEmpty(username)) {
            throw new AccessDeniedException("登录失效！");
        }
        // TODO: 将角色访问Profile写入credentials中
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(username, username);
        return Mono.just(new SecurityContextImpl(newAuthentication));
//        return authManager.authenticate(newAuthentication).map(SecurityContextImpl::new);
    }
}
