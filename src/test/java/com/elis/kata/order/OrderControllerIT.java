package com.elis.kata.order;

import com.elis.kata.config.SpringSecurityTestConfig;
import com.elis.kata.domain.order.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @WithUserDetails("externalUser")
    void createAnOrderForDeliveryDay1IsOk() throws Exception {
        long count = orderRepository.count();
        mockMvc.perform(post(API_DELIVERY_DAY_ORDER, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(regularOrder(3)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNotEmpty())
            .andExpect(content().json("""
                {
                    "status": "DRAFT",
                    "deliveryDayId": 1,
                    "nbItems": 3,
                    "type": "REGULAR"
                }
                """));
        assertThat(orderRepository.count()).isEqualTo(count + 1);
    }

    @Test
    @WithUserDetails("externalUser")
    void createAnOrderForDeliveryDay2IsOk() throws Exception {
        mockMvc.perform(post(API_DELIVERY_DAY_ORDER, 2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(regularOrder(5)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNotEmpty())
            .andExpect(content().json("""
                {
                    "status": "DRAFT",
                    "deliveryDayId": 2,
                    "nbItems": 5,
                    "type": "REGULAR"
                }
                """));
    }

    @Test
    @WithUserDetails("externalUser")
    void absentDeliveryDayIsBadRequest() throws Exception {
        mockMvc.perform(post(API_DELIVERY_DAY_ORDER, 99)
                .contentType(MediaType.APPLICATION_JSON)
                .content(regularOrder(1)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail", is("The delivery day does not exist")));
    }

    @Test
    @WithUserDetails("externalUser")
    void orderWithoutItemsIsBadRequest() throws Exception {
        mockMvc.perform(post(API_DELIVERY_DAY_ORDER, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(regularOrder(0)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail", is("An order must have at least one item")));
    }

    @Test
    @WithUserDetails("internalUser")
    void createOrderWithInternalUserIsForbidden() throws Exception {
        mockMvc.perform(post(API_DELIVERY_DAY_ORDER, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(regularOrder(1)))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.detail", is("Access Denied")));
    }

    @Test
    @WithUserDetails("externalUser")
    void createASecondOrderForADeliveryDayIsBadRequest() throws Exception {
        mockMvc.perform(post(API_DELIVERY_DAY_ORDER, 3)
                .contentType(MediaType.APPLICATION_JSON)
                .content(regularOrder(1)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail", is("An order already exists for this delivery day")));
    }

    @Test
    @WithUserDetails("externalUser")
    void createAnOrderInAnExpiredDeliveryDayIsBadRequest() throws Exception {
        mockMvc.perform(post(API_DELIVERY_DAY_ORDER, 4)
                .contentType(MediaType.APPLICATION_JSON)
                .content(regularOrder(1)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail", is("An order can't be created on an expired delivery day")));
    }

    @Test
    @WithUserDetails("externalUser")
    void createAnExceptionalOrderOnDelivery3IsOk() throws Exception {
        mockMvc.perform(post(API_DELIVERY_DAY_ORDER, 3)
                .contentType(MediaType.APPLICATION_JSON)
                .content(exceptionalOrder()))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNotEmpty())
            .andExpect(content().json("""
                {
                    "status": "DRAFT",
                    "deliveryDayId": 3,
                    "nbItems": 1,
                    "type": "EXCEPTIONAL"
                }
                """));
    }

    @Test
    @WithUserDetails("externalUser")
    void createAnExceptionalOrderOnDeliveryDayWithoutOrderIsBadRequest() throws Exception {
        mockMvc.perform(post(API_DELIVERY_DAY_ORDER, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(exceptionalOrder()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail", is("Exceptional order can't be created if the delivery day doesn't have a validated regular order")));
    }

    @Test
    @WithUserDetails("externalUser")
    void createAnExceptionalOrderOnDeliveryDayWithOrderDraftIsBadRequest() throws Exception {
        mockMvc.perform(post(API_DELIVERY_DAY_ORDER, 5)
                .contentType(MediaType.APPLICATION_JSON)
                .content(exceptionalOrder()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail", is("Exceptional order can't be created if the delivery day doesn't have a validated regular order")));
    }

    private String regularOrder(int nbItems) {
        return """
            {
                "nbItems": %d,
                "type": "REGULAR"
            }
            """.formatted(nbItems);
    }

    private String exceptionalOrder() {
        return """
            {
                "nbItems": 1,
                "type": "EXCEPTIONAL"
            }
            """;
    }
}
