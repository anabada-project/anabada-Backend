package com.example.anabadabackend.notice.notice.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notice")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 공지 제목
    @Column(nullable = false, length = 100)
    private String noticeTitle;

    // 공지 내용
    @Column(nullable = false, columnDefinition = "TEXT")
    private String noticeContent;

    // 공지 작성 시간
    @Column(nullable = false)
    private LocalDateTime noticeTime;

    @Builder
    public Notice(String noticeTitle, String noticeContent) {
        this.noticeTitle = noticeTitle;
        this.noticeContent = noticeContent;
        this.noticeTime = LocalDateTime.now();
    }

    public void update(String noticeTitle, String noticeContent) {
        this.noticeTitle = noticeTitle;
        this.noticeContent = noticeContent;
        this.noticeTime = LocalDateTime.now();
    }
}