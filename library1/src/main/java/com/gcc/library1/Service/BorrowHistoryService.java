package com.gcc.library1.service;

import com.gcc.library1.model.BorrowHistory;
import com.gcc.library1.repository.BorrowHistoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowHistoryService {
    private final BorrowHistoryRepository borrowHistoryRepository;

    public BorrowHistory addBorrowHistory(Long bookId, Long userId, String behavior) {
        BorrowHistory borrowHistory = new BorrowHistory();
        borrowHistory.setBookId(bookId);
        borrowHistory.setUserId(userId);
        borrowHistory.setDate(LocalDate.now());
        borrowHistory.setBehaviour(behavior);
        return borrowHistoryRepository.save(borrowHistory);
    }

    public List<BorrowHistory> getBorrowHistoryByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        return borrowHistoryRepository.findByUserId(userId);
    }

    public List<BorrowHistory> getBorrowHistoryByBookId(Long bookId) {
        if (bookId == null) {
            throw new IllegalArgumentException("书籍ID不能为空");
        }
        return borrowHistoryRepository.findByBookId(bookId);
    }
}
