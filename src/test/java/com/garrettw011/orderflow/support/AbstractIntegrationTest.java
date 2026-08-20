package com.garrettw011.orderflow.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import com.jayway.jsonpath.JsonPath;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest extends AbstractContainerTest {

    @Autowired
    protected MockMvc mvc;
    protected MediaType json = MediaType.APPLICATION_JSON;
    protected String testCredentials = """
            {"email":"customer@example.com","password":"CustomerPass123!"}""";
    protected String loginResp(String credentials) throws Exception {
        return mvc.perform(post("/api/v1/auth/login").contentType(json).content(credentials))
                .andReturn().getResponse().getContentAsString();
    }

    // === Token helpers ===

    protected String tokenFromLoginResp(String loginResp) {
        return JsonPath.read(loginResp, "$.accessToken");
    }

    protected String loginToken(String credentials) throws Exception {
        return tokenFromLoginResp(loginResp(credentials));
    }

    protected String token() throws Exception { return tokenFromLoginResp(loginResp(testCredentials)); }

    protected String tokenFor(String email, String password) throws Exception {
        return tokenFromLoginResp(loginResp(credentialBody(email, password)));
    }

    protected String adminToken() throws Exception { return tokenFor("admin@example.com", "AdminPass123!"); }

    protected String managerToken() throws Exception { return tokenFor("warehouse@example.com", "WarehousePass123!"); }

    protected String bearer(String token) { return "Bearer " + token; }


    // === Request body helpers ===

    protected String credentialBody(String email, String password) {
        return """
                {"email":"%s","password":"%s"}""".formatted(email, password);
    }

    protected String productBody(String sku, String name, String price) {
        return """
               {"sku":"%s","name":"%s","price":"%s"}""".formatted(sku, name, price);
    }

    protected String orderBody(long productId, int qty) {
        return """
                {"items":[%s]}""".formatted(orderItemBody(productId, qty));
    }

    protected String orderItemBody(long productId, int qty) {
        return """
                {"productId":%d,"quantity":%d}""".formatted(productId, qty);
    }

    protected String payBody(String provider, String token) {
        return """
                {"provider":"%s","paymentToken":"%s"}""".formatted(provider, token);
    }

    protected String payBody() {
        return payBody("provider", "tok_ok");
    }

    // === Register test customer ===

    protected String registerCustomer(String email) throws Exception {
        String body = """
                {"email":"%s","password":"Pa55w0rD!","firstName":"Barry","lastName":"Dees","phone":"+1-915-345-3737"}
                """.formatted(email);

        String resp = mvc.perform(post("/api/v1/auth/register")
                        .contentType(json).content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(resp, "$.accessToken");
    }

    // === Create test product ===

    protected long createProduct(String sku, String price) throws Exception {
        String created = mvc.perform(post("/api/v1/products")
                        .header("Authorization", bearer(adminToken()))
                        .contentType(json)
                        .content(productBody(sku, "Test ".concat(sku), price)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(created, "$.id")).longValue();
    }


    // === Stock new test product ===

    protected long createStockedProduct(String sku, String price, int qty) throws Exception {
        long pid = createProduct(sku, price);
        mvc.perform(patch("/api/v1/inventory/" + pid + "/adjust")
                        .header("Authorization", bearer(adminToken()))
                        .contentType(json)
                        .content("{\"delta\":" + qty + "}"))
                .andExpect(status().isOk());
        return pid;
    }

    // === Place new test order ===

    protected long placeOrder(long productId, int qty) throws Exception {
        String order = mvc.perform(post("/api/v1/orders")
                        .header("Authorization", bearer(token()))
                        .contentType(json)
                        .content(orderBody(productId, qty)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        return ((Number) JsonPath.read(order, "$.id")).longValue();
    }

    // === Simulate payment for test order ===

    protected void payOrder(long orderId) throws Exception {
        mvc.perform(post("/api/v1/orders/" + orderId + "/payments")
                        .header("Authorization", bearer(token()))
                        .contentType(json)
                        .content(payBody()))
                .andExpect(status().isCreated());
    }
}

