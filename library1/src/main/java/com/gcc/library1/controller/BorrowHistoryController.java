package com.gcc.library1.controller;

import com.gcc.library1.dto.BorrowHistoryDTO;
import com.gcc.library1.service.BorrowHistoryService;
import com.gcc.library1.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrowhistory")
@RequiredArgsConstructor
public class BorrowHistoryController {

    private final BorrowHistoryService borrowHistoryService;

    /**
     * 查询所有借阅历史（仅管理员）
     */
    @GetMapping("/all")
    public List<BorrowHistoryDTO> getAllBorrowHistory() {
        if (!SecurityUtils.isAdmin()) {
            throw new org.springframework.security.access.AccessDeniedException("仅管理员可查看全部借阅历史");
        }
        return borrowHistoryService.getAllBorrowHistory();
    }

    /**
     * 查询借阅历史（按 bookId）：
     * - 管理员：可查看任意书籍的借阅历史
     * - 普通用户：禁止（可通过 userId 查自己的）
     */
    @GetMapping("/getBorrowHistoryByBookId/{bookId}")
    public List<BorrowHistoryDTO> getBorrowHistoryByBookId(@PathVariable Long bookId) {
        if (!SecurityUtils.isAdmin()) {
            throw new org.springframework.security.access.AccessDeniedException("仅管理员可查看书籍借阅历史");
        }
        return borrowHistoryService.getBorrowHistoryByBookId(bookId);
    }

    /**
     * 查询借阅历史（按 userId）：
     * - 管理员：可查看任意用户的借阅历史
     * - 普通用户：只能查看自己的
     */
    @GetMapping("/getBorrowHistoryByUserId/{userId}")
    public List<BorrowHistoryDTO> getBorrowHistoryByUserId(@PathVariable Long userId) {
        // 普通用户只能查看自己的历史
        if (!SecurityUtils.isAdmin() && !SecurityUtils.getCurrentUserId().equals(userId)) {
            throw new org.springframework.security.access.AccessDeniedException("只能查看自己的借阅历史");
        }
        return borrowHistoryService.getBorrowHistoryByUserId(userId);
    }
}
