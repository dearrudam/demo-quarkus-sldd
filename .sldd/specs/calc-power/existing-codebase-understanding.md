# Existing Codebase Understanding

## Repository Structure Overview

**Technology Stack:**
- **Framework**: Quarkus 3.35.3
- **Language**: Java 25 (release)
- **Build**: Maven via `./mvnw`
- **REST Framework**: Quarkus REST + Jackson
- **Validation**: Hibernate Validator
- **Dependency Injection**: Quarkus Arc (CDI)

**Project Layout:**
```
src/main/java/org/soujava/demo/
├── CalcResource.java        (REST endpoints)
├── CalcService.java         (business logic, @ApplicationScoped)
├── {Operation}Request.java  (records for input validation)
└── {Operation}Response.java (records for output)
```

**Existing Operations:** sum, subtract, multiply, divide (all implemented)

## Architecture Summary

**Design Pattern: Request-Service-Response**
- **CalcResource** (JAX-RS endpoint) receives `@Valid` request records, delegates to `CalcService`
- **CalcService** (singleton bean) performs the math operation
- **Request/Response records** use Jakarta Validation (`@NotNull`)

**Current Endpoints:**
```
POST /api/calc/sum          → SumRequest {firstOperand, secondOperand} → {sum}
POST /api/calc/subtract     → SubtractRequest {firstOperand, secondOperand} → {difference}
POST /api/calc/multiply     → MultiplyRequest {multiplier, multiplicand} → {product}
POST /api/calc/divide       → DivideRequest {dividend, divisor, roundingContext?} → {quotient}
```

**Numeric Type:** All operations use `java.math.BigDecimal` for precision.

**Error Handling:** Invalid requests (validation failures, division by zero) return `400 Bad Request`.

## Conventions to Preserve

1. **Records as DTO containers** — Use records for all request/response classes.
2. **@NotNull on required fields** — All operands are required; validation is declarative.
3. **POST-only endpoints** — All calc operations are POST.
4. **Path pattern** — Operations live under `/api/calc/{operation}`.
5. **JSON field naming** — Match request intent (e.g., `multiplier` vs. `multiplicand`, not generic `operand1/operand2`).
6. **BigDecimal for all arithmetic** — Ensures precision; no float/double in public API.
7. **Response wrapping** — Each operation returns a record with a single field named for the operation result.
8. **No null checks in responses** — Service returns BigDecimal; constraint violation throws exception caught by JAX-RS.

## Integration Points

**Existing Codebase Dependencies:**
- CalcService is already in place; power operation will be a new public method.
- CalcResource is already structured to route; power endpoint is a new @POST method.
- Jackson is configured (no custom serializers needed).
- Hibernate Validator is active (records with @NotNull will be validated automatically).

**OpenAPI/Swagger:**
- Quarkus automatically generates OpenAPI schema; new endpoint will appear at `/q/openapi` once implemented.

**Testing:**
- Unit test pattern established in `src/test/java/org/soujava/demo/`.
- Integration tests use Quarkus test infra (surefire, skipITs default to true).

## Risks and Unknowns

| Risk | Mitigation | Status |
|------|-----------|--------|
| **BigDecimal.pow() semantics** | BigDecimal has a `pow(int)` method; exponent is int, not BigDecimal. Needs design review in Step 02. | ⚠️ To clarify |
| **Precision/Scale handling** | Unlike divide, power has no explicit scale parameter. Step 03 must define rounding behavior (if any). | ⚠️ To clarify |
| **Exponent range** | Very large exponents may cause performance or overflow. Step 03 must document limits. | ⚠️ Acceptable risk |
| **Integration test setup** | Integration tests require container/dev env setup; Step 04+ must verify in CI or locally. | 🟢 Standard Quarkus pattern |

## Context to Carry Into Steps 02–06

1. **Follow the request-service-response pattern** — No deviations.
2. **Use BigDecimal throughout** — Even if exponent must be coerced to int, the public request field should be BigDecimal for API consistency.
3. **Validate input early** — Declare @NotNull on request fields; let framework handle it.
4. **No custom serialization** — Jackson defaults work; records are supported natively.
5. **Preserve endpoint naming** — `POST /api/calc/power` with fields `{base, exponent}` and `{power}` (per Step 01 intent).
6. **OpenAPI auto-generation** — Quarkus will expose the endpoint automatically once code is in place; no manual schema updates.
7. **Test structure** — Existing CalcResourceTest.java pattern can be reused for power tests.
