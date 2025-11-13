package br.com.five.seven.food.infra.annotations.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Operation(summary = "Update an order", description = "Update an existing order by its ID.")@ApiResponses({
        @ApiResponse(responseCode = "200", description = "Order successfully updated"),
        @ApiResponse(responseCode = "404", description = "Order not found")
})
public @interface SwaggerUpdateOrderStatus {
}
