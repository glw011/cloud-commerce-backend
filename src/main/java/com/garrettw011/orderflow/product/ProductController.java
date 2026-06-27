package com.garrettw011.orderflow.product;

import com.garrettw011.orderflow.common.PageResponse;
import com.garrettw011.orderflow.product.dto.*;
import com.garrettw011.orderflow.common.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService service;

    public ProductController(ProductService service) { this.service = service; }

    private boolean isPrivileged() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;

        return auth.getAuthorities().stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_ADMIN")
                           || Objects.equals(a.getAuthority(), "ROLE_WAREHOUSE_MANAGER"));
    }

    @GetMapping
    public PageResponse<ProductResponse> list(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String sku,
            Pageable pageable) {
        Boolean effectiveActive = isPrivileged() ? active : Boolean.TRUE;
        var params = new ProductSearchParams(active, minPrice,maxPrice, name, sku);
        return PageResponse.from(service.search(params, pageable));
    }

    @GetMapping("/{id}")
    public ProductResponse getById(@PathVariable Long id) {
        ProductResponse p = service.getById(id);
        if (!p.active() && !isPrivileged()) { throw new ResourceNotFoundException("No product found with id: " + id); }
        return p;
    }

    @GetMapping("/sku/{sku}")
    public ProductResponse getBySku(@PathVariable String sku) {
        ProductResponse p = service.getBySku(sku);
        if (!p.active() && !isPrivileged()) { throw new ResourceNotFoundException("No product found with SKU: " + sku); }
        return p;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse create(@Valid @RequestBody ProductCreateRequest req) { return service.create(req); }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse update(@PathVariable Long id, @Valid @RequestBody ProductUpdateRequest req) {
        return service.update(id, req);
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse deactivate(@PathVariable Long id) { return service.deactivate(id); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) { service.delete(id); }
}

