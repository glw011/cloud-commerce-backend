package com.garrettw011.orderflow.report.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record RevenueReport(
        LocalDate from,
        LocalDate to,
        BigDecimal totalRevenue,
        long orderCount,
        List<DailyRevenue> daily
) {}