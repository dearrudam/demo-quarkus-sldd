# Step 02 — High-Level Technical Design

## Requirements Traceability

| Requisito da Step 01 | Decisão de design |
| --- | --- |
| Expor operação de multiplicação na API de calculadora | Adicionar novo endpoint `POST /api/calc/multiply` em `CalcResource` |
| Seguir padrão atual de contrato, validação e comportamento | Reutilizar base path `/api/calc`, JSON, `BigDecimal`, records e Bean Validation |
| Entrada com `multiplier` e `multiplicand` | Criar request record com os nomes aprovados na Step 01 |
| Operação `multiplier * multiplicand` | Adicionar método de serviço que chama `multiplier.multiply(multiplicand)` |
| Resposta com campo `product` | Criar response record com campo `product` |
| Payload inválido retorna `400 Bad Request` | Usar `@Valid` no recurso e `@NotNull` nos campos do request, além do comportamento padrão de desserialização |
| Endpoint aparece no OpenAPI | Usar recurso JAX-RS/Quarkus REST normal para integração automática com SmallRye OpenAPI |

## Architecture Diagram

```text
HTTP Client
  |
  | POST /api/calc/multiply
  | Content-Type: application/json
  v
CalcResource
  |
  | @Valid MultiplyRequest
  v
Bean Validation
  |
  | valid multiplier + multiplicand
  v
CalcService
  |
  | multiply(multiplier, multiplicand)
  v
BigDecimal multiplication
  |
  | result
  v
MultiplyResponse
  |
  | JSON { "product": ... }
  v
HTTP 200 OK
```

Fluxo de erro:

```text
Invalid JSON / missing field / null operand / incompatible type
  -> Quarkus/Jackson/Bean Validation
  -> HTTP 400 Bad Request
```

## Component Responsibilities

### `CalcResource`

Responsável por:

- expor o novo endpoint REST;
- manter o base path existente `/api/calc`;
- consumir e produzir JSON;
- receber `MultiplyRequest`;
- acionar validação via `@Valid`;
- delegar o cálculo para `CalcService`;
- retornar `MultiplyResponse`.

Não deve:

- conter regra matemática além da orquestração;
- criar envelope customizado de erro;
- alterar a estrutura geral da API de calculadora.

### `CalcService`

Responsável por:

- concentrar a operação matemática;
- adicionar operação de multiplicação;
- usar `BigDecimal.multiply` sem escala fixa ou arredondamento customizado.

Operação planejada:

```text
multiply(multiplier, multiplicand) = multiplier * multiplicand
```

### `MultiplyRequest`

Responsável por representar o payload de entrada aprovado na Step 01.

Campos:

- `multiplier`.
- `multiplicand`.

Ambos obrigatórios e numéricos, usando `BigDecimal` e `@NotNull`.

### `MultiplyResponse`

Responsável por representar o payload de saída.

Campo:

- `product`.

### Testes

Responsáveis por validar os acceptance criteria da Step 01:

- sucesso da multiplicação;
- `multiplier` ausente;
- `multiplicand` ausente;
- operando nulo;
- tipo inválido;
- presença do endpoint no OpenAPI.

## Data Flow

### Caso de sucesso

```text
1. Cliente envia POST /api/calc/multiply.
2. Corpo JSON contém multiplier e multiplicand.
3. Quarkus desserializa JSON em MultiplyRequest.
4. Bean Validation valida campos obrigatórios.
5. CalcResource chama CalcService.multiply(multiplier, multiplicand).
6. CalcService calcula multiplier.multiply(multiplicand).
7. CalcResource encapsula resultado em MultiplyResponse.
8. API retorna 200 OK com JSON contendo product.
```

Exemplo:

```json
{
  "multiplier": 10.25,
  "multiplicand": 5.75
}
```

Resposta:

```json
{
  "product": 58.9375
}
```

### Caso inválido

```text
1. Cliente envia JSON inválido, campo ausente, campo nulo ou tipo incompatível.
2. Desserialização ou Bean Validation falha.
3. Quarkus retorna 400 Bad Request usando comportamento padrão.
4. Nenhum envelope customizado é introduzido.
```

## Security and Observability Requirements

### Security

- Não há novo requisito de autenticação ou autorização.
- O endpoint segue o mesmo modelo público dos endpoints existentes.
- A entrada é limitada ao contrato JSON com dois operandos.
- A validação obrigatória reduz risco de chamada com valores ausentes ou nulos.
- Não deve haver execução dinâmica, parsing manual inseguro ou lógica dependente de strings para cálculo.

### Observability

- Não há requisito de logging customizado para esta feature.
- O endpoint deve ser visível via OpenAPI.
- Erros de validação devem seguir o comportamento padrão do Quarkus, sem novo formato de observabilidade ou erro.

## Trade-Offs and Alternatives

### Alternativa escolhida — endpoint dedicado `/multiply`

```text
POST /api/calc/multiply
```

Vantagens:

- segue o padrão existente de operação dedicada por cálculo;
- contrato simples;
- fácil de testar;
- baixo acoplamento com outras operações.

### Alternativa rejeitada — endpoint genérico de operação

Exemplo:

```text
POST /api/calc
{
  "operation": "multiply",
  "multiplier": 10,
  "multiplicand": 3
}
```

Motivo para rejeição:

- exigiria alterar ou ampliar o modelo atual;
- adicionaria validação de tipo de operação;
- seria mais complexo que o necessário para o escopo da Step 01.

### Alternativa rejeitada — resposta genérica `result`

Exemplo:

```json
{
  "result": 30
}
```

Motivo para rejeição:

- a Step 01 aprova explicitamente o campo `product`.

### Alternativa rejeitada — usar `double`

Motivo para rejeição:

- o projeto já usa `BigDecimal` nas operações existentes;
- `BigDecimal` preserva consistência com o padrão atual de números decimais.

### Alternativa rejeitada — definir escala fixa

Motivo para rejeição:

- a Step 01 coloca escala fixa e formatação específica fora de escopo;
- introduzir arredondamento criaria comportamento não solicitado.

## High-Level Test Scenario Map

| Cenário | Tipo | Resultado esperado |
| --- | --- | --- |
| Multiplicação válida com decimais | Sucesso | `200 OK` com `product` correto |
| `multiplier` ausente | Validação | `400 Bad Request` |
| `multiplicand` ausente | Validação | `400 Bad Request` |
| Operando nulo | Validação | `400 Bad Request` |
| Operando com tipo inválido | Desserialização/validação | `400 Bad Request` |
| OpenAPI lista `/api/calc/multiply` | Contrato | documentação contém novo endpoint |
