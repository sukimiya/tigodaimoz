package io.e2x.tigor.auth.data;

import java.math.BigInteger;

public interface UserInfoDto {
    BigInteger getId();
    String getUsername();
    String getPassword();
    String getEmail();
    int getSex();
    boolean isCredentialsExpired();
    String getAuthorities();
    int getStatus();
    String getCreator();
    String getUpdater();
    Boolean getDeleted();

}
