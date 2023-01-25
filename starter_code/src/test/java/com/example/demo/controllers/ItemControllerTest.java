package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {
    private ItemController itemController;
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void getItemsTest() {
        when(itemRepository.findAll()).thenReturn(new ArrayList<>());
        final ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void getItemByIdTest() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(createItem()));
        final ResponseEntity<Item> response = itemController.getItemById(1L);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Item item = response.getBody();
        assertEquals("TestItem", item.getName());
        assertEquals(BigDecimal.valueOf(2.5), item.getPrice());
    }

    @Test
    public void getItemsByNameTest() {
        List<Item> itemList = new ArrayList<>();
        itemList.add(createItem());
        when(itemRepository.findByName("TestItem")).thenReturn(itemList);
        final ResponseEntity<List<Item>> response = itemController.getItemsByName("TestItem");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<Item> items = response.getBody();
        assertEquals(1, items.size());
        assertEquals(Long.valueOf(1), items.get(0).getId());
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
