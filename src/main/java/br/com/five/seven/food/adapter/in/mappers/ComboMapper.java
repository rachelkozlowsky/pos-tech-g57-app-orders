package br.com.five.seven.food.adapter.in.mappers;

import br.com.five.seven.food.adapter.in.payload.combo.ComboRequest;
import br.com.five.seven.food.adapter.in.payload.combo.ComboResponse;
import br.com.five.seven.food.adapter.out.relational.entity.ComboEntity;
import br.com.five.seven.food.application.domain.Combo;

public interface ComboMapper {
    Combo requestToDomain(ComboRequest comboRequest);
    ComboEntity domainToEntity(Combo combo);
    Combo entityToDomain(ComboEntity combo);
    ComboResponse domainToResponse(Combo combo);
}
