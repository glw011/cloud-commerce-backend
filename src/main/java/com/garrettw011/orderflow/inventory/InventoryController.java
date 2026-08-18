package com.garrettw011.orderflow.inventory;

import com.garrettw011.orderflow.common.PageResponse;
import com.garrettw011.orderflow.inventory.dto.InventoryAdjustmentRequest;
import com.garrettw011.orderflow.inventory.dto.InventoryResponse;
import com.garrettw011.orderflow.inventory.dto.LowStockInventoryResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory")
@PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
public class InventoryController {
    private final InventoryService service;

    public InventoryController(InventoryService service) {
        this.service = service;
    }

    @GetMapping
    public PageResponse<InventoryResponse> list(Pageable pageable) {
        return PageResponse.from(service.getAll(pageable));
    }

    @GetMapping("/{productId}")
    public InventoryResponse getByProduct(@PathVariable Long productId) {
        return service.getByProductId(productId);
    }

    @PatchMapping("/{productId}/adjust")
    public InventoryResponse adjust(@PathVariable Long productId,
                                    @Valid @RequestBody InventoryAdjustmentRequest req) {
        return service.adjust(productId, req.delta(), req.reason());
    }

    @GetMapping("/low-stock")
    public List<LowStockInventoryResponse> lowStock() { return service.lowStock(); }
}

