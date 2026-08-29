package com.garrettw011.orderflow.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record RevenueReport(
        @Schema(description = "inclusive start of reporting window", example = "2026-06-01")
        LocalDate from,

        @Schema(description = "inclusive end of reporting window", example = "2026-06-30")
        LocalDate to,

        @Schema(description = "captured revenue during window", example = "12345.87")
        BigDecimal totalRevenue,

        @Schema(description = "number of captured payments during window", example = "44")
        long orderCount,

        @Schema(description = "revenue breakdown by day")
        List<DailyRevenue> daily
) {}