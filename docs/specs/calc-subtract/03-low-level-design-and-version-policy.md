# Step 03 — Low-Level Design and Version Policy

## Requirement-to-Design Traceability

| Requisito aprovado                       | Cobertura no design                                                   |
| ---------------------------------------- | --------------------------------------------------------------------- |
| Criar endpoint de subtração              | Novo método em `CalcResource` com path `POST /api/calc/subtract`          |
| Seguir padrão da soma                    | Mesmo base path `/api/calc`, JSON, `BigDecimal`, records, Bean Validation |
| Entrada com `firstOperand` e `secondOperand` | Novo DTO `SubtractRequest` com ambos os campos                          |
| Operação `firstOperand - secondOperand`    | Novo método `CalcService.subtract(firstOperand, secondOperand)`         |
| Resposta com `difference`                  | Novo DTO `SubtractResponse(BigDecimal difference)`                      |
| Inválidos retornam `400`                   | `@Valid` no recurso e `@NotNull` nos campos                               |
| OpenAPI deve listar endpoint             | Endpoint Quarkus REST normal, coberto por teste em `/q/openapi`         |
| Soma permanece inalterada                | Mudança aditiva; testes de regressão preservam `/api/calc/sum`          |

## API Contracts

### Endpoint

```http
POST /api/calc/subtract
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
  "difference": 4.50
}
```

### Operação

```text
difference = firstOperand - secondOperand
```

## Data Models

### `SubtractRequest`

Local previsto:

```text
src/main/java/org/soujava/demo/SubtractRequest.java
```

Modelo:

```java
public record SubtractRequest(
    @NotNull BigDecimal firstOperand,
    @NotNull BigDecimal secondOperand
) {
}
```

### `SubtractResponse`

Local previsto:

```text
src/main/java/org/soujava/demo/SubtractResponse.java
```

Modelo:

```java
public record SubtractResponse(BigDecimal difference) {
}
```

### `CalcService`

Novo método previsto:

```java
BigDecimal subtract(BigDecimal firstOperand, BigDecimal secondOperand)
```

Comportamento:

```java
return firstOperand.subtract(secondOperand);
```

### `CalcResource`

Novo método previsto:

```java
@POST
@Path("/subtract")
public SubtractResponse subtract(@Valid SubtractRequest request)
```

Responsabilidade:

- receber request validado;
- delegar para `CalcService`;
- retornar `SubtractResponse`.

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
- regressão do endpoint de soma existente.

## Test Scenario Catalog

| ID  | Cenário                   | Entrada                           | Esperado                        |
| --- | ------------------------- | --------------------------------- | ------------------------------- |
| T1  | Subtração válida          | `10.25 - 5.75`                      | `200`, `difference = 4.50`          |
| T2  | Primeiro operando ausente | apenas `secondOperand`              | `400`                             |
| T3  | Segundo operando ausente  | apenas `firstOperand`               | `400`                             |
| T4  | Primeiro operando nulo    | `firstOperand: null`                | `400`                             |
| T5  | Segundo operando nulo     | `secondOperand: null`               | `400`                             |
| T6  | Tipo inválido             | operando como string não numérica | `400`                             |
| T7  | OpenAPI                   | consultar `/q/openapi`              | contém `/api/calc/subtract`       |
| T8  | Regressão soma            | chamar `/api/calc/sum`              | continua retornando `sum` correto |

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
- manutenção: baixo impacto, padrão idêntico ao endpoint de soma.

## Ordered Implementation Plan

Para Step 04:

1. Adicionar testes falhando para `POST /api/calc/subtract`.
2. Cobrir sucesso com `difference`.
3. Cobrir campos ausentes/nulos/tipo inválido.
4. Cobrir OpenAPI.
5. Garantir regressão da soma.

Para Step 05:

1. Criar `SubtractRequest`.
2. Criar `SubtractResponse`.
3. Adicionar `CalcService.subtract`.
4. Adicionar endpoint `CalcResource.subtract`.
5. Executar testes.
6. Ajustar somente o mínimo necessário para passar.
