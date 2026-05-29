package com.gcc.library1.service;

import com.gcc.library1.dto.BookCreateRequest;
import com.gcc.library1.dto.BookResponse;
import com.gcc.library1.dto.BookUpdateRequest;
import com.gcc.library1.dto.mapper.EntityMapper;
import com.gcc.library1.model.Book;
import com.gcc.library1.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final EntityMapper mapper;

    public BookResponse addBook(BookCreateRequest request) {
        Book book = mapper.toEntity(request);
        return mapper.toBookResponse(bookRepository.save(book));
    }

    public BookResponse updateBook(BookUpdateRequest request) {
        Book book = bookRepository.findById(request.getId())
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id:" + request.getId()));
        mapper.updateEntity(book, request);
        return mapper.toBookResponse(bookRepository.save(book));
    }

    /**
     * 借书：count -1，borrowCount +1
     */
    @Transactional
    public Book borrowBook(Long bookId) {
        Book book = bookRepository.findByIdForUpdate(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id:" + bookId));
        if (book.getCount() <= 0) {
            throw new IllegalStateException("该书籍库存不足，无法借阅");
        }
        book.setCount(book.getCount() - 1);
        book.setBorrowCount(book.getBorrowCount() + 1);
        return bookRepository.save(book);
    }

    /**
     * 还书：count +1，borrowCount -1
     */
    @Transactional
    public Book returnBook(Long bookId) {
        Book book = bookRepository.findByIdForUpdate(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id:" + bookId));
        if (book.getBorrowCount() <= 0) {
            throw new IllegalStateException("该书没有被借出的记录，无法归还");
        }
        book.setCount(book.getCount() + 1);
        book.setBorrowCount(book.getBorrowCount() - 1);
        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new EntityNotFoundException("Book not found with id:" + id);
        }
        bookRepository.deleteById(id);
    }

    public List<BookResponse> getAllBooks() {
        return mapper.toBookResponseList(bookRepository.findAll());
    }

    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id:" + id));
        return mapper.toBookResponse(book);
    }

    public List<BookResponse> getBooksByTitleLike(String title) {
        return mapper.toBookResponseList(bookRepository.findByTitleLike(title));
    }

    public List<BookResponse> getBooksByIsbn(String isbn) {
        List<Book> books = bookRepository.findByIsbn(isbn);
        if (books.isEmpty()) {
            throw new EntityNotFoundException("未找到ISBN为: " + isbn + " 的书籍");
        }
        return mapper.toBookResponseList(books);
    }

    public long getCopyCountByIsbn(String isbn) {
        return bookRepository.countByIsbn(isbn);
    }
}
