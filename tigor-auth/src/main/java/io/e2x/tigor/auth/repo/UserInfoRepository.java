package io.e2x.tigor.auth.repo;

import io.e2x.tigor.auth.dal.vo.UserInfo;
import io.e2x.tigor.auth.data.UserInfoDto;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

@Repository
public interface UserInfoRepository extends ReactiveCrudRepository<UserInfo, BigInteger> {

    Mono<UserInfoDto> findByUsername(String username);

    Mono<Boolean> existsByUsername(String username);

    Mono<UserInfoDto> findByEmail(String email);
}