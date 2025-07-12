package io.e2x.tigor.auth.resolvers;

import graphql.kickstart.tools.GraphQLQueryResolver;
import io.e2x.tigor.auth.dal.sql.UserInfoRepository;
import io.e2x.tigor.auth.dal.vo.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class Query implements GraphQLQueryResolver {

    private final UserInfoRepository userRepository;

    public Mono<List<UserInfo>> users() {
        return userRepository.findAll().collect(Collectors.toList());
    }
}
