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
@Operation(summary = "Get all orders by Status", description = "Retrieve a paginated list of all orders of given statuses.")
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of orders")
})
public @interface SwaggerGetAllOrdersByStatus {
}
