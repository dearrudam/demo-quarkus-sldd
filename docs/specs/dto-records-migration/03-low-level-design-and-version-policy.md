# Step 03 — Low-Level Design and Version Policy

## API Contracts
### Request DTO
- `SumRequest(firstOperand, secondOperand)`
- Tipos: `BigDecimal` para ambos os operandos.
- Constraints:
  - `@NotNull` em `firstOperand`.
  - `@NotNull` em `secondOperand`.

### Response DTO
- `SumResponse(sum)`
- Tipo: `BigDecimal`.

### Endpoint
- `POST /api/calc/sum`
- Entrada JSON:
```json
{
  "firstOperand": 10.25,
  "secondOperand": 5.75
}
```
- Saída JSON:
```json
{
  "sum": 16.00
}
```

## Data Models
- Migrar classes POJO:
  - `public BigDecimal firstOperand;`
  - `public BigDecimal secondOperand;`
  - `public BigDecimal sum;`
- Para records equivalentes:
  - `record SumRequest(@NotNull BigDecimal firstOperand, @NotNull BigDecimal secondOperand)`
  - `record SumResponse(BigDecimal sum)`

## Error Model
- Entradas inválidas continuam retornando `400 Bad Request`.
- Tipos inválidos no payload continuam retornando `400 Bad Request`.
- Sem mudança no formato observável de erro neste passo.

## Implementation Plan
1. Converter `SumRequest` para `record` com constraints.
2. Converter `SumResponse` para `record`.
3. Ajustar `CalcResource` para uso de accessors de records.
4. Executar testes existentes para validar compatibilidade.

## Test Strategy
- Reutilizar `CalcResourceTest` como guarda de regressão de contrato.
- Garantir que todos os cenários atuais permaneçam verdes.
- Não introduzir novas dependências de teste.

## Version Policy
- Mudança classificada como **compatível de contrato HTTP** (sem breaking change externo esperado).
- Em caso de regressão detectada, rollback simples revertendo DTOs para POJOs.
