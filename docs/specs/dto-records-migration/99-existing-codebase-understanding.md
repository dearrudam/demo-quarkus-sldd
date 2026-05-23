# Step 99 — Existing Codebase Understanding and Context Summary

## Repository Structure Overview
- `src/main/java/org/soujava/demo`: código principal da API Quarkus.
- `src/test/java/org/soujava/demo`: testes de unidade/integração no modo QuarkusTest.
- `docs/specs/dto-records-migration`: artefatos SLDD desta mudança.

Arquivos diretamente relevantes para a migração de DTOs:
- `SumRequest` e `SumResponse` em `src/main/java/org/soujava/demo`.
- `CalcResource` como consumidor/produtor desses DTOs.
- `CalcResourceTest` como cobertura de contrato HTTP e validação.

## Architecture Summary
A aplicação expõe endpoint REST em `/api/calc/sum` via `CalcResource`, delega cálculo ao `CalcService` e serializa/deserializa DTOs JSON com Quarkus REST + Jackson.

Fluxo principal:
1. Cliente envia JSON para `POST /api/calc/sum`.
2. Quarkus desserializa para `SumRequest` e aplica Bean Validation (`@Valid` no resource + `@NotNull` nos campos).
3. `CalcResource` chama `CalcService.sum(...)` com operandos.
4. O resultado é retornado como `SumResponse` serializado em JSON.

## Conventions to Preserve
- Uso de Quarkus padrão (`quarkus-rest`, `quarkus-rest-jackson`, `quarkus-hibernate-validator`).
- Contrato JSON atual com campos `firstOperand`, `secondOperand` e `sum`.
- Comportamento de validação atual (payload inválido retorna HTTP 400 nos cenários já cobertos).
- Execução por Maven Wrapper (`./mvnw`) e suíte de testes verde antes de concluir mudanças.

## Integration Points
- **HTTP API**: `POST /api/calc/sum` depende da forma JSON dos DTOs.
- **Validation**: Bean Validation integrada ao endpoint via `@Valid` + constraints em DTO.
- **OpenAPI**: endpoint `/q/openapi` já exercitado por teste e deve continuar refletindo o contrato.
- **Testes existentes**: `CalcResourceTest` valida cenários de sucesso/erro e funciona como guarda de regressão para migração.

## Risks and Unknowns
- Migração para record pode alterar detalhes de desserialização/serialização se construtores/componentes não refletirem exatamente o contrato atual.
- Constraints de validação em records precisam ser posicionadas nos componentes do record para manter o mesmo comportamento.
- Mesmo com baixa complexidade do domínio atual, estratégia big-bang eleva risco de regressão caso novos DTOs sejam adicionados sem cobertura equivalente.

## Context to Carry Into Steps 02-06
- Escopo técnico atual é pequeno e concentrado em poucos arquivos, favorecendo execução rápida, porém com necessidade de preservar contrato estritamente.
- Step 02 deve propor desenho de migração para todos os DTOs preservando:
  - nomes de propriedades JSON;
  - validações existentes;
  - assinaturas e comportamento observável dos endpoints.
- Step 03 deve especificar mapeamento DTO-a-DTO (POJO → record), estratégia de validação e critérios de rollback.
- Step 04 deve reforçar cenários de compatibilidade JSON e validação (red tests) antes da implementação.
