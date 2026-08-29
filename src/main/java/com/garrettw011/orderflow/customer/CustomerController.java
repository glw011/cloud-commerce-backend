package com.garrettw011.orderflow.customer;

import com.garrettw011.orderflow.common.ApiError;
import com.garrettw011.orderflow.common.security.SecurityUtils;
import com.garrettw011.orderflow.customer.dto.CustomerResponse;
import com.garrettw011.orderflow.customer.dto.CustomerUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Customers", description = "Customer profile")
@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {
    private final CustomerService service;

    public CustomerController(CustomerService service) { this.service = service; }

    @Operation(summary = "Current customer profile")
    @ApiResponses({
            @ApiResponse(responseCode = "404",
                    description = "No customer profile exists",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/me")
    public CustomerResponse getMyProfile() { return service.getMyProfile(SecurityUtils.currentUserId()); }

    @Operation(summary = "Update current customer profile")
    @ApiResponses({
            @ApiResponse(responseCode = "400",
                    description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404",
                    description = "No customer profile exists",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/me")
    public CustomerResponse updateMyProfile(@Valid @RequestBody CustomerUpdateRequest req) {
        return service.updateMyProfile(SecurityUtils.currentUserId(), req);
    }
}

