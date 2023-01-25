package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private UserController userController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }
    @Test
    public void createUserTest() {
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("testPassword");
        request.setConfirmPassword("testPassword");
        final ResponseEntity<User> response = userController.createUser(request);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("test", user.getUsername());
        assertEquals("thisIsHashed", user.getPassword());
    }

    @Test
    public void findByIdTest() {
        User user = new User();
        user.setId(1);
        user.setUsername("test");
        user.setPassword("testPassword");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        final ResponseEntity<User> response = userController.findById(1L);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User resUser = response.getBody();
        assertNotNull(resUser);
        assertEquals(user.getId(), resUser.getId());
        assertEquals(user.getUsername(), resUser.getUsername());
        assertEquals(user.getPassword(), resUser.getPassword());
    }

    @Test
    public void findByUserNameTest() {
        User user = new User();
        user.setId(1);
        user.setUsername("test");
        user.setPassword("testPassword");
        when(userRepository.findByUsername("test")).thenReturn(user);
        final ResponseEntity<User> response = userController.findByUserName("test");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User resUser = response.getBody();
        assertNotNull(resUser);
        assertEquals(user.getId(), resUser.getId());
        assertEquals(user.getUsername(), resUser.getUsername());
        assertEquals(user.getPassword(), resUser.getPassword());
    }
}
