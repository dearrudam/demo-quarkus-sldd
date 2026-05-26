# Step 03 - Low-Level Design and Version Policy

## Requirement-to-Design Traceability

| Requisito aprovado | Cobertura no design |
| --- | --- |
| Criar endpoint de divisao | Novo metodo em `CalcResource` com path `POST /api/calc/divide` |
| Seguir padrao atual da calculadora e `calc-multiply` | Mesmo base path `/api/calc`, JSON, `BigDecimal`, records e Bean Validation |
| Entrada com `dividend` e `divisor` | Novo DTO `DivideRequest` com ambos os campos aprovados |
| Operacao `dividend / divisor` | Novo metodo `CalcService.divide(dividend, divisor)` |
| Resposta com `quotient` | Novo DTO `DivideResponse(BigDecimal quotient)` |
| Invalidos retornam `400 Bad Request` | `@Valid` no recurso, `@NotNull` nos campos e desserializacao padrao |
| `divisor` zero retorna `400 Bad Request` | Validacao explicita antes de chamar `CalcService.divide` |
| OpenAPI deve listar endpoint | Endpoint Quarkus REST normal, coberto por teste em `/q/openapi` |
| Arredondamento fora de escopo | Testes de sucesso devem usar divisao exata; implementacao nao deve adicionar escala fixa ou rounding |

Decisoes da Step 02 que restringem Step 04 e Step 05:

- Os testes devem usar `dividend` e `divisor`, nao nomes genericos de outros endpoints.
- A implementacao deve retornar `quotient`, nao `result`.
- A implementacao deve usar `BigDecimal` e nao `double`.
- A implementacao deve rejeitar `divisor` zero com `400 Bad Request`.
- O tratamento de erro deve depender do comportamento padrao do Quarkus/Jackson/Bean Validation sempre que possivel.
- Nenhuma dependencia nova deve ser introduzida.
- Nao deve haver regra de arredondamento, escala fixa ou formatacao para divisoes nao exatas.

## API Contracts

### Endpoint

```http
POST /api/calc/divide
Content-Type: application/json
Accept: application/json
```

### Request valido

```json
{
  "dividend": 10.25,
  "divisor": 2.5
}
```

### Response de sucesso

```http
HTTP/1.1 200 OK
Content-Type: application/json
```

```json
{
  "quotient": 4.1
}
```

### Operacao

```text
quotient = dividend / divisor
```

### Restricoes

- `divisor` deve ser diferente de zero.
- Divisoes nao exatas nao recebem politica de arredondamento nesta feature.
- Casos de sucesso da Step 04 devem usar operandos com resultado decimal exato.

## Data Models

### `DivideRequest`

Local previsto:

```text
src/main/java/org/soujava/demo/DivideRequest.java
```

Modelo:

```java
public record DivideRequest(
    @NotNull BigDecimal dividend,
    @NotNull BigDecimal divisor
) {
}
```

Cobertura de requisitos:

- cobre entrada JSON com `dividend` e `divisor`;
- cobre obrigatoriedade dos dois operandos via `@NotNull`;
- mantem numeros decimais com `BigDecimal`.

### `DivideResponse`

Local previsto:

```text
src/main/java/org/soujava/demo/DivideResponse.java
```

Modelo:

```java
public record DivideResponse(BigDecimal quotient) {
}
```

Cobertura de requisitos:

- cobre resposta JSON com campo `quotient`.

### `CalcService`

Novo metodo previsto:

```java
BigDecimal divide(BigDecimal dividend, BigDecimal divisor)
```

Comportamento:

```java
return dividend.divide(divisor);
```

Cobertura de requisitos:

- cobre operacao matematica `dividend / divisor`;
- mantem comportamento decimal compativel com `BigDecimal`;
- nao introduz arredondamento ou escala fixa.

### `CalcResource`

Novo metodo previsto:

```java
@POST
@Path("/divide")
public DivideResponse divide(@Valid DivideRequest request)
```

Responsabilidade:

- receber request validado;
- rejeitar `divisor` zero com `400 Bad Request`;
- delegar para `CalcService`;
- retornar `DivideResponse`.

Validacao planejada para divisor zero:

```java
if (BigDecimal.ZERO.compareTo(request.divisor()) == 0) {
    throw new BadRequestException();
}
```

Cobertura de requisitos:

- cobre endpoint `POST /api/calc/divide`;
- cobre integracao com Bean Validation;
- cobre regra explicita de `divisor` zero;
- cobre exposicao automatica no OpenAPI.

## Error Model

Sem envelope customizado.

Erros esperados:

| Caso | Resultado |
| --- | --- |
| `dividend` ausente | `400 Bad Request` |
| `divisor` ausente | `400 Bad Request` |
| qualquer operando `null` | `400 Bad Request` |
| tipo incompativel, ex. string | `400 Bad Request` |
| JSON malformado | `400 Bad Request` padrao |
| `divisor` zero | `400 Bad Request` |

A implementacao deve depender do comportamento existente de Quarkus/Jackson/Bean Validation para erros estruturais e usar `BadRequestException` para `divisor` zero.

Divisoes nao exatas estao fora de escopo. A Step 04 nao deve introduzir expectativa de arredondamento ou escala fixa.

## Test Strategy

Os testes devem ser escritos antes da implementacao na Step 04, seguindo o estilo de `CalcResourceTest`.

Cobertura planejada conforme acceptance criteria da Step 01:

- sucesso com decimais de divisao exata;
- `dividend` ausente;
- `divisor` ausente;
- operando nulo;
- tipo invalido;
- `divisor` zero;
- presenca em `/q/openapi`.

O teste de sucesso deve evitar divisoes nao exatas, por exemplo usar `10.25 / 2.5 = 4.1`.

## Test Scenario Catalog

| ID | Cenario | Entrada | Esperado |
| --- | --- | --- | --- |
| T1 | Divisao valida | `10.25 / 2.5` como `dividend` e `divisor` | `200`, `quotient = 4.1` |
| T2 | Primeiro operando ausente | apenas `divisor` | `400` |
| T3 | Segundo operando ausente | apenas `dividend` | `400` |
| T4 | Primeiro operando nulo | `dividend: null` | `400` |
| T5 | Segundo operando nulo | `divisor: null` | `400` |
| T6 | Tipo invalido | operando como string nao numerica | `400` |
| T7 | Divisor zero | `divisor: 0` | `400` |
| T8 | OpenAPI | consultar `/q/openapi` | contem `/api/calc/divide` |

## Dependency and Version Policy

Dependencias atuais sao suficientes.

Nenhuma nova dependencia deve ser adicionada.

Dependencias ja utilizadas pelo projeto e relevantes:

- Quarkus REST;
- Jackson via Quarkus REST Jackson;
- Hibernate Validator;
- SmallRye OpenAPI;
- Quarkus JUnit;
- REST Assured.

Politica:

- nao pinar versoes diretas de artefatos Quarkus;
- manter gerenciamento por BOM existente;
- nao alterar versao do Java;
- nao alterar versao do Quarkus;
- nao introduzir bibliotecas matematicas externas;
- nao introduzir biblioteca de erro ou envelope customizado.

Impacto:

- runtime: novo endpoint REST aditivo;
- testes: novos cenarios para divisao;
- manutencao: baixo impacto, seguindo padrao ja existente de recurso, servico e DTOs.

## Ordered Implementation Plan

Para Step 04:

1. Adicionar testes falhando para `POST /api/calc/divide`.
2. Cobrir sucesso com `quotient` usando divisao exata.
3. Cobrir `dividend` ausente.
4. Cobrir `divisor` ausente.
5. Cobrir operando nulo.
6. Cobrir tipo invalido.
7. Cobrir `divisor` zero.
8. Cobrir OpenAPI.

Para Step 05:

1. Criar `DivideRequest`.
2. Criar `DivideResponse`.
3. Adicionar `CalcService.divide`.
4. Adicionar endpoint `CalcResource.divide` com validacao de `divisor` zero.
5. Executar `./mvnw test`.
6. Ajustar somente o minimo necessario para passar.
