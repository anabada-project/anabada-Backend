package com.example.anabadabackend.notice.notice.dto;

import com.example.anabadabackend.notice.entity.Notice;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NoticeResponseDto {

    private final Long notice_id;
    private final String notice_title;
    private final String notice_content;
    private final LocalDateTime notice_time;

    public NoticeResponseDto(Notice notice) {
        this.notice_id = notice.getId();
        this.notice_title = notice.getNoticeTitle();
        this.notice_content = notice.getNoticeContent();
        this.notice_time = notice.getNoticeTime();
    }
}