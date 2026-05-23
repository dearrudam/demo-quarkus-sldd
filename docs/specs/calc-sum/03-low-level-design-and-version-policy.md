# Step 03 — Low-Level Design and Version Policy

## Requirement-to-Design Traceability
- **R1: Expor `POST /api/calc/sum`.**
  - Cobertura: contrato JAX-RS com `@POST`, `@Path("/api/calc/sum")`, `@Consumes`/`@Produces` JSON.
  - Impacto em Step 04/05: testes HTTP precisam validar rota/método corretos antes da implementação final.
- **R2: Usar `BigDecimal` para precisão decimal.**
  - Cobertura: `SumRequest.firstOperand` e `SumRequest.secondOperand` como `BigDecimal`; cálculo em serviço com `BigDecimal#add`.
  - Impacto em Step 04/05: testes devem evitar comparações de `double` e validar soma decimal conforme serialização JSON.
- **R3: Payloads tipados com nomes significativos.**
  - Cobertura: DTOs dedicados `SumRequest` e `SumResponse` com campos `firstOperand`, `secondOperand`, `sum`.
  - Impacto em Step 04/05: testes de contrato e OpenAPI devem validar nomes e tipos dos campos.
- **R4: OpenAPI acessível em dev mode.**
  - Cobertura: uso de extensão OpenAPI/Swagger UI com endpoint documentado via anotações/descoberta automática.
  - Impacto em Step 04/05: testes (ou verificação manual) devem confirmar presença do path e schemas gerados.
- **R5: Entradas inválidas retornam `400`.**
  - Cobertura: validação Bean Validation (`@NotNull`) nos campos do request e ativação de validação em método REST.
  - Impacto em Step 04/05: cenários negativos obrigatórios para ausente/nulo/tipo inválido com status `400`.

## API Contracts
### Endpoint
- **Method/Path:** `POST /api/calc/sum`
- **Content-Type (request):** `application/json`
- **Content-Type (response):** `application/json`

### Request Contract (`SumRequest`)
```json
{
  "firstOperand": 10.25,
  "secondOperand": 5.75
}
```
- `firstOperand`: obrigatório, decimal (`BigDecimal`), não nulo.
- `secondOperand`: obrigatório, decimal (`BigDecimal`), não nulo.

### Response Contract (`SumResponse`)
```json
{
  "sum": 16.00
}
```
- `sum`: decimal (`BigDecimal`) com resultado de `firstOperand + secondOperand`.

### Status Codes
- `200 OK`: soma efetuada com sucesso.
- `400 Bad Request`: payload inválido (ausente, nulo, tipo incompatível).

## Data Models
- **`SumRequest`**
  - `BigDecimal firstOperand`
  - `BigDecimal secondOperand`
  - Constraints: `@NotNull` em ambos.
- **`SumResponse`**
  - `BigDecimal sum`
- **`CalcService` (domínio)**
  - Método sugerido: `BigDecimal sum(BigDecimal firstOperand, BigDecimal secondOperand)`.
  - Regra: retorno direto de `firstOperand.add(secondOperand)` sem arredondamento adicional no MVP.

## Error Model
- **Fonte do erro:** falha de desserialização JSON ou violação de validação de bean.
- **Comportamento:** resposta HTTP `400` usando modelo padrão Quarkus/Jakarta REST para este projeto.
- **Escopo do MVP:** sem envelope customizado de erro.
- **Risco conhecido:** formato de erro default pode diferir de padrão corporativo futuro; adiar padronização para mudança posterior.

## Test Strategy
- **Nível principal (Step 04):** testes de recurso HTTP (foco em contrato) com Quarkus Test.
- **Nível complementar:** teste unitário do serviço de soma (opcional no MVP, recomendado para clareza da regra).
- **Verificação OpenAPI:** assert do documento OpenAPI gerado para endpoint e schemas tipados.
- **Critérios de aprovação:** todos os cenários do catálogo passam no pipeline local (`./mvnw test`) quando ambiente permitir dependências.

## Test Scenario Catalog
### Positivos
1. `firstOperand=10.25`, `secondOperand=5.75` ⇒ `200`, `sum=16.00`.
2. `firstOperand=-2.50`, `secondOperand=1.25` ⇒ `200`, `sum=-1.25`.
3. Escalas diferentes (`1.2` + `3.45`) ⇒ `200`, `sum=4.65`.

### Negativos
4. Ausência de `firstOperand` ⇒ `400`.
5. Ausência de `secondOperand` ⇒ `400`.
6. `firstOperand=null` ⇒ `400`.
7. `secondOperand=null` ⇒ `400`.
8. `firstOperand="abc"` (tipo inválido) ⇒ `400`.

### OpenAPI
9. Documento contém path `POST /api/calc/sum`.
10. Schema `SumRequest` contém `firstOperand` e `secondOperand` numéricos.
11. Schema `SumResponse` contém `sum` numérico.

## Dependency and Version Policy
### Sufficiency of Current Dependencies
- O projeto Quarkus já possui base REST e teste.
- Para atender requisito explícito de OpenAPI/Swagger em dev mode, é necessária extensão de OpenAPI caso ainda não esteja presente no `pom.xml`.

### Required Dependencies (if missing)
1. **`io.quarkus:quarkus-smallrye-openapi`**
   - **Por que:** gerar e expor spec OpenAPI automaticamente.
   - **Compatibilidade:** usar versão gerenciada pelo BOM da própria versão Quarkus do projeto (sem pin manual fora do BOM).
   - **Impacto runtime:** expõe endpoints de documentação OpenAPI/Swagger.
   - **Impacto testes:** permite validar contrato também via spec gerada.
   - **Impacto manutenção:** baixo; dependência padrão do ecossistema Quarkus.

### Version Policy
- Manter versões alinhadas ao BOM da versão de Quarkus já adotada no projeto.
- Evitar pinagem direta de versão para artefatos Quarkus salvo exceção justificada.
- Revisar compatibilidade em upgrades de Quarkus antes de alterar contratos públicos.

## Ordered Implementation Plan
1. **Adicionar/confirmar dependência OpenAPI** (`quarkus-smallrye-openapi`) no `pom.xml`.
2. **Criar DTOs tipados** `SumRequest` e `SumResponse` com campos e constraints definidos.
3. **Criar serviço de domínio** `CalcService` com operação de soma em `BigDecimal`.
4. **Implementar recurso REST** `CalcResource` para `POST /api/calc/sum` delegando ao serviço.
5. **Escrever testes Step 04 (red phase)** para cenários positivos/negativos e contrato OpenAPI.
6. **Implementar ajustes mínimos Step 05** até testes passarem.
7. **Executar suíte local** (`./mvnw test`) e registrar evidências para Step 06.
