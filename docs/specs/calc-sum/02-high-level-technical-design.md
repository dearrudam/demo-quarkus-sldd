# Step 02 — High-Level Technical Design

## Requirements Traceability
- **AC1 (soma com payload válido):** endpoint `POST /api/calc/sum` recebe DTO tipado com `firstOperand` e `secondOperand` (`BigDecimal`) e retorna DTO com `sum`.
- **AC2 (contrato tipado no OpenAPI):** endpoint e DTOs devem produzir schema OpenAPI com request/response tipados e nomes de campos semânticos.
- **AC3/AC4/AC5 (entrada inválida):** campos ausentes, nulos ou tipos incompatíveis devem resultar em `400 Bad Request` conforme comportamento padrão.

## Architecture Diagram
```text
Client
  |
  | HTTP POST /api/calc/sum (JSON)
  v
CalcResource (REST)
  |
  | validação de payload + mapeamento
  v
CalcService (domínio de soma)
  |
  | BigDecimal.add(...)
  v
SumResponse DTO
  |
  v
HTTP 200 (JSON { sum })

OpenAPI/Swagger (dev mode)
  ^
  | metadados de endpoint + schemas DTO tipados
  +-- CalcResource + DTOs
```

## Component Responsibilities
- **CalcResource**
  - Expor `POST /api/calc/sum`.
  - Receber `SumRequest` tipado.
  - Garantir integração com validações de entrada.
  - Delegar a operação de soma ao serviço.
  - Retornar `SumResponse`.
- **CalcService**
  - Implementar regra de negócio de soma com `BigDecimal`.
  - Manter lógica simples, sem efeitos colaterais.
- **DTOs (`SumRequest`, `SumResponse`)**
  - Definir contrato explícito para serialização/desserialização JSON.
  - Facilitar geração de schema OpenAPI com tipagem correta.

## Data Flow
1. Cliente envia `POST /api/calc/sum` com body JSON `{ "firstOperand": <decimal>, "secondOperand": <decimal> }`.
2. Camada REST desserializa o body para `SumRequest`.
3. Validação verifica obrigatoriedade/não nulidade de `firstOperand` e `secondOperand`.
4. `CalcService` executa `firstOperand.add(secondOperand)`.
5. Resultado é encapsulado em `SumResponse { sum }`.
6. API retorna `200 OK` para sucesso.
7. Em caso de payload inválido (campo ausente/nulo/tipo incompatível), API retorna `400 Bad Request`.

## Security and Observability Requirements
- **Security**
  - Sem requisitos novos de autenticação/autorização para este escopo inicial.
  - Manter políticas de segurança existentes da aplicação.
- **Observability**
  - Reaproveitar logs e tratamento padrão da plataforma para requisições inválidas.
  - Não introduzir telemetria customizada nesta etapa.

## Trade-Offs and Alternatives
- **`BigDecimal` vs `double`**
  - Escolha: `BigDecimal`.
  - Ganho: precisão decimal previsível e aderente ao objetivo do endpoint.
  - Custo: maior verbosidade de implementação e potencial overhead de performance (aceitável para escopo atual).
- **Validação padrão vs envelope de erro customizado**
  - Escolha: comportamento padrão de `400`.
  - Ganho: menor complexidade e entrega rápida.
  - Custo: formato de erro pode divergir de futuros padrões corporativos.

## High-Level Test Scenario Map
- **Sucesso**
  - Soma de dois decimais positivos.
  - Soma com sinal misto (positivo + negativo).
  - Soma com escala decimal diferente.
- **Falhas de validação**
  - `firstOperand` ausente.
  - `secondOperand` ausente.
  - `firstOperand` nulo.
  - `secondOperand` nulo.
  - Tipo inválido (string não numérica).
- **Contrato OpenAPI**
  - Verificar presença de `POST /api/calc/sum`.
  - Verificar schema de `SumRequest` com `firstOperand` e `secondOperand` tipados.
  - Verificar schema de `SumResponse` com `sum` tipado.
