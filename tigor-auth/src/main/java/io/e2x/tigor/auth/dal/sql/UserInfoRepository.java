package io.e2x.tigor.auth.dal.sql;

import io.e2x.tigor.auth.dal.vo.UserInfo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

@Repository
public interface UserInfoRepository extends ReactiveCrudRepository<UserInfo, BigInteger> {
    Mono<UserInfo> findByUsername(String username);

    Mono<Boolean> existsByUsername(String username);
}
