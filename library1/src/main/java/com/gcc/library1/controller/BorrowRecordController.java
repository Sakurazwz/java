package com.gcc.library1.controller;

import com.gcc.library1.model.BorrowRecord;
import com.gcc.library1.service.BookService;
import com.gcc.library1.service.BorrowHistoryService;
import com.gcc.library1.service.BorrowRecordService;
import com.gcc.library1.service.UserService;
import com.gcc.library1.util.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/borrow")
@RequiredArgsConstructor
public class BorrowRecordController {

    private final BorrowRecordService borrowService;
    private final BookService bookService;
    private final UserService userService;
    private final BorrowHistoryService borrowHistoryService;

    /**
     * 借书：
     * - 管理员：可指定 userId 帮任意用户借书
     * - 普通用户：忽略请求中的 userId，强制使用自己的 ID
     */
    @PostMapping("/add")
    public ResponseEntity<?> registerBorrow(@RequestBody BorrowRecord inputBorrowRecord) {
        Long bookId = inputBorrowRecord.getBookId();
        Long userId = resolveUserId(inputBorrowRecord.getUserId());

        if (bookId == null || userId == null) {
            return new ResponseEntity<>("bookId或者userId参数缺失", HttpStatus.BAD_REQUEST);
        }

        try {
            userService.getUserById(userId);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(userId + "，借书失败，用户不存在", HttpStatus.NOT_FOUND);
        }

        try {
            bookService.getBookById(bookId);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(bookId + "，已借失败，该书不存在", HttpStatus.NOT_FOUND);
        }

        // 同一用户不能重复借同一本书
        if (borrowService.hasBorrowedThisBook(bookId, userId)) {
            return new ResponseEntity<>(bookId + "，借书失败，你已借过该书", HttpStatus.CONFLICT);
        }

        try {
            LocalDate borrowDate = LocalDate.now();
            LocalDate returnDate = borrowDate.plusDays(90);

            borrowService.addBorrow(bookId, userId, borrowDate, returnDate);
            borrowHistoryService.addBorrowHistory(bookId, userId, "借书90天");
            return new ResponseEntity<>(userId + "," + bookId + "," + "已借成功", HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    /**
     * 续借：
     * - 管理员：可指定 userId 帮任意用户续借
     * - 普通用户：只能续借自己借的书
     */
    @PostMapping("/updateBorrow")
    public ResponseEntity<?> updateBorrow(@RequestBody BorrowRecord inputBorrowRecord) {
        Long bookId = inputBorrowRecord.getBookId();
        Long userId = resolveUserId(inputBorrowRecord.getUserId());

        if (bookId == null || userId == null) {
            return new ResponseEntity<>("bookId或者userId参数缺失", HttpStatus.BAD_REQUEST);
        }

        try {
            if (!borrowService.hasBorrowedThisBook(bookId, userId)) {
                return new ResponseEntity<>(bookId + "，续借失败，未找到该借阅记录", HttpStatus.NOT_FOUND);
            }

            BorrowRecord borrowRecord = borrowService.updateBorrow(bookId, userId,
                    borrowService.getBorrowedRecord(bookId, userId).getReturnDate().plusDays(90));
            borrowHistoryService.addBorrowHistory(bookId, userId, "续借90天");
            return new ResponseEntity<>(bookId + "归还日期已更改为" + borrowRecord.getReturnDate(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("系统异常：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 还书：
     * - 管理员：可指定 userId 帮任意用户还书
     * - 普通用户：只能还自己借的书
     */
    @DeleteMapping("/back")
    public ResponseEntity<?> backBook(@RequestBody BorrowRecord inputBorrowRecord) {
        if (inputBorrowRecord == null) {
            return new ResponseEntity<>("请求参数不能为空", HttpStatus.BAD_REQUEST);
        }

        Long bookId = inputBorrowRecord.getBookId();
        Long userId = resolveUserId(inputBorrowRecord.getUserId());
        if (bookId == null || userId == null) {
            return new ResponseEntity<>("书籍ID或用户ID不能为空", HttpStatus.BAD_REQUEST);
        }

        try {
            borrowService.deleteBorrow(bookId, userId);
            borrowHistoryService.addBorrowHistory(bookId, userId, "还书");
            return new ResponseEntity<>(bookId + "已还成功", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("还书失败，请稍后重试", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 查询借阅记录：
     * - 管理员：可查询任意用户的借阅记录
     * - 普通用户：只能查自己的
     */
    @PostMapping("/user")
    public ResponseEntity<?> getBorrowRecordsByUserId(@RequestBody BorrowRecord inputBorrowRecord) {
        Long userId = resolveUserId(inputBorrowRecord.getUserId());
        if (userId == null) {
            return new ResponseEntity<>("用户ID不能为空", HttpStatus.BAD_REQUEST);
        }

        try {
            userService.getUserById(userId);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("用户不存在", HttpStatus.NOT_FOUND);
        }

        try {
            List<BorrowRecord> borrowRecords = borrowService.getBorrowBooksByUserId(userId);
            return new ResponseEntity<>(borrowRecords, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("查询失败，请稍后重试", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 查询逾期记录：
     * - 管理员：可查询任意用户的
     * - 普通用户：只能查自己的
     */
    @PostMapping("/overdue")
    public ResponseEntity<?> getOverdueBorrowRecordsByUserId(@RequestBody BorrowRecord inputBorrowRecord) {
        Long userId = resolveUserId(inputBorrowRecord.getUserId());
        if (userId == null) {
            return new ResponseEntity<>("用户ID不能为空", HttpStatus.BAD_REQUEST);
        }

        try {
            userService.getUserById(userId);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("用户不存在", HttpStatus.NOT_FOUND);
        }

        try {
            List<BorrowRecord> overdueRecords = borrowService.getOverdueBorrowRecordsByUserId(userId);
            return new ResponseEntity<>(overdueRecords, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("查询失败，请稍后重试", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<?> getAllBorrowRecords() {
        try {
            List<BorrowRecord> allBorrowRecords = borrowService.getAllBorrowRecords();
            return new ResponseEntity<>(allBorrowRecords, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("查询失败，请稍后重试", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 解析 userId：
     * - 管理员：使用请求中传入的 userId（可用于帮其他用户操作）
     * - 普通用户：忽略请求中的 userId，强制使用自己的 ID
     */
    private Long resolveUserId(Long requestUserId) {
        if (SecurityUtils.isAdmin()) {
            return requestUserId;
        }
        // 普通用户只能用自己的 ID
        return SecurityUtils.getCurrentUserId();
    }
}
