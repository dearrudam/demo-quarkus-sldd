# Step 01 - Product Intent Specification

## Problem Statement

O servico de calculadora atualmente expoe operacoes matematicas via API REST, incluindo o padrao de operacoes dedicadas sob `/api/calc`, mas nao oferece uma operacao equivalente para divisao.

Usuarios da API precisam enviar dois operandos numericos e receber o resultado da divisao entre eles, seguindo o mesmo padrao de contrato, validacao e comportamento ja utilizado pelos endpoints existentes da calculadora e pela spec `calc-multiply`.

## Target Users

- Consumidores da API REST de calculadora.
- Desenvolvedores que usam o projeto como demonstracao de Quarkus, REST, Bean Validation, OpenAPI e fluxo SLDD.
- Testes automatizados que validam operacoes matematicas expostas pela API.

## Formalized Exploration Decisions

- A funcionalidade sera adicionada como uma nova operacao da calculadora existente.
- A feature deve seguir o padrao da spec `calc-multiply`.
- O endpoint devera ser exposto sob o base path existente:

```text
/api/calc
```

- O caminho da operacao sera:

```text
POST /api/calc/divide
```

- A entrada deve ser JSON.
- Os campos de entrada devem ser:

```json
{
  "dividend": 10.25,
  "divisor": 2.5
}
```

- A operacao matematica sera:

```text
dividend / divisor
```

- Os operandos devem ser tratados como numeros decimais compativeis com o padrao atual do projeto.
- A resposta deve ser JSON.
- O campo de resposta sera:

```json
{
  "quotient": 4.1
}
```

- Entradas invalidas, ausentes, nulas ou com tipos incompativeis devem resultar em `400 Bad Request`, seguindo o comportamento padrao ja existente.
- Requisicoes com `divisor` igual a zero devem resultar em `400 Bad Request`.
- O endpoint deve aparecer na documentacao OpenAPI exposta pela aplicacao.

## Success Metrics

- Consumidores conseguem chamar `POST /api/calc/divide` com dois operandos validos e recebem o resultado correto da divisao.
- A resposta usa o campo `quotient`.
- Requisicoes invalidas recebem `400 Bad Request`.
- Requisicoes com `divisor` igual a zero recebem `400 Bad Request`.
- A documentacao OpenAPI inclui o novo endpoint.
- Os testes automatizados relevantes passam.

## Out of Scope

- Criar operacoes de multiplicacao, soma, subtracao ou outras operacoes matematicas.
- Criar envelope customizado de erro.
- Introduzir autenticacao, autorizacao ou rate limiting.
- Alterar a estrutura geral da API de calculadora.
- Alterar contratos dos endpoints existentes.
- Criar interface grafica ou cliente externo.
- Alterar dependencias ou versoes do Quarkus sem necessidade direta.
- Definir regra de arredondamento, escala fixa ou formatacao monetaria especifica para divisoes nao exatas.

## Risks and Assumptions

- Assume-se que `dividend / divisor` e a operacao correta da divisao.
- Assume-se que o uso de numeros decimais deve seguir o mesmo comportamento das operacoes existentes.
- Assume-se que nao ha necessidade de regra de arredondamento ou escala fixa nesta feature.
- O principal risco funcional e a tentativa de dividir por zero; isso deve ser mitigado retornando `400 Bad Request`.
- Divisoes nao exatas podem exigir politica de arredondamento no futuro, mas essa decisao esta fora do escopo aprovado.

## Acceptance Criteria (Given/When/Then)

### Cenario 1 - Divisao valida

**Given** a API de calculadora esta disponivel
**And** o consumidor possui dois operandos numericos validos
**And** o `divisor` e diferente de zero
**When** o consumidor envia uma requisicao `POST /api/calc/divide` com `dividend` e `divisor`
**Then** a API deve retornar `200 OK`
**And** a resposta deve conter o campo `quotient`
**And** `quotient` deve ser igual a `dividend / divisor`.

### Cenario 2 - Primeiro operando ausente

**Given** a API de calculadora esta disponivel
**When** o consumidor envia uma requisicao `POST /api/calc/divide` sem o campo `dividend`
**Then** a API deve retornar `400 Bad Request`.

### Cenario 3 - Segundo operando ausente

**Given** a API de calculadora esta disponivel
**When** o consumidor envia uma requisicao `POST /api/calc/divide` sem o campo `divisor`
**Then** a API deve retornar `400 Bad Request`.

### Cenario 4 - Operando nulo

**Given** a API de calculadora esta disponivel
**When** o consumidor envia uma requisicao `POST /api/calc/divide` com qualquer operando nulo
**Then** a API deve retornar `400 Bad Request`.

### Cenario 5 - Tipo invalido

**Given** a API de calculadora esta disponivel
**When** o consumidor envia uma requisicao `POST /api/calc/divide` com um operando de tipo incompativel com numero
**Then** a API deve retornar `400 Bad Request`.

### Cenario 6 - Divisor zero

**Given** a API de calculadora esta disponivel
**When** o consumidor envia uma requisicao `POST /api/calc/divide` com `divisor` igual a zero
**Then** a API deve retornar `400 Bad Request`.

### Cenario 7 - Documentacao OpenAPI

**Given** a aplicacao expoe documentacao OpenAPI
**When** a documentacao OpenAPI e consultada
**Then** ela deve listar o endpoint `POST /api/calc/divide`.
