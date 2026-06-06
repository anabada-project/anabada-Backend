package com.example.anabadabackend.global.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EmailAuthException extends RuntimeException {

    private final HttpStatus status;

    public EmailAuthException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    // 🔴 [추가] 로그인 실패 전용 정적 팩토리 메서드
    // 보안을 위해 이메일이 틀렸든 비밀번호가 틀렸든 동일한 메시지와 BAD_REQUEST(400)를 반환합니다.
    public static EmailAuthException loginFailed() {
        return new EmailAuthException("이메일 또는 비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
    }

    // 자주 쓰는 케이스 정적 팩토리
    public static EmailAuthException codeNotFound() {
        return new EmailAuthException("인증 코드가 존재하지 않거나 만료되었습니다.", HttpStatus.BAD_REQUEST);
    }

    public static EmailAuthException codeNotMatch() {
        return new EmailAuthException("인증 코드가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
    }

    public static EmailAuthException notVerified() {
        return new EmailAuthException("이메일 인증이 완료되지 않았습니다.", HttpStatus.FORBIDDEN);
    }

    public static EmailAuthException sendFailed() {
        return new EmailAuthException("이메일 발송에 실패했습니다. 잠시 후 다시 시도해주세요.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}