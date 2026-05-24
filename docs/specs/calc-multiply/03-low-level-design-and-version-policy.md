# Step 03 — Low-Level Design and Version Policy

## Requirement-to-Design Traceability

| Requisito aprovado | Cobertura no design |
| --- | --- |
| Criar endpoint de multiplicação | Novo método em `CalcResource` com path `POST /api/calc/multiply` |
| Seguir padrão atual da calculadora | Mesmo base path `/api/calc`, JSON, `BigDecimal`, records e Bean Validation |
| Entrada com `multiplier` e `multiplicand` | Novo DTO `MultiplyRequest` com ambos os campos aprovados |
| Operação `multiplier * multiplicand` | Novo método `CalcService.multiply(multiplier, multiplicand)` |
| Resposta com `product` | Novo DTO `MultiplyResponse(BigDecimal product)` |
| Inválidos retornam `400 Bad Request` | `@Valid` no recurso, `@NotNull` nos campos e desserialização padrão |
| OpenAPI deve listar endpoint | Endpoint Quarkus REST normal, coberto por teste em `/q/openapi` |

Decisões da Step 02 que restringem Step 04 e Step 05:

- Os testes devem usar `multiplier` e `multiplicand`, não nomes genéricos de outros endpoints.
- A implementação deve retornar `product`, não `result`.
- A implementação deve usar `BigDecimal.multiply` sem escala fixa, arredondamento ou formatação customizada.
- O tratamento de erro deve depender do comportamento padrão do Quarkus/Jackson/Bean Validation.
- Nenhuma dependência nova deve ser introduzida.

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
  "multiplier": 10.25,
  "multiplicand": 5.75
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
product = multiplier * multiplicand
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
    @NotNull BigDecimal multiplier,
    @NotNull BigDecimal multiplicand
) {
}
```

Cobertura de requisitos:

- cobre entrada JSON com `multiplier` e `multiplicand`;
- cobre obrigatoriedade dos dois operandos via `@NotNull`;
- mantém números decimais com `BigDecimal`.

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

Cobertura de requisitos:

- cobre resposta JSON com campo `product`.

### `CalcService`

Novo método previsto:

```java
BigDecimal multiply(BigDecimal multiplier, BigDecimal multiplicand)
```

Comportamento:

```java
return multiplier.multiply(multiplicand);
```

Cobertura de requisitos:

- cobre operação matemática `multiplier * multiplicand`;
- mantém comportamento decimal compatível com `BigDecimal`;
- não introduz arredondamento ou escala fixa.

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

Cobertura de requisitos:

- cobre endpoint `POST /api/calc/multiply`;
- cobre integração com Bean Validation;
- cobre exposição automática no OpenAPI.

## Error Model

Sem envelope customizado.

Erros esperados:

| Caso | Resultado |
| --- | --- |
| `multiplier` ausente | `400 Bad Request` |
| `multiplicand` ausente | `400 Bad Request` |
| qualquer operando `null` | `400 Bad Request` |
| tipo incompatível, ex. string | `400 Bad Request` |
| JSON malformado | `400 Bad Request` padrão |

A implementação deve depender do comportamento existente de Quarkus/Jackson/Bean Validation.

## Test Strategy

Os testes devem ser escritos antes da implementação na Step 04, seguindo o estilo de `CalcResourceTest`.

Cobertura planejada conforme acceptance criteria da Step 01:

- sucesso com decimais;
- `multiplier` ausente;
- `multiplicand` ausente;
- operando nulo;
- tipo inválido;
- presença em `/q/openapi`.

## Test Scenario Catalog

| ID | Cenário | Entrada | Esperado |
| --- | --- | --- | --- |
| T1 | Multiplicação válida | `10.25 * 5.75` como `multiplier` e `multiplicand` | `200`, `product = 58.9375` |
| T2 | Primeiro operando ausente | apenas `multiplicand` | `400` |
| T3 | Segundo operando ausente | apenas `multiplier` | `400` |
| T4 | Primeiro operando nulo | `multiplier: null` | `400` |
| T5 | Segundo operando nulo | `multiplicand: null` | `400` |
| T6 | Tipo inválido | operando como string não numérica | `400` |
| T7 | OpenAPI | consultar `/q/openapi` | contém `/api/calc/multiply` |

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
- testes: novos cenários para a multiplicação;
- manutenção: baixo impacto, seguindo padrão já existente de recurso, serviço e DTOs.

## Ordered Implementation Plan

Para Step 04:

1. Adicionar testes falhando para `POST /api/calc/multiply`.
2. Cobrir sucesso com `product`.
3. Cobrir `multiplier` ausente.
4. Cobrir `multiplicand` ausente.
5. Cobrir operando nulo.
6. Cobrir tipo inválido.
7. Cobrir OpenAPI.

Para Step 05:

1. Criar `MultiplyRequest`.
2. Criar `MultiplyResponse`.
3. Adicionar `CalcService.multiply`.
4. Adicionar endpoint `CalcResource.multiply`.
5. Executar `./mvnw test`.
6. Ajustar somente o mínimo necessário para passar.
