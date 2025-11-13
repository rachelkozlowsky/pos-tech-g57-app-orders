package br.com.five.seven.food.adapter.in.mappers.impl;

import br.com.five.seven.food.adapter.in.mappers.ItemMapper;
import br.com.five.seven.food.adapter.in.mappers.ProductMapper;
import br.com.five.seven.food.adapter.in.payload.combo.item.ItemRequest;
import br.com.five.seven.food.adapter.in.payload.combo.item.ItemResponse;
import br.com.five.seven.food.adapter.out.relational.entity.ItemEntity;
import br.com.five.seven.food.application.domain.Item;
import br.com.five.seven.food.application.domain.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ItemMapperImpl implements ItemMapper {

    private final ProductMapper productMapper;

    @Override
    public Item requestToDomain(ItemRequest itemRequest) {
        Product product = new Product();
        product.setId(itemRequest.getProductId());
        return new Item(null, product, itemRequest.getQuantity());
    }

    @Override
    public ItemEntity domainToEntity(Item item) {
        return new ItemEntity(
                1L,
                productMapper.fromDomain(item.getProduct()),
                item.getQuantity(),
                null
        );
    }

    @Override
    public Item entityToDomain(ItemEntity itemEntity) {
        Product product = new Product();
        product.setId(itemEntity.getProduct().getId());
        return new Item(null, product, itemEntity.getQuantity());
    }

    @Override
    public ItemResponse domainToResponse(Item item) {
        return new ItemResponse(
                item.getProduct().toResponse(),
                item.getQuantity()
        );
    }
}
