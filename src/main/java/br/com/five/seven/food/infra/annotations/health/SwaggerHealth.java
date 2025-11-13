package br.com.five.seven.food.infra.annotations.health;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Operation(summary = "Health check", description = "Returns a simple string to indicate the service is running.")
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "Service is healthy")
})
public @interface SwaggerHealth {
}
