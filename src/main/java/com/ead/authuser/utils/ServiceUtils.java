package com.ead.authuser.utils;

import org.springframework.data.domain.Pageable;

import java.util.UUID;

public class ServiceUtils {
    private ServiceUtils() {
    }

    public static String createUrlGetAllCoursesByUser(UUID userId, Pageable pageable) {
        return "/courses?userId=" + userId + "&page=" + pageable.getPageNumber() + "&size="
            + pageable.getPageSize() + "&sort=" + pageable.getSort().toString().replaceAll(": ", ",");
    }
}
