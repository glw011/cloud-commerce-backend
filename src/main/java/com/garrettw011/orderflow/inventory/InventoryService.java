package com.garrettw011.orderflow.inventory;

import com.garrettw011.orderflow.common.exception.InsufficientInventoryException;
import com.garrettw011.orderflow.common.exception.ResourceNotFoundException;
import com.garrettw011.orderflow.inventory.dto.InventoryResponse;
import com.garrettw011.orderflow.inventory.dto.LowStockInventoryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class InventoryService {
    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    private final InventoryRepository inventory;

    public InventoryService(InventoryRepository inventory) {
        this.inventory = inventory;
    }

    private InventoryItem getEntity(Long productId) {
        return inventory.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("No inventory exists for product id: " + productId));
    }

    private InventoryResponse toResponse(InventoryItem i) {
        return new InventoryResponse(
                i.getId(), i.getProduct().getId(), i.getProduct().getSku(), i.getQuantityOnHand(),
                i.getQuantityReserved(), i.getAvailableQuantity(), i.getReorderThreshold(), i.getUpdatedAt());
    }

    private LowStockInventoryResponse toLowStock(InventoryItem i) {
        return new LowStockInventoryResponse(
                i.getProduct().getId(), i.getProduct().getSku(), i.getProduct().getName(), i.getQuantityOnHand(),
                i.getAvailableQuantity(), i.getReorderThreshold());
    }

    @Transactional(readOnly = true)
    public Page<InventoryResponse> getAll(Pageable pageable) {
        return inventory.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public InventoryResponse getByProductId(Long productId) {
        return toResponse(getEntity(productId));
    }

    @Transactional
    public InventoryResponse adjust(Long productId, int delta, String reason) {
        InventoryItem item = getEntity(productId);
        int newOnHand = item.getQuantityOnHand() + delta;

        if (newOnHand < 0) {
            String msg = "Adjustment cannot be made: On-hand quantity (%d) would be negative after adjusting (%d)."
                    .formatted(item.getQuantityOnHand(), delta);
            throw new InsufficientInventoryException(msg);
        }

        if (newOnHand < item.getQuantityReserved()) {
            String msg = "Adjustment cannot be made: Reserved quantity (%d) would exceed on-hand quantity (%d)."
                    .formatted(item.getQuantityReserved(), item.getQuantityOnHand());
            throw new InsufficientInventoryException(msg);
        }

        item.setQuantityOnHand(newOnHand);
        InventoryItem saved = inventory.save(item);
        log.info("Inventory adjusted: Reason - {} (productId={}, delta={}, newOnHand={})",
                reason, productId, delta, newOnHand);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<LowStockInventoryResponse> lowStock() {
        return inventory.findLowStock().stream().map(this::toLowStock).toList();
    }
}
