package com.garrettw011.orderflow.customer;

import com.garrettw011.orderflow.common.exception.ResourceNotFoundException;
import com.garrettw011.orderflow.customer.dto.CustomerResponse;
import com.garrettw011.orderflow.customer.dto.CustomerUpdateRequest;
import com.garrettw011.orderflow.user.User;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {
    private final CustomerRepository customers;

    private CustomerResponse toResponse(Customer c) {
        return new CustomerResponse(c.getId(), c.getUser().getEmail(), c.getFirstName(), c.getLastName(),
                                    c.getPhone(), c.getCreatedAt(), c.getUpdatedAt());
    }

    public CustomerService(CustomerRepository customers) { this.customers = customers; }

    @Transactional(readOnly = true)
    public CustomerResponse getMyProfile(Long userId) { return toResponse(getByUserId(userId)); }

    @Transactional(readOnly = true)
    public Customer getByUserId(Long userId) {
        return customers.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile could not be found."));
    }

    public Customer createProfile(User user, String firstName, String lastName, String phone) {
        Customer customer = new Customer();
        customer.setUser(user);
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setPhone(phone);
        return customers.save(customer);
    }

    @Transactional
    public CustomerResponse updateMyProfile(Long userId, CustomerUpdateRequest req) {
        Customer customer = getByUserId(userId);
        customer.setFirstName(req.firstName());
        customer.setLastName(req.lastName());
        customer.setPhone(req.phone());
        return toResponse(customers.save(customer));
    }

    @Transactional(readOnly = true)
    public CustomerResponse getById(Long id) {
        Customer customer = customers.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No customer found with id: " + id));
        return toResponse(customer);
    }

    @Transactional(readOnly = true)
    public Page<CustomerResponse> list(Pageable pageable) {
        return customers.findAll(pageable).map(this::toResponse);
    }
}
