package io.e2x.tigor.auth.service.impl;

import com.alibaba.fastjson.JSON;
import io.e2x.tigor.auth.dal.vo.UserInfo;
import io.e2x.tigor.auth.repo.UserInfoRepository;
import io.e2x.tigor.auth.service.IUserInfoService;
import io.e2x.tigor.frameworks.common.exception.ServiceException;
import io.e2x.tigor.frameworks.common.exception.enums.GlobalErrorCodeConstants;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class UserInfoService implements IUserInfoService {

    private static final Logger logger = Logger.getLogger(UserInfoService.class.getName());

    @Resource
    private UserInfoRepository userInfoRepository;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public Mono<Boolean> hasUserByName(String username) throws ServiceException {
        return userInfoRepository.existsByUsername(username)
                .switchIfEmpty(Mono.error(new ServiceException(GlobalErrorCodeConstants.USER_NOT_EXIST)));
    }

    @Override
    public Mono<UserInfo> getUserById(BigInteger id) throws ServiceException {
        return userInfoRepository.findById(id)
                .switchIfEmpty(Mono.error(new ServiceException(GlobalErrorCodeConstants.USER_NOT_EXIST)));
    }

    @Override
    public Mono<UserInfo> getUserByUsernamePassword(String username, String password) throws ServiceException {
        return userInfoRepository.findByUsername( username).flatMap(dto -> {
            UserInfo userInfo = new UserInfo(dto);
            if (passwordEncoder.matches(password, userInfo.getPassword())) {
                return Mono.just(userInfo);
            } else {
                return Mono.error(new ServiceException(GlobalErrorCodeConstants.USER_PASSWORD_ERROR));
            }
        });
    }

    @Override
    public Mono<UserInfo> getUserByEmail(String email) throws ServiceException {
        return userInfoRepository.findByEmail(email).map(UserInfo::new).flatMap(Mono::just)
                .switchIfEmpty(Mono.error(new ServiceException(GlobalErrorCodeConstants.USER_NOT_EXIST)));
    }

    @Override
    public Mono<Boolean> createUser(String username, String password, String email) throws ServiceException {
        return userInfoRepository.existsByUsername(username).flatMap(exists -> {
            if (exists) {
                return Mono.error(new ServiceException(GlobalErrorCodeConstants.USER_EXIST));
            } else {
                UserInfo userInfo = new UserInfo();
                userInfo.setUsername(username);
                userInfo.setPassword(passwordEncoder.encode(password));
                userInfo.setEmail(email);
                return userInfoRepository.save(userInfo).then(Mono.just(true));
            }
        });
    }

    @Override
    public Mono<Void> updateUser(UserInfo userInfo) throws ServiceException {
        return userInfoRepository.existsById(userInfo.getId()).flatMap(exists -> {
            if (exists) {
                return userInfoRepository.save(userInfo).then();
            } else {
                return Mono.error(new ServiceException(GlobalErrorCodeConstants.USER_NOT_EXIST));
            }
        });
    }

    @Override
    public Mono<Void> deleteUser(BigInteger id) throws ServiceException {
        return userInfoRepository.existsById(id).flatMap(exists -> {
            if (exists) {
                return userInfoRepository.deleteById(id).then();
            } else {
                return Mono.error(new ServiceException(GlobalErrorCodeConstants.USER_NOT_EXIST));
            }
        });
    }

    @Override
    public Mono<UserInfo> getUserByName(String name) {
        return userInfoRepository.existsByUsername( name).flatMap(exists -> {
            if (exists) {
                return userInfoRepository.findByUsername(name).map(UserInfo::new);
            } else {
                return Mono.error(new ServiceException(GlobalErrorCodeConstants.USER_NOT_EXIST));
            }
        });
    }

    @Override
    public Mono<List<UserInfo>> getAllUsers() {
        return userInfoRepository.findAll().collectList();
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return getUserByName( username).flatMap(userInfo -> {
            userInfo.getPassword();
            return Mono.just(userInfo);
        });
    }
}
