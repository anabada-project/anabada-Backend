package com.example.anabadabackend.notice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticeRequestDto {

    private String noticeTitle;
    private String noticeContent;
}