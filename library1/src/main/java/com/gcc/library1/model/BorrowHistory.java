package com.gcc.library1.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "borrowHistory")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long bookId;

    private LocalDate date;

    // 注意：数据库实际列名为 "behavour"（拼写错误）
    // 如果未来重建数据库，应改为正确的 "behavior"
    @Column(name = "behavour", nullable = false)
    private String behaviour;

    // 兼容原有调用方的 getter/setter
    public String getBehavior() {
        return behaviour;
    }

    public void setBehavior(String behavior) {
        this.behaviour = behavior;
    }
}