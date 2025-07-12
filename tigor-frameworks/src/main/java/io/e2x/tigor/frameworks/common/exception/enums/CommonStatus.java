package io.e2x.tigor.frameworks.common.exception.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@AllArgsConstructor
@Data
public class CommonStatus implements Serializable {
    private int status;

    public static final CommonStatus DISABLED = new CommonStatus(0);
    public static final CommonStatus ENABLED = new CommonStatus(1);

    public static class Handler extends BaseTypeHandler<CommonStatus> {
        Conventer conventer = new Conventer();
        @Override
        public void setNonNullParameter(PreparedStatement ps, int i, CommonStatus parameter, JdbcType jdbcType) throws SQLException {
            ps.setInt(i, parameter.getStatus());
        }

        @Override
        public CommonStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
            return conventer.valueOf(rs.getInt(columnName));
        }

        @Override
        public CommonStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
            return conventer.valueOf(rs.getInt(columnIndex));
        }

        @Override
        public CommonStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
            return conventer.valueOf(cs.getInt(columnIndex));
        }
    }
    public static class Conventer {
        public CommonStatus valueOf(int status) {
            return switch (status) {
                case 1 -> CommonStatus.ENABLED;
                case 0 -> CommonStatus.DISABLED;
                default -> CommonStatus.DISABLED;
            };
        }
        public int toInt(CommonStatus status) {
            return status.getStatus();
        }
    }
}
