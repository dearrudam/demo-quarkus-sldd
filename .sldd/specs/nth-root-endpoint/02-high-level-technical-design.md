# Requirements Traceability
- AC1/AC2/AC3 -> Novo endpoint `POST /api/calc/nth-root` delega ao serviço e retorna resultado com arredondamento configurável.
- AC4 -> Validação de domínio no resource ou service para rejeitar `radicand < 0` com `index` par.
- AC5 -> Validação de `index > 0` antes do cálculo.
- AC6 -> Reuso de `RoundingContext` com validação de `scale >= 0`.

# Architecture Diagram
Client -> CalcResource (`/api/calc/nth-root`) -> CalcService (`nthRoot`) -> BigDecimal result -> NthRootResponse

# Component Responsibilities
- **CalcResource**
  - Receber `NthRootRequest`.
  - Aplicar defaults/validação de `RoundingContext`.
  - Validar domínio de `radicand/index` e traduzir falhas em 400.
  - Delegar cálculo para `CalcService`.
- **CalcService**
  - Implementar cálculo de raiz n-ésima real em `BigDecimal` com algoritmo iterativo.
  - Aplicar regra para radicando negativo com índice ímpar.
- **DTOs**
  - `NthRootRequest(radicand, index, roundingContext)`.
  - `NthRootResponse(result)`.

# Data Flow
1. Cliente envia payload JSON com `radicand`, `index`, `roundingContext`.
2. Resource valida entrada e resolve parâmetros de arredondamento (default quando omitido).
3. Resource chama service com argumentos normalizados.
4. Service calcula raiz n-ésima com precisão/rounding configurados.
5. Resource retorna response JSON com resultado.

# Security and Observability Requirements
- Sem novos requisitos de autenticação/autorização (mantém padrão atual).
- Logs não devem expor dados sensíveis; payload numérico é de baixo risco.
- Erros de domínio devem ser observáveis via status HTTP 400.

# Trade-Offs and Alternatives
- **Algoritmo iterativo em `BigDecimal`** (escolha principal): boa precisão, maior complexidade.
- **Conversão para `double` + `Math.pow`**: mais simples, porém perda de precisão e inconsistência com `BigDecimal`.
- **Biblioteca externa para matemática de precisão**: pode simplificar implementação, mas adiciona dependências e custo de manutenção.

# High-Level Test Scenario Map
- Cenários de sucesso: radicando positivo; zero; negativo com índice ímpar.
- Cenários de falha: índice <= 0; radicando negativo com índice par; `roundingContext.scale` negativo.
- Cenário de default: `roundingContext` ausente usando valores padrão definidos.
