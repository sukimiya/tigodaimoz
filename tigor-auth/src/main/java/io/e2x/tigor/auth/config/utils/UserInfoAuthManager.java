package io.e2x.tigor.auth.config.utils;

import io.e2x.tigor.auth.config.JwtTool;
import io.e2x.tigor.auth.dal.vo.UserInfo;
import io.e2x.tigor.auth.service.IUserInfoService;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserInfoAuthManager implements ReactiveAuthenticationManager {

    private final IUserInfoService userInfoService;

    private final JwtTool jwtTool;

    public UserInfoAuthManager(IUserInfoService userInfoService, JwtTool jwtTool) {
        this.userInfoService = userInfoService;
        this.jwtTool = jwtTool;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();
        return userInfoService.findByUsername(jwtTool.getUserName(authentication.getCredentials().toString()))
                .flatMap(userInfo -> {
                    List<String> roles = jwtTool.getUserRoles(token);
                    List<GrantedAuthority> authorities = roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
                    UserInfo principal = (UserInfo) userInfo;
                    Authentication auth = new UsernamePasswordAuthenticationToken(principal, authentication.getCredentials(), authorities);
                    return Mono.just(auth);
                });
    }
}
