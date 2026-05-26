# Step 01 — Product Intent Specification

## Problem Statement

O projeto demo-quarkus-sldd possui uma API de calculadora com operações de soma, subtração, multiplicação e divisão. Atualmente não há suporte para operações de potenciação (exponenciais), limitando a capacidade da API.

## Target Users

Clientes HTTP que usam a API de calculadora e precisam de operações de potenciação como parte do conjunto de funcionalidades disponíveis.

## Formalized Exploration Decisions

- A funcionalidade será adicionada como uma nova operação da calculadora existente.
- O endpoint deve seguir o padrão do endpoint de multiplicação atual.
- O endpoint deverá ser exposto sob o base path existente:

```text
/api/calc
```

- O caminho da operação será:

```text
POST /api/calc/power
```

- A entrada deve ser JSON.
- Os campos de entrada devem seguir o padrão atual:

```json
{
  "base": 2.0,
  "exponent": 3.0
}
```

- A operação matemática será:

```text
power = base ^ exponent
```

- Os operandos devem ser tratados como números decimais (`BigDecimal`) compatíveis com o padrão atual do projeto.
- A resposta deve ser JSON.
- O campo de resposta será:

```json
{
  "power": 8.0
}
```

## Success Metrics

- Endpoint `POST /api/calc/power` está funcional e acessível.
- Requisições válidas retornam `200 OK` com o resultado correto em JSON.
- Requisições com payload inválido retornam `400 Bad Request`.
- Endpoint aparece no contrato OpenAPI (`/q/openapi`).

## Out of Scope

- Escala ou arredondamento customizado (usar padrão `BigDecimal`).
- Tratamento especial de overflow ou casos extremos além do comportamento natural de `BigDecimal`.
- Funcionalidades de logaritmo, raiz, ou outras operações além de potenciação.

## Risks and Assumptions

- **Assumption**: Java `BigDecimal` fornece suporte adequado para potenciação sem necessidade de escala fixa como em divisão.
- **Risk**: Operações com expoentes decimais ou muito grandes podem ter precisão limitada; será documentado em Step 03 se necessário.

## Acceptance Criteria (Given/When/Then)

```gherkin
Scenario: Calcular potência com operandos válidos
  Given que o cliente envia uma requisição POST para /api/calc/power
  When o body contém {"base": 2.0, "exponent": 3.0}
  Then o status é 200 OK
  And a resposta contém {"power": 8.0}

Scenario: Rejeitar requisição sem field "base"
  Given que o cliente envia uma requisição POST para /api/calc/power
  When o body contém {"exponent": 3.0} (falta "base")
  Then o status é 400 Bad Request

Scenario: Rejeitar requisição sem field "exponent"
  Given que o cliente envia uma requisição POST para /api/calc/power
  When o body contém {"base": 2.0} (falta "exponent")
  Then o status é 400 Bad Request

Scenario: Rejeitar requisição com campo nulo
  Given que o cliente envia uma requisição POST para /api/calc/power
  When o body contém {"base": null, "exponent": 3.0}
  Then o status é 400 Bad Request

Scenario: Endpoint exposto no contrato OpenAPI
  Given que o cliente solicita GET /q/openapi
  Then o contrato contém "/api/calc/power"
```
