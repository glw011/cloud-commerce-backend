package com.garrettw011.orderflow.payment;

import org.jspecify.annotations.Nullable;

public record PaymentResult(boolean success, @Nullable String reference) {}
