package com.gcc.library1.service;

import com.gcc.library1.model.BorrowRecord;
import com.gcc.library1.repository.BorrowRecordRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowRecordService {
    private final BorrowRecordRepository borrowRecordRepository;
    private final BookService bookService;

    @Transactional
    public BorrowRecord addBorrow(Long bookId, Long userId, LocalDate borrowDate, LocalDate returnDate) {
        // 借书时更新书籍的count和borrowCount（带悲观锁防并发）
        bookService.borrowBook(bookId);

        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setBookId(bookId);
        borrowRecord.setUserId(userId);
        borrowRecord.setBorrowDate(borrowDate);
        borrowRecord.setReturnDate(returnDate);
        return borrowRecordRepository.save(borrowRecord);
    }

    public BorrowRecord updateBorrow(Long bookId, Long userId, LocalDate returnDate) {
        BorrowRecord borrowRecord = borrowRecordRepository.findByBookIdAndUserId(bookId, userId)
                .orElseThrow(() -> new EntityNotFoundException("BorrowRecord not found with bookId:" + bookId + " and userId:" + userId));
        borrowRecord.setReturnDate(returnDate);
        return borrowRecordRepository.save(borrowRecord);
    }

    public List<BorrowRecord> getBorrowBooksByUserId(Long userId) {
        return borrowRecordRepository.findByUserId(userId);
    }

    @Transactional
    public void deleteBorrow(Long bookId, Long userId) {
        BorrowRecord borrowRecord = borrowRecordRepository.findByBookIdAndUserId(bookId, userId)
                .orElseThrow(() -> new EntityNotFoundException("BorrowRecord not found with bookId:" + bookId + " and userId:" + userId));

        // 先还书（可能因校验失败），再删记录
        bookService.returnBook(bookId);
        borrowRecordRepository.deleteById(borrowRecord.getId());
    }

    public boolean hasBorrowedThisBook(Long bookId, Long userId) {
        return borrowRecordRepository.findByBookIdAndUserId(bookId, userId).isPresent();
    }

    public BorrowRecord getBorrowedRecord(Long bookId, Long userId) {
        return borrowRecordRepository.findByBookIdAndUserId(bookId, userId)
                .orElseThrow(() -> new EntityNotFoundException("BorrowRecord not found with bookId:" + bookId + " and userId:" + userId));
    }

    public boolean isBookBorrowedByAnyone(Long bookId) {
        return borrowRecordRepository.findByBookId(bookId).isPresent();
    }

    public boolean hasBorrowedBooks(Long userId) {
        return borrowRecordRepository.existsByUserId(userId);
    }

    public List<BorrowRecord> getOverdueBorrowRecordsByUserId(Long userId) {
        LocalDate today = LocalDate.now();
        return borrowRecordRepository.findByUserIdAndReturnDateBefore(userId, today);
    }

    public List<BorrowRecord> getAllBorrowRecords() {
        return borrowRecordRepository.findAll();
    }
}
