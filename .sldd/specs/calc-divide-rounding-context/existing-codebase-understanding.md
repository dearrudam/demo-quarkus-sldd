# Repository Structure Overview

- The project is a Java 25 Quarkus 3.35.3 REST service.
- Main calculator code is under `src/main/java/org/soujava/demo/`.
- REST tests are under `src/test/java/org/soujava/demo/` and use Quarkus Test with RestAssured.
- Existing SLDD artifacts exist for previous calculator features under `.sldd/specs/` and `docs/specs/`.

# Architecture Summary

- `CalcResource` exposes JSON REST endpoints under `/api/calc`.
- `CalcService` owns arithmetic behavior for sum, subtract, multiply, and divide.
- Request and response payloads are Java records using `BigDecimal` for numeric values.
- Bean Validation annotations are used on request records to reject missing or null required numeric fields.
- Division currently rejects divisor zero in `CalcResource` before calling `CalcService.divide`.

# Conventions to Preserve

- Use `./mvnw`, not system `mvn`.
- Keep endpoint behavior covered by `CalcResourceTest`.
- Preserve existing contracts for `/api/calc/sum`, `/api/calc/subtract`, and `/api/calc/multiply`.
- Preserve the current package `org.soujava.demo` and simple record-based request/response style.
- Return `400 Bad Request` for invalid request payloads or invalid division inputs.

# Integration Points

- `/api/calc/divide` receives `DivideRequest` and returns `DivideResponse`.
- `DivideRequest` currently contains `dividend` and `divisor` only.
- `CalcResource.divide` validates zero divisor and delegates calculation to `CalcService.divide`.
- `CalcService.divide` currently calls `BigDecimal.divide(divisor)` without scale or rounding mode.
- OpenAPI exposure is tested by asserting `/api/calc/divide` appears in `/q/openapi`.

# Risks and Unknowns

- `BigDecimal.divide(divisor)` can throw `ArithmeticException` for non-terminating decimal expansions.
- JSON deserialization of Java enum values is expected to accept enum names such as `HALF_UP` and reject unknown names with `400 Bad Request`.
- `RoundingMode.UNNECESSARY` can throw when rounding is required and should be mapped to `400 Bad Request` for invalid operation inputs.
- Negative `scale` values should be rejected to avoid surprising decimal-place behavior.
- Existing tests assert numeric values using floating matchers; new tests should remain precise enough for expected JSON numeric responses.

# Context to Carry Into Steps 02-06

- Scope the change to division only.
- Add a dedicated optional request object for division rounding context.
- Default missing context or missing fields to `scale = 2` and `roundingMode = HALF_UP`.
- Keep production changes minimal and aligned with the existing simple resource/service structure.
- Step 04 should add Red tests before implementation and must not change production code.
