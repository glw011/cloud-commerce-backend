package com.garrettw011.orderflow.payment;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface DailyRevenueRow {
    LocalDate getDay();
    BigDecimal getTotal();
}

