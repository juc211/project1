package community.communityBoard.config;

import community.communityBoard.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration // @Bean을 사용하여 수동으로 스프링 컨테이너에 빈을 등록하는 어노테이션
@EnableWebSecurity //Spring Security 설정을 활성화하는 어노테이션
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;


    /**
     * Spring security의 필터 체인을 설정하기 위한 메서드
     * 접근 권한, 보안 기능 등 설정을 하는 메인 메서드
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {

        http
                // 1. CSRF(Cross-Site Request Forgery) 비활성화 : JWT에서는 필요X
                .csrf(csrf -> csrf.disable())

                // 2. formLogin 비활성화
                .formLogin((auth) -> auth.disable())

                // 3. httpBasic 비활성화
                .httpBasic((auth)-> auth.disable())

                // 4. 세션 관리 정책 : JWT를 사용하기 때문에 세션 사용X
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 5. 접근 권한 설정
                .authorizeHttpRequests(auth -> auth

                        // - 회원가입, 로그인은 토큰 없이도 접근 가능해야 함 -> permitAll();
                        .requestMatchers("/api/members/signup", "/api/members/login").permitAll()

                        // - 게시글 목록 조회, 상세 조회는 비로그인 사용자도 볼 수 있게 허용
                        .requestMatchers(HttpMethod.GET, "/api/boards", "/api/boards/**").permitAll()

                        // - 댓글 조회도 비로그인 사용자에게 허용
                        .requestMatchers(HttpMethod.GET, "/api/boards/*/comments").permitAll()

                        //위에서 명시되지 않은 나머지 요청은 모두 인증(jwt 토큰) 필요 -> authenticated();
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 비밀번호 암호화를 위한 BCryptPasswordEncoder 도입
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 로그인 요청시 실제 인증을 처리하는 AuthenticationManager 등록
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }
}
