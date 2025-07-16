package io.e2x.tigor.frameworks.dal.typehandlers;

import org.apache.ibatis.type.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes({List.class})
public class GrantedAuthorityTypeHandler extends BaseTypeHandler<List<GrantedAuthority>> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<GrantedAuthority> parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining( ",")));
    }

    @Override
    public List<GrantedAuthority> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return Arrays.stream(rs.getString(columnName).split( ","))
                .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    public List<GrantedAuthority> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return Arrays.stream(rs.getString(columnIndex).split( ","))
                .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    public List<GrantedAuthority> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return Arrays.stream(cs.getString(columnIndex).split( ","))
                .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }
}
