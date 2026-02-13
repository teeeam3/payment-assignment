package sparta.paymentassignment.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import sparta.paymentassignment.exception.ErrorCode;
import sparta.paymentassignment.exception.UnauthorizedException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 토큰 생성 및 검증 유틸리티
 * 개선할 부분: Refresh Token, Token Expiry 관리, Claims 커스터마이징 등
 */
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long tokenValidityInMilliseconds;

    public JwtTokenProvider(
        @Value("${jwt.secret:commercehub-secret-key-for-demo-please-change-this-in-production-environment}") String secret,
        @Value("${jwt.token-validity-in-seconds:86400}") long tokenValidityInSeconds
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.tokenValidityInMilliseconds = tokenValidityInSeconds * 1000;
    }

    /**
     * JWT 토큰 생성
     *
     * TODO: 개선 사항
     * - 사용자 역할(Role) 정보 추가
     * - 추가 Claims 정보 (이름, 이메일 등)
     * - Refresh Token 발급 로직
     */
    public String createToken(Authentication authentication) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + tokenValidityInMilliseconds);

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        Long userId = customUserDetails.getId();
        String role = customUserDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority).orElse("ROLE_USER");

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role)
                .issuedAt(now)
                .expiration(validity)
                .signWith(secretKey)
                .compact();
    }

    /**
     * JWT 토큰에서 사용자 이름 추출
     */
    public String getEmail(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();

        return claims.getSubject();
    }

    public Long getUserId(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return Long.valueOf(claims.getSubject());
    }

    public String getRole(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("role", String.class);
    }

    /**
     * JWT 토큰 유효성 검증
     *
     * TODO: 개선 사항
     * - 토큰 블랙리스트 체크 (로그아웃된 토큰)
     * - 토큰 갱신 로직
     * - 상세한 예외 처리
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
            return true;
            // TODO: 구체적인 예외 처리 구현
            // - ExpiredJwtException: 만료된 토큰
            // - MalformedJwtException: 잘못된 형식
            // - SignatureException: 서명 오류
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException(ErrorCode.TOKEN_EXPIRED);
        } catch (MalformedJwtException e) {
            throw new UnauthorizedException(ErrorCode.TOKEN_MALFORMED);
        } catch (SignatureException e) {
            throw new UnauthorizedException(ErrorCode.TOKEN_INVALID_SIGNATURE);
        }
    }
}
