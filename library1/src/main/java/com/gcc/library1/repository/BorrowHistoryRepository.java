package com.gcc.library1.repository;

import com.gcc.library1.model.BorrowHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BorrowHistoryRepository extends JpaRepository<BorrowHistory, Long> {
    List<BorrowHistory> findByUserIdOrderByDateDesc(Long userId);

    List<BorrowHistory> findByBookIdOrderByDateDesc(Long bookId);

    List<BorrowHistory> findAllByOrderByDateDesc();
}
