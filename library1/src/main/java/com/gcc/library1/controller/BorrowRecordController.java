package com.gcc.library1.controller;

import com.gcc.library1.dto.BorrowRecordCreateRequest;
import com.gcc.library1.dto.BorrowRecordResponse;
import com.gcc.library1.service.BookService;
import com.gcc.library1.service.BorrowHistoryService;
import com.gcc.library1.service.BorrowRecordService;
import com.gcc.library1.util.SecurityUtils;
import jakarta.validation.Valid;
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
    private final BorrowHistoryService borrowHistoryService;

    /**
     * 借书：
     * - 管理员：可指定 userId 帮任意用户借书
     * - 普通用户：忽略请求中的 userId，强制使用自己的 ID
     */
    @PostMapping("/add")
    public ResponseEntity<?> registerBorrow(@RequestBody @Valid BorrowRecordCreateRequest request) {
        Long bookId = request.getBookId();
        Long userId = resolveUserId(request.getUserId());

        if (bookId == null || userId == null) {
            return new ResponseEntity<>("bookId或者userId参数缺失", HttpStatus.BAD_REQUEST);
        }

        // 同一用户不能重复借同一本书
        if (borrowService.hasBorrowedThisBook(bookId, userId)) {
            return new ResponseEntity<>(bookId + "，借书失败，你已借过该书", HttpStatus.CONFLICT);
        }

        LocalDate borrowDate = LocalDate.now();
        LocalDate returnDate = borrowDate.plusDays(90);

        borrowService.addBorrow(bookId, userId, borrowDate, returnDate);
        bookService.borrowBook(bookId);
        borrowHistoryService.addBorrowHistory(bookId, userId, "借书90天");
        return new ResponseEntity<>(userId + "," + bookId + "," + "已借成功", HttpStatus.OK);
    }

    /**
     * 续借：
     * - 管理员：可指定 userId 帮任意用户续借
     * - 普通用户：只能续借自己借的书
     */
    @PostMapping("/updateBorrow")
    public ResponseEntity<?> updateBorrow(@RequestBody @Valid BorrowRecordCreateRequest request) {
        Long bookId = request.getBookId();
        Long userId = resolveUserId(request.getUserId());

        if (bookId == null || userId == null) {
            return new ResponseEntity<>("bookId或者userId参数缺失", HttpStatus.BAD_REQUEST);
        }

        var updated = borrowService.updateBorrow(bookId, userId,
                LocalDate.now().plusDays(90));
        borrowHistoryService.addBorrowHistory(bookId, userId, "续借90天");
        return ResponseEntity.ok(bookId + "归还日期已更改为" + updated.getReturnDate());
    }

    /**
     * 还书：
     * - 管理员：可指定 userId 帮任意用户还书
     * - 普通用户：只能还自己借的书
     */
    @DeleteMapping("/back")
    public ResponseEntity<?> backBook(@RequestBody @Valid BorrowRecordCreateRequest request) {
        Long bookId = request.getBookId();
        Long userId = resolveUserId(request.getUserId());
        if (bookId == null || userId == null) {
            return new ResponseEntity<>("书籍ID或用户ID不能为空", HttpStatus.BAD_REQUEST);
        }

        borrowService.deleteBorrow(bookId, userId);
        bookService.returnBook(bookId);
        borrowHistoryService.addBorrowHistory(bookId, userId, "还书");
        return ResponseEntity.ok(bookId + "已还成功");
    }

    /**
     * 查询借阅记录：
     * - 管理员：可查询任意用户的借阅记录
     * - 普通用户：只能查自己的
     */
    @GetMapping("/user")
    public ResponseEntity<List<BorrowRecordResponse>> getBorrowRecordsByUserId(
            @RequestParam(required = false) Long userId) {
        Long resolvedUserId = resolveUserId(userId);
        return ResponseEntity.ok(borrowService.getBorrowBooksByUserId(resolvedUserId));
    }

    /**
     * 查询逾期记录：
     * - 管理员：可查询任意用户的
     * - 普通用户：只能查自己的
     */
    @GetMapping("/overdue")
    public ResponseEntity<List<BorrowRecordResponse>> getOverdueBorrowRecordsByUserId(
            @RequestParam(required = false) Long userId) {
        Long resolvedUserId = resolveUserId(userId);
        return ResponseEntity.ok(borrowService.getOverdueBorrowRecordsByUserId(resolvedUserId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public List<BorrowRecordResponse> getAllBorrowRecords(
            @RequestParam(required = false) Long userId) {
        return borrowService.searchBorrowRecords(userId);
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
