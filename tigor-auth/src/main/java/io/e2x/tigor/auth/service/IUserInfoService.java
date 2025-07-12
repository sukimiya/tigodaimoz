package io.e2x.tigor.auth.service;

import io.e2x.tigor.auth.dal.vo.UserInfo;
import io.e2x.tigor.frameworks.common.pojo.CommonResult;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IUserInfoService extends ReactiveUserDetailsService {
    Mono<CommonResult<Boolean>> login(String username, String password);
    Mono<UserInfo> createUser(String username, String password, String email);
    Mono<UserInfo> updateUser(UserInfo userInfo);
    Mono<Void> setUserAuthorities(String username, List<GrantedAuthority> authorities);
    Mono<Void> setUserAutthorities(String username, String... authorities);
    Mono<Void> hasAuthority(String username, String authority);
}
