package io.e2x.tigor.frameworks.dal.converters;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@WritingConverter
@AllArgsConstructor
@Component
public class ListGrantedAuthorityWhitingConverter implements Converter<List<GrantedAuthority>, String> {
    @Override
    public String convert(List<GrantedAuthority> source) {
        return source
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining( ","));
    }
}
