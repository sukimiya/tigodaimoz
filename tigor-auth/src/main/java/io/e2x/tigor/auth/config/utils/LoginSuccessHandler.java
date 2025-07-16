package io.e2x.tigor.auth.config.utils;

import com.alibaba.fastjson.JSON;
import io.e2x.tigor.auth.config.JwtTool;
import io.e2x.tigor.auth.dal.vo.UserInfo;
import io.e2x.tigor.auth.entity.AuthResp;
import io.e2x.tigor.frameworks.common.pojo.CommonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.nio.charset.StandardCharsets;

@Component
public class LoginSuccessHandler implements ServerAuthenticationSuccessHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    JwtTool jwtTool;

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        // TODO: check Principal
        UserInfo user = (UserInfo) authentication.getPrincipal();
        String username = user.getUsername();
        List<GrantedAuthority> authorities = (List<GrantedAuthority>) user.getAuthorities();
        List<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        String token = jwtTool.CreateToken(String.valueOf(user.getId()), username, roles);
        String refreshToken = jwtTool.CreateToken(String.valueOf(user.getId()), username, roles, 1000 * 60 * 60 * 24 * 7);

        AuthResp authResp = new AuthResp(username, token, refreshToken, new Date(System.currentTimeMillis() + JwtTool.DEFAULT_EXPIRE_TIME).getTime());

        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // 构造 CommonResult 响应对象
        CommonResult<AuthResp> result = CommonResult.success(authResp);
        String json = JSON.toJSONString(result);
        logger.debug("登录成功！token: " + authResp.getToken());
        // 将 JSON 字符串写入响应体
        return response.writeAndFlushWith(
                Flux.just(ByteBufFlux.just(response.bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8))))
        );
    }
}

