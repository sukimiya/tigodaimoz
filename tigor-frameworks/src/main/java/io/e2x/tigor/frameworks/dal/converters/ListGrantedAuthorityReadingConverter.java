package io.e2x.tigor.frameworks.dal.converters;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ReadingConverter
@AllArgsConstructor
@Component
public class ListGrantedAuthorityReadingConverter implements Converter<String, List<GrantedAuthority>> {
    @Override
    public List<GrantedAuthority> convert(@NonNull String s) {
        return Arrays.stream(s.split( ",")).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

}
