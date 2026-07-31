package com.garrettw011.orderflow.report.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DailyRevenue(LocalDate date, BigDecimal revenue) {}

