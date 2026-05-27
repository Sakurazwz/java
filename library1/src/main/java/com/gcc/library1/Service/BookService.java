package com.gcc.library1.service;

import com.gcc.library1.model.Book;
import com.gcc.library1.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

    public Book updateBook(Long id, Book inputBook) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id:" + id));
        book.setIsbn(inputBook.getIsbn());
        book.setTitle(inputBook.getTitle());
        book.setAuthor(inputBook.getAuthor());
        book.setDescription(inputBook.getDescription());
        book.setCover(inputBook.getCover());
        book.setCount(inputBook.getCount());
        return bookRepository.save(book);
    }

    /**
     * 借书：count -1，borrowCount +1
     */
    public Book borrowBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id:" + bookId));
        if (book.getCount() == null || book.getCount() <= 0) {
            throw new IllegalStateException("该书籍库存不足，无法借阅");
        }
        book.setCount(book.getCount() - 1);
        book.setBorrowCount((book.getBorrowCount() == null ? 0 : book.getBorrowCount()) + 1);
        return bookRepository.save(book);
    }

    /**
     * 还书：count +1，borrowCount -1
     */
    public Book returnBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id:" + bookId));
        int currentBorrowCount = book.getBorrowCount() == null ? 0 : book.getBorrowCount();
        if (currentBorrowCount <= 0) {
            throw new IllegalStateException("该书没有被借出的记录，无法归还");
        }
        book.setCount((book.getCount() == null ? 0 : book.getCount()) + 1);
        book.setBorrowCount(currentBorrowCount - 1);
        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new EntityNotFoundException("Book not found with id:" + id);
        }
        bookRepository.deleteById(id);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id:" + id));
    }

    public List<Book> getBooksByTitleLike(String title) {
        List<Book> books = bookRepository.findByTitleLike(title);
        if (books.isEmpty()) {
            throw new EntityNotFoundException("未找到书名为: " + title + " 的书籍");
        }
        return books;
    }

    public List<Book> getBooksByIsbn(String isbn) {
        List<Book> books = bookRepository.findByIsbn(isbn);
        if (books.isEmpty()) {
            throw new EntityNotFoundException("未找到ISBN为: " + isbn + " 的书籍");
        }
        return books;
    }

    public long getCopyCountByIsbn(String isbn) {
        return bookRepository.countByIsbn(isbn);
    }
}
