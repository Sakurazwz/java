package com.example.demo5work.service.impl;

import com.example.demo5work.entity.Book;
import com.example.demo5work.entity.BorrowRecord;
import com.example.demo5work.repository.BookRepository;
import com.example.demo5work.repository.BorrowRecordRepository;
import com.example.demo5work.service.BorrowRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Transactional
@Service
public class BorrowRecordServiceImpl implements BorrowRecordService {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;



    @Override
    public List<BorrowRecord> getBorrowRecordsByBook(Long bookId) {
        Optional<Book> bookOpt = bookRepository.findByIdWithBorrowRecords(bookId);
        if (bookOpt.isEmpty()) {
            throw new RuntimeException("图书不存在");
        }

        return List.copyOf(bookOpt.get().getBorrowRecords());
    }

    @Override
    public Optional<BorrowRecord> getCurrentBorrowRecord(Long bookId) {
        Optional<Book> bookOpt = bookRepository.findByIdWithBorrowRecords(bookId);
        if (bookOpt.isEmpty()) {
            return Optional.empty();
        }

        return bookOpt.get().getBorrowRecords().stream()
                .filter(record -> record.getReturnDate() == null)
                .findFirst();
    }

    @Override
    public List<BorrowRecord> findUnreturned() {
        return borrowRecordRepository.findUnreturned();
    }

}
