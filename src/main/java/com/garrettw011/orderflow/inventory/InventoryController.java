package com.garrettw011.orderflow.inventory;

import com.garrettw011.orderflow.common.ApiDocs;
import com.garrettw011.orderflow.common.ApiError;
import com.garrettw011.orderflow.common.PageResponse;
import com.garrettw011.orderflow.inventory.dto.InventoryAdjustmentRequest;
import com.garrettw011.orderflow.inventory.dto.InventoryResponse;
import com.garrettw011.orderflow.inventory.dto.LowStockInventoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;

@Tag(name = "Inventory", description = "Inventory of products")
@RestController
@SecurityRequirement(name = ApiDocs.BEARER_SCHEME)
@RequestMapping("/api/v1/inventory")
@PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
public class InventoryController {
    private final InventoryService service;

    public InventoryController(InventoryService service) {
        this.service = service;
    }

    @Operation(summary = "List inventory")
    @ApiResponses({
            @ApiResponse(responseCode = "403",
                    description = "Caller is not admin or manager",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping
    public PageResponse<InventoryResponse> list(@ParameterObject Pageable pageable) {
        return PageResponse.from(service.getAll(pageable));
    }

    @Operation(summary = "Get inventory for product")
    @ApiResponses({
            @ApiResponse(responseCode = "403",
                    description = "Caller is not admin or manager",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404",
                    description = "No inventory for product",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/{productId}")
    public InventoryResponse getByProduct(@PathVariable Long productId) {
        return service.getByProductId(productId);
    }

    @Operation(summary = "Adjust stock", description = "Applies a signed delta to adjust inventory quantity.")
    @ApiResponses({
            @ApiResponse(responseCode = "400",
                    description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403",
                    description = "Caller is not admin or manager",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404",
                    description = "No inventory for product",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409",
                    description = "Adjustment would make inventory quantity negative, or drop below reserved",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PatchMapping("/{productId}/adjust")
    public InventoryResponse adjust(@PathVariable Long productId,
                                    @Valid @RequestBody InventoryAdjustmentRequest req) {
        return service.adjust(productId, req.delta(), req.reason());
    }

    @Operation(summary = "List low-stock items")
    @ApiResponses({
            @ApiResponse(responseCode = "403",
                    description = "Caller is not admin or manager",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/low-stock")
    public List<LowStockInventoryResponse> lowStock() { return service.lowStock(); }
}

