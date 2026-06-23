package com.garrettw011.orderflow.auth;

import com.garrettw011.orderflow.support.AbstractIntegrationTest;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RefreshTokenRotationTest extends AbstractIntegrationTest {

    private String login() throws Exception {
        String body = mvc.perform(post("/api/v1/auth/login").contentType(appJson).content(testCredentials))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.refreshToken");
    }

    private String refreshBody(String refreshToken) {
        return """
            {"refreshToken":"%s"}""".formatted(refreshToken);
    }

    @Test
    void refreshReturnsNewTokenPair() throws Exception {
        String originalRefresh = login();

        String body =
                mvc.perform(post("/api/v1/auth/refresh").contentType(appJson).content(refreshBody(originalRefresh)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.accessToken").exists())
                        .andExpect(jsonPath("$.refreshToken").exists())
                        .andReturn().getResponse().getContentAsString();

        String rotatedRefresh = JsonPath.read(body, "$.refreshToken");
        assertThat(rotatedRefresh).isNotEqualTo(originalRefresh);
    }

    @Test
    void reusingConsumedRefreshTokenReturns401() throws Exception {
        String originalRefresh = login();

        // first use succeeds/rotates token
        mvc.perform(post("/api/v1/auth/refresh").contentType(appJson).content(refreshBody(originalRefresh)))
                .andExpect(status().isOk());

        // reusing consumed refresh token should be rejected
        mvc.perform(post("/api/v1/auth/refresh").contentType(appJson).content(refreshBody(originalRefresh)))
                .andExpect(status().isUnauthorized());
    }
}

