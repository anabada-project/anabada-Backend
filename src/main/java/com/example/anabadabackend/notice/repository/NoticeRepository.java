package com.example.anabadabackend.notice.repository;

import com.example.anabadabackend.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    // 최신순 전체 조회
    List<Notice> findAllByOrderByNoticeTimeDesc();
}