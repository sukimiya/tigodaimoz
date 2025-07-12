package io.e2x.tigor.auth.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.token.TokenService;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.server.ServerWebExchange;

public interface IAuthService extends TokenService {
    Authentication getFrom(WebSocketSession webSocketSession);

    Authentication getFrom(ServerWebExchange serverWebExchange);

}
