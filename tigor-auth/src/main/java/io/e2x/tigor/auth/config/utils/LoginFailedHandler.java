package io.e2x.tigor.auth.config.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.e2x.tigor.frameworks.common.exception.enums.GlobalErrorCodeConstants;
import io.e2x.tigor.frameworks.common.pojo.CommonResult;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Component
public class LoginFailedHandler implements ServerAuthenticationFailureHandler {
    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException e) {
        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        // 构造 CommonResult 响应对象
        CommonResult<String> result = CommonResult.error(GlobalErrorCodeConstants.USER_LOGIN_FAILED);
        String json = JSON.toJSONString(result);

        // 将 JSON 字符串写入响应体
        return response.writeAndFlushWith(
                Flux.just(ByteBufFlux.just(response.bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8))))
        );
    }
}
