package io.e2x.tigor.auth.config;

import com.alibaba.fastjson.JSONObject;
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
        String token = exchange.getRequest().getHeaders().getFirst("token");
        if (StringUtils.isBlank(token)) {
            JSONObject jsonObject = setResultErrorMsg(401,"登录失效");
            DataBuffer buffer = response.bufferFactory().wrap(jsonObject.toJSONString().getBytes());
            return response.writeWith(Mono.just(buffer));
        }
        boolean isold = jwtTool.VerityToken(token);
        if (!isold) {
            JSONObject jsonObject = setResultErrorMsg(401,"登录失效");
            DataBuffer buffer = response.bufferFactory().wrap(jsonObject.toJSONString().getBytes());
            return response.writeWith(Mono.just(buffer));
        }
        String username = jwtTool.getUserName(token);
        if (StringUtils.isEmpty(username)) {
            JSONObject jsonObject = setResultErrorMsg(401,"登录失效");
            DataBuffer buffer = response.bufferFactory().wrap(jsonObject.toJSONString().getBytes());
            return response.writeWith(Mono.just(buffer));
        }
        return chain.filter(exchange);
    }

    private JSONObject setResultErrorMsg(Integer code,String msg) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        jsonObject.put("message", msg);
        return jsonObject;
    }
}

