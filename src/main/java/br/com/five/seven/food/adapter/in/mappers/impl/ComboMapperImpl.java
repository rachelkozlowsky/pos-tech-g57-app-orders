package br.com.five.seven.food.adapter.in.mappers.impl;

import br.com.five.seven.food.adapter.in.mappers.ComboMapper;
import br.com.five.seven.food.adapter.in.mappers.ItemMapper;
import br.com.five.seven.food.adapter.in.payload.combo.ComboRequest;
import br.com.five.seven.food.adapter.in.payload.combo.ComboResponse;
import br.com.five.seven.food.adapter.out.relational.entity.ComboEntity;
import br.com.five.seven.food.adapter.out.relational.entity.ItemEntity;
import br.com.five.seven.food.application.domain.Combo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class ComboMapperImpl implements ComboMapper {

    private final ItemMapper itemMapper;

    @Override
    public Combo requestToDomain(ComboRequest comboRequest) {
        return new Combo(
                null,
                itemMapper.requestListToDomainList(comboRequest.getSnack()),
                itemMapper.requestListToDomainList(comboRequest.getGarnish()),
                itemMapper.requestListToDomainList(comboRequest.getDrink()),
                itemMapper.requestListToDomainList(comboRequest.getDessert())
        );
    }

    @Override
    public ComboEntity domainToEntity(Combo combo) {
        return new ComboEntity(
                combo.getId(),
                itemMapper.domainListToEntityList(combo.getSnack()),
                itemMapper.domainListToEntityList(combo.getGarnish()),
                itemMapper.domainListToEntityList(combo.getDrink()),
                itemMapper.domainListToEntityList(combo.getDessert())
        );
    }

    @Override
    public Combo entityToDomain(ComboEntity combo) {
        return new Combo(
                combo.getId(),
                itemMapper.entityListToDomainList(combo.getSnack()),
                itemMapper.entityListToDomainList(combo.getGarnish()),
                itemMapper.entityListToDomainList(combo.getDrink()),
                itemMapper.entityListToDomainList(combo.getDessert())
        );
    }

    @Override
    public ComboResponse domainToResponse(Combo combo) {
        return new ComboResponse(
                combo.getId(),
                itemMapper.domainListToResponseList(combo.getSnack()),
                itemMapper.domainListToResponseList(combo.getGarnish()),
                itemMapper.domainListToResponseList(combo.getDrink()),
                itemMapper.domainListToResponseList(combo.getDessert())
        );
    }
}
