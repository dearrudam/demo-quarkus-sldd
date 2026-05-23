# Step 02 — High-Level Technical Design

## Requirements Traceability
- **R1 (Escopo total)**: migrar todos os DTOs para `record`.
- **R2 (Compatibilidade JSON)**: preservar propriedades JSON existentes.
- **R3 (Validações)**: manter constraints atuais de validação.
- **R4 (Dependências)**: não introduzir Lombok/MapStruct.
- **R5 (Big-bang)**: executar migração em uma única entrega.

Mapeamento para solução:
- `SumRequest` e `SumResponse` serão convertidos para `record` (R1, R5).
- Componentes do record manterão nomes `firstOperand`, `secondOperand`, `sum` (R2).
- `@NotNull` continuará aplicado em componentes de `SumRequest` (R3).
- Sem novas bibliotecas (R4).

## Architecture Diagram
```text
Client JSON
   |
   v
POST /api/calc/sum (CalcResource)
   |  @Valid SumRequest(record)
   v
CalcService.sum(firstOperand, secondOperand)
   |
   v
SumResponse(record) -> JSON
```

## Component Responsibilities
- **DTO layer (`SumRequest`, `SumResponse`)**: contrato de entrada/saída imutável.
- **Resource layer (`CalcResource`)**: orquestra validação e chamada de serviço.
- **Service layer (`CalcService`)**: regra de cálculo sem mudanças de comportamento.
- **Test layer (`CalcResourceTest`)**: garantir compatibilidade de contrato e validação.

## Data Flow
1. JSON recebido em `/api/calc/sum`.
2. Jackson desserializa para `SumRequest` record.
3. Bean Validation valida `@NotNull` dos componentes.
4. `CalcService` calcula soma.
5. Recurso retorna `SumResponse` record serializado.

## Security and Observability Requirements
- Sem alteração de autenticação/autorização.
- Sem alteração de logging/tracing.
- Observabilidade mínima preservada pelos testes HTTP e contrato OpenAPI existentes.

## Trade-Offs and Alternatives
- **Escolha**: record nativo Java.
  - Prós: imutabilidade, menos boilerplate, sem dependência extra.
  - Contras: quebra potencial em pontos que esperem mutabilidade.
- **Alternativa rejeitada**: Lombok `@Value` (fora de escopo por decisão explícita).
- **Alternativa rejeitada**: manter POJOs (não atende objetivo de redução de boilerplate).

## High-Level Test Scenario Map
- **Contrato de sucesso**: payload válido retorna 200 e `sum` correta.
- **Validação**: campos ausentes/nulos retornam 400.
- **Tipo inválido**: payload incompatível retorna 400.
- **Contrato OpenAPI**: endpoint `/api/calc/sum` permanece exposto.
