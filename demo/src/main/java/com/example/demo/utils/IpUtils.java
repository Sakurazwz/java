package com.example.demo.utils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * IP 工具类
 */
public class IpUtils {

    private static final String UNKNOWN = "unknown";
    private static final String LOCALHOST_IP = "127.0.0.1";
    private static final String LOCALHOST_IP_16 = "0:0:0:0:0:0:0:1";
    private static final int IP_LEN = 15;

    /**
     * 获取客户端 IP 地址
     */
    public static String getIpAddr(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }

        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 对于通过多个代理的情况，第一个 IP 为客户端真实 IP，多个 IP 按照','分割
        if (ip != null && ip.length() > IP_LEN) {
            int index = ip.indexOf(",");
            if (index > 0) {
                ip = ip.substring(0, index);
            }
        }

        // 将 IPv6 的 localhost 转换为 IPv4
        if (LOCALHOST_IP_16.equals(ip)) {
            ip = LOCALHOST_IP;
        }

        return ip;
    }
}
