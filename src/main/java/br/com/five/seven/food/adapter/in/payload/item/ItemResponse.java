package br.com.five.seven.food.adapter.in.payload.item;

import br.com.five.seven.food.adapter.in.payload.products.ProductResponse;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponse {

    @NotNull
    private ProductResponse product;
    @Min(1)
    private Integer quantity;
}
