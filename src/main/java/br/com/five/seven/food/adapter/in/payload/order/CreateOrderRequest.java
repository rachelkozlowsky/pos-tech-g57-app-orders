package br.com.five.seven.food.adapter.in.payload.order;

import br.com.five.seven.food.adapter.in.payload.item.ItemRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequest {
    private String cpfClient;
    @NotNull
    private String title;
    private String description;
    @Size(min = 1)
    @Valid
    private List<ItemRequest> items;
}
