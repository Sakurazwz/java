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

    Optional<BorrowRecord> findByBookIdAndUserId(Long bookId, Long userId);

    List<BorrowRecord> findByUserId(Long userId);

    // 查询逾期未还的借阅记录：应还日期已过（记录仍存在即未归还）
    List<BorrowRecord> findByUserIdAndReturnDateBefore(Long userId, LocalDate date);

    boolean existsByUserId(Long userId);
}
