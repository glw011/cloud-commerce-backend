package com.garrettw011.orderflow.customer;

import com.garrettw011.orderflow.common.PageResponse;
import com.garrettw011.orderflow.customer.dto.CustomerResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/customers")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCustomerController {
    private final CustomerService service;

    public AdminCustomerController(CustomerService service) { this.service = service; }

    @GetMapping
    public PageResponse<CustomerResponse> list(@PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(service.list(pageable));
    }

    @GetMapping("/{id}")
    public CustomerResponse getById(@PathVariable Long id) { return service.getById(id); }
}