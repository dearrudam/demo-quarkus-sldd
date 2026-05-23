# Step 03 — Low-Level Design and Version Policy

## Requirement-to-Design Traceability

| Requisito aprovado                          | Cobertura no design                                                   |
| ------------------------------------------- | --------------------------------------------------------------------- |
| Criar endpoint de multiplicação             | Novo método em `CalcResource` com path `POST /api/calc/multiply`          |
| Seguir padrão das operações existentes      | Mesmo base path `/api/calc`, JSON, `BigDecimal`, records, Bean Validation |
| Entrada com `firstOperand` e `secondOperand`    | Novo DTO `MultiplyRequest` com ambos os campos                          |
| Operação `firstOperand * secondOperand`       | Novo método `CalcService.multiply(firstOperand, secondOperand)`         |
| Resposta com `product`                        | Novo DTO `MultiplyResponse(BigDecimal product)`                         |
| Inválidos retornam `400`                      | `@Valid` no recurso e `@NotNull` nos campos                               |
| OpenAPI deve listar endpoint                | Endpoint Quarkus REST normal, coberto por teste em `/q/openapi`         |
| Soma e subtração permanecem inalteradas     | Mudança aditiva; testes de regressão preservam endpoints existentes     |

## API Contracts

### Endpoint

```http
POST /api/calc/multiply
Content-Type: application/json
Accept: application/json
```

### Request válido

```json
{
  "firstOperand": 10.25,
  "secondOperand": 5.75
}
```

### Response de sucesso

```http
HTTP/1.1 200 OK
Content-Type: application/json
```

```json
{
  "product": 58.9375
}
```

### Operação

```text
product = firstOperand * secondOperand
```

## Data Models

### `MultiplyRequest`

Local previsto:

```text
src/main/java/org/soujava/demo/MultiplyRequest.java
```

Modelo:

```java
public record MultiplyRequest(
    @NotNull BigDecimal firstOperand,
    @NotNull BigDecimal secondOperand
) {
}
```

### `MultiplyResponse`

Local previsto:

```text
src/main/java/org/soujava/demo/MultiplyResponse.java
```

Modelo:

```java
public record MultiplyResponse(BigDecimal product) {
}
```

### `CalcService`

Novo método previsto:

```java
BigDecimal multiply(BigDecimal firstOperand, BigDecimal secondOperand)
```

Comportamento:

```java
return firstOperand.multiply(secondOperand);
```

### `CalcResource`

Novo método previsto:

```java
@POST
@Path("/multiply")
public MultiplyResponse multiply(@Valid MultiplyRequest request)
```

Responsabilidade:

- receber request validado;
- delegar para `CalcService`;
- retornar `MultiplyResponse`.

## Error Model

Sem envelope customizado.

Erros esperados:

| Caso                          | Resultado              |
| ----------------------------- | ---------------------- |
| `firstOperand` ausente          | `400 Bad Request`        |
| `secondOperand` ausente         | `400 Bad Request`        |
| qualquer operando `null`        | `400 Bad Request`        |
| tipo incompatível, ex. string | `400 Bad Request`        |
| JSON malformado               | `400 Bad Request` padrão |

A implementação deve depender do comportamento existente de Quarkus/Jackson/Bean Validation.

## Test Strategy

Os testes devem ser escritos antes da implementação na Step 04, seguindo `CalcResourceTest`.

Cobertura planejada:

- sucesso com decimais;
- `firstOperand` ausente;
- `secondOperand` ausente;
- operando nulo;
- tipo inválido;
- presença em `/q/openapi`;
- regressão dos endpoints de soma e subtração existentes.

## Test Scenario Catalog

| ID  | Cenário                       | Entrada                           | Esperado                                  |
| --- | ----------------------------- | --------------------------------- | ----------------------------------------- |
| T1  | Multiplicação válida          | `10.25 * 5.75`                      | `200`, `product = 58.9375`                  |
| T2  | Primeiro operando ausente     | apenas `secondOperand`              | `400`                                       |
| T3  | Segundo operando ausente      | apenas `firstOperand`               | `400`                                       |
| T4  | Primeiro operando nulo        | `firstOperand: null`                | `400`                                       |
| T5  | Segundo operando nulo         | `secondOperand: null`               | `400`                                       |
| T6  | Tipo inválido                 | operando como string não numérica | `400`                                       |
| T7  | OpenAPI                       | consultar `/q/openapi`              | contém `/api/calc/multiply`                 |
| T8  | Regressão soma                | chamar `/api/calc/sum`              | continua retornando `sum` correto           |
| T9  | Regressão subtração           | chamar `/api/calc/subtract`         | continua retornando `difference` correto    |

## Dependency and Version Policy

Dependências atuais são suficientes.

Nenhuma nova dependência deve ser adicionada.

Dependências já utilizadas pelo projeto e relevantes:

- Quarkus REST;
- Jackson via Quarkus REST Jackson;
- Hibernate Validator;
- SmallRye OpenAPI;
- Quarkus JUnit;
- REST Assured.

Política:

- não pinçar versões diretas de artefatos Quarkus;
- manter gerenciamento por BOM existente;
- não alterar versão do Java;
- não alterar versão do Quarkus;
- não introduzir bibliotecas matemáticas externas.

Impacto:

- runtime: novo endpoint REST aditivo;
- testes: novos cenários em suíte existente;
- manutenção: baixo impacto, padrão idêntico aos endpoints existentes.

## Ordered Implementation Plan

Para Step 04:

1. Adicionar testes falhando para `POST /api/calc/multiply`.
2. Cobrir sucesso com `product`.
3. Cobrir campos ausentes/nulos/tipo inválido.
4. Cobrir OpenAPI.
5. Garantir regressão da soma e da subtração.

Para Step 05:

1. Criar `MultiplyRequest`.
2. Criar `MultiplyResponse`.
3. Adicionar `CalcService.multiply`.
4. Adicionar endpoint `CalcResource.multiply`.
5. Executar testes.
6. Ajustar somente o mínimo necessário para passar.
