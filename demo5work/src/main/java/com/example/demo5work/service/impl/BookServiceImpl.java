package com.example.demo5work.service.impl;

import com.example.demo5work.entity.Book;
import com.example.demo5work.entity.BorrowRecord;
import com.example.demo5work.repository.BookRepository;
import com.example.demo5work.repository.BorrowRecordRepository;
import com.example.demo5work.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class BookServiceImpl implements BookService {

    @Autowired
    private  BookRepository bookRepository;

    @Autowired
    private  BorrowRecordRepository borrowRecordRepository;

    @Override
    public BorrowRecord borrowBook(Long bookId, String borrower) {
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        if (bookOpt.isEmpty()) {
            throw new RuntimeException("图书不存在");
        }

        Book book = bookOpt.get();

        // 检查是否已借出未还
        Set<BorrowRecord> records = book.getBorrowRecords();
        for (BorrowRecord record : records) {
            if (record.getReturnDate() == null) {
                throw new RuntimeException("该图书已被借出，尚未归还");
            }
        }

        // 创建借阅记录
        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setBook(book);
        borrowRecord.setBorrower(borrower);
        borrowRecord.setBorrowDate(LocalDateTime.now());

        return borrowRecordRepository.save(borrowRecord);
    }

    @Override
    public BorrowRecord returnBook(Long recordId) {
        Optional<BorrowRecord> recordOpt = borrowRecordRepository.findById(recordId);
        if (recordOpt.isEmpty()) {
            throw new RuntimeException("借阅记录不存在");
        }

        BorrowRecord record = recordOpt.get();

        if (record.getReturnDate() != null) {
            throw new RuntimeException("该图书已归还");
        }

        record.setReturnDate(LocalDateTime.now());
        return borrowRecordRepository.save(record);
    }

}
