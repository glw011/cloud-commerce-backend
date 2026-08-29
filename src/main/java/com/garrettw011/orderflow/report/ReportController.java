package com.garrettw011.orderflow.report;

import com.garrettw011.orderflow.common.ApiDocs;
import com.garrettw011.orderflow.common.ApiError;
import com.garrettw011.orderflow.report.dto.TopProduct;
import com.garrettw011.orderflow.report.dto.RevenueReport;
import com.garrettw011.orderflow.report.dto.OrderStatusCount;
import com.garrettw011.orderflow.inventory.dto.LowStockInventoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.jspecify.annotations.Nullable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.time.LocalDate;

@Tag(name = "Reports")
@RestController
@SecurityRequirement(name = ApiDocs.BEARER_SCHEME)
@RequestMapping("/api/v1/admin/reports")
@PreAuthorize("hasRole('ADMIN')")
public class ReportController {
    private final ReportService reports;

    public ReportController(ReportService reports) {
        this.reports = reports;
    }

    @Operation(summary = "Revenue report", description = "Recorded revenue over a date range with daily breakdown." +
            " Defaults to previous 30 days.")
    @ApiResponses({
            @ApiResponse(responseCode = "400",
                    description = "Invalid date range",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403",
                    description = "Non-admin caller",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
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

    @Operation(summary = "Best selling products", description = "Products ranked by units sold over date range.")
    @ApiResponses({
            @ApiResponse(responseCode = "400",
                    description = "Invalid date range",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403",
                    description = "Non-admin caller",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
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

    @Operation(summary = "Order counts by status")
    @ApiResponses({
            @ApiResponse(responseCode = "403",
                    description = "Non-admin caller",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/order-status-counts")
    public List<OrderStatusCount> orderStatusCounts() { return reports.orderStatusCounts(); }

    @Operation(summary = "Low stock products")
    @ApiResponses({
            @ApiResponse(responseCode = "403",
                    description = "Non-admin caller",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/low-stock")
    public List<LowStockInventoryResponse> lowStock() { return reports.lowStock(); }
}
