# Step 02 - High-Level Technical Design

## Requirements Traceability

| Requisito da Step 01 | Decisao de design |
| --- | --- |
| Expor operacao de divisao na API de calculadora | Adicionar novo endpoint `POST /api/calc/divide` em `CalcResource` |
| Seguir padrao atual e a spec `calc-multiply` | Reutilizar base path `/api/calc`, JSON, `BigDecimal`, records e Bean Validation |
| Entrada com `dividend` e `divisor` | Criar request record com os nomes aprovados na Step 01 |
| Operacao `dividend / divisor` | Adicionar metodo de servico para divisao |
| Resposta com `quotient` | Criar response record com campo `quotient` |
| Payload invalido retorna `400 Bad Request` | Usar `@Valid`, `@NotNull` e comportamento padrao de desserializacao |
| `divisor` zero retorna `400 Bad Request` | Validar divisor zero antes de executar a divisao |
| Endpoint aparece no OpenAPI | Usar recurso JAX-RS/Quarkus REST normal para integracao automatica com SmallRye OpenAPI |
| Arredondamento de divisoes nao exatas fora de escopo | Nao introduzir escala fixa, arredondamento ou formatacao customizada |

## Architecture Diagram

```text
HTTP Client
  |
  | POST /api/calc/divide
  | Content-Type: application/json
  v
CalcResource
  |
  | @Valid DivideRequest
  v
Bean Validation
  |
  | valid dividend + divisor
  v
Divisor zero guard
  |
  | divisor != 0
  v
CalcService
  |
  | divide(dividend, divisor)
  v
BigDecimal division
  |
  | result
  v
DivideResponse
  |
  | JSON { "quotient": ... }
  v
HTTP 200 OK
```

Fluxo de erro:

```text
Invalid JSON / missing field / null operand / incompatible type
  -> Quarkus/Jackson/Bean Validation
  -> HTTP 400 Bad Request

divisor == 0
  -> application validation guard
  -> HTTP 400 Bad Request without custom error envelope
```

## Component Responsibilities

### `CalcResource`

Responsavel por:

- expor o novo endpoint REST;
- manter o base path existente `/api/calc`;
- consumir e produzir JSON;
- receber `DivideRequest`;
- acionar validacao via `@Valid`;
- garantir que `divisor` zero retorne `400 Bad Request`;
- delegar o calculo para `CalcService`;
- retornar `DivideResponse`.

Nao deve:

- conter regra matematica alem da orquestracao e validacao HTTP;
- criar envelope customizado de erro;
- alterar a estrutura geral da API de calculadora.

### `CalcService`

Responsavel por:

- concentrar a operacao matematica;
- adicionar operacao de divisao;
- usar `BigDecimal`;
- nao introduzir escala fixa, arredondamento ou formatacao customizada.

Operacao planejada:

```text
divide(dividend, divisor) = dividend / divisor
```

### `DivideRequest`

Responsavel por representar o payload de entrada aprovado na Step 01.

Campos:

- `dividend`.
- `divisor`.

Ambos obrigatorios e numericos, usando `BigDecimal` e `@NotNull`.

### `DivideResponse`

Responsavel por representar o payload de saida.

Campo:

- `quotient`.

### Testes

Responsaveis por validar os acceptance criteria da Step 01:

- sucesso da divisao;
- `dividend` ausente;
- `divisor` ausente;
- operando nulo;
- tipo invalido;
- `divisor` zero;
- presenca do endpoint no OpenAPI.

## Data Flow

### Caso de sucesso

```text
1. Cliente envia POST /api/calc/divide.
2. Corpo JSON contem dividend e divisor.
3. Quarkus desserializa JSON em DivideRequest.
4. Bean Validation valida campos obrigatorios.
5. CalcResource valida que divisor nao e zero.
6. CalcResource chama CalcService.divide(dividend, divisor).
7. CalcService calcula dividend.divide(divisor).
8. CalcResource encapsula resultado em DivideResponse.
9. API retorna 200 OK com JSON contendo quotient.
```

Exemplo:

```json
{
  "dividend": 10.25,
  "divisor": 2.5
}
```

Resposta:

```json
{
  "quotient": 4.1
}
```

### Caso invalido

```text
1. Cliente envia JSON invalido, campo ausente, campo nulo ou tipo incompativel.
2. Desserializacao ou Bean Validation falha.
3. Quarkus retorna 400 Bad Request usando comportamento padrao.
4. Nenhum envelope customizado e introduzido.
```

### Divisor zero

```text
1. Cliente envia divisor igual a zero.
2. Request e desserializado e validado como numero presente.
3. CalcResource detecta divisor zero antes da divisao.
4. API retorna 400 Bad Request sem envelope customizado.
```

## Security and Observability Requirements

### Security

- Nao ha novo requisito de autenticacao ou autorizacao.
- O endpoint segue o mesmo modelo publico dos endpoints existentes.
- A entrada e limitada ao contrato JSON com dois operandos.
- A validacao obrigatoria reduz risco de chamada com valores ausentes ou nulos.
- A validacao de divisor zero evita execucao invalida da operacao.
- Nao deve haver execucao dinamica, parsing manual inseguro ou logica dependente de strings para calculo.

### Observability

- Nao ha requisito de logging customizado para esta feature.
- O endpoint deve ser visivel via OpenAPI.
- Erros de validacao devem seguir o comportamento padrao do Quarkus, sem novo formato de observabilidade ou erro.

## Trade-Offs and Alternatives

### Alternativa escolhida - endpoint dedicado `/divide`

```text
POST /api/calc/divide
```

Vantagens:

- segue o padrao existente de operacao dedicada por calculo;
- contrato simples;
- facil de testar;
- baixo acoplamento com outras operacoes.

### Alternativa rejeitada - endpoint generico de operacao

Exemplo:

```text
POST /api/calc
{
  "operation": "divide",
  "dividend": 10,
  "divisor": 2
}
```

Motivo para rejeicao:

- exigiria alterar ou ampliar o modelo atual;
- adicionaria validacao de tipo de operacao;
- seria mais complexo que o necessario para o escopo da Step 01.

### Alternativa rejeitada - resposta generica `result`

Exemplo:

```json
{
  "result": 5
}
```

Motivo para rejeicao:

- a Step 01 aprova explicitamente o campo `quotient`.

### Alternativa rejeitada - usar `double`

Motivo para rejeicao:

- o projeto ja usa `BigDecimal` nas operacoes existentes;
- `BigDecimal` preserva consistencia com o padrao atual de numeros decimais.

### Alternativa rejeitada - definir escala fixa ou arredondamento

Motivo para rejeicao:

- a Step 01 coloca arredondamento, escala fixa e formatacao especifica fora de escopo;
- introduzir arredondamento criaria comportamento nao solicitado.

## High-Level Test Scenario Map

| Cenario | Tipo | Resultado esperado |
| --- | --- | --- |
| Divisao valida com decimais exatos | Sucesso | `200 OK` com `quotient` correto |
| `dividend` ausente | Validacao | `400 Bad Request` |
| `divisor` ausente | Validacao | `400 Bad Request` |
| Operando com `null` | Validacao | `400 Bad Request` |
| Operando com tipo invalido | Desserializacao/validacao | `400 Bad Request` |
| `divisor` zero | Regra de dominio HTTP | `400 Bad Request` |
| OpenAPI lista `/api/calc/divide` | Contrato | documentacao contem novo endpoint |
