package com.example.anabadabackend.global.security;

import com.example.anabadabackend.global.util.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter  {

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 🟢 [변경 핵심] 프리패스 API 주소들은 토큰이 없으므로 이 필터 검증을 그냥 건너뛰게 만듭니다.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        // 🟢 .contains() 또는 주소의 앞부분만 체크하는 방식을 쓰면
        // 뒤에 슬래시(/)가 붙거나 주소가 미세하게 달라져도 완벽하게 잡아내서 통과시킵니다.
        return path.contains("/api/auth/email") ||
                path.contains("/api/auth/signin") ||
                path.contains("/api/auth/signup");
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 헤더에서 토큰 추출
        String token = resolveToken(request);

        // 토큰이 존재하고, 유효성 검증을 통과했다면 인증 객체를 생성하여 세션에 넣어줍니다.
        if (token != null && jwtTokenProvider.validateToken(token)) {
            Long userId = jwtTokenProvider.getUserId(token);

            // 로그인 상태를 유지해주는 핵심 객체 생성
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());

            // 스프링 시큐리티 시스템 장부에 "이 유저 인증됨"이라고 기록
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 뒤의 실제 토큰 문자열만 잘라냄
        }
        return null;
    }
}