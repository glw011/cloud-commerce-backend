package com.garrettw011.orderflow.payment;

import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.math.BigDecimal;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByOrderId(Long orderId);

    @Query("""
        SELECT SUM(p.amount) FROM Payment p
        WHERE p.status = :status AND p.createdAt >= :from AND p.createdAt < :to""")
    @Nullable
    BigDecimal sumAmountByStatusBetween(@Param("status") PaymentStatus status,
                                        @Param("from") Instant from,
                                        @Param("to") Instant to);

    @Query("""
        SELECT COUNT(p) FROM Payment p
        WHERE p.status = :status AND p.createdAt >= :from AND p.createdAt < :to""")
    long countByStatusBetween(@Param("status") PaymentStatus status,
                              @Param("from") Instant from,
                              @Param("to") Instant to);

    @Query(value = """
        SELECT (date_trunc('day', created_at AT TIME ZONE 'UTC'))
                ::date AS day, SUM(amount) as total FROM payments
        WHERE status = :status
                    AND created_at >= :from
                    AND created_at < :to
            GROUP BY (date_trunc('day', created_at AT TIME ZONE 'UTC'))::date
            ORDER BY day""",
    nativeQuery = true)
    List<DailyRevenueRow> dailyRevenueByStatusBetween(@Param("status") String status,
                                                      @Param("from") Instant from,
                                                      @Param("to") Instant to);
}
