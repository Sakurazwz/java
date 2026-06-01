package com.gcc.library1.repository;

import com.gcc.library1.model.BorrowHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BorrowHistoryRepository extends JpaRepository<BorrowHistory, Long> {
    List<BorrowHistory> findByUserIdOrderByDateDesc(Long userId);

    List<BorrowHistory> findByBookIdOrderByDateDesc(Long bookId);

    List<BorrowHistory> findAllByOrderByDateDesc();

    // 按 userId 过滤
    List<BorrowHistory> findByUserId(Long userId);

    // 按日期区间过滤
    List<BorrowHistory> findByDateBetweenOrderByDateDesc(LocalDateTime start, LocalDateTime end);

    // 按 userId + 日期区间
    List<BorrowHistory> findByUserIdAndDateBetweenOrderByDateDesc(Long userId, LocalDateTime start, LocalDateTime end);
}
