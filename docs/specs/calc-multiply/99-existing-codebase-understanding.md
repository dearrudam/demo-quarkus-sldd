# Step 99 — Existing Codebase Understanding — Calculator Multiplication Endpoint

## Repository Structure Overview

O projeto é uma aplicação Java/Quarkus REST.

Áreas relevantes:

- `src/main/java/org/soujava/demo/`
  - contém recursos REST, DTOs e serviços da aplicação.
- `src/test/java/org/soujava/demo/`
  - contém testes Quarkus com REST Assured.
- `docs/specs/`
  - contém fluxos SLDD existentes, incluindo `calc-sum`, `calc-subtract` e `dto-records-migration`.

Arquivos relevantes encontrados:

- `CalcResource.java`
  - recurso REST da calculadora.
  - base path: `/api/calc`.
  - contém operações REST para soma e subtração.

- `CalcService.java`
  - serviço de cálculo.
  - contém operações matemáticas com `BigDecimal`.

- `SumRequest.java` e `SubtractRequest.java`
  - DTOs de entrada como Java records.
  - campos:
    - `firstOperand`
    - `secondOperand`
  - ambos com `@NotNull`.

- `SumResponse.java` e `SubtractResponse.java`
  - DTOs de saída como Java records.
  - campos semânticos por operação:
    - `sum`
    - `difference`

- `CalcResourceTest.java`
  - testes dos endpoints de calculadora.
  - valida sucesso, payload inválido e presença dos endpoints no OpenAPI.

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

Para operações existentes:

```text
POST /api/calc/sum
  -> SumRequest(firstOperand, secondOperand)
  -> CalcService.sum(...)
  -> SumResponse(sum)

POST /api/calc/subtract
  -> SubtractRequest(firstOperand, secondOperand)
  -> CalcService.subtract(...)
  -> SubtractResponse(difference)
```

A multiplicação deve seguir o mesmo padrão:

```text
POST /api/calc/multiply
  -> MultiplyRequest(firstOperand, secondOperand)
  -> CalcService.multiply(...)
  -> MultiplyResponse(product)
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
- Manter os endpoints de soma e subtração inalterados.
- Manter testes no estilo de `CalcResourceTest`.
- Validar presença do novo endpoint no OpenAPI.

## Integration Points

- REST layer:
  - `CalcResource`
  - novo endpoint `POST /api/calc/multiply`.

- Service layer:
  - `CalcService`
  - novo método de multiplicação.

- DTOs:
  - novo request record para multiplicação.
  - novo response record para multiplicação.

- Validation:
  - Jakarta Bean Validation via `@Valid` e `@NotNull`.

- Tests:
  - `CalcResourceTest`
  - novos cenários para multiplicação.
  - regressão dos endpoints de soma e subtração existentes.

- OpenAPI:
  - `/q/openapi` deve listar `/api/calc/multiply`.

## Risks and Unknowns

- O principal risco funcional é divergência de escala ou representação decimal no resultado de multiplicação.
- A Step 01 define que não haverá regra de arredondamento, escala fixa ou formatação monetária específica.
- O campo de resposta escolhido na Step 01 é `product`; isso deve ser preservado no design e nos testes.
- O principal risco de compatibilidade é alterar sem querer o contrato ou comportamento de `POST /api/calc/sum` ou `POST /api/calc/subtract`.
- Não há envelope customizado de erro; introduzir um seria desvio do padrão atual.
- Não foi identificada necessidade de nova dependência.

## Context to Carry Into Steps 02-06

- A Step 02 deve desenhar a solução como extensão do recurso e serviço existentes.
- A Step 03 deve detalhar contratos:
  - `POST /api/calc/multiply`
  - request com `firstOperand` e `secondOperand`
  - response com `product`.
- A Step 04 deve escrever testes antes da implementação:
  - sucesso;
  - campo ausente;
  - campo nulo;
  - tipo inválido;
  - OpenAPI;
  - regressão da soma e da subtração.
- A Step 05 deve implementar somente o mínimo necessário para passar nos testes.
- A Step 06 deve verificar conformidade com Step 01, Step 02, Step 03 e testes.
