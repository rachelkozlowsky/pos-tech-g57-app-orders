package br.com.five.seven.food.adapter.in.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@DisplayName("Health Controller Tests")
class HealthControllerTest {

    @InjectMocks
    private HealthController healthController;

    @Test
    @DisplayName("Should return UP when health endpoint is called")
    void givenHealthEndpoint_whenCalled_thenReturnUp() {
        // When
        String result = healthController.health();

        // Then
        assertEquals("UP!", result);
    }
}
