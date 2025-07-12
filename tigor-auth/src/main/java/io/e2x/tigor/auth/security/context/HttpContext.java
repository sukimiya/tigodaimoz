package io.e2x.tigor.auth.security.context;

import graphql.kickstart.spring.GraphQLSpringContext;
import lombok.Getter;
import lombok.NonNull;
import org.dataloader.DataLoaderRegistry;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ServerWebExchange;

import java.util.Map;

public class HttpContext implements GraphQLSpringContext, ContextWithAuthentication {

    private final GraphQLSpringContext graphQLSpringServerWebExchangeContext;

    private final Map<Object, Object> map;

    @Getter
    private final Authentication authentication;

    public HttpContext(GraphQLSpringContext graphQLSpringServerWebExchangeContext, Authentication authentication) {
        this.graphQLSpringServerWebExchangeContext = graphQLSpringServerWebExchangeContext;
        this.authentication = authentication;
        map = graphQLSpringServerWebExchangeContext.getMapOfContext();
    }

    @Override
    public ServerWebExchange getServerWebExchange() {
        return graphQLSpringServerWebExchangeContext.getServerWebExchange();
    }

    @Override
    public @NonNull DataLoaderRegistry getDataLoaderRegistry() {
        return graphQLSpringServerWebExchangeContext.getDataLoaderRegistry();
    }

    @Override
    public Map<Object, Object> getMapOfContext() {
        return map;
    }
}
