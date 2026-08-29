package com.garrettw011.orderflow.common;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class MoneyUtilsTest {
    @Test
    void normalizedRoundingTwoPlacesHalfUp() {
        assertThat(MoneyUtils.normalize(new BigDecimal("1.005")))
                .isEqualByComparingTo("1.01");
        assertThat(MoneyUtils.normalize(new BigDecimal("1.004")))
                .isEqualByComparingTo("1.00");
        assertThat(MoneyUtils.normalize(new BigDecimal("10")))
                .isEqualByComparingTo("10.00");
    }

    @Test
    void normalizedLineTotalMultiply() {
        assertThat(MoneyUtils.lineTotal(new BigDecimal("9.99"), 3))
                .isEqualByComparingTo("29.97");
        assertThat(MoneyUtils.lineTotal(new BigDecimal("10.00"), 0))
                .isEqualByComparingTo("0.00");
    }

    @Test
    void taxAppliesAndRounds() {
        assertThat(MoneyUtils.taxOf(new BigDecimal("100.00"), new BigDecimal("0.08")))
                .isEqualByComparingTo("8.00");
        assertThat(MoneyUtils.taxOf(new BigDecimal("19.99"), new BigDecimal("0.08")))
                .isEqualByComparingTo("1.60");
    }
}