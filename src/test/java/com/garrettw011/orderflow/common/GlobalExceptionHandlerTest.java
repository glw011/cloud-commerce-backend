package com.garrettw011.orderflow.common;

import com.garrettw011.orderflow.common.exception.ResourceNotFoundException;
import com.garrettw011.orderflow.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Import(GlobalExceptionHandlerTest.ProbeController.class)
class GlobalExceptionHandlerTest extends AbstractIntegrationTest {
    @RestController
    static class ProbeController {
        @GetMapping("/_probe/not-found")
        String notFound() { throw new ResourceNotFoundException("that's a nope"); }
    }

    @Test
    void notFoundProducesApiErrorShape() throws Exception {
        String token = loginToken(testCredentials);

        mvc.perform(get("/_probe/not-found").header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.path").value("/_probe/not-found"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}


