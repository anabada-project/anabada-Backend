package com.example.anabadabackend.notice.service;

import com.example.anabadabackend.global.exception.EmailAuthException;
import com.example.anabadabackend.notice.dto.NoticeRequestDto;
import com.example.anabadabackend.notice.dto.NoticeResponseDto;
import com.example.anabadabackend.notice.entity.Notice;
import com.example.anabadabackend.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeRepository noticeRepository;

    // 공지 작성
    @Transactional
    public NoticeResponseDto create(NoticeRequestDto request) {
        Notice notice = Notice.builder()
                .noticeTitle(request.getNoticeTile())
                .noticeContent(request.getNoticeContent())
                .build();
        return new NoticeResponseDto(noticeRepository.save(notice));
    }

    // 공지 수정
    @Transactional
    public NoticeResponseDto update(Long noticeId, NoticeRequestDto request) {
        Notice notice = findNoticeById(noticeId);
        notice.update(request.getNoticeTile(), request.getNoticeContent());
        return new NoticeResponseDto(notice);
    }

    // 공지 전체 조회
    public List<NoticeResponseDto> getAll() {
        return noticeRepository.findAllByOrderByNoticeTimeDesc()
                .stream()
                .map(NoticeResponseDto::new)
                .collect(Collectors.toList());
    }

    // 공지 단건 조회 (바로가기)
    public NoticeResponseDto getOne(Long noticeId) {
        return new NoticeResponseDto(findNoticeById(noticeId));
    }

    // 공지 삭제
    @Transactional
    public void delete(Long noticeId) {
        Notice notice = findNoticeById(noticeId);
        noticeRepository.delete(notice);
    }

    private Notice findNoticeById(Long noticeId) {
        return noticeRepository.findById(noticeId)
                .orElseThrow(() -> new EmailAuthException("존재하지 않는 공지입니다.", HttpStatus.NOT_FOUND));
    }
}