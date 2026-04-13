package com.example.jpaadvanceddemo.service.impl;

import com.example.jpaadvanceddemo.dto.UserCreateRequestDTO;
import com.example.jpaadvanceddemo.dto.UserWithProfileDTO;
import com.example.jpaadvanceddemo.entity.User;
import com.example.jpaadvanceddemo.entity.UserProfile;
import com.example.jpaadvanceddemo.repository.UserProfileRepository;
import com.example.jpaadvanceddemo.repository.UserRepository;
import com.example.jpaadvanceddemo.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * 用户 Service 实现类
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    @Override
    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    @Override
    public User getUserByIdWithProfile(Long id) {
        return userRepository.findByIdWithProfile(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public User createUserWithDTO(UserCreateRequestDTO dto) {
        // 创建用户
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);

        // 先保存用户（需要先有用户ID）
        User savedUser = userRepository.save(user);

        // 如果有 profile 数据，创建并保存
        if (dto.getUserProfile() != null) {
            UserProfile profile = new UserProfile();
            profile.setRealName(dto.getUserProfile().getRealName());
            profile.setGender(dto.getUserProfile().getGender());
            profile.setBirthday(LocalDate.parse(dto.getUserProfile().getBirthday()));
            profile.setAddress(dto.getUserProfile().getAddress());
            profile.setUser(savedUser);

            userProfileRepository.save(profile);
        }

        // 重新查询以获取完整的关联数据
        return userRepository.findByIdWithProfile(savedUser.getId())
                .orElse(savedUser);
    }

    @Override
    public UserWithProfileDTO getUserByIdWithProfileDTO(Long id) {
        User user = userRepository.findByIdWithProfile(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 手动构建 DTO
        UserWithProfileDTO dto = new UserWithProfileDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setStatus(user.getStatus());
        dto.setCreateTime(user.getCreateTime().toString());
        dto.setUpdateTime(user.getUpdateTime().toString());
        dto.setUserProfile(user.getUserProfile());

        return dto;
    }
}
