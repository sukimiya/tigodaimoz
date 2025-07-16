package io.e2x.tigor.auth.service;

import io.e2x.tigor.auth.dal.vo.UserInfo;
import io.e2x.tigor.frameworks.common.exception.ServiceException;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetailsService;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface IUserInfoService extends ReactiveUserDetailsService {
    Mono<Boolean> hasUserByName(String username) throws ServiceException;
    Mono<UserInfo> getUserById(BigInteger id) throws ServiceException;
    Mono<UserInfo> getUserByUsernamePassword(String username, String password) throws ServiceException;
    Mono<UserInfo> getUserByEmail(String email) throws ServiceException;
    Mono<Boolean> createUser(String username, String password, String email) throws ServiceException;
    Mono<Void> updateUser(UserInfo userInfo) throws ServiceException;
    Mono<Void> deleteUser(BigInteger id) throws ServiceException;

    Mono<UserInfo> getUserByName(String name);

    Mono<List<UserInfo>> getAllUsers();
}
