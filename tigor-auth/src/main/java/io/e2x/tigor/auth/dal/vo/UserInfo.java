package io.e2x.tigor.auth.dal.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.e2x.tigor.frameworks.common.exception.enums.CommonStatus;
import io.e2x.tigor.frameworks.common.pojo.BaseDO;
import io.e2x.tigor.frameworks.common.pojo.GrantedAuthorityTypeHandler;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.JdbcType;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "system_user")
public class UserInfo extends BaseDO implements UserDetails, CredentialsContainer {

    @Id
    private BigInteger id;
    private String username;
    private String password;
    private String email;
    private boolean credentialsExpired;
    @TableField(jdbcType = JdbcType.VARCHAR, typeHandler = GrantedAuthorityTypeHandler.class)
    private List<GrantedAuthority> authorities;
    @TableField(jdbcType = JdbcType.INTEGER, typeHandler = CommonStatus.Handler.class)
    private CommonStatus status = CommonStatus.DISABLED;

    public UserInfo(String username, String password, String email,CommonStatus status, boolean b1) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.credentialsExpired = b1;
        this.status = status;
        super.setDeleted(false);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public void eraseCredentials() {
        password = null;
    }


}
