package br.com.five.seven.food.application.ports.out;

import br.com.five.seven.food.adapter.out.api.response.ClientResponse;

import java.util.Optional;

public interface IClientApiOut {

    /**
     * Retrieves client information by CPF
     *
     * @param cpf the client's CPF
     * @return Optional containing the ClientResponse if found
     */
    Optional<ClientResponse> getClientByCpf(String cpf);
}

