package br.com.five.seven.food.adapter.in.payload.order;

import br.com.five.seven.food.application.domain.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusRequest {
    @NotNull
    private Long id;

    @NotNull
    private OrderStatus orderStatus;
}
