package com.garrettw011.orderflow.common;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *  Helpers used for money calculations so totals always round same way
 */

public final class MoneyUtils {
    public static final int DECIMAL_DIGITS = 2;
    public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    private MoneyUtils() {}

    public static BigDecimal normalize(BigDecimal value) { return value.setScale(DECIMAL_DIGITS, ROUNDING_MODE); }

    public static BigDecimal lineTotal(BigDecimal unitPrice, int quantity) {
        return normalize(unitPrice.multiply(BigDecimal.valueOf(quantity)));
    }

    public static BigDecimal taxOf(BigDecimal subtotal, BigDecimal rate) { return normalize(subtotal.multiply(rate)); }
}
