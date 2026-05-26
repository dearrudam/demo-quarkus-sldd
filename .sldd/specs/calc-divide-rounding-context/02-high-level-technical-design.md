# Requirements Traceability

- Step 01 requires the change to apply only to `/api/calc/divide`; the design keeps sum, subtract, and multiply unchanged.
- Step 01 requires an optional dedicated `roundingContext` object; the design adds it to the division request only.
- Step 01 requires `scale` and enum `roundingMode`; the design models `roundingMode` with Java `RoundingMode` and `scale` as an integer decimal-place count.
- Step 01 requires defaults when the context or individual fields are missing; the design resolves defaults at the division boundary before calling arithmetic logic.
- Step 01 requires invalid scale, invalid rounding mode, divisor zero, and impossible `UNNECESSARY` rounding to return `400 Bad Request`.

# Architecture Diagram

```text
Client
  |
  | POST /api/calc/divide
  | { dividend, divisor, roundingContext? }
  v
CalcResource.divide
  |-- validates divisor != 0
  |-- resolves rounding defaults
  |-- maps invalid arithmetic/rounding inputs to 400
  v
CalcService.divide(dividend, divisor, scale, roundingMode)
  |
  | BigDecimal.divide(divisor, scale, roundingMode)
  v
DivideResponse { quotient }
```

# Component Responsibilities

- `DivideRequest`: represents the public division request contract with required `dividend` and `divisor`, plus optional `roundingContext`.
- `RoundingContext`: represents optional rounding choices for division, with nullable `scale` and nullable enum `roundingMode` so defaults can apply per field.
- `CalcResource`: remains the HTTP boundary, validates division-specific request concerns, resolves defaults, and converts invalid division operations to `400 Bad Request`.
- `CalcService`: performs arithmetic using explicit scale and rounding mode for division.
- `DivideResponse`: remains unchanged and returns the calculated `quotient`.

# Data Flow

1. Client submits `dividend`, `divisor`, and optionally `roundingContext` to `/api/calc/divide`.
2. JSON binding maps `roundingContext.roundingMode` to a Java enum value; unknown enum values fail request binding and return `400 Bad Request`.
3. Bean Validation rejects missing or null `dividend` and `divisor`.
4. Resource validation rejects divisor zero.
5. Resource validation rejects negative `scale` when provided.
6. Resource defaulting uses `scale = 2` when context or field is missing.
7. Resource defaulting uses `roundingMode = HALF_UP` when context or field is missing.
8. Service divides with explicit scale and rounding mode.
9. If the selected mode cannot satisfy the operation, such as `UNNECESSARY` with `1 / 3`, the resource returns `400 Bad Request`.
10. Successful responses return `DivideResponse` with the rounded quotient.

# Security and Observability Requirements

- No authentication or authorization changes are required.
- Invalid request payloads must not expose stack traces to clients.
- Existing HTTP status conventions should be preserved: validation and invalid arithmetic inputs return `400 Bad Request`.
- No new logging requirement is introduced for this small calculator feature.

# Trade-Offs and Alternatives

- `scale` is selected instead of `precision` because API consumers usually expect decimal places rather than significant digits.
- Per-request `roundingContext` is selected instead of application-level configuration because the endpoint contract may change and callers may need different rounding behavior per operation.
- A dedicated `roundingContext` object is selected instead of top-level `scale` and `roundingMode` fields to keep the contract extensible and explicit.
- Java `RoundingMode` is selected instead of a custom enum to avoid duplicating platform semantics and reduce implementation code.
- The design keeps default resolution in the resource layer to keep `CalcService` focused on arithmetic with explicit inputs.

# High-Level Test Scenario Map

- Existing division request without `roundingContext` returns a default rounded quotient.
- Division with explicit `scale = 3` and `roundingMode = HALF_UP` returns three decimal places for `1 / 3`.
- Division with only `scale` uses default `HALF_UP`.
- Division with only `roundingMode` uses default scale `2`.
- Negative `scale` returns `400 Bad Request`.
- Unknown `roundingMode` returns `400 Bad Request` during request binding.
- `UNNECESSARY` returns `400 Bad Request` when the operation requires rounding.
- Division by zero continues to return `400 Bad Request`.
- Sum, subtract, and multiply regression tests continue passing unchanged.
