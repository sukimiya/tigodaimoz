package io.e2x.tigor.auth.config;

import io.e2x.tigor.auth.config.utils.*;
import io.e2x.tigor.auth.repo.JwtSecurityContextRepository;
import io.e2x.tigor.auth.service.IUserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    @Autowired
    AuthManagerHandler authManagerHandler;

    @Autowired
    AccessDeniedHandler accessDeniedHandler;

    @Autowired
    LoginSuccessHandler loginSuccessHandler;

    @Autowired
    LoginFailedHandler loginFailedHandler;

    @Autowired
    LoginLoseHandler loginLoseHandler;

    @Autowired
    JwtSecurityContextRepository jwtSecurityContextRepository;

    @Autowired
    JwtWebFilter jwtWebFilter;

    //security的鉴权排除列表
    public static final String[] excludedAuthPages = {
            "/auth/login",
            "/auth/logout"
    };

    @Autowired
    JwtTool jwtTool;

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager(IUserInfoService userDetailService) {
        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailService);
        authenticationManager.setPasswordEncoder(passwordEncoder());
        return authenticationManager;
    }

    @Bean
    SecurityWebFilterChain webFluxSecurityFilterChain(ServerHttpSecurity http) throws Exception {
        return http.authorizeExchange( exchange ->
                exchange.pathMatchers(excludedAuthPages).permitAll()
                        .pathMatchers(HttpMethod.OPTIONS).permitAll()
                        .pathMatchers("/**").access(authManagerHandler)
                        .anyExchange().authenticated()
            ).addFilterAfter(jwtWebFilter, SecurityWebFiltersOrder.FIRST)
                .securityContextRepository(jwtSecurityContextRepository)
                .formLogin(formLoginSpec -> formLoginSpec.loginPage("/auth/login")
                        .authenticationSuccessHandler(loginSuccessHandler)
                        .authenticationFailureHandler(loginFailedHandler)
                        .authenticationEntryPoint(loginLoseHandler))
                .exceptionHandling(exceptionHandlingSpec ->
                        exceptionHandlingSpec.accessDeniedHandler(accessDeniedHandler))
                .cors(corsSpec -> corsSpec.disable())
                .csrf(csrfSpec -> csrfSpec.disable())
                .build();
    }
}
