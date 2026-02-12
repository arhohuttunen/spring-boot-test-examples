package com.arhohuttunen.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OrderController.class)
class OrderControllerIntegrationTests {
    @MockitoBean
    private OrderService orderService;
    @Autowired
    private MockMvc mockMvc;
}
