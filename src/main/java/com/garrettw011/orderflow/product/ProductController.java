package com.garrettw011.orderflow.product;

import com.garrettw011.orderflow.common.ApiDocs;
import com.garrettw011.orderflow.common.ApiError;
import com.garrettw011.orderflow.common.PageResponse;
import com.garrettw011.orderflow.product.dto.*;
import com.garrettw011.orderflow.common.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Objects;

@Tag(name = "Products", description = "View, create, & update products")
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

    @Operation(summary = "Get products lists", description = "Returns paginated, filtered list of products. " +
            "Non-admin callers see only active products. Admins see all products.")
    @ApiResponses({
            @ApiResponse(responseCode = "400",
                    description = "Invalid query parameter",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping
    public PageResponse<ProductResponse> list(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String sku,
            @ParameterObject Pageable pageable) {
        Boolean effectiveActive = isPrivileged() ? active : Boolean.TRUE;
        var params = new ProductSearchParams(effectiveActive, minPrice,maxPrice, name, sku);
        return service.search(params, pageable);
    }

    @Operation(summary = "Get product using product id", description = "Returns the product details for a product")
    @ApiResponses({
            @ApiResponse(responseCode = "404",
                    description = "Product not found or is unavailable (non-admin caller)",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/{id}")
    public ProductResponse getById(@PathVariable Long id) {
        ProductResponse p = service.getById(id);
        if (!p.active() && !isPrivileged()) { throw new ResourceNotFoundException("No product found with id: " + id); }
        return p;
    }

    @Operation(summary = "Get product using product sku", description = "Returns product details for a product")
    @ApiResponses({
            @ApiResponse(responseCode = "404",
                    description = "Product not found or is unavailable (non-admin caller)",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/sku/{sku}")
    public ProductResponse getBySku(@PathVariable String sku) {
        ProductResponse p = service.getBySku(sku);
        if (!p.active() && !isPrivileged()) { throw new ResourceNotFoundException("No product found with SKU: " + sku); }
        return p;
    }

    @Operation(summary = "Add new product", description = "Create new product in catalog with no stock")
    @ApiResponses({
            @ApiResponse(responseCode = "400",
                    description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403",
                    description = "Non-admin caller",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409",
                    description = "Duplicate sku",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @SecurityRequirement(name = ApiDocs.BEARER_SCHEME)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse create(@Valid @RequestBody ProductCreateRequest req) { return service.create(req); }

    @Operation(summary = "Update product", description = "Updates product details with new data")
    @ApiResponses({
            @ApiResponse(responseCode = "400",
                    description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403",
                    description = "Non-admin caller",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404",
                    description = "Product not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @SecurityRequirement(name = ApiDocs.BEARER_SCHEME)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse update(@PathVariable Long id, @Valid @RequestBody ProductUpdateRequest req) {
        return service.update(id, req);
    }

    @Operation(summary = "Deactivate product", description = "Removes product from public product list. " +
                    "Still viewable by admin.")
    @ApiResponses({
            @ApiResponse(responseCode = "403",
                    description = "Non-admin caller",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404",
                    description = "Product not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @SecurityRequirement(name = ApiDocs.BEARER_SCHEME)
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse deactivate(@PathVariable Long id) { return service.deactivate(id); }

    @Operation(summary = "Delete product", description = "Deletes a product from catalog if it has no order history.")
    @ApiResponses({
            @ApiResponse(responseCode = "403",
                    description = "Non-admin caller",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404",
                    description = "Product not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409",
                    description = "Product has order history, deactivate instead",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @SecurityRequirement(name = ApiDocs.BEARER_SCHEME)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) { service.delete(id); }
}

