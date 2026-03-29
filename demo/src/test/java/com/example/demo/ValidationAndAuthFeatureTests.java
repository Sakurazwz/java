package com.example.demo;

import com.example.demo.dto.LoginDTO;
import com.example.demo.dto.UserRegisterDTO;
import com.example.demo.dto.UserUpdateDTO;
import com.example.demo.entity.User;
import com.example.demo.exception.BusinessException;
import com.example.demo.service.UserService;
import com.example.demo.validation.ValidationGroup;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ValidationAndAuthFeatureTests {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void shouldValidateIdCardOnRegisterDto() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setUsername("newuser");
        dto.setPassword("Password123");
        dto.setConfirmPassword("Password123");
        dto.setEmail("newuser@example.com");
        dto.setIdCard("123456789012345678");

        Set<ConstraintViolation<UserRegisterDTO>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> "身份证号格式不正确".equals(v.getMessage())));
    }

    @Test
    void shouldApplyCreateAndUpdateValidationGroups() {
        UserUpdateDTO createDto = new UserUpdateDTO();
        Set<ConstraintViolation<UserUpdateDTO>> createViolations =
                validator.validate(createDto, ValidationGroup.Create.class);
        assertTrue(createViolations.stream().anyMatch(v -> "用户名不能为空".equals(v.getMessage())));
        assertTrue(createViolations.stream().anyMatch(v -> "邮箱不能为空".equals(v.getMessage())));

        UserUpdateDTO updateDto = new UserUpdateDTO();
        updateDto.setId(1L);
        Set<ConstraintViolation<UserUpdateDTO>> updateViolations =
                validator.validate(updateDto, ValidationGroup.Update.class);
        assertTrue(updateViolations.isEmpty());

        UserUpdateDTO missingIdDto = new UserUpdateDTO();
        missingIdDto.setUsername("updateName");
        Set<ConstraintViolation<UserUpdateDTO>> missingIdViolations =
                validator.validate(missingIdDto, ValidationGroup.Update.class);
        assertTrue(missingIdViolations.stream().anyMatch(v -> "用户ID不能为空".equals(v.getMessage())));
    }

    @Test
    void shouldEncryptPasswordOnRegisterAndVerifyOnLogin() {
        UserService userService = new UserService();

        UserRegisterDTO registerDTO = new UserRegisterDTO();
        registerDTO.setUsername("register_user");
        registerDTO.setPassword("Password123");
        registerDTO.setConfirmPassword("Password123");
        registerDTO.setEmail("register_user@example.com");
        registerDTO.setPhone("13800138123");
        registerDTO.setIdCard("11010119900307987X");

        userService.register(registerDTO);

        User registeredUser = userService.list().stream()
                .filter(user -> "register_user".equals(user.getUsername()))
                .findFirst()
                .orElseThrow();

        assertNotEquals("Password123", registeredUser.getPassword());
        assertTrue(new BCryptPasswordEncoder().matches("Password123", registeredUser.getPassword()));

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("register_user");
        loginDTO.setPassword("Password123");
        assertDoesNotThrow(() -> userService.login(loginDTO));

        loginDTO.setPassword("WrongPass123");
        assertThrows(BusinessException.class, () -> userService.login(loginDTO));
    }
}

