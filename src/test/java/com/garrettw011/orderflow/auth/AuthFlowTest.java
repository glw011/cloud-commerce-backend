package com.garrettw011.orderflow.auth;

import com.garrettw011.orderflow.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthFlowTest extends AbstractIntegrationTest {

    @Test
    void registerReturnsTokens() throws Exception {
        String body = """
                {"email":"newuser@example.com", "password":"NewPass123!","firstName":"Hank","lastName":"Hill"}""";

        mvc.perform(post("/api/v1/auth/register").contentType(appJson).content(body))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    void loginSuccessWithCorrectPassword() throws Exception {
        mvc.perform(post("/api/v1/auth/login").contentType(appJson).content(testCredentials))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void loginFailsWithBadPassword() throws Exception {
        String badCredentials = formatCredentials("customer@example.com", "BadPass123!");

        mvc.perform(post("/api/v1/auth/login").contentType(appJson).content(badCredentials))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void meRequiresToken() throws Exception {
        mvc.perform(get("/api/v1/auth/me"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void invalidTokenReturns401() throws Exception {
        mvc.perform(get("/api/v1/auth/me").header("Authorization", "Bearer Very-Invalid-Token"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void meReturnsUserWithValidToken() throws Exception {
        MvcResult login =
                mvc.perform(post("/api/v1/auth/login").contentType(appJson).content(testCredentials))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andReturn();

        String token = tokenFromLoginResp(login.getResponse().getContentAsString());

        mvc.perform(get("/api/v1/auth/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("customer@example.com"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
    }

    @Test
    void customerCannotCreateProduct() throws Exception {
        mvc.perform(post("/api/v1/products")
                        .header("Authorization", bearer(token()))
                        .contentType(appJson)
                        .content(productBody("CUST-ITM-001", "Customer Item", "13.99")))
                .andExpect(status().isForbidden());
    }

    @Test
    void createWithNoTokenReturns401() throws Exception {
        mvc.perform(post("/api/v1/products")
                        .contentType(appJson)
                        .content(productBody("ANON-ITM-001", "Anon Item", "1.00")))
                .andExpect(status().isUnauthorized());
    }
}

