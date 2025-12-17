package br.com.five.seven.food.adapter.out.api.client;

import br.com.five.seven.food.adapter.out.api.response.ClientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "client-api",
    url = "${api.client.url}"
)
public interface ClientApiClient {

    @GetMapping("/v1/clients/{cpf}")
    ClientResponse getClientByCpf(@PathVariable("cpf") String cpf);
}

