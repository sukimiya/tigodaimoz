package io.e2x.tigor.auth.security.context;

import graphql.kickstart.spring.GraphQLSpringContext;
import graphql.kickstart.spring.webflux.DefaultGraphQLSpringWebSocketSessionContext;
import graphql.kickstart.spring.webflux.DefaultGraphQLSpringWebfluxContextBuilder;
import io.e2x.tigor.auth.security.jwt.GLTokenService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.server.ServerWebExchange;

@AllArgsConstructor
public class ContextBuilder extends DefaultGraphQLSpringWebfluxContextBuilder {

    private final GLTokenService tokenService;

    @Override
    public DefaultGraphQLSpringWebSocketSessionContext build(WebSocketSession webSocketSession) {
        Authentication authentication = tokenService.getFrom(webSocketSession);
        return new WebsocketContext(webSocketSession, authentication);
    }

    @Override
    public GraphQLSpringContext build(ServerWebExchange serverWebExchange) {
        GraphQLSpringContext graphQLSpringContext = super.build(serverWebExchange);
        Authentication authentication = tokenService.getFrom(serverWebExchange);

        return new HttpContext(graphQLSpringContext, authentication);
    }
}
