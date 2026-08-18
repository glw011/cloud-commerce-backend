package com.garrettw011.orderflow.auth;

import com.garrettw011.orderflow.auth.dto.RegisterRequest;
import com.garrettw011.orderflow.common.exception.DuplicateResourceException;
import com.garrettw011.orderflow.customer.CustomerService;
import com.garrettw011.orderflow.user.Role;
import com.garrettw011.orderflow.user.User;
import com.garrettw011.orderflow.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationService {
    private final UserService userService;
    private final CustomerService customerService;

    public RegistrationService(UserService userService, CustomerService customerService) {
        this.userService = userService;
        this.customerService = customerService;
    }

    @Transactional
    public User registerCustomer(RegisterRequest req) {
        if (userService.emailExists(req.email())) {
            throw new DuplicateResourceException("Email already associated with another profile.");
        }

        User user = userService.createUser(req.email(), req.password(), Role.CUSTOMER);
        customerService.createProfile(user, req.firstName(), req.lastName(), req.phone());

        return user;
    }
}