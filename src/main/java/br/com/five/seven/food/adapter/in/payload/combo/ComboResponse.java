package br.com.five.seven.food.adapter.in.payload.combo;

import br.com.five.seven.food.adapter.in.payload.combo.item.ItemResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComboResponse {
    private Long id;
    private List<ItemResponse> snack = new ArrayList<>();
    private List<ItemResponse> garnish = new ArrayList<>();
    private List<ItemResponse> drink = new ArrayList<>();
    private List<ItemResponse> dessert = new ArrayList<>();
}
