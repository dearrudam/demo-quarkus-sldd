# Problem Statement

The calculator division endpoint can fail or behave unpredictably for decimal divisions that require rounding, such as `1 / 3`, because the current division behavior does not let API clients define how non-terminating decimal results should be rounded.

The product intent is to let division clients provide an optional dedicated rounding context in the request body, while preserving safe defaults when the context or its fields are omitted.

# Target Users

- API consumers using `/api/calc/divide`.
- Developers integrating with the calculator service who need predictable decimal division behavior.
- Testers validating calculation results and invalid rounding inputs.

# Formalized Exploration Decisions

- Only the division operation is in scope.
- Sum, subtract, and multiply behavior must remain unchanged.
- The division request contract may change.
- Rounding configuration must be enveloped in a dedicated object.
- The dedicated object is named `roundingContext`.
- `roundingContext` contains:
  - `scale`: number of decimal places to keep.
  - `roundingMode`: enum value for the rounding strategy.
- `roundingMode` must be modeled as an enum, aligned with Java `RoundingMode` values.
- If `roundingContext` is absent, default values are used.
- If an individual field inside `roundingContext` is absent, the default for that field is used.
- Recommended defaults:
  - `scale = 2`
  - `roundingMode = HALF_UP`

# Success Metrics

- `/api/calc/divide` returns a rounded decimal result for divisions that require rounding.
- Existing valid division requests without `roundingContext` continue to work using defaults.
- Requests with valid `roundingContext` values return results matching the requested scale and rounding mode.
- Invalid rounding context inputs return `400 Bad Request`.
- Existing sum, subtract, and multiply tests remain unaffected.

# Out of Scope

- Changing sum, subtract, or multiply contracts or behavior.
- Adding global application-level rounding configuration.
- Adding per-request rounding support to non-division endpoints.
- Replacing `BigDecimal` with floating-point types.
- Defining high-level architecture or implementation classes in this step.

# Risks and Assumptions

- Assumption: API consumers understand `scale` as decimal places.
- Assumption: using enum names such as `HALF_UP` in JSON is acceptable.
- Risk: `RoundingMode.UNNECESSARY` may produce an error when rounding is required; this should be treated as a bad request if accepted in the enum.
- Risk: negative `scale` values could produce surprising behavior and should be rejected.
- Risk: changing the division request contract must preserve compatibility for requests that only send `dividend` and `divisor`.

# Acceptance Criteria (Given/When/Then)

## Default rounding context

Given a division request without `roundingContext`
When the client posts to `/api/calc/divide`
Then the service uses `scale = 2` and `roundingMode = HALF_UP`

## Explicit rounding context

Given a division request with `roundingContext.scale = 3` and `roundingContext.roundingMode = HALF_UP`
When the client divides `1` by `3`
Then the response quotient is rounded to `0.333`

## Partial rounding context

Given a division request with `roundingContext.scale = 4` and no `roundingMode`
When the client divides `1` by `3`
Then the service uses `roundingMode = HALF_UP`

Given a division request with `roundingContext.roundingMode = DOWN` and no `scale`
When the client divides `1` by `3`
Then the service uses `scale = 2`

## Invalid scale

Given a division request with a negative `roundingContext.scale`
When the client posts to `/api/calc/divide`
Then the service returns `400 Bad Request`

## Invalid rounding mode

Given a division request with an unknown `roundingContext.roundingMode`
When the client posts to `/api/calc/divide`
Then the service returns `400 Bad Request`

## Division by zero

Given a division request with divisor `0`
When the client posts to `/api/calc/divide`
Then the service returns `400 Bad Request`

## Scope preservation

Given requests to sum, subtract, or multiply
When clients call their existing endpoints
Then their contracts and calculation behavior remain unchanged
