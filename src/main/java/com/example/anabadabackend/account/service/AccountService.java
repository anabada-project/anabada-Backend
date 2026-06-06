package com.example.anabadabackend.account.service;

import com.example.anabadabackend.account.dto.AccountResponse;
import com.example.anabadabackend.auth.repository.UserRepository;
import com.example.anabadabackend.entity.User;
import com.example.anabadabackend.global.exception.EmailAuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {

    private final UserRepository userRepository;


    public AccountResponse getMyInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EmailAuthException(
                        "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        return new AccountResponse(user);
    }
}
