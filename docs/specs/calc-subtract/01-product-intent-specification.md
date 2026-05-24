# Step 01 — Product Intent Specification

## Problem Statement

O serviço de calculadora atualmente expõe uma operação de adição via API REST, mas não oferece uma operação equivalente para subtração.

Usuários da API precisam enviar dois operandos numéricos e receber o resultado da subtração entre eles, seguindo o mesmo padrão de contrato, validação e comportamento já utilizado pelo endpoint de adição existente.

A nova funcionalidade deve adicionar uma operação de subtração sem alterar o comportamento existente da operação de soma.

## Target Users

- Consumidores da API REST de calculadora.
- Desenvolvedores que usam o projeto como demonstração de Quarkus, REST, Bean Validation, OpenAPI e fluxo SLDD.
- Testes automatizados que validam operações matemáticas expostas pela API.

## Formalized Exploration Decisions

- A funcionalidade será adicionada como uma nova operação da calculadora existente.
- O endpoint deve seguir o padrão do endpoint de soma atual.
- O endpoint deverá ser exposto sob o base path existente:

```text
/api/calc
```

- O caminho da operação será:

```text
POST /api/calc/subtract
```

- A entrada deve ser JSON.
- Os campos de entrada devem seguir o padrão atual:

```json
{
  "firstOperand": 10.25,
  "secondOperand": 5.75
}
```

- A operação matemática será:

```text
firstOperand - secondOperand
```

- Os operandos devem ser tratados como números decimais compatíveis com o padrão atual do projeto.
- A resposta deve ser JSON.
- O campo de resposta será:

```json
{
  "difference": 4.50
}
```

- Entradas inválidas, ausentes, nulas ou com tipos incompatíveis devem resultar em `400 Bad Request`, seguindo o comportamento padrão já existente.
- O endpoint deve aparecer na documentação OpenAPI exposta pela aplicação.
- O endpoint de soma existente permanece inalterado.

## Success Metrics

- Consumidores conseguem chamar `POST /api/calc/subtract` com dois operandos válidos e recebem o resultado correto da subtração.
- A resposta usa o campo `difference`.
- Requisições inválidas recebem `400 Bad Request`.
- A operação de soma existente continua funcionando.
- A documentação OpenAPI inclui o novo endpoint.
- Os testes automatizados relevantes passam.

## Out of Scope

- Alterar o contrato do endpoint de soma.
- Criar operações de multiplicação, divisão ou outras operações matemáticas.
- Criar envelope customizado de erro.
- Introduzir autenticação, autorização ou rate limiting.
- Alterar a estrutura geral da API de calculadora.
- Alterar o formato dos nomes dos operandos.
- Criar interface gráfica ou cliente externo.
- Alterar dependências ou versões do Quarkus sem necessidade direta.

## Risks and Assumptions

- Assume-se que o padrão atual do endpoint de soma é a referência obrigatória para contrato, validação e testes.
- Assume-se que `firstOperand - secondOperand` é a ordem correta da subtração.
- Assume-se que o uso de números decimais deve seguir o mesmo comportamento da soma.
- O principal risco funcional é ambiguidade na ordem dos operandos; isso é mitigado mantendo nomes explícitos e aceitando que o primeiro operando é subtraído pelo segundo.
- O principal risco de compatibilidade é alterar acidentalmente o comportamento da soma; isso deve ser prevenido por testes de regressão existentes.

## Acceptance Criteria

### Cenário 1 — Subtração válida

**Given** a API de calculadora está disponível  
**And** o consumidor possui dois operandos numéricos válidos  
**When** o consumidor envia uma requisição `POST /api/calc/subtract` com `firstOperand` e `secondOperand`  
**Then** a API deve retornar `200 OK`  
**And** a resposta deve conter o campo `difference`  
**And** `difference` deve ser igual a `firstOperand - secondOperand`.

### Cenário 2 — Primeiro operando ausente

**Given** a API de calculadora está disponível  
**When** o consumidor envia uma requisição `POST /api/calc/subtract` sem o campo `firstOperand`  
**Then** a API deve retornar `400 Bad Request`.

### Cenário 3 — Segundo operando ausente

**Given** a API de calculadora está disponível  
**When** o consumidor envia uma requisição `POST /api/calc/subtract` sem o campo `secondOperand`  
**Then** a API deve retornar `400 Bad Request`.

### Cenário 4 — Operando nulo

**Given** a API de calculadora está disponível  
**When** o consumidor envia uma requisição `POST /api/calc/subtract` com qualquer operando nulo  
**Then** a API deve retornar `400 Bad Request`.

### Cenário 5 — Tipo inválido

**Given** a API de calculadora está disponível  
**When** o consumidor envia uma requisição `POST /api/calc/subtract` com um operando de tipo incompatível com número  
**Then** a API deve retornar `400 Bad Request`.

### Cenário 6 — Documentação OpenAPI

**Given** a aplicação expõe documentação OpenAPI  
**When** a documentação OpenAPI é consultada  
**Then** ela deve listar o endpoint `POST /api/calc/subtract`.

### Cenário 7 — Regressão da soma

**Given** o endpoint de soma existente já funciona  
**When** a feature de subtração é adicionada  
**Then** o endpoint `POST /api/calc/sum` deve continuar funcionando conforme seu contrato atual.
