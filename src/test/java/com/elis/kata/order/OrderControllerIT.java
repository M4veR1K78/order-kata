package com.elis.kata.order;

import com.elis.kata.config.SpringSecurityTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = SpringSecurityTestConfig.class)
@ActiveProfiles("test")
@Sql(scripts = "classpath:sql/OrderControllerIT.sql", config = @SqlConfig(encoding = "utf-8"))
@Sql(scripts = "classpath:sql/clearTables.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@AutoConfigureMockMvc
class OrderControllerIT {

    private static final String API_DELIVERY_DAY_ORDER = "/api/delivery-day/{id}/order";

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    void itCreatesAnOrderSuccessfully() throws Exception {
        mockMvc.perform(post(API_DELIVERY_DAY_ORDER, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "nbItems": 3
                    }
                    """))
            .andExpect(status().isCreated());
    }
}
