package io.e2x.tigor.auth.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.e2x.tigor.frameworks.common.exception.ServiceException;
import io.e2x.tigor.frameworks.common.exception.enums.GlobalErrorCodeConstants;
import io.e2x.tigor.frameworks.common.pojo.CommonResult;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Arrays;

import static io.e2x.tigor.auth.config.SecurityConfig.excludedAuthPages;

@Slf4j
@Component
public class JwtWebFilter implements WebFilter {

    @Autowired
    JwtTool jwtTool;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        HttpHeaders header = response.getHeaders();
        header.add("Content-Type", "application/json; charset=UTF-8");
        String path = request.getPath().value();
        if(Arrays.stream(excludedAuthPages).anyMatch((first -> first != null && path.contains(first)))){
            return chain.filter(exchange);
        }
        byte[] outputString = null;
        String token = header.getFirst(HttpHeaders.AUTHORIZATION);
        token = token.substring("Bearer ".length(), token.length());
        if (StringUtils.isBlank(token)) {
            outputString = notLoginResponse();
        } else if (!jwtTool.VerityToken(token)) {
            outputString = expiredResponse();
        } else if (jwtTool.getUserName(token).isEmpty()) {
            outputString = unauthorizedResponse();
        }
        if (outputString != null) {
            DataBuffer buffer = response.bufferFactory().wrap(outputString);
            return response.writeWith(Mono.just(buffer));
        }
        return chain.filter(exchange);
    }

    private byte[] unauthorizedResponse() {
        try {
            return new ObjectMapper().writeValueAsString(CommonResult.error(GlobalErrorCodeConstants.UNAUTHORIZED)).getBytes();
        } catch (JsonProcessingException e) {
            throw new ServiceException(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR);
        }
    }
    private byte[] forbiddenResponse() {
        try {
            return new ObjectMapper().writeValueAsString(CommonResult.error(GlobalErrorCodeConstants.FORBIDDEN)).getBytes();
        } catch (JsonProcessingException e) {
            throw new ServiceException(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR);
        }
    }
    private byte[] expiredResponse() {
        try {
            return new ObjectMapper().writeValueAsString(CommonResult.error(GlobalErrorCodeConstants.USER_LOGIN_EXPIRED)).getBytes();
        } catch (JsonProcessingException e) {
            throw new ServiceException(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR);
        }
    }
    private byte[] notLoginResponse() {
        try {
            return new ObjectMapper().writeValueAsString(CommonResult.error(GlobalErrorCodeConstants.USER_NOT_LOGIN)).getBytes();
        } catch (JsonProcessingException e) {
            throw new ServiceException(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR);
        }
    }
}

