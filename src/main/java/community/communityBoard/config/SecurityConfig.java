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


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {

        http
                // 1. csrf 및 기본 로그인 방식 비활성화
                .csrf(csrf -> csrf.disable())

                .formLogin((auth) -> auth.disable())

                .httpBasic((auth)-> auth.disable())

                // 2. 세션 관리 정책 : JWT를 사용하기 때문에 세션 사용X
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 3. 접근 권한 설정
                .authorizeHttpRequests(auth -> auth

                        // - 회원가입, 로그인은 토큰 없이도 접근 가능해야 함
                        .requestMatchers("/api/members/signup", "/api/members/login").permitAll()

                        // - 게시글 목록 조회, 상세 조회는 비로그인 사용자도 볼 수 있게 허용
                        .requestMatchers(HttpMethod.GET, "/api/boards", "/api/boards/**").permitAll()

                        // - 댓글 조회도 비로그인 사용자에게 허용
                        .requestMatchers(HttpMethod.GET, "/api/boards/*/comments").permitAll()

                        //위에서 명시되지 않은 나머지 요청은 모두 인증(jwt 토큰) 필요
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }
}
