# Step 01 — Product Intent Specification

## Problem Statement
O projeto utiliza DTOs baseados em POJOs, gerando boilerplate (getters/setters/equals/hashCode/toString/construtores) e aumentando custo de manutenção. A mudança propõe migrar todos os DTOs para Java Records, preservando o comportamento funcional e o contrato JSON existente da API.

## Target Users
- Desenvolvedores do backend Quarkus/Java que mantêm e evoluem a API.
- Consumidores da API (impactados apenas se houver quebra de contrato, o que deve ser evitado).

## Formalized Exploration Decisions
- Escopo: migrar todos os DTOs do projeto.
- Compatibilidade JSON: manter o padrão atual (nomes/campos/comportamento de serialização e deserialização).
- Validações: manter as validações já existentes.
- Dependências/customizações: não introduzir Lombok nem MapStruct.
- Estratégia: big-bang (migração em uma única mudança coesa).

## Success Metrics
- 100% dos DTOs alvo convertidos para `record`.
- Nenhuma regressão funcional na API (mesmas respostas/requests válidos).
- Nenhuma quebra de contrato JSON observável por clientes.
- Suíte de testes existente passando integralmente após a migração.

## Out of Scope
- Alterar regras de negócio de endpoints/serviços.
- Redesenhar contratos de API (payload shape/versionamento).
- Introduzir novos frameworks de mapeamento/boilerplate (ex.: MapStruct, Lombok).
- Refatorações arquiteturais não necessárias para a migração de DTOs.

## Risks and Assumptions
### Riscos
- Diferenças sutis de serialização/deserialização ao trocar POJO por record.
- Incompatibilidade em pontos que dependem de mutabilidade/setters.
- Ajustes necessários em validações/anotações para manter comportamento.

### Assumptions
- Stack atual suporta records adequadamente no fluxo JSON usado pelo projeto.
- Testes existentes cobrem os principais contratos e fluxos da API.
- A equipe aceita imutabilidade de DTOs como padrão após a migração.

## Acceptance Criteria (Given/When/Then)
1. **Cobertura de escopo**
   - Given o conjunto de DTOs atual do projeto
   - When a mudança for concluída
   - Then todos os DTOs no escopo estarão representados como Java Records.

2. **Compatibilidade de contrato JSON**
   - Given clientes que enviam/recebem payloads no formato atual
   - When interagirem com a API após a migração
   - Then os contratos JSON permanecerão compatíveis com o padrão vigente.

3. **Preservação de validações**
   - Given entradas válidas e inválidas segundo as regras atuais
   - When forem processadas após a migração
   - Then o comportamento de validação permanecerá equivalente ao atual.

4. **Restrições de dependências**
   - Given o conjunto de tecnologias permitido para a mudança
   - When a migração for implementada
   - Then não haverá introdução de Lombok nem MapStruct.

5. **Estratégia de entrega**
   - Given a abordagem definida
   - When a mudança for aplicada
   - Then a migração ocorrerá em estratégia big-bang (alteração única e consistente).
