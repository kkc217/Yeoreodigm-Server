package com.yeoreodigm.server.jwt;

import com.yeoreodigm.server.domain.Authority;
import com.yeoreodigm.server.dto.jwt.TokenDto;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.exception.LoginRequiredException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.*;

import static com.yeoreodigm.server.dto.constraint.JWTConst.AUTHORITIES_KEY;

@Getter
@Component
public class TokenProvider {

    private final long ACCESS_TOKEN_EXPIRE_TIME;

    private final long REFRESH_TOKEN_EXPIRE_TIME;

    private final Key key;

    public TokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access-token-expire-time}") long accessTime,
            @Value("${jwt.refresh-token-expire-time}") long refreshTime) {
        this.ACCESS_TOKEN_EXPIRE_TIME = accessTime;
        this.REFRESH_TOKEN_EXPIRE_TIME = refreshTime;
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    protected String createToken(String email, Authority auth, long tokenValid) {
        Claims claims = Jwts.claims().setSubject(email);

        claims.put(AUTHORITIES_KEY, auth.toString());


        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValid))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String createAccessToken(String email, Authority auth) {
        return this.createToken(email, auth, ACCESS_TOKEN_EXPIRE_TIME);
    }

    public String createRefreshToken(String email, Authority auth) {
        return this.createToken(email, auth, REFRESH_TOKEN_EXPIRE_TIME);
    }

    public String getEmailByToken(String token) {
        return this.parseClaims(token).getSubject();
    }

    public TokenDto createTokenDto(String accessToken, String refreshToken, String grantType) {
        return new TokenDto(accessToken, refreshToken, grantType);
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (Objects.isNull(claims.get(AUTHORITIES_KEY)) || !StringUtils.hasText(claims.get(AUTHORITIES_KEY).toString())) {
            throw new BadRequestException("유저에게 아무런 권한이 없습니다.");
        }

        List<String> authList = new ArrayList<>();
        authList.add(claims.get(AUTHORITIES_KEY).toString());
        Collection<? extends GrantedAuthority> authorities = authList.stream().map(SimpleGrantedAuthority::new).toList();
        UserDetails principal = new User(claims.getSubject(), "", authorities);

        return new CustomEmailPasswordAuthToken(principal, "", authorities);
    }

    public int validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return 1;
        } catch (ExpiredJwtException e) {
            return 2;
        } catch (SignatureException e) {
          return 3;
        } catch (Exception e) {
            return -1;
        }
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (SignatureException e) {
            throw new LoginRequiredException("다시 로그인해주시기 바랍니다.");
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

}
