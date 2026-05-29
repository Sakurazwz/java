package com.gcc.library1.repository;

import com.gcc.library1.model.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    List<BorrowRecord> findByBookId(Long bookId);

    List<BorrowRecord> findByUserIdAndReturnDateIsNull(Long userId);

    List<BorrowRecord> findByUserId(Long userId);

    // 查询逾期未还的记录：returnDate 在今天之前
    List<BorrowRecord> findByUserIdAndReturnDateBefore(Long userId, LocalDate date);
}
