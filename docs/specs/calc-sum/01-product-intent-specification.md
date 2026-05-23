# Step 01 — Product Intent Specification

## Problem Statement
Hoje não há um endpoint padronizado para soma de dois valores decimais com precisão adequada e documentação OpenAPI clara. Precisamos de um endpoint REST `POST /api/calc/sum` que aceite dois números decimais tipados e retorne a soma, evitando problemas de precisão de ponto flutuante binário.

## Target Users
- Desenvolvedores de frontend/backend que consomem APIs internas.
- QA/automação que precisa de contrato previsível e testável.
- Equipe técnica que depende de documentação OpenAPI em dev mode para integração rápida.

## Formalized Exploration Decisions
1. O endpoint será `POST /api/calc/sum`.
2. O tipo numérico de entrada e processamento será `BigDecimal`.
3. Os payloads serão tipados (DTOs), com nomes semânticos:
   - Request: `firstOperand`, `secondOperand`
   - Response: `sum`
4. Haverá suporte OpenAPI acessível no Quarkus Dev Mode.
5. Validação mínima:
   - campos obrigatórios;
   - campos não nulos;
   - payload inválido retorna `400 Bad Request`.
6. Formato de erro: padrão do Quarkus (sem envelope customizado nesta etapa).

## Success Metrics
- Endpoint responde corretamente com soma decimal para entradas válidas.
- Contrato OpenAPI exibe request/response tipados corretamente.
- Em dev mode, a documentação OpenAPI/Swagger UI está acessível.
- Requisições inválidas retornam `400` conforme esperado.

## Out of Scope
- Operações além de soma (subtração, multiplicação, divisão).
- Customização avançada de metadata OpenAPI.
- Padronização custom de envelope de erro.
- Regras de negócio adicionais (arredondamento específico de domínio financeiro, limites de escala, etc.) nesta fase.

## Risks and Assumptions
- Assumption: `BigDecimal` atende a expectativa de precisão do produto atual.
- Risk: sem definição de escala/arredondamento de apresentação, clientes podem interpretar formato de saída de maneira diferente.
- Assumption: comportamento default de erro do Quarkus é aceitável para consumidores neste MVP.
- Risk: divergência futura caso APIs irmãs exijam envelope de erro padronizado.

## Acceptance Criteria (Given/When/Then)
1. **Soma com payload válido**
   - Given um payload JSON com `firstOperand` e `secondOperand` válidos
   - When o cliente chama `POST /api/calc/sum`
   - Then a API responde sucesso com JSON contendo `sum` igual à soma exata decimal dos operandos

2. **Contrato tipado no OpenAPI**
   - Given a aplicação em Quarkus Dev Mode
   - When o usuário acessa a documentação OpenAPI
   - Then o schema exibe request e response com campos tipados e nomes significativos definidos

3. **Campo ausente**
   - Given payload sem `firstOperand` ou sem `secondOperand`
   - When o cliente chama `POST /api/calc/sum`
   - Then a API retorna `400 Bad Request`

4. **Campo nulo**
   - Given payload com `firstOperand` ou `secondOperand` nulo
   - When o cliente chama `POST /api/calc/sum`
   - Then a API retorna `400 Bad Request`

5. **Tipo inválido**
   - Given payload com tipo incompatível (ex.: string não numérica)
   - When o cliente chama `POST /api/calc/sum`
   - Then a API retorna `400 Bad Request`
