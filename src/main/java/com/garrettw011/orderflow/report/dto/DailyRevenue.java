package com.garrettw011.orderflow.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DailyRevenue(
        @Schema(description = "calendar day (UTC)", example = "2026-06-01")
        LocalDate date,

        @Schema(description = "captured revenue that day", example = "142.50")
        BigDecimal revenue
) {}

