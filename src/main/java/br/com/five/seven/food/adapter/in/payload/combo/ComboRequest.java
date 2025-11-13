package br.com.five.seven.food.adapter.in.payload.combo;

import br.com.five.seven.food.adapter.in.payload.combo.item.ItemRequest;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComboRequest {
    @Null
    private String id;
    private List<ItemRequest> snack = new ArrayList<>();
    private List<ItemRequest> garnish = new ArrayList<>();
    private List<ItemRequest> drink = new ArrayList<>();
    private List<ItemRequest> dessert = new ArrayList<>();
}
