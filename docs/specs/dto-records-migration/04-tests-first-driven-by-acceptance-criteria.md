# Step 04 — Tests First Driven by Acceptance Criteria

## Red-Phase Test Plan
Critérios de aceitação cobertos por testes já existentes em `CalcResourceTest`:
- payload válido retorna soma esperada;
- ausência/null de operandos retorna 400;
- tipo inválido retorna 400;
- OpenAPI expõe `/api/calc/sum`.

## Test Decisions
- Não criar novos cenários neste passo porque os critérios de aceitação já estão cobertos por testes automatizados existentes.
- Executar a suíte após implementação para confirmar preservação do comportamento.
