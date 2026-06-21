package com.garrettw011.orderflow.user;

import com.garrettw011.orderflow.support.AbstractPostgresTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=validate")
@AutoConfigureTestDatabase(replace = NONE)
class UserRepositoryTest extends AbstractPostgresTest {
    @Autowired UserRepository users;

    private User user(String email) {
        User u = new User();
        u.setEmail(email);
        u.setPasswordHash("NA");
        u.setRole(Role.CUSTOMER);
        return u;
    }

    @Test
    void duplicateEmailIsRejected() {
        users.saveAndFlush(user("dupe@dupe.com"));
        assertThatThrownBy(() -> users.saveAndFlush(user("dupe@dupe.com")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}

