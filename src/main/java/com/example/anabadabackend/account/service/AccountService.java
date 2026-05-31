package com.example.anabadabackend.account.service;

import com.example.anabadabackend.account.repository.AccountRepository;
import com.example.anabadabackend.entity.User;
import com.example.anabadabackend.global.exception.EmailAuthException;
import com.example.anabadabackend.account.dto.AccountResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountResponseDto getMyInfo(Long id) {
        User user = accountRepository.findById(id)
                .orElseThrow(() ->
                        new EmailAuthException("존재하지 않는 유저입니다.", HttpStatus.NOT_FOUND));
        return new AccountResponseDto(user);
    }
}