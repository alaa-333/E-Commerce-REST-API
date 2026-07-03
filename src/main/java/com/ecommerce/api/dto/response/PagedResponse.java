package com.ecommerce.api.dto.response;

import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;
@Builder
public record PagedResponse<T>(
        List<T> data,
        int pageNumber,
        int pageSize,
        long totalElement,
        int totalPages,
        boolean isFirst,
        boolean isLast
) {

    public  static <T> PagedResponse<T> from(Page<T> page) {
        return new PagedResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}
