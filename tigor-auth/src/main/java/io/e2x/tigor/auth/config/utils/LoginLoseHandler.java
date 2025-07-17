package io.e2x.tigor.auth.config.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.e2x.tigor.frameworks.common.exception.enums.GlobalErrorCodeConstants;
import io.e2x.tigor.frameworks.common.pojo.CommonResult;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.authentication.HttpBasicServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;

import java.nio.charset.StandardCharsets;

@Component
public class LoginLoseHandler extends HttpBasicServerAuthenticationEntryPoint {

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException e) {
        ServerHttpResponse response = exchange.getResponse();
        // 构造 CommonResult 响应对象
        CommonResult<String> result = CommonResult.error(GlobalErrorCodeConstants.USER_LOGIN_FAILED);
        String json = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            json = objectMapper.writeValueAsString(result);
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(e);
        }
        // 将 JSON 字符串写入响应体
        return response.writeAndFlushWith(
                Flux.just(ByteBufFlux.just(response.bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8))))
        );
    }
}

