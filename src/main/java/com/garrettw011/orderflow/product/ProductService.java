package com.garrettw011.orderflow.product;

import com.garrettw011.orderflow.common.exception.DuplicateResourceException;
import com.garrettw011.orderflow.common.exception.InvalidStateTransitionException;
import com.garrettw011.orderflow.common.exception.ResourceNotFoundException;
import com.garrettw011.orderflow.inventory.InventoryRepository;
import com.garrettw011.orderflow.order.OrderItemRepository;
import com.garrettw011.orderflow.product.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {
    private final ProductRepository products;
    private final InventoryRepository inventory;
    private final OrderItemRepository orderItems;

    private ProductResponse toResponse(Product p) {
        return new ProductResponse(
                p.getId(),
                p.getSku(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.isActive(),
                p.getCreatedAt(),
                p.getUpdatedAt());
    }

    private Product getEntity(Long id) {
        return products.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No product found with ID: " + id));
    }

    private String normalizeSku(String sku) { return sku == null ? null : sku.trim().toUpperCase(); }

    public ProductService(ProductRepository products, InventoryRepository inventory, OrderItemRepository orderItems) {
        this.products = products;
        this.inventory = inventory;
        this.orderItems = orderItems;
    }

    @Transactional
    public ProductResponse create(ProductCreateRequest req) {
        String sku = normalizeSku(req.sku());

        if (products.existsBySku(sku)) {
            throw new DuplicateResourceException("Product already exists with SKU: " + sku);
        }

        Product p = new Product();
        p.setSku(sku);
        p.setName(req.name());
        p.setDescription(req.description());
        p.setPrice(req.price());
        p.setActive(req.active() == null || req.active());

        return toResponse(products.save(p));
    }

    @Transactional
    public ProductResponse update(Long id, ProductUpdateRequest req) {
        Product p = getEntity(id);
        p.setName(req.name());
        p.setDescription(req.description());
        p.setPrice(req.price());
        p.setActive(req.active());

        return toResponse(products.save(p));
    }

    @Transactional
    public ProductResponse deactivate(Long id) {
        Product p = getEntity(id);
        p.setActive(false);
        return toResponse(products.save(p));
    }

    @Transactional
    public void delete(Long id) {
        if (orderItems.existsByProductId(id)) {
            throw new InvalidStateTransitionException(
                    "Products in order history cannot be deleted, deactivate instead.");
        }

        Product p = getEntity(id);
        inventory.findByProductId(id).ifPresent(inventory::delete);
        products.delete(p);
    }

    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        return toResponse(getEntity(id));
    }

    @Transactional(readOnly = true)
    public ProductResponse getBySku(String sku) {
        return products.findBySku(normalizeSku(sku))
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("No product found with SKU: " + sku));
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> search(ProductSearchParams params, Pageable pageable) {
        return products.findAll(ProductSpecifications.withFilters(params), pageable)
                .map(this::toResponse);
    }


}











