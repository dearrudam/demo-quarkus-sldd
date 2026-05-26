# Requirement-to-Design Traceability

- Division-only scope is covered by changing only `DivideRequest`, a new division-specific rounding context model, `CalcResource.divide`, `CalcService.divide`, and division-focused tests.
- Dedicated request envelope is covered by adding optional `roundingContext` to `DivideRequest`.
- Decimal-place rounding is covered by `roundingContext.scale` and `BigDecimal.divide(divisor, scale, roundingMode)`.
- Enum rounding mode is covered by using `java.math.RoundingMode` as the request model type.
- Missing context or missing fields are covered by default resolution before service invocation.
- Invalid `scale`, invalid enum value, division by zero, and impossible rounding are covered by explicit error handling or JSON binding behavior.
- Existing non-division endpoints are covered by regression tests that must remain unchanged and passing.

# API Contracts

## Request: `/api/calc/divide`

Existing required fields remain:

```json
{
  "dividend": 1,
  "divisor": 3
}
```

New optional contract:

```json
{
  "dividend": 1,
  "divisor": 3,
  "roundingContext": {
    "scale": 2,
    "roundingMode": "HALF_UP"
  }
}
```

## Response

The response contract remains unchanged:

```json
{
  "quotient": 0.33
}
```

## Defaults

- Missing `roundingContext` means `scale = 2` and `roundingMode = HALF_UP`.
- Missing `roundingContext.scale` means `scale = 2`.
- Missing `roundingContext.roundingMode` means `roundingMode = HALF_UP`.

## Accepted Rounding Modes

Use Java `RoundingMode` enum names in JSON:

- `UP`
- `DOWN`
- `CEILING`
- `FLOOR`
- `HALF_UP`
- `HALF_DOWN`
- `HALF_EVEN`
- `UNNECESSARY`

# Data Models

## `DivideRequest`

Fields:

- `@NotNull BigDecimal dividend`
- `@NotNull BigDecimal divisor`
- `RoundingContext roundingContext`

`roundingContext` is nullable so the default context can apply when omitted.

## `RoundingContext`

Fields:

- `Integer scale`
- `RoundingMode roundingMode`

Both fields are nullable so field-level defaults can apply independently.

## `DivideResponse`

No change:

- `BigDecimal quotient`

## `CalcService`

Division should accept explicit rounding inputs:

- `BigDecimal divide(BigDecimal dividend, BigDecimal divisor, int scale, RoundingMode roundingMode)`

# Error Model

- Missing or null `dividend` returns `400 Bad Request` through Bean Validation.
- Missing or null `divisor` returns `400 Bad Request` through Bean Validation.
- Divisor equal to zero returns `400 Bad Request`.
- Negative `roundingContext.scale` returns `400 Bad Request`.
- Unknown `roundingContext.roundingMode` returns `400 Bad Request` through JSON enum binding.
- `RoundingMode.UNNECESSARY` with a division that requires rounding returns `400 Bad Request` by mapping arithmetic failure at the resource boundary.
- Other invalid arithmetic conditions caused by the requested rounding context should return `400 Bad Request` rather than `500 Internal Server Error`.

# Test Strategy

- Step 04 must add failing tests before production changes.
- Tests should exercise `/api/calc/divide` through RestAssured to validate the public HTTP contract.
- Existing tests for sum, subtract, multiply, validation, divisor zero, and OpenAPI exposure should remain passing.
- Tests should verify both defaulting behavior and explicit rounding behavior.
- Tests should verify invalid context behavior with HTTP status `400`.
- Tests should avoid implementation details and assert observable API behavior.

# Test Scenario Catalog

- `POST /api/calc/divide` with `1 / 3` and no `roundingContext` returns `quotient = 0.33`.
- `POST /api/calc/divide` with `1 / 3`, `scale = 3`, and `roundingMode = HALF_UP` returns `quotient = 0.333`.
- `POST /api/calc/divide` with `1 / 3`, `scale = 4`, and no `roundingMode` returns `quotient = 0.3333`.
- `POST /api/calc/divide` with `1 / 3`, `roundingMode = DOWN`, and no `scale` returns `quotient = 0.33`.
- `POST /api/calc/divide` with a negative `scale` returns `400 Bad Request`.
- `POST /api/calc/divide` with an unknown `roundingMode` returns `400 Bad Request`.
- `POST /api/calc/divide` with `roundingMode = UNNECESSARY` for `1 / 3` returns `400 Bad Request`.
- Existing divisor-zero test remains `400 Bad Request`.
- Existing sum, subtract, and multiply tests remain unchanged and passing.

# Dependency and Version Policy

- The current dependency set is sufficient.
- No new runtime dependency is required.
- No new test dependency is required.
- Java `BigDecimal`, `RoundingMode`, Bean Validation, Quarkus REST, Jackson enum binding, Quarkus Test, and RestAssured already cover the needed behavior.
- Version compatibility remains tied to the current Maven configuration: Quarkus 3.35.3 and Java release 25.
- Maintenance impact is limited to one new small request model and explicit division rounding behavior.

# Ordered Implementation Plan

1. In Step 04, add Red tests for the division rounding context contract and invalid inputs without modifying production code.
2. Confirm Step 04 is Red for the expected missing behavior.
3. In Step 05, add the `RoundingContext` request model.
4. In Step 05, extend `DivideRequest` with optional `roundingContext`.
5. In Step 05, update `CalcService.divide` to receive `scale` and `RoundingMode` and call `BigDecimal.divide(divisor, scale, roundingMode)`.
6. In Step 05, update `CalcResource.divide` to resolve defaults, reject negative scale, preserve divisor-zero validation, and map arithmetic failures to `400 Bad Request`.
7. In Step 05, run the test suite and confirm Green.
8. In Step 06, verify behavior, artifacts, and regression coverage before Go/No-Go.
