package io.e2x.tigor.auth.dal.vo.r2dbc;

import io.e2x.tigor.frameworks.dal.converters.ListGrantedAuthorityReadingConverter;
import io.e2x.tigor.frameworks.dal.converters.ListGrantedAuthorityWhitingConverter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.DialectResolver;
import org.springframework.r2dbc.core.DatabaseClient;

import java.util.ArrayList;
import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class R2dbcConvertorsConfig {
    private final DatabaseClient databaseClient;

    @Bean
    public R2dbcCustomConversions r2dbcCustomConversions(ListGrantedAuthorityReadingConverter readingConverter,
                                                         ListGrantedAuthorityWhitingConverter writingConverter) {
        val dialect = DialectResolver.getDialect(this.databaseClient.getConnectionFactory());
        val converters = new ArrayList<>();
        converters.add(readingConverter);
        converters.add(writingConverter);
        converters.addAll(dialect.getConverters());
        return R2dbcCustomConversions.of(dialect, converters);
    }
}
