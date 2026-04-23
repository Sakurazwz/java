package com.example.demo5work.dto;

import com.example.demo5work.entity.Author;
import com.example.demo5work.entity.Book;
import com.example.demo5work.entity.BorrowRecord;
import com.example.demo5work.entity.Category;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DTOMapper {

    public static BookDTO toBookDTO(Book book) {
        if (book == null) return null;
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setIsbn(book.getIsbn());
        dto.setPublishDate(book.getPublishDate());
        dto.setPrice(book.getPrice());
        return dto;
    }

    public static List<BookDTO> toBookDTOList(List<Book> books) {
        return books.stream()
                .map(DTOMapper::toBookDTO)
                .collect(Collectors.toList());
    }

    public static AuthorDTO toAuthorDTO(Author author) {
        if (author == null) return null;
        AuthorDTO dto = new AuthorDTO();
        dto.setId(author.getId());
        dto.setName(author.getName());
        dto.setNationality(author.getNationality());
        return dto;
    }

    public static List<AuthorDTO> toAuthorDTOList(Set<Author> authors) {
        return authors.stream()
                .map(DTOMapper::toAuthorDTO)
                .collect(Collectors.toList());
    }

    public static BorrowRecordDTO toBorrowRecordDTO(BorrowRecord record) {
        if (record == null) return null;
        BorrowRecordDTO dto = new BorrowRecordDTO();
        dto.setId(record.getId());
        dto.setBookTitle(record.getBook().getTitle());
        dto.setBorrower(record.getBorrower());
        dto.setBorrowDate(record.getBorrowDate());
        dto.setReturnDate(record.getReturnDate());
        return dto;
    }

    public static List<BorrowRecordDTO> toBorrowRecordDTOList(List<BorrowRecord> records) {
        return records.stream()
                .map(DTOMapper::toBorrowRecordDTO)
                .collect(Collectors.toList());
    }

    public static BorrowRecordDetailDTO toBorrowRecordDetailDTO(BorrowRecord record) {
        if (record == null) return null;
        BorrowRecordDetailDTO dto = new BorrowRecordDetailDTO();
        dto.setId(record.getId());

        Book book = record.getBook();
        if (book != null) {
            dto.setBookTitle(book.getTitle());
            dto.setBookIsbn(book.getIsbn());

            if (book.getCategory() != null) {
                dto.setCategoryName(book.getCategory().getName());
            }

            if (book.getAuthors() != null) {
                dto.setAuthors(toAuthorDTOList(book.getAuthors()));
            }
        }

        dto.setBorrower(record.getBorrower());
        dto.setBorrowDate(record.getBorrowDate());
        dto.setReturnDate(record.getReturnDate());
        return dto;
    }

    public static List<BorrowRecordDetailDTO> toBorrowRecordDetailDTOList(List<BorrowRecord> records) {
        return records.stream()
                .map(DTOMapper::toBorrowRecordDetailDTO)
                .collect(Collectors.toList());
    }
}
