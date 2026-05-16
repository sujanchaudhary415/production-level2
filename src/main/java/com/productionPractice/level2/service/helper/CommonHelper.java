package com.productionPractice.level2.service.helper;

import org.springframework.stereotype.Component;

@Component
public class CommonHelper {

    public String normalize(String value) {
        return value == null ? null : value.trim().replaceAll("\\s+", " ");
    }

    public String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    public boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }
}