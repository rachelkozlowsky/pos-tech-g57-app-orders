package br.com.five.seven.food.adapter.in.payload.order;

import br.com.five.seven.food.adapter.in.payload.combo.ComboRequest;
import br.com.five.seven.food.application.domain.enums.OrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderRequest {

    private String cpfClient;

    private String title;

    private String description;

    @Valid
    private OrderStatus orderStatus;

    @NotNull
    @Valid
    private ComboRequest combo;
}
