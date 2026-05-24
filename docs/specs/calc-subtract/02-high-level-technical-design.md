# Step 02 — High-Level Technical Design

## Requirements Traceability

| Requisito da Step 01                              | Decisão de design                                                                       |
| ------------------------------------------------- | --------------------------------------------------------------------------------------- |
| Expor operação de subtração na API de calculadora | Adicionar novo endpoint `POST /api/calc/subtract` em `CalcResource`                         |
| Seguir padrão do endpoint de soma                 | Reutilizar base path `/api/calc`, JSON, `BigDecimal`, DTOs como records e validação Jakarta |
| Entrada com `firstOperand` e `secondOperand`          | Criar request record com os mesmos nomes de campos                                      |
| Operação `firstOperand - secondOperand`             | Adicionar método de serviço que chama `firstOperand.subtract(secondOperand)`              |
| Resposta com campo `difference`                     | Criar response record com campo `difference`                                              |
| Payload inválido retorna `400 Bad Request`          | Usar `@Valid` no recurso e `@NotNull` nos campos do request                                 |
| Endpoint aparece no OpenAPI                       | Usar recurso JAX-RS/Quarkus REST normal para integração automática com SmallRye OpenAPI |
| Soma permanece inalterada                         | Fazer extensão aditiva, sem alterar contrato de `POST /api/calc/sum`                      |

## Architecture Diagram

```text
HTTP Client
  |
  | POST /api/calc/subtract
  | Content-Type: application/json
  v
CalcResource
  |
  | @Valid SubtractRequest
  v
Bean Validation
  |
  | valid firstOperand + secondOperand
  v
CalcService
  |
  | subtract(firstOperand, secondOperand)
  v
BigDecimal subtraction
  |
  | result
  v
SubtractResponse
  |
  | JSON { "difference": ... }
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
- receber `SubtractRequest`;
- acionar validação via `@Valid`;
- delegar o cálculo para `CalcService`;
- retornar `SubtractResponse`.

Não deve:

- conter regra matemática complexa além da orquestração;
- criar envelope customizado de erro;
- alterar o endpoint de soma.

### `CalcService`

Responsável por:

- concentrar a operação matemática;
- adicionar operação de subtração;
- preservar a operação de soma existente.

Operação planejada:

```text
subtract(firstOperand, secondOperand) = firstOperand - secondOperand
```

### `SubtractRequest`

Responsável por representar o payload de entrada.

Campos:

- `firstOperand`
- `secondOperand`

Ambos obrigatórios e numéricos, seguindo o padrão atual com `BigDecimal` e `@NotNull`.

### `SubtractResponse`

Responsável por representar o payload de saída.

Campo:

- `difference`

### Testes

Responsáveis por validar:

- sucesso da subtração;
- payload com campo ausente;
- payload com campo nulo;
- payload com tipo inválido;
- presença do endpoint no OpenAPI;
- ausência de regressão no endpoint de soma.

## Data Flow

### Caso de sucesso

```text
1. Cliente envia POST /api/calc/subtract.
2. Corpo JSON contém firstOperand e secondOperand.
3. Quarkus desserializa JSON em SubtractRequest.
4. Bean Validation valida campos obrigatórios.
5. CalcResource chama CalcService.subtract(firstOperand, secondOperand).
6. CalcService calcula firstOperand.subtract(secondOperand).
7. CalcResource encapsula resultado em SubtractResponse.
8. API retorna 200 OK com JSON contendo difference.
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
  "difference": 4.50
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
- O endpoint segue o mesmo modelo público do endpoint de soma existente.
- A entrada é limitada ao contrato JSON com dois operandos.
- A validação obrigatória reduz risco de chamada com valores ausentes ou nulos.
- Não deve haver execução dinâmica, parsing manual inseguro ou lógica dependente de strings para cálculo.

### Observability

- Não há requisito de logging customizado para esta feature.
- O endpoint deve ser visível via OpenAPI.
- Erros de validação devem seguir o comportamento padrão do Quarkus, sem novo formato de observabilidade ou erro.

## Trade-Offs and Alternatives

### Alternativa escolhida — endpoint dedicado `/subtract`

```text
POST /api/calc/subtract
```

Vantagens:

- segue o padrão existente de `/api/calc/sum`;
- contrato simples;
- fácil de testar;
- baixa chance de regressão.

### Alternativa rejeitada — endpoint genérico de operação

Exemplo:

```text
POST /api/calc
{
  "operation": "subtract",
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
  "result": 7
}
```

Motivo para rejeição:

- a soma existente usa campo semântico `sum`;
- `difference` mantém resposta semântica por operação.

### Alternativa rejeitada — usar `double`

Motivo para rejeição:

- o projeto já usa `BigDecimal` na soma;
- `BigDecimal` preserva consistência e evita mudança de precisão.

## High-Level Test Scenario Map

| Cenário                               | Tipo                      | Resultado esperado                 |
| ------------------------------------- | ------------------------- | ---------------------------------- |
| Subtração válida com decimais         | Sucesso                   | `200 OK` com `difference` correto      |
| `firstOperand` ausente                  | Validação                 | `400 Bad Request`                    |
| `secondOperand` ausente                 | Validação                 | `400 Bad Request`                    |
| Operando nulo                         | Validação                 | `400 Bad Request`                    |
| Operando com tipo inválido            | Desserialização/validação | `400 Bad Request`                    |
| OpenAPI consultado                    | Documentação              | `/api/calc/subtract` presente        |
| Endpoint de soma chamado após mudança | Regressão                 | `/api/calc/sum` continua funcionando |
