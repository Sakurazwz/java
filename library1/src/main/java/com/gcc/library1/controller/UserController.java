package com.gcc.library1.controller;

import com.gcc.library1.dto.UserRegisterRequest;
import com.gcc.library1.dto.UserResponse;
import com.gcc.library1.service.BorrowRecordService;
import com.gcc.library1.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final BorrowRecordService borrowService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid UserRegisterRequest request) {
        try {
            userService.getUserByName(request.getName());
            return new ResponseEntity<>("用户已存在", HttpStatus.CONFLICT);
        } catch (EntityNotFoundException e) {
            UserResponse registeredUser = userService.addUser(request);
            return new ResponseEntity<>(registeredUser, HttpStatus.OK);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (borrowService.hasBorrowedBooks(id)) {
            return new ResponseEntity<>("该用户已借书，无法删除", HttpStatus.CONFLICT);
        }
        userService.deleteUser(id);
        return new ResponseEntity<>("用户删除成功", HttpStatus.OK);
    }
}
