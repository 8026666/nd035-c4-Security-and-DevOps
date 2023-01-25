package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {
    private CartController cartController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);
    private Authentication authentication = mock(Authentication.class);

    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
    }

    @Test
    public void addToCartTest() {
        User user = createUser();
        Item item = createItem();
        when(authentication.getName()).thenReturn("TestUser");
        when(userRepository.findByUsername("TestUser")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        // Test 1
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("TestUser");
        request.setItemId(1);
        request.setQuantity(2);
        final ResponseEntity<Cart> response = cartController.addToCart(request, authentication);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Cart cart = response.getBody();
        assertEquals(2, cart.getItems().size());
        assertEquals(BigDecimal.valueOf(5.0), cart.getTotal());
        assertEquals(1, cart.getUser().getId());
        // Test 2
        ModifyCartRequest request2 = new ModifyCartRequest();
        request2.setUsername("TestUser2");
        request2.setItemId(2);
        request2.setQuantity(1);
        final ResponseEntity<Cart> response2 = cartController.addToCart(request2, authentication);
        assertNotNull(response2);
        assertEquals(404, response2.getStatusCodeValue());
    }

    @Test
    public void removeFromCartTest() {
        User user = createUser();
        user.getCart().addItem(createItem());
        user.getCart().addItem(createItem());
        Item item = createItem();
        when(authentication.getName()).thenReturn("TestUser");
        when(userRepository.findByUsername("TestUser")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("TestUser");
        request.setItemId(1);
        request.setQuantity(1);
        final ResponseEntity<Cart> response = cartController.removeFromCart(request, authentication);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Cart cart = response.getBody();
        assertEquals(1, cart.getItems().size());
        assertEquals(BigDecimal.valueOf(2.5), cart.getTotal());
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
        cart.setTotal(BigDecimal.valueOf(0));
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
