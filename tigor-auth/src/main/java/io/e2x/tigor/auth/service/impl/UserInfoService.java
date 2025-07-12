package io.e2x.tigor.auth.service.impl;

import io.e2x.tigor.auth.dal.sql.UserInfoRepository;
import io.e2x.tigor.auth.dal.vo.UserInfo;
import io.e2x.tigor.auth.service.IUserInfoService;
import io.e2x.tigor.frameworks.common.exception.ServiceException;
import io.e2x.tigor.frameworks.common.exception.enums.CommonStatus;
import io.e2x.tigor.frameworks.common.pojo.CommonResult;
import jakarta.annotation.Resource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static io.e2x.tigor.frameworks.common.exception.enums.GlobalErrorCodeConstants.BAD_REQUEST;
import static io.e2x.tigor.frameworks.common.exception.enums.GlobalErrorCodeConstants.UNAUTHORIZED;

@Component
public class UserInfoService implements IUserInfoService {
    @Resource
    UserInfoRepository userInfoRepository;

    @Resource
    BCryptPasswordEncoder passwordEncoder;

    @Override
    public Mono<CommonResult<Boolean>> login(String username, String password) {
        return userInfoRepository.findByUsername(username)
                .flatMap(userInfo -> {
                    if (passwordEncoder.matches(password, userInfo.getPassword())) {
                        return Mono.just(CommonResult.success(true));
                    }
                    return Mono.just(CommonResult.success(false));
                });
    }

    @Override
    public Mono<UserInfo> createUser(String username, String password, String email) {
        return checkUserExists(username)
                .flatMap(exists -> exists ? Mono.error(ServiceException.build(BAD_REQUEST)) :
                        userInfoRepository.save(new UserInfo(username, passwordEncoder.encode( password), email, CommonStatus.ENABLED, false)));
    }

    @Override
    public Mono<UserInfo> updateUser(UserInfo userInfo) {
        return userInfoRepository.save(userInfo);
    }

    @Override
    public Mono<Void> setUserAuthorities(String username, List<GrantedAuthority> authorities) {
        return userInfoRepository.findByUsername(username)
                .flatMap(userInfo -> {
                    userInfo.setAuthorities(authorities);
                    userInfoRepository.save(userInfo);
                    return Mono.empty();
                }).then();
    }

    @Override
    public Mono<Void> setUserAutthorities(String username, String... authorities) {
        return setUserAuthorities(username, Arrays.stream(authorities)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList()));
    }

    @Override
    public Mono<Void> hasAuthority(String username, String authority) {
        return userInfoRepository.findByUsername(username)
                .flatMap(userInfo -> userInfo.getAuthorities().stream()
                        .anyMatch(authority::equals) ? Mono.empty() : Mono.error(ServiceException.build(UNAUTHORIZED)));
    }

    private Mono<Boolean> checkUserExists(String username) {
        return userInfoRepository.existsByUsername(username);
    }
    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userInfoRepository.findByUsername(username)
                .cast(UserDetails.class).switchIfEmpty(Mono.error(ServiceException.build(BAD_REQUEST)));
    }
}
