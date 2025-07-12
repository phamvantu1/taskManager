package com.example.taskManager.filter;

import java.util.List;

public class SecurityConstants {
    public static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/api/auth/**"
    );
}