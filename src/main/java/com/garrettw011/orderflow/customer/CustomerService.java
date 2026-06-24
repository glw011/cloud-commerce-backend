package com.garrettw011.orderflow.customer;

import com.garrettw011.orderflow.common.exception.ResourceNotFoundException;
import com.garrettw011.orderflow.user.User;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {
    private final CustomerRepository customers;

    public CustomerService(CustomerRepository customers) { this.customers = customers; }

    public Customer createProfile(User user, String firstName, String lastName, String phone) {
        Customer customer = new Customer();

        customer.setUser(user);
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setPhone(phone);

        return customers.save(customer);
    }

    public Customer getByUserId(Long userId) {
        return customers.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found for user: " + userId));
    }
}
