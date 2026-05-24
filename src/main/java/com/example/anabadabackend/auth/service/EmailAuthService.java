package com.example.anabadabackend.auth.service;

import com.example.anabadabackend.auth.repository.EmailAuthRedisRepository;
import com.example.anabadabackend.global.exception.*;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailAuthService {

    private final EmailAuthRedisRepository emailAuthRedisRepository;
    private final JavaMailSender mailSender;

    @Value("${email-auth.code-ttl}")
    private long codeTtl;

    @Value("${email-auth.verified-ttl}")
    private long verifiedTtl;

    @Value("${email-auth.code-length}")
    private int codeLength;

    @Value("${email-auth.mock-send:false}")
    private boolean mockSend;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * 인증 이메일 발송
     */
    public void sendVerificationEmail(String email) {
        String code = generateCode();
        emailAuthRedisRepository.saveCode(email, code, codeTtl);

        if (mockSend) {
            // 로컬 개발: 실제 발송 대신 로그 출력
            log.info("[이메일 인증 코드 - 로컬 모드] email={}, code={}", email, code);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject("[아나바다] 이메일 인증 코드");
            helper.setText(buildEmailTemplate(code), true); // true = HTML
            mailSender.send(message);
            log.info("[이메일 인증 발송 완료] email={}", email);
        } catch (Exception e) {
            log.error("[이메일 발송 실패] email={}, error={}", email, e.getMessage());
            emailAuthRedisRepository.delete(email); // 발송 실패 시 Redis에서 삭제
            throw EmailAuthException.sendFailed();
        }
    }

    /**
     * 인증 코드 검증
     */
    public void verifyCode(String email, String inputCode) {
        String savedCode = emailAuthRedisRepository.getCode(email)
                .orElseThrow(EmailAuthException::codeNotFound);

        if (!savedCode.equals(inputCode)) {
            throw EmailAuthException.codeNotMatch();
        }

        // 인증 완료 상태로 저장 (TTL 갱신)
        emailAuthRedisRepository.markVerified(email, verifiedTtl);
        log.info("[이메일 인증 완료] email={}", email);
    }

    /**
     * 인증 완료 여부 확인 (회원가입 시 호출)
     */
    public void checkVerified(String email) {
        if (!emailAuthRedisRepository.isVerified(email)) {
            throw EmailAuthException.notVerified();
        }
    }

    /**
     * 인증 정보 삭제 (회원가입 완료 후 호출)
     */
    public void deleteVerification(String email) {
        emailAuthRedisRepository.delete(email);
    }

    // ── private ────────────────────────────────────────────

    private String generateCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < codeLength; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private String buildEmailTemplate(String code) {
        return """
                <!DOCTYPE html>
                <html lang="ko">
                <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;">
                  <div style="max-width: 500px; margin: 0 auto; background: #fff; border-radius: 8px; padding: 40px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
                    <h2 style="color: #2d8c4e; margin-bottom: 8px;">아나바다</h2>
                    <p style="color: #555; font-size: 15px;">이메일 인증 코드를 확인해주세요.</p>
                    <div style="margin: 30px 0; padding: 20px; background: #f0f9f4; border-radius: 6px; text-align: center;">
                      <span style="font-size: 36px; font-weight: bold; letter-spacing: 8px; color: #2d8c4e;">%s</span>
                    </div>
                    <p style="color: #999; font-size: 13px;">이 코드는 <strong>5분간</strong> 유효합니다.</p>
                    <p style="color: #999; font-size: 12px;">본인이 요청하지 않았다면 이 이메일을 무시해주세요.</p>
                  </div>
                </body>
                </html>
                """.formatted(code);
    }
}
