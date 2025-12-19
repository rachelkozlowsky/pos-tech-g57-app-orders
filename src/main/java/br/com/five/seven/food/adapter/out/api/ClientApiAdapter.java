package br.com.five.seven.food.adapter.out.api;

import br.com.five.seven.food.adapter.out.api.client.ClientApiClient;
import br.com.five.seven.food.adapter.out.api.response.ClientResponse;
import br.com.five.seven.food.application.ports.out.IClientApiOut;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ClientApiAdapter implements IClientApiOut {

    private final ClientApiClient clientApiClient;

    public ClientApiAdapter(ClientApiClient clientApiClient) {
        this.clientApiClient = clientApiClient;
    }

    @Override
    public Optional<ClientResponse> getClientByCpf(String cpf) {
        try {
            ClientResponse response = clientApiClient.getClientByCpf(cpf);
            return Optional.ofNullable(response);
        } catch (FeignException.NotFound e) {
            return Optional.empty();
        } catch (FeignException e) {
            throw new RuntimeException("Error communicating with Client API", e);
        }
    }
}

