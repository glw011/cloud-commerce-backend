package com.garrettw011.orderflow.customer;

import com.garrettw011.orderflow.common.security.SecurityUtils;
import com.garrettw011.orderflow.customer.dto.CustomerResponse;
import com.garrettw011.orderflow.customer.dto.CustomerUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {
    private final CustomerService service;

    public CustomerController(CustomerService service) { this.service = service; }

    @GetMapping("/me")
    public CustomerResponse getMyProfile() { return service.getMyProfile(SecurityUtils.currentUserId()); }

    @PutMapping("/me")
    public CustomerResponse updateMyProfile(@Valid @RequestBody CustomerUpdateRequest req) {
        return service.updateMyProfile(SecurityUtils.currentUserId(), req);
    }
}

