package com.garrettw011.orderflow.order;

import com.garrettw011.orderflow.report.dto.TopProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Collection;
import java.time.Instant;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    boolean existsByProductId(Long productId);

    @Query("""
        SELECT new com.garrettw011.orderflow.report.dto.TopProduct(
                p.id, p.sku, p.name, SUM(oi.quantity), SUM(oi.lineTotal))
        FROM OrderItem oi 
                JOIN oi.product p
                JOIN oi.order o
        WHERE o.status IN :statuses
                AND o.createdAt >= :from
                AND o.createdAt < :to
            GROUP BY p.id, p.sku, p.name
            ORDER BY SUM(oi.quantity) DESC""")
    List<TopProduct> topProducts(@Param("statuses") Collection<OrderStatus> statuses,
                                 @Param("from") Instant from,
                                 @Param("to") Instant to,
                                 Pageable pageable);
}
