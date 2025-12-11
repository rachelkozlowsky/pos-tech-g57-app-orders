package br.com.five.seven.food.adapter.in.mappers.impl;

import br.com.five.seven.food.adapter.in.mappers.ItemMapper;
import br.com.five.seven.food.adapter.in.mappers.ProductMapper;
import br.com.five.seven.food.adapter.in.payload.item.ItemRequest;
import br.com.five.seven.food.adapter.in.payload.item.ItemResponse;
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
        // Note: Order relationship is set by OrderMapperImpl to avoid circular reference
        return new ItemEntity(
                item.getId(),
                productMapper.fromDomain(item.getProduct()),
                item.getQuantity(),
                null  // Will be set by parent Order mapper
        );
    }

    @Override
    public Item entityToDomain(ItemEntity itemEntity) {
        // Map the full product from entity
        Product product = productMapper.toDomain(itemEntity.getProduct());

        // Create item without order to avoid circular reference
        // Order relationship managed at Order level
        return new Item(
                itemEntity.getId(),
                product,
                itemEntity.getQuantity()
        );
    }

    @Override
    public ItemResponse domainToResponse(Item item) {
        return new ItemResponse(
                item.getProduct().toResponse(),
                item.getQuantity()
        );
    }
}
