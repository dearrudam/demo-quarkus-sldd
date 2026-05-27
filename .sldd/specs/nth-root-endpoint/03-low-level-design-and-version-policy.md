# Requirement-to-Design Traceability
- AC1/AC2/AC3 cobertos por:
  - contrato `NthRootRequest` + endpoint `POST /api/calc/nth-root`;
  - método `CalcService.nthRoot(...)` com cálculo iterativo e suporte a sinal.
- AC4 coberto por validação explícita de paridade do `index` quando `radicand < 0`.
- AC5 coberto por validação de `index` positivo antes de qualquer iteração.
- AC6 coberto por reuso de `RoundingContext` com regra `scale >= 0` e fallback de defaults.

# API Contracts
- **Endpoint**: `POST /api/calc/nth-root`
- **Request** (`NthRootRequest`):
  - `radicand: BigDecimal` (obrigatório)
  - `index: BigInteger` (obrigatório)
  - `roundingContext: RoundingContext` (opcional)
- **Response** (`NthRootResponse`):
  - `result: BigDecimal`
- **Erros**:
  - HTTP 400 para violações de domínio/validação.

# Data Models
- Novo record `NthRootRequest` com validação `@NotNull` em `radicand` e `index`.
- Novo record `NthRootResponse` com campo `result`.
- Reuso de `RoundingContext(Integer scale, RoundingMode roundingMode)` sem alteração estrutural.

# Error Model
Retornar `BadRequestException` quando:
1. `index <= 0`.
2. `radicand < 0 && index` par.
3. `roundingContext.scale < 0`.
4. Conversão necessária de `index` para `int` exceder limite suportado da implementação.
5. Algoritmo iterativo não convergir dentro de limite configurado (fallback para 400 com causa técnica encapsulada).

# Test Strategy
- Testes de resource via RestAssured/JUnit em `CalcResourceTest`.
- Casos de sucesso e falha alinhados aos critérios Step 01.
- Verificação de defaults de `RoundingContext` quando omitido.
- Testes unitários opcionais focados no método `CalcService.nthRoot` se a lógica iterativa ficar extensa.

# Test Scenario Catalog
1. Deve retornar 200 e resultado esperado para `radicand=27`, `index=3`.
2. Deve retornar 200 e `0` para `radicand=0`, `index=5`.
3. Deve retornar 200 e valor negativo para `radicand=-8`, `index=3`.
4. Deve retornar 400 para `radicand=-16`, `index=2`.
5. Deve retornar 400 para `index=0`.
6. Deve retornar 400 para `index=-3`.
7. Deve retornar 400 para `roundingContext.scale=-1`.
8. Deve aplicar default de arredondamento quando `roundingContext` ausente.

# Dependency and Version Policy
- Não adicionar novas dependências inicialmente.
- Reutilizar stack atual Quarkus + JUnit + RestAssured.
- Se biblioteca matemática externa se tornar necessária, registrar versão fixa em `pom.xml`, justificar precisão/complexidade, e avaliar impacto em build/test.

# Ordered Implementation Plan
1. Criar `NthRootRequest` e `NthRootResponse`.
2. Adicionar endpoint `POST /api/calc/nth-root` em `CalcResource` com validações de entrada e rounding defaults.
3. Adicionar método `nthRoot` em `CalcService` com algoritmo iterativo em `BigDecimal`.
4. Escrever testes de resource cobrindo catálogo do Step 03 (fase Red).
5. Rodar `./mvnw test` para confirmar falhas esperadas no Step 04.
