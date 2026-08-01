package com.garrettw011.orderflow.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record TopProduct(
        @Schema(example = "3")
        Long productId,

        @Schema(example = "KM-KB-MECH-001")
        String sku,

        @Schema(example = "KeyMaster: Mechanical Keyboard")
        String name,

        @Schema(description = "units sold in window", example = "32")
        Long unitsSold,

        @Schema(description = "total revenue from product during window=", example = "2879.68")
        BigDecimal revenue
) {}

