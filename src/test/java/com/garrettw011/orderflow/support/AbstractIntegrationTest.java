package com.garrettw011.orderflow.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.springframework.http.MediaType;
import com.jayway.jsonpath.JsonPath;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
public abstract class AbstractIntegrationTest {
    @ServiceConnection
    static final PostgreSQLContainer POSTGRES =
            new PostgreSQLContainer(DockerImageName.parse("postgres:16-alpine"));

    @ServiceConnection
    static final GenericContainer<?> REDIS =
            new GenericContainer<>(DockerImageName.parse("redis:7-alpine")).withExposedPorts(6379);
    static {
        POSTGRES.start();
        REDIS.start();
    }

    @Autowired
    protected MockMvc mvc;
    protected MediaType appJson = MediaType.APPLICATION_JSON;
    protected String testCredentials = """
            {"email":"customer@example.com","password":"CustomerPass123!"}""";

    protected String loginResp(String credentials) throws Exception {
        return mvc.perform(post("/api/v1/auth/login").contentType(appJson).content(credentials))
                .andReturn().getResponse().getContentAsString();
    }

    protected String tokenFromLoginResp(String loginResp) {
        return JsonPath.read(loginResp, "$.accessToken");
    }

    protected String formatCredentials(String email, String password) {
        return """
                {"email":"%s","password":"%s"}""".formatted(email, password);
    }

    protected String loginToken(String credentials) throws Exception {
        return tokenFromLoginResp(loginResp(credentials));
    }

    protected String token() throws Exception { return tokenFromLoginResp(loginResp(testCredentials)); }

    protected String tokenFor(String email, String password) throws Exception {
        return tokenFromLoginResp(loginResp(formatCredentials(email, password)));
    }

    protected String adminToken() throws Exception { return tokenFor("admin@example.com", "AdminPass123!"); }

    protected String bearer(String token) { return "Bearer " + token; }

    protected String productBody(String sku, String name, String price) {
        return """
               {"sku":"%s","name":"%s","price":"%s"}""".formatted(sku, name, price);
    }
}

