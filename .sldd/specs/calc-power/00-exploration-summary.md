# Current Understanding

A funcionalidade será um novo endpoint REST para operações de potenciação, seguindo o padrão estabelecido pelas operações de calculadora existentes (soma, subtração, multiplicação, divisão).

# Candidate Product Decisions

- **Endpoint**: `POST /api/calc/power`
- **Request Record**: `PowerRequest` com campos `base: BigDecimal` e `exponent: BigDecimal`
- **Response Record**: `PowerResponse` com campo `power: BigDecimal`
- **Operação Matemática**: `power = base ^ exponent`
- **Padrão Técnico**: Reutilizar base path `/api/calc`, JSON, `BigDecimal`, records, validação Jakarta

# Candidate Technical Ideas (Non-Binding)

- Usar `BigDecimal` para base e expoente, seguindo padrão do projeto
- Delegar cálculo para novo método em `CalcService`
- Manter validação Bean Validation (`@Valid`, `@NotNull`)
- Exposição automática em OpenAPI

# Alternatives Discussed

Nenhuma alternativa foi considerada; o padrão POST com BigDecimal é consistente com as operações existentes.

# Open Questions

Nenhuma em aberto. Exploração completada com aprovação do usuário.

# Risks and Assumptions

- **Assumption**: Java/BigDecimal pode lidar com potenciação de forma natural (sem escala customizada como em divisão)
- **Risk Mitigado**: Comportamento será definido em Step 03 se necessário ajuste

# Suggested Next SLDD Step

Step 01 — Especificação de Intenção do Produto (Product Intent Specification)
