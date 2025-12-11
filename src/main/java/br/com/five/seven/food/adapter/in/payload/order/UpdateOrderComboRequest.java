package br.com.five.seven.food.adapter.in.payload.order;

import br.com.five.seven.food.adapter.in.payload.combo.item.ItemRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderComboRequest {
    @NotEmpty(message = "Order must have at least one item")
    @Valid
    private List<ItemRequest> items = new ArrayList<>();
}
