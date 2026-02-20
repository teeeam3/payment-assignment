package sparta.paymentassignment.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sparta.paymentassignment.exception.ErrorCode;
import sparta.paymentassignment.exception.UnauthorizedException;
import java.io.IOException;
import java.util.List;

/**
 * JWT 토큰 인증 필터
 * 모든 요청에서 JWT 토큰을 검증하고 SecurityContext에 인증 정보 설정
 *
 * TODO: 개선 사항
 * - 역할(Role) 정보를 토큰에서 추출
 * - 예외 처리 개선
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            // 1. Request Header에서 JWT 토큰 추출
            String token = getJwtFromRequest(request);

            // 2. 토큰 유효성 검증
            if (token != null && jwtTokenProvider.validateToken(token)) {
                // 3. 토큰에서 사용자 정보 추출
//                String email = jwtTokenProvider.getEmail(token);
                Long userId = jwtTokenProvider.getUserId(token);
                String role = jwtTokenProvider.getRole(token);


                // 4. 인증 객체 생성
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userId,
                        null,
                            List.of(new SimpleGrantedAuthority(role)));

                // 5. SecurityContext에 인증 정보 설정
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (UnauthorizedException e) {
            throw new UnauthorizedException(ErrorCode.TOKEN_EXPIRED);
            // TODO: 구현 - 적절한 에러 응답
        } catch (Exception e) {
            throw new UnauthorizedException(ErrorCode.TOKEN_INVALID);
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Request Header에서 JWT 토큰 추출
     * Authorization: Bearer {token}
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}
