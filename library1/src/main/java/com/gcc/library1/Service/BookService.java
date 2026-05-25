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

    public List<Book> getBooksByTitle(String title) {
        List<Book> books = bookRepository.findByTitle(title);
        if (books.isEmpty()) {
            throw new EntityNotFoundException("未找到书名为: " + title + " 的书籍");
        }
        return books;
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
