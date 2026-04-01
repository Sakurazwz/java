package com.example.demo.validation;

import jakarta.validation.groups.Default;

/**
 * 校验分组
 */
public interface ValidationGroup {

    /**
     * 新增分组
     */
    interface Create extends Default {
    }

    /**
     * 更新分组
     */
    interface Update extends Default {
    }

    /**
     * 删除分组
     */
    interface Delete {
    }
}
