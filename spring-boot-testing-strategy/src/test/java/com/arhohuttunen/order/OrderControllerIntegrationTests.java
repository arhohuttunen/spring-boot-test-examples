package com.arhohuttunen.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OrderController.class)
class OrderControllerIntegrationTests {
    @MockBean
    private OrderService orderService;
    @Autowired
    private MockMvc mockMvc;
}
