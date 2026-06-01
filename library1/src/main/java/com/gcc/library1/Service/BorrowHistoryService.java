package com.gcc.library1.service;

import com.gcc.library1.dto.BorrowHistoryDTO;
import com.gcc.library1.dto.mapper.EntityMapper;
import com.gcc.library1.model.BorrowHistory;
import com.gcc.library1.repository.BorrowHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowHistoryService {
    private final BorrowHistoryRepository borrowHistoryRepository;
    private final EntityMapper mapper;

    public List<BorrowHistoryDTO> getAllBorrowHistory() {
        return mapper.toBorrowHistoryDTOList(borrowHistoryRepository.findAllByOrderByDateDesc());
    }

    public BorrowHistory addBorrowHistory(Long bookId, Long userId, String behavior) {
        BorrowHistory borrowHistory = new BorrowHistory();
        borrowHistory.setBookId(bookId);
        borrowHistory.setUserId(userId);
        borrowHistory.setDate(LocalDateTime.now());
        borrowHistory.setBehaviour(behavior);
        return borrowHistoryRepository.save(borrowHistory);
    }

    public List<BorrowHistoryDTO> getBorrowHistoryByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        return mapper.toBorrowHistoryDTOList(borrowHistoryRepository.findByUserIdOrderByDateDesc(userId));
    }

    public List<BorrowHistoryDTO> getBorrowHistoryByBookId(Long bookId) {
        if (bookId == null) {
            throw new IllegalArgumentException("书籍ID不能为空");
        }
        return mapper.toBorrowHistoryDTOList(borrowHistoryRepository.findByBookIdOrderByDateDesc(bookId));
    }

    public List<BorrowHistoryDTO> searchBorrowHistory(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        List<BorrowHistory> result;
        if (userId != null && startDate != null && endDate != null) {
            result = borrowHistoryRepository.findByUserIdAndDateBetweenOrderByDateDesc(userId, startDate, endDate);
        } else if (startDate != null && endDate != null) {
            result = borrowHistoryRepository.findByDateBetweenOrderByDateDesc(startDate, endDate);
        } else if (userId != null) {
            result = borrowHistoryRepository.findByUserIdOrderByDateDesc(userId);
        } else {
            result = borrowHistoryRepository.findAllByOrderByDateDesc();
        }
        return mapper.toBorrowHistoryDTOList(result);
    }
}
