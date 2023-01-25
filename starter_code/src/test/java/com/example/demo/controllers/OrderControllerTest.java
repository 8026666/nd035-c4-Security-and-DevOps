package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {
    private OrderController orderController;
    private UserRepository userRepository = mock(UserRepository.class);
    private OrderRepository orderRepository = mock(OrderRepository.class);
    private Authentication authentication = mock(Authentication.class);

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
    }

    @Test
    public void submitTest() {
        User user = createUser();
        when(authentication.getName()).thenReturn("TestUser");
        when(userRepository.findByUsername("TestUser")).thenReturn(user);
        final ResponseEntity<UserOrder> response = orderController.submit("TestUser", authentication);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        UserOrder userOrder = response.getBody();
        assertEquals(1, userOrder.getItems().size());
        assertEquals(BigDecimal.valueOf(2.5), userOrder.getTotal());
    }

    @Test
    public void getOrdersForUserTest() {
        User user = createUser();
        when(authentication.getName()).thenReturn("TestUser");
        when(userRepository.findByUsername("TestUser")).thenReturn(user);
        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("TestUser", authentication);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
    }

    private User createUser() {
        User user = new User();
        user.setId(1);
        user.setUsername("TestUser");
        user.setPassword("TestPassword");
        user.setCart(createCart(user));
        user.getCart().setUser(user);
        return user;
    }

    private Cart createCart(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setId(1L);
        cart.setItems(new ArrayList<>());
        cart.addItem(createItem());
        return cart;
    }

    private Item createItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName("TestItem");
        item.setPrice(BigDecimal.valueOf(2.5));
        item.setDescription("TestItemDescription");
        return item;
    }
}
