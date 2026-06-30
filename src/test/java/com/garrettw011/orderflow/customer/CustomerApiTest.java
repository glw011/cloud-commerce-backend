package com.garrettw011.orderflow.customer;

import com.garrettw011.orderflow.support.AbstractIntegrationTest;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CustomerApiTest extends AbstractIntegrationTest {
    private String registerCustomer(String email) throws Exception {
        String body = """
                {"email":"%s","password":"Pa55w0rD!","firstName":"Barry","lastName":"Dees","phone":"+1-985-534-3737"}
                """.formatted(email);

        String resp = mvc.perform(post("/api/v1/auth/register")
                        .contentType(appJson).content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(resp, "$.accessToken");
    }

    @Test
    void meRequiresAuth() throws Exception {
        mvc.perform(get("/api/v1/customers/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getMyProfileReturnsCallerProfile() throws Exception {
        mvc.perform(get("/api/v1/customers/me")
                        .header("Authorization", bearer(token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("customer@example.com"))
                .andExpect(jsonPath("$.firstName").value("Demo"))
                .andExpect(jsonPath("$.lastName").value("Customer"));
    }

    @Test
    void updateMyProfilePersists() throws Exception {
        String t = registerCustomer("updatetest@example.com");
        String b = """
                {"firstName":"Update","lastName":"Test","phone":"+1-987-654-3210"}""";

        mvc.perform(put("/api/v1/customers/me")
                        .header("Authorization", bearer(t))
                        .contentType(appJson).content(b))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Update"))
                .andExpect(jsonPath("$.lastName").value("Test"))
                .andExpect(jsonPath("$.phone").value("+1-987-654-3210"))
                .andExpect(jsonPath("$.email").value("updatetest@example.com"));

        mvc.perform(get("/api/v1/customers/me")
                        .header("Authorization", bearer(t)))
                .andExpect(jsonPath("$.firstName").value("Update"))
                .andExpect(jsonPath("$.lastName").value("Test"))
                .andExpect(jsonPath("$.phone").value("+1-987-654-3210"));
    }

    @Test
    void updateBlankNameReturns400() throws Exception {
        String b = """
                {"firstName":"","lastName":"Blankfirst"}""";

        mvc.perform(put("/api/v1/customers/me")
                        .header("Authorization", bearer(token()))
                        .contentType(appJson).content(b))
                .andExpect(status().isBadRequest());
    }

    @Test
    void meReturns404ForNoProfileAccount() throws Exception {
        mvc.perform(get("/api/v1/customers/me")
                        .header("Authorization", bearer(adminToken())))
                .andExpect(status().isNotFound());
    }

    @Test
    void adminCanListCustomers() throws Exception {
        mvc.perform(get("/api/v1/admin/customers")
                        .header("Authorization", bearer(adminToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").exists());
    }

    @Test
    void adminCanGetCustomerById() throws Exception {
        String t = registerCustomer("getcustbyid@test.com");

        String c = mvc.perform(get("/api/v1/customers/me")
                        .header("Authorization", bearer(t)))
                .andReturn().getResponse().getContentAsString();

        Integer id = JsonPath.read(c, "$.id");

        mvc.perform(get("/api/v1/admin/customers/" + id)
                        .header("Authorization", bearer(adminToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("getcustbyid@test.com"));
    }

    @Test
    void adminGetByBadIdReturns404() throws Exception {
        mvc.perform(get("/api/v1/admin/customers/99999999")
                        .header("Authorization", bearer(adminToken())))
                .andExpect(status().isNotFound());
    }

    @Test
    void customerAdminAccessReturns403() throws Exception {
        mvc.perform(get("/api/v1/admin/customers")
                        .header("Authorization", bearer(token())))
                .andExpect(status().isForbidden());
    }
}


