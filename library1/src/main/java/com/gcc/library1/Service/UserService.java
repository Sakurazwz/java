package com.gcc.library1.service;

import com.gcc.library1.dto.UserRegisterRequest;
import com.gcc.library1.dto.UserResponse;
import com.gcc.library1.dto.mapper.EntityMapper;
import com.gcc.library1.model.User;
import com.gcc.library1.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityMapper mapper;

    public UserResponse addUser(UserRegisterRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        return mapper.toUserResponse(userRepository.save(user));
    }

    public User updateUser(Long id, User user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found " + id));
        existingUser.setName(user.getName());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found " + id));
        userRepository.deleteById(id);
    }

    public UserResponse updateRole(Long id, String newRole) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found " + id));
        user.setRole(newRole);
        return mapper.toUserResponse(userRepository.save(user));
    }

    public List<UserResponse> getAllUsers() {
        return mapper.toUserResponseList(userRepository.findAll());
    }

    public List<UserResponse> searchUsersByName(String name) {
        return mapper.toUserResponseList(userRepository.findByNameContainingIgnoreCase(name));
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found " + id));
        return mapper.toUserResponse(user);
    }

    public UserResponse getUserByName(String name) {
        User user = userRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("User not found " + name));
        return mapper.toUserResponse(user);
    }

    /**
     * 验证用户名和密码（使用 BCrypt 解密比较）
     */
    public User authenticate(String name, String password) {
        User user = userRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("用户名或密码错误"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new EntityNotFoundException("用户名或密码错误");
        }
        return user;
    }
}
