package br.com.five.seven.food.adapter.in.mappers;

import br.com.five.seven.food.adapter.in.payload.item.ItemRequest;
import br.com.five.seven.food.adapter.in.payload.item.ItemResponse;
import br.com.five.seven.food.adapter.out.relational.entity.ItemEntity;
import br.com.five.seven.food.application.domain.Item;

import java.util.List;

public interface ItemMapper {
    default List<Item> requestListToDomainList(List<ItemRequest> items) {
        return items.stream()
                .map(this::requestToDomain)
                .toList();
    }

    default List<ItemEntity> domainListToEntityList(List<Item> items) {
        return items.stream()
                .map(this::domainToEntity)
                .toList();
    }

    default List<Item> entityListToDomainList(List<ItemEntity> items) {
        return items.stream()
                .map(this::entityToDomain)
                .toList();
    }

    default List<ItemResponse> domainListToResponseList(List<Item> items) {
        return items.stream()
                .map(this::domainToResponse)
                .toList();
    }

    Item requestToDomain(ItemRequest itemRequest);
    ItemEntity domainToEntity(Item item);
    Item entityToDomain(ItemEntity itemEntity);
    ItemResponse domainToResponse(Item itemResponse);
}
