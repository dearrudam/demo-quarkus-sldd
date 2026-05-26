# Step 03 — Low-Level Design and Version Policy

## Requirement-to-Design Traceability

| Requisito Step 01 | Design Step 02 | Cobertura em Step 03 |
| --- | --- | --- |
| Endpoint `POST /api/calc/power` | CalcResource.power() | API Contract: `POST /api/calc/power` |
| Input: base, exponent | PowerRequest record | Data Model: PowerRequest com @NotNull fields |
| Output: power | PowerResponse record | Data Model: PowerResponse com BigDecimal power |
| Operação: base ^ exponent | CalcService.power() | CalcService.power(BigDecimal, BigDecimal) |
| JSON request/response | Jackson + Quarkus REST | Media Type: application/json |
| 400 Bad Request inválido | Bean Validation @Valid | Error: BadRequestException para campos nulos/inválidos |
| Endpoint em OpenAPI | Anotações REST | Quarkus expõe em /q/openapi automaticamente |

## API Contracts

### Endpoint

```http
POST /api/calc/power
Content-Type: application/json
Accept: application/json
```

### Request válido

```json
{
  "base": 2,
  "exponent": 3
}
```

### Response de sucesso

```http
HTTP/1.1 200 OK
Content-Type: application/json
```

```json
{
  "power": 8
}
```

### Operação

```text
power = base ^ exponent
```

Exemplos:
- 2 ^ 3 = 8
- 2.5 ^ 2 = 6.25
- 10 ^ -1 = 0.1
- 0 ^ 0 = 1 (comportamento padrão BigDecimal)

### Request inválido (falta campo)

```http
POST /api/calc/power
Content-Type: application/json

{
  "base": 2
}
```

Response:

```http
HTTP/1.1 400 Bad Request
Content-Type: application/json
```

## Data Models

### PowerRequest

```java
public record PowerRequest(
    @NotNull(message = "base must not be null")
    BigDecimal base,
    
    @NotNull(message = "exponent must not be null")
    BigDecimal exponent
) {}
```

Localização: `src/main/java/org/soujava/demo/PowerRequest.java`

Comportamento:
- Ambos os campos são obrigatórios
- Jackson desserializa JSON em PowerRequest
- Bean Validation falha se algum é nulo
- Tipo inválido causa 400 Bad Request durante desserialização

### PowerResponse

```java
public record PowerResponse(BigDecimal power) {}
```

Localização: `src/main/java/org/soujava/demo/PowerResponse.java`

Comportamento:
- Jackson serializa em JSON com campo `power`

### CalcService.power()

```java
public BigDecimal power(BigDecimal base, BigDecimal exponent) {
    return base.pow(exponent.intValue());
}
```

Localização: `src/main/java/org/soujava/demo/CalcService.java` (método novo)

Comportamento:
- `BigDecimal.pow(int exponent)` requer expoente inteiro
- `exponent.intValue()` converte BigDecimal para int
- Se expoente for muito grande, pode causar `ArithmeticException`
- Será capturado em CalcResource e retornará 400 Bad Request

### CalcResource.power()

```java
@POST
@Path("/power")
public PowerResponse power(@Valid PowerRequest request) {
    return new PowerResponse(calcService.power(request.base(), request.exponent()));
}
```

Localização: `src/main/java/org/soujava/demo/CalcResource.java` (método novo)

Comportamento:
- Recebe PowerRequest validado
- Delega para CalcService
- Retorna PowerResponse
- Exceções são convertidas para 400 Bad Request pelo framework

## Error Model

| Cenário | HTTP Status | Descrição |
| --- | --- | --- |
| Sucesso | 200 OK | Resultado retornado em JSON |
| Campo base ausente | 400 Bad Request | Desserialização falha |
| Campo exponent ausente | 400 Bad Request | Desserialização falha |
| Campo nulo | 400 Bad Request | Bean Validation falha |
| Tipo inválido (string) | 400 Bad Request | Jackson desserialização falha |
| Expoente muito grande | 400 Bad Request | ArithmeticException (futuro tratamento) |

## Test Strategy

Testes devem cobrir:

1. **Sucesso com valores válidos** — validar resposta correta
2. **Campos obrigatórios ausentes** — validar 400 Bad Request
3. **Campos nulos** — validar 400 Bad Request
4. **Tipos inválidos** — validar 400 Bad Request
5. **OpenAPI contrato** — validar endpoint está exposto

## Test Scenario Catalog

Para **Step 04 (Red Phase)**:

1. Sucesso: base=2, exponent=3 → power=8, status 200
2. Sucesso: base=2.5, exponent=2 → power=6.25, status 200
3. Sucesso: base=10, exponent=-1 → power=0.1, status 200
4. Bad Request: falta "base" → status 400
5. Bad Request: falta "exponent" → status 400
6. Bad Request: base=null → status 400
7. Bad Request: exponent=null → status 400
8. Bad Request: tipo inválido (string para base) → status 400
9. OpenAPI: GET /q/openapi contém "/api/calc/power"

## Dependency and Version Policy

### Dependências existentes (reutilizadas)

- **Quarkus REST** (via quarkus-rest)
  - Versão: Mantém versão do projeto (3.35.3+)
  - Uso: Exposição de endpoint REST e serialização JSON
  - Impacto: Nenhum impacto (já usado em todas as operações)

- **Jakarta Validation** (via quarkus-hibernate-validator)
  - Versão: Mantém versão do projeto
  - Uso: Bean Validation (@Valid, @NotNull)
  - Impacto: Nenhum impacto (já usado em todas as operações)

- **Jackson** (via quarkus-jackson)
  - Versão: Mantém versão do projeto
  - Uso: Desserialização JSON e serialização
  - Impacto: Nenhum impacto (já usado em todas as operações)

### Novas dependências

Nenhuma nova dependência é necessária. BigDecimal é parte da `java.math` padrão.

### Constraints de compatibilidade

- **Java 21+**: BigDecimal.pow() está disponível (não há mudança de API entre versões)
- **BigDecimal.pow(int)**: Requer expoente como int (conversão automática de BigDecimal)

## Ordered Implementation Plan

### Step 04 (Red Phase)

1. Adicionar testes falhando para `POST /api/calc/power`.
2. Cobrir sucesso com `power` (base=2, exponent=3 → power=8).
3. Cobrir `base` ausente.
4. Cobrir `exponent` ausente.
5. Cobrir operando nulo.
6. Cobrir tipo inválido.
7. Cobrir OpenAPI.

### Step 05 (Green Phase)

1. Criar `PowerRequest.java` record.
2. Criar `PowerResponse.java` record.
3. Adicionar `CalcService.power(BigDecimal, BigDecimal)`.
4. Adicionar `CalcResource.power(@Valid PowerRequest)`.
5. Executar `./mvnw test`.
6. Ajustar somente o mínimo necessário para passar.
