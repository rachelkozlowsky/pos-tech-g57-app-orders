package br.com.five.seven.food.adapter.out.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientResponse {
    private String id;
    private String cpf;
    private String name;
    private String email;
    private String phone;
}

