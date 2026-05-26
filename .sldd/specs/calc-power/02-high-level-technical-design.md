# Step 02 — High-Level Technical Design

## Requirements Traceability

| Requisito da Step 01 | Decisão de design |
| --- | --- |
| Expor operação de potenciação na API de calculadora | Adicionar novo endpoint `POST /api/calc/power` em `CalcResource` |
| Seguir padrão do endpoint de multiplicação | Reutilizar base path `/api/calc`, JSON, `BigDecimal`, DTOs como records e validação Jakarta |
| Entrada com `base` e `exponent` | Criar request record com os mesmos nomes de campos |
| Operação `base ^ exponent` | Adicionar método de serviço que calcula potenciação |
| Resposta com campo `power` | Criar response record com campo `power` |
| Payload inválido retorna `400 Bad Request` | Usar `@Valid` no recurso e `@NotNull` nos campos do request |
| Endpoint exposto no OpenAPI | Quarkus expõe automaticamente via anotações REST |

## Architecture Diagram

```
HTTP Client
  ↓
POST /api/calc/power (JSON)
  ↓
CalcResource.power(@Valid PowerRequest)
  ├─ Bean Validation: @NotNull base, @NotNull exponent
  ├─ Desserialização JSON
  ↓
CalcService.power(base: BigDecimal, exponent: BigDecimal)
  ├─ Cálculo: base ^ exponent
  ↓
PowerResponse(power: BigDecimal)
  ↓
Serialização JSON
  ↓
HTTP 200 OK (JSON)
```

## Component Responsibilities

### `CalcResource`

Responsável por:

- expor o novo endpoint REST sob `/api/calc/power`;
- manter o base path existente `/api/calc`;
- consumir e produzir JSON;
- receber `PowerRequest`;
- acionar validação via `@Valid`;
- delegar o cálculo para `CalcService`;
- retornar `PowerResponse`.

Não deve:

- conter regra matemática além da orquestração;
- criar envelope customizado de erro;
- alterar a estrutura geral da API de calculadora.

### `CalcService`

Responsável por:

- concentrar a operação matemática;
- adicionar operação de potenciação;
- usar `BigDecimal` para base e expoente.

Operação planejada:

```text
power(base, exponent) = base ^ exponent
```

### DTOs

- `PowerRequest`: record com `@NotNull BigDecimal base`, `@NotNull BigDecimal exponent`
- `PowerResponse`: record com `BigDecimal power`

## Data Flow

### Caso de sucesso

```
1. Cliente envia POST /api/calc/power.
2. Corpo JSON contém base e exponent.
3. Quarkus desserializa JSON em PowerRequest.
4. Bean Validation valida campos obrigatórios.
5. CalcResource chama CalcService.power(base, exponent).
6. CalcService calcula base.pow(exponent) usando BigDecimal.
7. CalcResource encapsula resultado em PowerResponse.
8. API retorna 200 OK com JSON contendo power.
```

Exemplo:

```json
{
  "base": 2,
  "exponent": 3
}
```

Resposta:

```json
{
  "power": 8
}
```

### Caso de erro (Missing field)

```
1. Cliente envia POST /api/calc/power sem campo "base".
2. Quarkus não consegue desserializar para PowerRequest.
3. Bean Validation falha.
4. API retorna 400 Bad Request.
```

## Security and Observability Requirements

- **Validação de entrada**: Bean Validation garante que campos obrigatórios são presentes e não nulos.
- **Tratamento de erro**: Deixar `BadRequestException` ou `ArithmeticException` natural ser lançada se houver problema (overflow, etc).
- **Observabilidade**: Endpoint aparecerá automaticamente em `/q/openapi` para que clientes descubram contrato.

## Trade-Offs and Alternatives

- **BigDecimal vs Double**: Usar `BigDecimal` (como todas as operações) para precisão decimal consistente, mesmo que seja mais lento.
- **Expoente fixo (inteiro)**: Permitir `BigDecimal` para expoente (como base) para máxima flexibilidade, consistente com o padrão; se precisão for problema, documentar em Step 03.

## High-Level Test Scenario Map

| Cenário | Componentes | Validação |
| --- | --- | --- |
| Potência válida (2^3=8) | CalcResource + CalcService | Status 200, power=8 |
| Campo base ausente | CalcResource + Bean Validation | Status 400 |
| Campo exponent ausente | CalcResource + Bean Validation | Status 400 |
| Campo nulo | CalcResource + Bean Validation | Status 400 |
| Tipo inválido (string) | Desserialização JSON | Status 400 |
| OpenAPI contém /api/calc/power | CalcResource + Quarkus | GET /q/openapi retorna path |
