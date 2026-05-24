# Step 06 — Verification and Feedback Report

## Compliance Matrix

| Item aprovado                         | Evidência                                                       | Status   |
| ------------------------------------- | --------------------------------------------------------------- | -------- |
| `POST /api/calc/subtract`               | Implementado em `CalcResource`                                    | Conforme |
| Entrada `firstOperand` e `secondOperand`  | `SubtractRequest` com `BigDecimal` e `@NotNull`                       | Conforme |
| Operação `firstOperand - secondOperand` | `CalcService.subtract()` usa `firstOperand.subtract(secondOperand)` | Conforme |
| Resposta `difference`                   | `SubtractResponse(BigDecimal difference)`                         | Conforme |
| Inválidos retornam `400`                | Testes Step 04/05 passaram                                      | Conforme |
| OpenAPI contém endpoint               | Teste `/q/openapi` passou                                         | Conforme |
| Soma preservada                       | Testes existentes de soma passaram                              | Conforme |
| Sem novas dependências                | Nenhuma dependência adicionada                                  | Conforme |

## Version and Dependency Validation

- Nenhuma nova dependência foi adicionada.
- Versões de Quarkus e Java permanecem inalteradas.
- A implementação usa dependências já existentes:
  - Quarkus REST;
  - Jackson;
  - Hibernate Validator;
  - SmallRye OpenAPI;
  - JUnit/REST Assured para testes.

Status: **Conforme**.

## Test Convention Compliance

Comandos executados na Step 05:

```bash
./mvnw test -Dtest=CalcResourceTest
./mvnw test
```

Resultados:

```text
Focused tests: PASS — 12 tests, 0 failures, 0 errors
Full unit tests: PASS — 13 tests, 0 failures, 0 errors
Build result: SUCCESS
```

Status: **Conforme**.

## Risks by Severity

### Critical

Nenhum risco crítico identificado.

### Major

Nenhum risco major identificado.

### Minor

- O endpoint segue o padrão atual sem envelope customizado de erro; isso é intencional e alinhado ao projeto.
- A documentação formal cobre o fluxo, mas ainda não foi arquivada/finalizada além da Step 06.

## Remediation Steps

Nenhuma correção obrigatória identificada.

Ações opcionais futuras:

- Criar operações adicionais de calculadora em fluxos SLDD separados, como multiplicação ou divisão.
- Padronizar documentação futura se novas operações forem adicionadas.

## Go/No-Go Decision and Rationale

Decisão:

```text
GO
```

Racional:

- A implementação cumpre Step 01, Step 02, Step 03 e Step 99.
- A Step 04 confirmou RED antes da implementação.
- A Step 05 confirmou GREEN com testes focados e suíte unitária completa.
- Revisões das Steps 01–05 não encontraram problemas críticos ou major.
- O escopo foi mantido mínimo, aditivo e compatível com o endpoint de soma existente.
