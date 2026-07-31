package com.garrettw011.orderflow.report;

import com.garrettw011.orderflow.common.MoneyUtils;
import com.garrettw011.orderflow.inventory.InventoryService;
import com.garrettw011.orderflow.inventory.dto.LowStockInventoryResponse;
import com.garrettw011.orderflow.order.OrderStatus;
import com.garrettw011.orderflow.order.OrderRepository;
import com.garrettw011.orderflow.order.OrderItemRepository;
import com.garrettw011.orderflow.payment.PaymentRepository;
import com.garrettw011.orderflow.payment.PaymentStatus;
import com.garrettw011.orderflow.report.dto.DailyRevenue;
import com.garrettw011.orderflow.report.dto.RevenueReport;
import com.garrettw011.orderflow.report.dto.TopProduct;
import com.garrettw011.orderflow.report.dto.OrderStatusCount;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.Instant;
import java.math.BigDecimal;
import java.util.List;

@Service
public class ReportService {
    private static final List<OrderStatus> PAID_STATUSES
            = List.of(OrderStatus.PAID, OrderStatus.FULFILLING, OrderStatus.SHIPPED);
    private static final int MAX_TOP_PRODUCTS = 100;

    private final PaymentRepository payments;
    private final OrderItemRepository orderItems;
    private final OrderRepository orders;
    private final InventoryService inventoryService;

    public ReportService(PaymentRepository payments, OrderItemRepository orderItems,
                         OrderRepository orders, InventoryService inventoryService) {
        this.payments = payments;
        this.orderItems = orderItems;
        this.orders = orders;
        this.inventoryService = inventoryService;
    }

    @Transactional(readOnly=true)
    public RevenueReport revenue(LocalDate from, LocalDate to) {
        Range range = Range.resolve(from, to);

        BigDecimal total = payments.sumAmountByStatusBetween(
                PaymentStatus.CAPTURED, range.fromInstant(), range.toInstant());
        total = MoneyUtils.normalize(total != null ? total : BigDecimal.ZERO);

        long count = payments.countByStatusBetween(
                PaymentStatus.CAPTURED, range.fromInstant(), range.toInstant());

        List<DailyRevenue> daily = payments.dailyRevenueByStatusBetween(
                        PaymentStatus.CAPTURED.name(), range.fromInstant(), range.toInstant())
                .stream()
                .map(r -> new DailyRevenue(r.getDay(), MoneyUtils.normalize(r.getTotal())))
                .toList();

        return new RevenueReport(range.from(), range.to(), total, count, daily);
    }

    @Transactional(readOnly=true)
    public List<TopProduct> topProducts(LocalDate from, LocalDate to, int limit) {
        Range range = Range.resolve(from, to);
        int cap = Math.clamp(limit, 1, MAX_TOP_PRODUCTS);
        return orderItems.topProducts(PAID_STATUSES, range.fromInstant(), range.toInstant(),
                                      PageRequest.of(0, cap));
    }

    @Transactional(readOnly=true)
    public List<OrderStatusCount> orderStatusCounts() { return orders.statusCounts(); }

    @Transactional(readOnly=true)
    public List<LowStockInventoryResponse> lowStock() { return inventoryService.lowStock(); }

    // Reporting range window with bounds => [from{00:00}, (to + 1 day){00:00}) UTC
    private record Range(LocalDate from, LocalDate to, Instant fromInstant, Instant toInstant) {
        static Range resolve(LocalDate from, LocalDate to) {
            LocalDate resolvedTo = (to != null) ? to : LocalDate.now(ZoneOffset.UTC);
            LocalDate resolvedFrom = (from != null) ? from : resolvedTo.minusDays(30);
            if (resolvedFrom.isAfter(resolvedTo)) {
                throw new IllegalArgumentException("'from' must be on or before 'to'...");
            }
            return new Range(resolvedFrom, resolvedTo,
                             resolvedFrom.atStartOfDay(ZoneOffset.UTC).toInstant(),
                             resolvedTo.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant());
        }
    }
}
