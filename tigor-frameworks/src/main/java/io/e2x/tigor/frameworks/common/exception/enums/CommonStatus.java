package io.e2x.tigor.frameworks.common.exception.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@AllArgsConstructor
@Data
public class CommonStatus implements Serializable {
    private int status;

    public static final CommonStatus DISABLED = new CommonStatus(1);
    public static final CommonStatus ENABLED = new CommonStatus(0);

}