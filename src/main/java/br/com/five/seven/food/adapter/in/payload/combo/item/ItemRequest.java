package br.com.five.seven.food.adapter.in.payload.combo.item;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {

    @NotNull
    private Long productId;
    @Min(1)
    private Integer quantity;
}
