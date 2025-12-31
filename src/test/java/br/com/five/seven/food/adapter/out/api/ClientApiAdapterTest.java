package br.com.five.seven.food.adapter.out.api;

import br.com.five.seven.food.adapter.out.api.client.ClientApiClient;
import br.com.five.seven.food.adapter.out.api.response.ClientResponse;
import feign.FeignException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Client API Adapter Tests")
class ClientApiAdapterTest {

    @Mock
    private ClientApiClient clientApiClient;

    @InjectMocks
    private ClientApiAdapter clientApiAdapter;

    @Test
    @DisplayName("Should return client when CPF exists")
    void givenValidCpf_whenGettingClient_thenClientShouldBeReturned() {
        // Given
        String cpf = "12345678900";
        ClientResponse clientResponse = new ClientResponse();
        clientResponse.setCpf(cpf);
        when(clientApiClient.getClientByCpf(cpf)).thenReturn(clientResponse);

        // When
        Optional<ClientResponse> result = clientApiAdapter.getClientByCpf(cpf);

        // Then
        assertTrue(result.isPresent());
        assertEquals(cpf, result.get().getCpf());
        verify(clientApiClient, times(1)).getClientByCpf(cpf);
    }

    @Test
    @DisplayName("Should return empty when CPF not found")
    void givenNonExistentCpf_whenGettingClient_thenEmptyShouldBeReturned() {
        // Given
        String cpf = "99999999999";
        when(clientApiClient.getClientByCpf(cpf))
                .thenThrow(mock(FeignException.NotFound.class));

        // When
        Optional<ClientResponse> result = clientApiAdapter.getClientByCpf(cpf);

        // Then
        assertFalse(result.isPresent());
        verify(clientApiClient, times(1)).getClientByCpf(cpf);
    }

    @Test
    @DisplayName("Should throw exception when API communication fails")
    void givenApiError_whenGettingClient_thenExceptionShouldBeThrown() {
        // Given
        String cpf = "12345678900";
        FeignException feignException = mock(FeignException.class);
        when(clientApiClient.getClientByCpf(cpf)).thenThrow(feignException);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> clientApiAdapter.getClientByCpf(cpf));
        assertEquals("Error communicating with Client API", exception.getMessage());
        verify(clientApiClient, times(1)).getClientByCpf(cpf);
    }
}
