package com.example.jpaadvanceddemo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@Entity
@Table(name = "sys_user")  // 使用 sys_user 避免 SQL 保留关键字冲突
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(of = "id")  // 只使用 id 字段生成 hashCode 和 equals
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 32, message = "用户名长度必须在3-32之间")
    @Column(unique = true, nullable = false, length = 32)
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 64, message = "密码长度必须在6-64之间")
    @Column(nullable = false, length = 64)
    private String password;

    @Email(message = "邮箱格式不正确")
    @Column(length = 64)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(columnDefinition = "TINYINT DEFAULT 1")
    private Integer status = 1;  // 0-禁用，1-正常

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updateTime;

    // 一对一关联：用户详情
    // mappedBy = "user" 表示关系由 UserProfile 的 user 属性维护
    @JsonIgnore  // 避免JSON序列化时触发懒加载问题
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserProfile userProfile;
}
