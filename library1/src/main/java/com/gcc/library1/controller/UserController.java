package com.gcc.library1.controller;

import com.gcc.library1.model.User;
import com.gcc.library1.service.BorrowRecordService;
import com.gcc.library1.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final BorrowRecordService borrowService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            if (user == null) {
                return new ResponseEntity<>("用户信息不能为空", HttpStatus.BAD_REQUEST);
            }
            if (user.getName() == null || user.getName().trim().isEmpty()) {
                return new ResponseEntity<>("用户名不能为空", HttpStatus.BAD_REQUEST);
            }
            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                return new ResponseEntity<>("密码不能为空", HttpStatus.BAD_REQUEST);
            }

            try {
                userService.getUserByName(user.getName());
                return new ResponseEntity<>("用户已存在", HttpStatus.CONFLICT);
            } catch (EntityNotFoundException e) {
                // 用户不存在，可以继续注册（密码会在 Service 层加密）
                User registeredUser = userService.addUser(user);
                return new ResponseEntity<>(registeredUser, HttpStatus.OK);
            }
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("注册失败，请稍后重试", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestBody User user) {
        if (user == null || user.getId() == null) {
            return new ResponseEntity<>("用户信息不完整", HttpStatus.BAD_REQUEST);
        }

        Long id = user.getId();

        try {
            userService.getUserById(id);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("找不到用户", HttpStatus.NOT_FOUND);
        }

        boolean hasBorrowedBooks = borrowService.hasBorrowedBooks(id);
        if (hasBorrowedBooks) {
            return new ResponseEntity<>("该用户已借书，无法删除", HttpStatus.CONFLICT);
        }

        try {
            userService.deleteUser(id);
            return new ResponseEntity<>("用户删除成功", HttpStatus.OK);
        } catch (EntityNotFoundException userNotFound) {
            return new ResponseEntity<>("找不到用户", HttpStatus.NOT_FOUND);
        }
    }
}
