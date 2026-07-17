package com.uteq.pfc.dto;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Metadata de paginacion exigida por el criterio de verificacion del Paso 2:
 * current_page, total, last_page.
 */
public record PageResponse<T>(
        List<T> data,
        int currentPage,
        int pageSize,
        long total,
        int lastPage
) {
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages() == 0 ? 0 : page.getTotalPages() - 1
        );
    }
}
