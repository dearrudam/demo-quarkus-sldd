# Step 02 — High-Level Technical Design

## Requirements Traceability

| Requisito da Step 01                                 | Decisão de design                                                                       |
| ---------------------------------------------------- | --------------------------------------------------------------------------------------- |
| Expor operação de multiplicação na API de calculadora | Adicionar novo endpoint `POST /api/calc/multiply` em `CalcResource`                         |
| Seguir padrão dos endpoints existentes               | Reutilizar base path `/api/calc`, JSON, `BigDecimal`, DTOs como records e validação Jakarta |
| Entrada com `firstOperand` e `secondOperand`             | Criar request record com os mesmos nomes de campos                                      |
| Operação `firstOperand * secondOperand`                | Adicionar método de serviço que chama `firstOperand.multiply(secondOperand)`             |
| Resposta com campo `product`                           | Criar response record com campo `product`                                                |
| Payload inválido retorna `400 Bad Request`             | Usar `@Valid` no recurso e `@NotNull` nos campos do request                                 |
| Endpoint aparece no OpenAPI                          | Usar recurso JAX-RS/Quarkus REST normal para integração automática com SmallRye OpenAPI |
| Soma e subtração permanecem inalteradas              | Fazer extensão aditiva, sem alterar contratos existentes                                |

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
  | valid firstOperand + secondOperand
  v
CalcService
  |
  | multiply(firstOperand, secondOperand)
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

- conter regra matemática complexa além da orquestração;
- criar envelope customizado de erro;
- alterar os endpoints de soma ou subtração.

### `CalcService`

Responsável por:

- concentrar a operação matemática;
- adicionar operação de multiplicação;
- preservar as operações existentes.

Operação planejada:

```text
multiply(firstOperand, secondOperand) = firstOperand * secondOperand
```

### `MultiplyRequest`

Responsável por representar o payload de entrada.

Campos:

- `firstOperand`
- `secondOperand`

Ambos obrigatórios e numéricos, seguindo o padrão atual com `BigDecimal` e `@NotNull`.

### `MultiplyResponse`

Responsável por representar o payload de saída.

Campo:

- `product`

### Testes

Responsáveis por validar:

- sucesso da multiplicação;
- payload com campo ausente;
- payload com campo nulo;
- payload com tipo inválido;
- presença do endpoint no OpenAPI;
- ausência de regressão nos endpoints de soma e subtração.

## Data Flow

### Caso de sucesso

```text
1. Cliente envia POST /api/calc/multiply.
2. Corpo JSON contém firstOperand e secondOperand.
3. Quarkus desserializa JSON em MultiplyRequest.
4. Bean Validation valida campos obrigatórios.
5. CalcResource chama CalcService.multiply(firstOperand, secondOperand).
6. CalcService calcula firstOperand.multiply(secondOperand).
7. CalcResource encapsula resultado em MultiplyResponse.
8. API retorna 200 OK com JSON contendo product.
```

Exemplo:

```json
{
  "firstOperand": 10.25,
  "secondOperand": 5.75
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

- segue o padrão existente de `/api/calc/sum` e `/api/calc/subtract`;
- contrato simples;
- fácil de testar;
- baixa chance de regressão.

### Alternativa rejeitada — endpoint genérico de operação

Exemplo:

```text
POST /api/calc
{
  "operation": "multiply",
  "firstOperand": 10,
  "secondOperand": 3
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

- a soma existente usa campo semântico `sum`;
- a subtração existente usa campo semântico `difference`;
- `product` mantém resposta semântica por operação.

### Alternativa rejeitada — usar `double`

Motivo para rejeição:

- o projeto já usa `BigDecimal` nas operações existentes;
- `BigDecimal` preserva consistência e evita mudança de precisão.

### Alternativa rejeitada — definir escala fixa

Motivo para rejeição:

- os endpoints existentes não aplicam escala fixa;
- introduzir arredondamento nesta feature criaria comportamento inconsistente sem requisito de produto.

## High-Level Test Scenario Map

| Cenário                                  | Tipo                      | Resultado esperado                |
| ---------------------------------------- | ------------------------- | --------------------------------- |
| Multiplicação válida com decimais        | Sucesso                   | `200 OK` com `product` correto      |
| `firstOperand` ausente                     | Validação                 | `400 Bad Request`                   |
| `secondOperand` ausente                    | Validação                 | `400 Bad Request`                   |
| Operando nulo                            | Validação                 | `400 Bad Request`                   |
| Operando com tipo inválido               | Desserialização/validação | `400 Bad Request`                   |
| OpenAPI lista `/api/calc/multiply`        | Contrato                  | documentação contém novo endpoint |
| Regressão de soma e subtração existentes | Regressão                 | endpoints existentes seguem OK    |
