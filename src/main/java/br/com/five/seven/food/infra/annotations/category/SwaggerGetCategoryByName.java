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
@Operation(summary = "Get category by name", description = "Retrieve a specific category by its name.")
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "Category successfully retrieved"),
        @ApiResponse(responseCode = "404", description = "Category not found")
})
public @interface SwaggerGetCategoryByName {
}
