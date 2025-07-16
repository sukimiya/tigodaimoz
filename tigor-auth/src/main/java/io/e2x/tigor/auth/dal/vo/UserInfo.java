package io.e2x.tigor.auth.dal.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.e2x.tigor.auth.data.UserInfoDto;
import io.e2x.tigor.frameworks.common.pojo.BaseDO;
import io.e2x.tigor.frameworks.dal.typehandlers.GrantedAuthorityTypeHandler;
import lombok.*;
import org.apache.ibatis.type.JdbcType;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(value = "system_users")
@TableName(value = "system_users", autoResultMap = true)
public class UserInfo extends BaseDO implements UserDetails, CredentialsContainer {

    @Id
    private BigInteger id;
    private String username;
    private String password;
    private String email;
    private int sex;
    private boolean credentialsExpired;
    @TableField(jdbcType = JdbcType.VARCHAR, typeHandler = GrantedAuthorityTypeHandler.class, value = "authorities")
    private List<GrantedAuthority> authorities;

    private int status;

    public UserInfo(String username, String password, String email,int status, boolean b1) {
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

    public UserInfo(UserInfoDto userInfoDto) {
        this.id = userInfoDto.getId();
        this.username = userInfoDto.getUsername();
        this.password = userInfoDto.getPassword();
        this.email = userInfoDto.getEmail();
        this.sex = userInfoDto.getSex();
        this.credentialsExpired = userInfoDto.isCredentialsExpired();
        this.authorities = Arrays.stream(userInfoDto.getAuthorities()
                .split( ","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        this.status = userInfoDto.getStatus();
        super.setCreator(userInfoDto.getCreator());
        super.setUpdater(userInfoDto.getUpdater());
        super.setDeleted(userInfoDto.getDeleted());
    }
}
