# Step 99 - Existing Codebase Understanding and Context Summary

## Repository Structure Overview

O projeto e uma aplicacao Java/Quarkus REST.

Areas relevantes:

- `src/main/java/org/soujava/demo/`
  - contem recursos REST, DTOs e servicos da aplicacao.
- `src/test/java/org/soujava/demo/`
  - contem testes Quarkus com REST Assured.
- `.sldd/specs/`
  - contem os novos artefatos SLDD gerenciados por journal.
- `docs/specs/`
  - contem specs legacy, incluindo `calc-multiply`, usada como padrao para esta feature.

Arquivos relevantes encontrados:

- `CalcResource.java`
  - recurso REST da calculadora.
  - base path: `/api/calc`.
  - contem operacoes REST existentes para soma, subtracao e multiplicacao.
- `CalcService.java`
  - servico de calculo.
  - contem operacoes matematicas com `BigDecimal`.
- `SumRequest.java`, `SubtractRequest.java` e `MultiplyRequest.java`
  - DTOs de entrada como Java records.
  - usam `BigDecimal` e `@NotNull`.
- `SumResponse.java`, `SubtractResponse.java` e `MultiplyResponse.java`
  - DTOs de saida como Java records.
  - usam campos semanticos por operacao: `sum`, `difference` e `product`.
- `CalcResourceTest.java`
  - testes dos endpoints de calculadora.
  - valida sucesso, payload invalido e presenca dos endpoints existentes no OpenAPI.
- `pom.xml`
  - define Quarkus `3.35.3`, Java release `25` e dependencias atuais.

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

Operacoes existentes observadas:

```text
POST /api/calc/sum
  -> SumRequest(firstOperand, secondOperand)
  -> CalcService.sum(...)
  -> SumResponse(sum)

POST /api/calc/subtract
  -> SubtractRequest(firstOperand, secondOperand)
  -> CalcService.subtract(...)
  -> SubtractResponse(difference)

POST /api/calc/multiply
  -> MultiplyRequest(multiplier, multiplicand)
  -> CalcService.multiply(...)
  -> MultiplyResponse(product)
```

A divisao da Step 01 deve seguir a estrutura tecnica existente, mas com o contrato aprovado para esta feature:

```text
POST /api/calc/divide
  -> DivideRequest(dividend, divisor)
  -> CalcService.divide(...)
  -> DivideResponse(quotient)
```

## Conventions to Preserve

- Usar o base path existente `/api/calc`.
- Usar `POST` para operacao de calculo.
- Consumir e produzir `application/json`.
- Usar `BigDecimal` para operandos e resultado.
- Usar Java records para DTOs.
- Usar `@Valid` no parametro do recurso.
- Usar `@NotNull` nos campos obrigatorios do request.
- Retornar `400 Bad Request` padrao para entradas invalidas.
- Nao criar envelope customizado de erro.
- Manter testes no estilo de `CalcResourceTest`.
- Validar presenca do novo endpoint no OpenAPI.
- Preservar o contrato aprovado na Step 01: `dividend`, `divisor` e `quotient`.
- Preservar a decisao de retornar `400 Bad Request` quando `divisor` for zero.
- Nao introduzir regra de arredondamento, escala fixa ou formatacao para divisoes nao exatas.

## Integration Points

- REST layer:
  - `CalcResource`.
  - novo endpoint `POST /api/calc/divide`.
- Service layer:
  - `CalcService`.
  - novo metodo de divisao.
- DTOs:
  - novo request record para divisao.
  - novo response record para divisao.
- Validation:
  - Jakarta Bean Validation via `@Valid` e `@NotNull`.
  - validacao adicional para `divisor` zero.
- Tests:
  - `CalcResourceTest`.
  - novos cenarios para divisao conforme acceptance criteria da Step 01.
- OpenAPI:
  - `/q/openapi` deve listar `/api/calc/divide`.

## Risks and Unknowns

- A Step 01 define os campos de entrada como `dividend` e `divisor`; usar nomes genericos como `firstOperand` e `secondOperand` violaria a spec aprovada.
- O campo de resposta aprovado e `quotient`; retornar `result` ou outro campo violaria a spec aprovada.
- `BigDecimal.divide` pode lancar excecao para divisoes nao exatas quando usado sem escala ou `MathContext`.
- Arredondamento, escala fixa e formatacao para divisoes nao exatas estao fora de escopo.
- A Step 01 exige `400 Bad Request` para `divisor` zero; sera necessario modelar essa validacao sem criar envelope customizado.
- Nao foi identificada necessidade de nova dependencia.

## Context to Carry Into Steps 02-06

- A Step 02 deve desenhar a solucao como extensao do recurso e servico existentes.
- A Step 03 deve detalhar contratos:
  - `POST /api/calc/divide`.
  - request com `dividend` e `divisor`.
  - response com `quotient`.
  - `400 Bad Request` para `divisor` zero.
- A Step 03 deve explicitar como evitar comportamento indefinido de `BigDecimal.divide` para divisoes nao exatas, respeitando que arredondamento esta fora de escopo.
- A Step 04 deve escrever testes antes da implementacao para os acceptance criteria da Step 01:
  - sucesso;
  - `dividend` ausente;
  - `divisor` ausente;
  - operando nulo;
  - tipo invalido;
  - `divisor` zero;
  - OpenAPI.
- A Step 05 deve implementar somente o minimo necessario para passar nos testes.
- A Step 06 deve verificar conformidade com Step 01, Step 02, Step 03 e testes.
