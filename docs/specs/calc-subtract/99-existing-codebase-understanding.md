# Step 99 — Existing Codebase Understanding — Calculator Subtraction Endpoint

## Repository Structure Overview

O projeto é uma aplicação Java/Quarkus REST.

Áreas relevantes:

- `src/main/java/org/soujava/demo/`
  - contém recursos REST, DTOs e serviços da aplicação.
- `src/test/java/org/soujava/demo/`
  - contém testes Quarkus com REST Assured.
- `docs/specs/`
  - contém fluxos SLDD existentes, incluindo `calc-sum`.

Arquivos relevantes encontrados:

- `CalcResource.java`
  - recurso REST da calculadora.
  - base path: `/api/calc`.
  - já contém `POST /api/calc/sum`.

- `CalcService.java`
  - serviço de cálculo.
  - já contém operação de soma com `BigDecimal`.

- `SumRequest.java`
  - DTO de entrada como Java record.
  - campos:
    - `firstOperand`
    - `secondOperand`
  - ambos com `@NotNull`.

- `SumResponse.java`
  - DTO de saída como Java record.
  - campo:
    - `sum`.

- `CalcResourceTest.java`
  - testes do endpoint de soma.
  - valida sucesso, payload inválido e presença no OpenAPI.

## Architecture Summary

A arquitetura atual da calculadora segue um fluxo simples:

```text
HTTP Client
  -> CalcResource
    -> DTO Request validado com Bean Validation
      -> CalcService
        -> BigDecimal operation
      -> DTO Response
  -> JSON response
```

Para soma:

```text
POST /api/calc/sum
  -> SumRequest(firstOperand, secondOperand)
  -> CalcService.sum(...)
  -> SumResponse(sum)
```

A subtração deve seguir o mesmo padrão:

```text
POST /api/calc/subtract
  -> SubtractRequest(firstOperand, secondOperand)
  -> CalcService.subtract(...)
  -> SubtractResponse(difference)
```

## Conventions to Preserve

- Usar o base path existente `/api/calc`.
- Usar `POST` para operação de cálculo.
- Consumir e produzir `application/json`.
- Usar `BigDecimal` para operandos e resultado.
- Usar Java records para DTOs.
- Usar `firstOperand` e `secondOperand` como nomes dos campos de entrada.
- Usar `@Valid` no parâmetro do recurso.
- Usar `@NotNull` nos campos obrigatórios do request.
- Retornar `400 Bad Request` padrão para entradas inválidas.
- Não criar envelope customizado de erro.
- Manter o endpoint de soma inalterado.
- Manter testes no estilo de `CalcResourceTest`.
- Validar presença do novo endpoint no OpenAPI.

## Integration Points

- REST layer:
  - `CalcResource`
  - novo endpoint `POST /api/calc/subtract`.

- Service layer:
  - `CalcService`
  - novo método de subtração.

- DTOs:
  - novo request record para subtração.
  - novo response record para subtração.

- Validation:
  - Jakarta Bean Validation via `@Valid` e `@NotNull`.

- Tests:
  - `CalcResourceTest`
  - novos cenários para subtração.
  - regressão implícita do endpoint de soma existente.

- OpenAPI:
  - `/q/openapi` deve listar `/api/calc/subtract`.

## Risks and Unknowns

- O principal risco funcional é a ordem da operação:
  - a Step 01 define que deve ser `firstOperand - secondOperand`.

- O principal risco de compatibilidade é alterar sem querer o contrato ou comportamento de `POST /api/calc/sum`.

- O campo de resposta escolhido na Step 01 é `difference`; isso deve ser preservado no design e nos testes.

- Não há envelope customizado de erro; introduzir um seria desvio do padrão atual.

- Não foi identificada necessidade de nova dependência.

## Context to Carry Into Steps 02-06

- A Step 02 deve desenhar a solução como extensão do recurso e serviço existentes.
- A Step 03 deve detalhar contratos:
  - `POST /api/calc/subtract`
  - request com `firstOperand` e `secondOperand`
  - response com `difference`.
- A Step 04 deve escrever testes antes da implementação:
  - sucesso;
  - campo ausente;
  - campo nulo;
  - tipo inválido;
  - OpenAPI;
  - regressão da soma.
- A Step 05 deve implementar somente o mínimo necessário para passar nos testes.
- A Step 06 deve verificar conformidade com Step 01, Step 02, Step 03 e testes.
