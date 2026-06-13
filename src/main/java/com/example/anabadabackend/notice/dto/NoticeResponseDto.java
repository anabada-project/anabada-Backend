package com.example.anabadabackend.notice.dto;

import com.example.anabadabackend.notice.entity.Notice;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NoticeResponseDto {

    private final Long noticeId;
    private final String noticeTitle;
    private final String noticeContent;
    private final LocalDateTime noticeTime;

    public NoticeResponseDto(Notice notice) {
        this.noticeId = notice.getId();
        this.noticeTitle = notice.getNoticeTitle();
        this.noticeContent = notice.getNoticeContent();
        this.noticeTime = notice.getNoticeTime();
    }
}