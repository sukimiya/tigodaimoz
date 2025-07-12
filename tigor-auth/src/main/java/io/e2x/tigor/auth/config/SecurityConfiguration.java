package io.e2x.tigor.auth.config;

import graphql.kickstart.execution.subscriptions.apollo.ApolloSubscriptionConnectionListener;
import graphql.kickstart.spring.webflux.GraphQLSpringWebfluxContextBuilder;
import graphql.kickstart.tools.SchemaParserOptions;
import io.e2x.tigor.auth.security.CredentialsSubscriptionConnectionListener;
import io.e2x.tigor.auth.security.context.ContextBuilder;
import io.e2x.tigor.auth.security.context.ContextWithAuthentication;
import io.e2x.tigor.auth.security.jwt.GLTokenService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfiguration {
    @Bean
    SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(it -> it.anyExchange().permitAll())
                .build();
    }

    @Bean
    public GraphQLSpringWebfluxContextBuilder graphQLSpringWebfluxContextBuilder(GLTokenService tokenService) {
        return new ContextBuilder(tokenService);
    }

    @Bean
    ApolloSubscriptionConnectionListener subscriptionCredentialsHandler() {
        return new CredentialsSubscriptionConnectionListener();
    }

    @Bean
    GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("");
    }

    @Bean
    @SuppressWarnings("unchecked")
    List<SchemaParserOptions.GenericWrapper> genericWrappers() {
        return List.of(
                SchemaParserOptions.GenericWrapper.withTransformer(
                        Mono.class,
                        0,
                        (mono, environment) -> {
                            Object context = environment.getGraphQlContext();
                            if (context instanceof ContextWithAuthentication) {
                                val contextWithAuthentication = (ContextWithAuthentication) context;
                                val authentication = contextWithAuthentication.getAuthentication();

                                return mono
                                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication))
                                        .toFuture();
                            }

                            return mono.toFuture();
                        },
                        t -> t
                ),
                SchemaParserOptions.GenericWrapper.withTransformer(
                        Flux.class,
                        0,
                        (flux, environment) -> {
                            Object context = environment.getGraphQlContext();
                            if (context instanceof ContextWithAuthentication contextWithAuthentication) {
                                val authentication = contextWithAuthentication.getAuthentication();

                                return flux
                                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
                            }

                            return flux;
                        },
                        t -> t
                )
        );
    }
}
