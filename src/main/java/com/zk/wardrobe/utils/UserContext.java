package com.zk.wardrobe.utils;

/**
 * 用户上下文工具类 (基于 ThreadLocal)
 */
public class UserContext {

    // 保存当前线程的用户ID
    private static final ThreadLocal<Long> USER_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 存入用户ID
     */
    public static void setUserId(Long userId) {
        USER_THREAD_LOCAL.set(userId);
    }

    /**
     * 获取用户ID
     */
    public static Long getUserId() {
        return USER_THREAD_LOCAL.get();
    }

    /**
     * 清除当前线程的用户ID (防止内存泄漏，极其重要！)
     */
    public static void remove() {
        USER_THREAD_LOCAL.remove();
    }
}