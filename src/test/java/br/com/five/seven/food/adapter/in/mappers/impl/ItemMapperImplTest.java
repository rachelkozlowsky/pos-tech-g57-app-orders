package br.com.five.seven.food.adapter.in.mappers.impl;

import br.com.five.seven.food.adapter.in.mappers.ProductMapper;
import br.com.five.seven.food.adapter.in.payload.item.ItemRequest;
import br.com.five.seven.food.adapter.in.payload.item.ItemResponse;
import br.com.five.seven.food.adapter.out.relational.entity.ItemEntity;
import br.com.five.seven.food.adapter.out.relational.entity.ProductEntity;
import br.com.five.seven.food.application.domain.Category;
import br.com.five.seven.food.application.domain.Item;
import br.com.five.seven.food.application.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Item Mapper Tests")
class ItemMapperImplTest {

    @Mock
    private ProductMapper productMapper;

    private ItemMapperImpl itemMapper;

    @BeforeEach
    void setUp() {
        itemMapper = new ItemMapperImpl(productMapper);
    }

    @Test
    @DisplayName("Should map ItemRequest to Item domain")
    void givenItemRequest_whenMappingToDomain_thenItemShouldBeCreated() {
        // Given
        ItemRequest request = new ItemRequest();
        request.setProductId(1L);
        request.setQuantity(2);

        // When
        Item result = itemMapper.requestToDomain(request);

        // Then
        assertNotNull(result);
        assertNull(result.getId());
        assertEquals(1L, result.getProduct().getId());
        assertEquals(2, result.getQuantity());
    }

    @Test
    @DisplayName("Should map Item domain to ItemEntity")
    void givenItemDomain_whenMappingToEntity_thenItemEntityShouldBeCreated() {
        // Given
        Product product = createProduct(1L, "Hambúrguer");
        Item item = new Item(1L, product, 2);
        ProductEntity productEntity = createProductEntity(1L, "Hambúrguer");

        when(productMapper.fromDomain(product)).thenReturn(productEntity);

        // When
        ItemEntity result = itemMapper.domainToEntity(item);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(2, result.getQuantity());
        assertNotNull(result.getProduct());
        verify(productMapper, times(1)).fromDomain(product);
    }

    @Test
    @DisplayName("Should map ItemEntity to Item domain")
    void givenItemEntity_whenMappingToDomain_thenItemShouldBeCreated() {
        // Given
        ProductEntity productEntity = createProductEntity(1L, "Hambúrguer");
        ItemEntity itemEntity = new ItemEntity(1L, productEntity, 2, null);
        Product product = createProduct(1L, "Hambúrguer");

        when(productMapper.toDomain(productEntity)).thenReturn(product);

        // When
        Item result = itemMapper.entityToDomain(itemEntity);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(2, result.getQuantity());
        assertNotNull(result.getProduct());
        verify(productMapper, times(1)).toDomain(productEntity);
    }

    @Test
    @DisplayName("Should map Item domain to ItemResponse")
    void givenItemDomain_whenMappingToResponse_thenItemResponseShouldBeCreated() {
        // Given
        Product product = createProduct(1L, "Hambúrguer");
        Item item = new Item(1L, product, 2);

        // When
        ItemResponse result = itemMapper.domainToResponse(item);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getQuantity());
        assertNotNull(result.getProduct());
    }

    @Test
    @DisplayName("Should map list of ItemRequest to list of Item domain")
    void givenItemRequestList_whenMappingToDomainList_thenItemListShouldBeCreated() {
        // Given
        ItemRequest request1 = new ItemRequest();
        request1.setProductId(1L);
        request1.setQuantity(2);

        ItemRequest request2 = new ItemRequest();
        request2.setProductId(2L);
        request2.setQuantity(3);

        List<ItemRequest> requests = Arrays.asList(request1, request2);

        // When
        List<Item> result = itemMapper.requestListToDomainList(requests);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getProduct().getId());
        assertEquals(2, result.get(0).getQuantity());
        assertEquals(2L, result.get(1).getProduct().getId());
        assertEquals(3, result.get(1).getQuantity());
    }

    @Test
    @DisplayName("Should map list of Item domain to list of ItemEntity")
    void givenItemDomainList_whenMappingToEntityList_thenItemEntityListShouldBeCreated() {
        // Given
        Product product1 = createProduct(1L, "Hambúrguer");
        Product product2 = createProduct(2L, "Refrigerante");
        Item item1 = new Item(1L, product1, 2);
        Item item2 = new Item(2L, product2, 3);
        List<Item> items = Arrays.asList(item1, item2);

        ProductEntity productEntity1 = createProductEntity(1L, "Hambúrguer");
        ProductEntity productEntity2 = createProductEntity(2L, "Refrigerante");

        when(productMapper.fromDomain(product1)).thenReturn(productEntity1);
        when(productMapper.fromDomain(product2)).thenReturn(productEntity2);

        // When
        List<ItemEntity> result = itemMapper.domainListToEntityList(items);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    @DisplayName("Should map list of ItemEntity to list of Item domain")
    void givenItemEntityList_whenMappingToDomainList_thenItemListShouldBeCreated() {
        // Given
        ProductEntity productEntity1 = createProductEntity(1L, "Hambúrguer");
        ProductEntity productEntity2 = createProductEntity(2L, "Refrigerante");
        ItemEntity itemEntity1 = new ItemEntity(1L, productEntity1, 2, null);
        ItemEntity itemEntity2 = new ItemEntity(2L, productEntity2, 3, null);
        List<ItemEntity> itemEntities = Arrays.asList(itemEntity1, itemEntity2);

        Product product1 = createProduct(1L, "Hambúrguer");
        Product product2 = createProduct(2L, "Refrigerante");

        when(productMapper.toDomain(productEntity1)).thenReturn(product1);
        when(productMapper.toDomain(productEntity2)).thenReturn(product2);

        // When
        List<Item> result = itemMapper.entityListToDomainList(itemEntities);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    @DisplayName("Should map list of Item domain to list of ItemResponse")
    void givenItemDomainList_whenMappingToResponseList_thenItemResponseListShouldBeCreated() {
        // Given
        Product product1 = createProduct(1L, "Hambúrguer");
        Product product2 = createProduct(2L, "Refrigerante");
        Item item1 = new Item(1L, product1, 2);
        Item item2 = new Item(2L, product2, 3);
        List<Item> items = Arrays.asList(item1, item2);

        // When
        List<ItemResponse> result = itemMapper.domainListToResponseList(items);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(2, result.get(0).getQuantity());
        assertEquals(3, result.get(1).getQuantity());
    }

    private Product createProduct(Long id, String name) {
        Category category = new Category();
        category.setId(1L);
        category.setName("Lanches");

        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(25.90));
        product.setCategory(category);
        product.setActive(true);
        return product;
    }

    private ProductEntity createProductEntity(Long id, String name) {
        ProductEntity entity = new ProductEntity();
        entity.setId(id);
        entity.setName(name);
        entity.setPrice(BigDecimal.valueOf(25.90));
        entity.setActive(true);
        return entity;
    }
}
