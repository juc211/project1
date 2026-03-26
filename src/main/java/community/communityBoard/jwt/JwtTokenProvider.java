package community.communityBoard.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

/**
 * JwtTokenProvider의 핵심 기능
 * 1. 토큰 생성 : 사용자의 정보를 받아 Jwt를 만든다.
 * 2. 토큰 복호화 : 토큰을 받아 사용자의 정보를 꺼낸다.
 * 3. 토큰 검증 : 토큰의 유효기간이 지나지 않았는지 위조된 토큰이 아닌지 검증
 */
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long expirationTime;

    private final UserDetailsService userDetailsService;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKeyString,
                            @Value("${jwt.expiration}") long expirationTime,
                            UserDetailsService userDetailsService){
        byte[] keyBytes = Base64.getDecoder().decode(secretKeyString);
        this.secretKey  = Keys.hmacShaKeyFor(keyBytes);
        this.expirationTime = expirationTime;
        this.userDetailsService = userDetailsService;
    }

    /**
     * 토큰 생성 메서드
     */
    public String createToken(String email) {
        Date now = new Date();                              // 현재 시간
        Date expiry = new Date(now.getTime() + expirationTime); // 만료 시간

        return Jwts.builder()
                .subject(email)       // Payload의 "sub" 클레임 → 사용자 식별자
                .issuedAt(now)        // Payload의 "iat" 클레임 → 발급 시간
                .expiration(expiry)   // Payload의 "exp" 클레임 → 만료 시간
                .signWith(secretKey)  // 비밀키로 서명 → Signature 부분 생성
                .compact();           // 최종 JWT 문자열로 직렬화
    }

    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)       // 서명 검증에 사용할 키 설정
                .build()
                .parseSignedClaims(token)    // 토큰 파싱 + 서명 검증 (실패 시 예외 발생)
                .getPayload()                // Payload(Claims) 객체 추출
                .getSubject();               // "sub" 클레임 (= 이메일) 반환
    }

    /**
     * 인증 정보 추출 메서드
     */
    public Authentication getAuthentication(String token) {
        // 1. 토큰에서 이메일 추출
        String email = getEmailFromToken(token);

        // 2. 이메일로 유저 정보 로드 (CustomUserDetailService 호출)
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // 3. 스프링 시큐리티 인증 토큰 생성 (비밀번호는 이미 검증되었으므로 빈 문자열)
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    /**
     * 토큰 유효성 검사 메서드
     */
    public boolean validateToken(String token) {
        try {
            // 토큰 파싱 시도 → 서명 검증 + 만료 확인이 자동으로 수행됨
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true; // 위 과정에서 예외가 발생하지 않으면 유효한 토큰
        } catch (ExpiredJwtException e) {
            // 토큰 만료 (가장 흔한 케이스)
            // → 클라이언트는 이 응답을 받으면 재로그인하거나 Refresh Token으로 갱신해야 함
            System.out.println("만료된 JWT 토큰입니다: " + e.getMessage());
        } catch (MalformedJwtException e) {
            // 토큰 형식이 잘못됨 (누군가 토큰을 변조했을 가능성)
            System.out.println("잘못된 JWT 토큰 형식입니다: " + e.getMessage());
        } catch (SecurityException e) {
            // 서명 검증 실패 (비밀키가 다름)
            System.out.println("JWT 서명 검증에 실패했습니다: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            // 지원하지 않는 JWT 형식
            System.out.println("지원하지 않는 JWT 토큰입니다: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            // 토큰이 빈 문자열이거나 null
            System.out.println("JWT 토큰이 비어있습니다: " + e.getMessage());
        }
        return false; // 예외가 발생하면 유효하지 않은 토큰
    }
}
