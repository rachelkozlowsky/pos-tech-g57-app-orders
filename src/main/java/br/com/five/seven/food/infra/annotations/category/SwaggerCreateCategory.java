package br.com.five.seven.food.infra.annotations.category;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Operation(summary = "Create a new category", description = "Create a new category and return the created category details.")
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "Category successfully created"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
})
public @interface SwaggerCreateCategory {
}
