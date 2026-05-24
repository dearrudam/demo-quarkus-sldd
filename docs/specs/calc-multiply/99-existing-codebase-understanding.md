# Step 99 — Existing Codebase Understanding and Context Summary

## Repository Structure Overview

O projeto é uma aplicação Java/Quarkus REST.

Áreas relevantes:

- `src/main/java/org/soujava/demo/`
  - contém recursos REST, DTOs e serviços da aplicação.
- `src/test/java/org/soujava/demo/`
  - contém testes Quarkus com REST Assured.
- `docs/specs/`
  - contém os artefatos SLDD do projeto.

Arquivos relevantes encontrados:

- `CalcResource.java`
  - recurso REST da calculadora.
  - base path: `/api/calc`.
  - contém operações REST existentes para soma e subtração.
- `CalcService.java`
  - serviço de cálculo.
  - contém operações matemáticas com `BigDecimal`.
- `SumRequest.java` e `SubtractRequest.java`
  - DTOs de entrada como Java records.
  - usam `BigDecimal` e `@NotNull`.
- `SumResponse.java` e `SubtractResponse.java`
  - DTOs de saída como Java records.
  - usam campos semânticos por operação.
- `CalcResourceTest.java`
  - testes dos endpoints de calculadora.
  - valida sucesso, payload inválido e presença dos endpoints existentes no OpenAPI.
- `pom.xml`
  - define Quarkus `3.35.3`, Java release `25` e dependências atuais.

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

Operações existentes observadas:

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

A multiplicação da Step 01 deve seguir a estrutura técnica existente, mas com o contrato aprovado para esta feature:

```text
POST /api/calc/multiply
  -> MultiplyRequest(multiplier, multiplicand)
  -> CalcService.multiply(...)
  -> MultiplyResponse(product)
```

## Conventions to Preserve

- Usar o base path existente `/api/calc`.
- Usar `POST` para operação de cálculo.
- Consumir e produzir `application/json`.
- Usar `BigDecimal` para operandos e resultado.
- Usar Java records para DTOs.
- Usar `@Valid` no parâmetro do recurso.
- Usar `@NotNull` nos campos obrigatórios do request.
- Retornar `400 Bad Request` padrão para entradas inválidas.
- Não criar envelope customizado de erro.
- Manter testes no estilo de `CalcResourceTest`.
- Validar presença do novo endpoint no OpenAPI.
- Preservar o contrato aprovado na Step 01: `multiplier`, `multiplicand` e `product`.

## Integration Points

- REST layer:
  - `CalcResource`.
  - novo endpoint `POST /api/calc/multiply`.
- Service layer:
  - `CalcService`.
  - novo método de multiplicação.
- DTOs:
  - novo request record para multiplicação.
  - novo response record para multiplicação.
- Validation:
  - Jakarta Bean Validation via `@Valid` e `@NotNull`.
- Tests:
  - `CalcResourceTest`.
  - novos cenários para multiplicação conforme acceptance criteria da Step 01.
- OpenAPI:
  - `/q/openapi` deve listar `/api/calc/multiply`.

## Risks and Unknowns

- A Step 01 define os campos de entrada como `multiplier` e `multiplicand`; usar os nomes genéricos dos endpoints existentes violaria a spec aprovada.
- O principal risco funcional é divergência de escala ou representação decimal no resultado de multiplicação.
- A Step 01 define que não haverá regra de arredondamento, escala fixa ou formatação monetária específica.
- O campo de resposta aprovado é `product`; isso deve ser preservado no design e nos testes.
- Não há envelope customizado de erro; introduzir um seria desvio do padrão atual e do escopo aprovado.
- Não foi identificada necessidade de nova dependência.

## Context to Carry Into Steps 02-06

- A Step 02 deve desenhar a solução como extensão do recurso e serviço existentes.
- A Step 03 deve detalhar contratos:
  - `POST /api/calc/multiply`.
  - request com `multiplier` e `multiplicand`.
  - response com `product`.
- A Step 04 deve escrever testes antes da implementação para os acceptance criteria da Step 01:
  - sucesso;
  - `multiplier` ausente;
  - `multiplicand` ausente;
  - operando nulo;
  - tipo inválido;
  - OpenAPI.
- A Step 05 deve implementar somente o mínimo necessário para passar nos testes.
- A Step 06 deve verificar conformidade com Step 01, Step 02, Step 03 e testes.
