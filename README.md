# pos-tech-g57 - Sistema de Gerenciamento de Pedidos

Sistema para gerenciamento de pedidos, desenvolvido como parte do Tech Challenge da FIAP.

# Índice

1. [Problema e Solução](#problema-e-solução) 
2. [Versão](#versão)
3. [Stack Tecnológica](#stack-tecnológica)
4. [Arquitetura](#arquitetura)
    - [Estrutura de Pacotes](#estrutura-de-pacotes)
5. [Funcionalidades](#funcionalidades)
6. [Próximas Features](#próximas-features)
7. [Como Executar](#como-executar)
    - [Requisitos mínimos](#requisitos-mínimos)
    - [Localmente](#localmente)
    - [Com Docker Compose](#com-docker-compose)
8. [Documentação da API](#documentação-da-api)
    - [Principais Endpoints](#principais-endpoints)
9. [Colaboradores](#colaboradores)
10. [Licença](#licença)


## Problema e Solução
Visite o [documeto](documentations/PROBLEM.md) anexo para mais detalhes sobre o problema que este projeto resolve.

## Versão
0.0.1

## Stack Tecnológica

- **Linguagem**: ☕ Java 21
- **Framework**: 🌱 Spring Boot 3.4.4
- **Maven**: 🛠️ Maven 3.9.6
- **Banco de Dados**: 🐘 PostgreSQL (produção) / H2 (local)
- **ORM**: 🔄 JPA/Hibernate
- **Containerização**: 🐳 Docker + 🛠️ Docker Compose 3.8
- **Documentação API**: 📄 Swagger/OpenAPI 3.0

## Arquitetura

O projeto utiliza Arquitetura Hexagonal (Ports & Adapters) para garantir:
- Separação clara entre domínio e infraestrutura
- Independência de frameworks
- Facilidade de testes
- Baixo acoplamento
- Escalabilidade
- Flexibilidade

### Estrutura de Pacotes

```
br.com.five.seven.food
├── adapter
│   ├── in
│   │   └── controller   # Controllers REST
│   └── out
│       └── repository   # Adaptadores de Persistência
├── application
│   ├── domain           # Entidades e Regras de Domínio
│   ├── ports
│   │   ├── in           # Portas de Entrada (Use Cases)
│   │   └── out          # Portas de Saída (Repositories)
│   └── service          # Implementação dos Use Cases
└── config               # Configurações da Aplicação
```

## Funcionalidades

- Cadastro e gestão de categorias
- Cadastro e gestão de produtos
- Gerenciamento de pedidos
- Atualização de status dos pedidos
- Autenticação e autorização com Spring Security
- Integração com MongoDB para persistência
- Integração com MercadoPago para pagamento
- Documentação da API com Swagger/OpenAPI

## Próximas Features
Visite o [documeto](documentations/FEATURES.md) anexo para mais detalhes sobre as próximas features planejadas.

## Como Executar

### Requisitos mínimos
- git
- docker e docker-compose
- Altere oarquivo `des.env` na raiz do projeto para incluir o token do Mercado Pago:

```bash
## Ambiente (des ou prod)
ENVIRONMENT_PROFILE_DEV_NAME=des
ENVIRONMENT_PROFILE_PROD_NAME=prd
ENVIRONMENT_PROFILE_VALUE=des

## 1. PAYMENT MERCADO PAGO

# Token de producao para viabilizar o pix
JWT_TOKEN_PIX_APPLICATION_PAYMENT=SEU_TOKEN_AQUI

```

Atenção: Para viabilizar a geração do qrCode, o TOKEN do mercado pago deve ser o de produção ([saiba mais](https://www.mercadopago.com.br/developers/pt/docs/checkout-api-v2/payment-integration/pix)).
O token fornecido no arquivo des.env é apenas para teste e será descontinuado após a conclusão do curso.


### Com Docker Compose

1. Clone o repositório
    ```bash
    git clone https://gitlab.com/filipepereira1/pos-tech-g57.git
    ```
2. Execute:
   ```bash
   docker-compose up
   ```
3. Pronto a aplicação estará disponível em http://localhost:8080
4. Swagger UI estará disponível em http://localhost:8080/swagger-ui.html
5. Na raiz do projeto é disponibilizado a collection do Postman para testes, basta importar o arquivo `collection.yaml` no Postman.

### Localmente

1. Clone o repositório

```bash
git clone https://gitlab.com/filipepereira1/pos-tech-g57.git
```
2. Certifique-se de ter Docker instalado
3. em application-local.yml crie a variável
```yaml
# Pode ser usado o token válido acima
JWT_TOKEN_PIX_APPLICATION_PAYMENT: SEU_TOKEN_AQUI
```

4. Execute o comando:
   ```bash
   docker-compose-local up
   ```
5. Execute a aplicação com perfil local:
   ```bash
   ./mvnw spring-boot:run -Dspring.profiles.active=local
   ```

## Documentação da API

A documentação da API está disponível via Swagger UI em:
- Local: http://localhost:8080/swagger-ui.html
- Docker: http://localhost:8080/swagger-ui.html

### Principais Endpoints
- Cadastro do Cliente:
   - <code style="color : aqua">POST - /v1/clients</code>
- Identificação do Cliente via CPF:
   - <code style="color : aqua">GET - /v1/clients/{cpf}</code>
- Criar, editar e remover products:
   - <code style="color : aqua">POST - /v1/products</code>
   - <code style="color : aqua">PUT - /v1/products/{id}</code>
   - <code style="color : aqua">DELETE - /v1/products/{id}</code>
- Buscar produtos por categoria:
   - <code style="color : aqua">GET - /v1/products/categories/{categoryName}</code>
- Fake checkout, apenas enviar os produtos escolhidos para a fila. O checkout é a finalização do pedido.
   - <code style="color : aqua">POST - /v1/payments/notification</code>
- Listar os pedidos
   - <code style="color : aqua">GET - /v1/orders</code>

## Colaboradores

Agradecemos às seguintes pessoas que contribuíram para este projeto:

- [@filipepereir](https://github.com/filipepereir) - Filipe Pereira - RM362782
- [@fnakata](https://github.com/Nakatasama) - Felipe Nakata - RM364391
- [@forgelucas](https://github.com/forgelucas) - Lucas Forge - RM364441
- [@rachelkozlowsky](https://github.com/rachelkozlowsky) - Rachel Kozlowsky - RM362994
- [@railsonribeiro](https://github.com/railsonribeiro) - Railson Ribeiro - RM362790

## Licença

Esse projeto está sob licença. Veja o arquivo [LICENÇA](documentations/LICENSE.md) para mais detalhes.
