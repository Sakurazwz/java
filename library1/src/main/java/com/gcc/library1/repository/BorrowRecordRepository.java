package com.gcc.library1.repository;

import com.gcc.library1.model.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    Optional<BorrowRecord> findByBookId(Long bookId);

    List<BorrowRecord> findByUserIdAndReturnDateIsNull(Long userId);

    List<BorrowRecord> findByUserId(Long userId);

    // 查询已归还且归还日期早于指定日期的记录
    List<BorrowRecord> findByUserIdAndReturnDateBeforeAndReturnDateIsNotNull(Long userId, LocalDate date);
}
