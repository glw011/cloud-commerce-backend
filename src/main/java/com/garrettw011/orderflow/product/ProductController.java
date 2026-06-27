package com.garrettw011.orderflow.product;

import com.garrettw011.orderflow.common.PageResponse;
import com.garrettw011.orderflow.product.dto.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService service;

    public ProductController(ProductService service) { this.service = service; }

    @GetMapping
    public PageResponse<ProductResponse> list(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String sku,
            Pageable pageable) {

        var params = new ProductSearchParams(active, minPrice,maxPrice, name, sku);
        return PageResponse.from(service.search(params, pageable));
    }

    @GetMapping("/{id}")
    public ProductResponse getById(@PathVariable Long id) { return service.getById(id); }

    @GetMapping("/sku/{sku}")
    public ProductResponse getBySku(@PathVariable String sku) { return service.getBySku(sku); }

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

