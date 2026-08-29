package com.garrettw011.orderflow.docs;

import com.garrettw011.orderflow.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OpenApiDocTest extends AbstractIntegrationTest {

    @Test
    void docsDescribeApiAndServedWithoutAuth() throws Exception {
        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title")
                        .value("OrderFlow API"))
                .andExpect(jsonPath("$.components.securitySchemes.bearer-jwt.scheme")
                        .value("bearer"))
                .andExpect(jsonPath("$.paths./api/v1/orders").exists());
    }

    @Test
    void swaggerUiRedirectIsPublic() throws Exception {
        mvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection());
    }
}