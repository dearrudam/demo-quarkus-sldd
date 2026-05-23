# Step 01 — Product Intent Specification

## Problem Statement

O serviço de calculadora atualmente expõe operações de soma e subtração via API REST, mas não oferece uma operação equivalente para multiplicação.

Usuários da API precisam enviar dois operandos numéricos e receber o resultado da multiplicação entre eles, seguindo o mesmo padrão de contrato, validação e comportamento já utilizado pelos endpoints existentes da calculadora.

A nova funcionalidade deve adicionar uma operação de multiplicação sem alterar o comportamento existente das operações de soma e subtração.

## Target Users

- Consumidores da API REST de calculadora.
- Desenvolvedores que usam o projeto como demonstração de Quarkus, REST, Bean Validation, OpenAPI e fluxo SLDD.
- Testes automatizados que validam operações matemáticas expostas pela API.

## Formalized Exploration Decisions

- A funcionalidade será adicionada como uma nova operação da calculadora existente.
- O endpoint deve seguir o padrão dos endpoints atuais de soma e subtração.
- O endpoint deverá ser exposto sob o base path existente:

```text
/api/calc
```

- O caminho da operação será:

```text
POST /api/calc/multiply
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
firstOperand * secondOperand
```

- Os operandos devem ser tratados como números decimais compatíveis com o padrão atual do projeto.
- A resposta deve ser JSON.
- O campo de resposta será:

```json
{
  "product": 58.9375
}
```

- Entradas inválidas, ausentes, nulas ou com tipos incompatíveis devem resultar em `400 Bad Request`, seguindo o comportamento padrão já existente.
- O endpoint deve aparecer na documentação OpenAPI exposta pela aplicação.
- Os endpoints existentes de soma e subtração permanecem inalterados.

## Success Metrics

- Consumidores conseguem chamar `POST /api/calc/multiply` com dois operandos válidos e recebem o resultado correto da multiplicação.
- A resposta usa o campo `product`.
- Requisições inválidas recebem `400 Bad Request`.
- As operações de soma e subtração existentes continuam funcionando.
- A documentação OpenAPI inclui o novo endpoint.
- Os testes automatizados relevantes passam.

## Out of Scope

- Alterar o contrato dos endpoints de soma ou subtração.
- Criar operações de divisão ou outras operações matemáticas.
- Criar envelope customizado de erro.
- Introduzir autenticação, autorização ou rate limiting.
- Alterar a estrutura geral da API de calculadora.
- Alterar o formato dos nomes dos operandos.
- Criar interface gráfica ou cliente externo.
- Alterar dependências ou versões do Quarkus sem necessidade direta.
- Definir regra de arredondamento, escala fixa ou formatação monetária específica.

## Risks and Assumptions

- Assume-se que o padrão atual dos endpoints de soma e subtração é a referência obrigatória para contrato, validação e testes.
- Assume-se que `firstOperand * secondOperand` é a operação correta da multiplicação.
- Assume-se que o uso de números decimais deve seguir o mesmo comportamento das operações existentes.
- Assume-se que não há necessidade de regra de arredondamento ou escala fixa nesta feature.
- O principal risco funcional é divergência de escala/representação decimal entre clientes; isso é mitigado mantendo `BigDecimal` e sem formatação customizada.
- O principal risco de compatibilidade é alterar acidentalmente o comportamento das operações existentes; isso deve ser prevenido por testes de regressão.

## Acceptance Criteria (Given/When/Then)

### Cenário 1 — Multiplicação válida

**Given** a API de calculadora está disponível  
**And** o consumidor possui dois operandos numéricos válidos  
**When** o consumidor envia uma requisição `POST /api/calc/multiply` com `firstOperand` e `secondOperand`  
**Then** a API deve retornar `200 OK`  
**And** a resposta deve conter o campo `product`  
**And** `product` deve ser igual a `firstOperand * secondOperand`.

### Cenário 2 — Primeiro operando ausente

**Given** a API de calculadora está disponível  
**When** o consumidor envia uma requisição `POST /api/calc/multiply` sem o campo `firstOperand`  
**Then** a API deve retornar `400 Bad Request`.

### Cenário 3 — Segundo operando ausente

**Given** a API de calculadora está disponível  
**When** o consumidor envia uma requisição `POST /api/calc/multiply` sem o campo `secondOperand`  
**Then** a API deve retornar `400 Bad Request`.

### Cenário 4 — Operando nulo

**Given** a API de calculadora está disponível  
**When** o consumidor envia uma requisição `POST /api/calc/multiply` com qualquer operando nulo  
**Then** a API deve retornar `400 Bad Request`.

### Cenário 5 — Tipo inválido

**Given** a API de calculadora está disponível  
**When** o consumidor envia uma requisição `POST /api/calc/multiply` com um operando de tipo incompatível com número  
**Then** a API deve retornar `400 Bad Request`.

### Cenário 6 — Documentação OpenAPI

**Given** a aplicação expõe documentação OpenAPI  
**When** a documentação OpenAPI é consultada  
**Then** ela deve listar o endpoint `POST /api/calc/multiply`.

### Cenário 7 — Regressão das operações existentes

**Given** os endpoints de soma e subtração existentes já funcionam  
**When** a feature de multiplicação é adicionada  
**Then** os endpoints `POST /api/calc/sum` e `POST /api/calc/subtract` devem continuar funcionando conforme seus contratos atuais.
