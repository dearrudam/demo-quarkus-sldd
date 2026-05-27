# Repository Structure Overview
- Projeto Quarkus com código principal em `src/main/java/org/soujava/demo` e testes em `src/test/java/org/soujava/demo`.
- Recursos REST centralizados em `CalcResource` e `GreetingResource`.
- Lógica aritmética concentrada em `CalcService`.
- DTOs request/response por operação com records Java dedicados.

# Architecture Summary
- Arquitetura simples em camadas leves:
  - Resource JAX-RS recebe request JSON, valida e traduz erros.
  - Service executa cálculo com `BigDecimal`.
  - Response record encapsula o valor retornado.
- `CalcResource` já define defaults e validações de arredondamento para divisão.

# Conventions to Preserve
- Usar `record` para payloads request/response.
- Manter endpoints no namespace `/api/calc` com método `POST`.
- Validação e regras de entrada feitas no resource antes/depois de chamar service.
- Tratamento de erros de domínio via `BadRequestException` (HTTP 400).
- Uso de `BigDecimal` nas operações numéricas.

# Integration Points
- `CalcResource` receberá novo endpoint para raiz n-ésima real.
- `CalcService` receberá novo método para cálculo da raiz.
- Novos DTOs request/response devem seguir padrão existente.
- `RoundingContext` existente deve ser reutilizado para controlar escala e modo de arredondamento.

# Risks and Unknowns
- Não há implementação atual de raiz n-ésima; será necessário escolher algoritmo numérico adequado para `BigDecimal`.
- `index` em `BigInteger` pode exceder limites de APIs que exigem `int`; será preciso política de validação/conversão.
- Critérios de convergência e precisão podem afetar estabilidade e desempenho.

# Context to Carry Into Steps 02-06
- A solução deve permanecer simples, consistente com o estilo atual do projeto.
- O contrato deve aceitar `radicand: BigDecimal`, `index: BigInteger` e `RoundingContext`.
- Regras de domínio aprovadas na exploração/Step 01 devem ser refletidas no design, testes e implementação.
