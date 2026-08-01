package com.garrettw011.orderflow.common;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;
import java.util.List;

public record PageResponse<T>(
        @Schema(description = "items on this page")
        List<T> content,

        @Schema(description = "zero based page index", example = "0")
        int page,

        @Schema(description = "items per page", example = "10")
        int size,

        @Schema(description = "total items across all pages", example = "123")
        long totalElements,

        @Schema(description = "total number of pages", example = "7")
        int totalPages,

        @Schema(description = "bool indicating first item ", example = "true")
        boolean first,

        @Schema(description = "bool indicating last item", example = "false")
        boolean last
) {
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(),
                page.getTotalPages(), page.isFirst(), page.isLast());
    }
}

