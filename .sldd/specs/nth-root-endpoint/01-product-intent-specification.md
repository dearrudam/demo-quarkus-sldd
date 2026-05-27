# Problem Statement
O serviço atual expõe operações aritméticas básicas (`sum`, `subtract`, `multiply`, `divide`, `power`) em `/api/calc`, mas não oferece uma operação para raiz n-ésima real. Isso limita cenários que exigem extração de raízes com precisão decimal e regras explícitas de domínio para entradas negativas e índice par/ímpar.

# Target Users
- Consumidores da API REST de cálculo (`/api/calc`) que já utilizam payloads JSON com `BigDecimal`.
- Desenvolvedores backend e QA que precisam de comportamento determinístico de arredondamento via `RoundingContext`.

# Formalized Exploration Decisions
- A nova operação será de **raiz n-ésima real**.
- O payload usará nomes significativos:
  - `radicand: BigDecimal`
  - `index: BigInteger`
- O arredondamento usará o objeto existente `RoundingContext`.
- Regras de domínio:
  - `index > 0` é obrigatório.
  - `radicand < 0` com `index` ímpar é permitido.
  - `radicand < 0` com `index` par é inválido para domínio real (erro 400).

# Success Metrics
- Endpoint novo documentado e funcional no mesmo padrão dos demais recursos de `CalcResource`.
- Respostas corretas para casos representativos (ex.: `radicand=27`, `index=3` -> `3`; `radicand=-8`, `index=3` -> `-2`).
- Erros de domínio/validação retornados como HTTP 400 para entradas inválidas.
- Testes automatizados cobrindo fluxos válidos e inválidos.

# Out of Scope
- Suporte a números complexos.
- Ajustes de contrato em endpoints já existentes que não sejam necessários para a nova operação.
- Refatorações amplas de arquitetura fora do mínimo necessário.

# Risks and Assumptions
- Assume-se que `RoundingContext` (scale + roundingMode) é adequado para determinar precisão de saída da raiz.
- Cálculo de raiz n-ésima em `BigDecimal` pode exigir estratégia iterativa (ex.: Newton-Raphson), com risco de erro numérico se não houver critérios claros de convergência.
- `index` será modelado como `BigInteger` no contrato para preservar semântica de inteiro arbitrário; internamente pode haver limites práticos para conversão/iteração.

# Acceptance Criteria (Given/When/Then)
1. **Raiz real positiva**
   - Given `radicand` positivo, `index > 0`, e `RoundingContext` válido
   - When o cliente chama o endpoint de raiz n-ésima
   - Then a API retorna HTTP 200 com o valor da raiz arredondado conforme `RoundingContext`.

2. **Radicando zero**
   - Given `radicand = 0` e `index > 0`
   - When o cliente chama o endpoint
   - Then a API retorna HTTP 200 com resultado `0`.

3. **Radicando negativo com índice ímpar**
   - Given `radicand` negativo e `index` ímpar positivo
   - When o cliente chama o endpoint
   - Then a API retorna HTTP 200 com resultado real negativo.

4. **Radicando negativo com índice par**
   - Given `radicand` negativo e `index` par positivo
   - When o cliente chama o endpoint
   - Then a API retorna HTTP 400 por domínio inválido para raiz real.

5. **Índice inválido**
   - Given `index <= 0`
   - When o cliente chama o endpoint
   - Then a API retorna HTTP 400.

6. **Arredondamento inválido**
   - Given `RoundingContext` com `scale` negativo
   - When o cliente chama o endpoint
   - Then a API retorna HTTP 400.
