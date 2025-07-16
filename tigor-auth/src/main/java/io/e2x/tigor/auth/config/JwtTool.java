package io.e2x.tigor.auth.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Data
@Component
public class JwtTool {
    @Value("${jwt.secret}")
    private String key; // 引用 application.yml 中的 jwt.secret 配置

    public static final long DEFAULT_EXPIRE_TIME = 1000L * 60L * 60L * 24L;

    public String CreateToken(String userid, String username, List<String> roles) {
        return CreateToken(userid, username, roles, DEFAULT_EXPIRE_TIME);
    }
    public String CreateToken(String userid, String username, List<String> roles, long ot) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        JwtBuilder builder = Jwts.builder()
                .setId(userid)
                .setSubject(username)
                .setIssuedAt(now)
                .signWith(SignatureAlgorithm.HS256, key)
                .claim("roles", roles);
        if (ot > 0L) {
            builder.setExpiration(new Date(nowMillis + ot));
        }
        return builder.compact();
    }

    public boolean VerityToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token)
                    .getBody();
            if (claims != null) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getUserid(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token)
                    .getBody();
            if (claims != null) {
                return claims.getId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getUserName(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token)
                    .getBody();
            if (claims != null) {
                return claims.getSubject();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getUserRoles(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token)
                    .getBody();
            if (claims != null) {
                return (List<String>) claims.get("roles");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getClaims(String token, String param) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token)
                    .getBody();
            if (claims != null) {
                return claims.get(param).toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
