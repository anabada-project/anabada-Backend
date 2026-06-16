// 기존 SecurityConfig에서 permitAll() 목록에 아래 경로 추가하세요.
// 예시:

@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/health").permitAll()   // ← 이거 추가
            // ... 기존 설정
            .anyRequest().authenticated()
        );
    // ... 나머지 설정
    return http.build();
}
