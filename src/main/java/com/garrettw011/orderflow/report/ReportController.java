package com.garrettw011.orderflow.report;

import com.garrettw011.orderflow.report.dto.TopProduct;
import com.garrettw011.orderflow.report.dto.RevenueReport;
import com.garrettw011.orderflow.report.dto.OrderStatusCount;
import com.garrettw011.orderflow.inventory.dto.LowStockInventoryResponse;
import org.jspecify.annotations.Nullable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/admin/reports")
@PreAuthorize("hasRole('ADMIN')")
public class ReportController {
    private final ReportService reports;

    public ReportController(ReportService reports) {
        this.reports = reports;
    }

    @GetMapping("/revenue")
    public RevenueReport revenue(
                    @RequestParam(required=false)
                    @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)
                    @Nullable LocalDate from,
                    @RequestParam(required=false)
                    @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)
                    @Nullable LocalDate to) {
        return reports.revenue(from, to);
    }

    @GetMapping("/top-products")
    public List<TopProduct> topProducts(
                    @RequestParam(required=false)
                    @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)
                    @Nullable LocalDate from,
                    @RequestParam(required=false)
                    @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)
                    @Nullable LocalDate to,
                    @RequestParam(defaultValue="10")
                    int limit) {
        return reports.topProducts(from, to, limit);
    }

    @GetMapping("/order-status-counts")
    public List<OrderStatusCount> orderStatusCounts() { return reports.orderStatusCounts(); }

    @GetMapping("/low-stock")
    public List<LowStockInventoryResponse> lowStock() { return reports.lowStock(); }
}
