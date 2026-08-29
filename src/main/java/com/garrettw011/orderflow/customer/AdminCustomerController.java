package com.garrettw011.orderflow.customer;

import com.garrettw011.orderflow.common.ApiDocs;
import com.garrettw011.orderflow.common.ApiError;
import com.garrettw011.orderflow.common.PageResponse;
import com.garrettw011.orderflow.customer.dto.CustomerResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin")
@RestController
@SecurityRequirement(name = ApiDocs.BEARER_SCHEME)
@RequestMapping("/api/v1/admin/customers")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCustomerController {
    private final CustomerService service;

    public AdminCustomerController(CustomerService service) { this.service = service; }

    @Operation(summary = "List customers")
    @ApiResponses({
            @ApiResponse(responseCode = "403",
                    description = "Non-admin caller",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping
    public PageResponse<CustomerResponse> list(@ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(service.list(pageable));
    }

    @Operation(summary = "Get customer by id")
    @ApiResponses({
            @ApiResponse(responseCode = "403",
                    description = "Non-admin caller",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404",
                    description = "Customer not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/{id}")
    public CustomerResponse getById(@PathVariable Long id) { return service.getById(id); }
}