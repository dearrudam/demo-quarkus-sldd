package org.soujava.demo;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class CalcResourceTest {

    @Test
    void shouldReturnSumForValidPayload() {
        given()
            .contentType("application/json")
            .body("""
                {
                  "firstOperand": 10.25,
                  "secondOperand": 5.75
                }
                """)
            .when()
            .post("/api/calc/sum")
            .then()
            .statusCode(200)
            .body("sum", is(16.00f));
    }

    @Test
    void shouldReturnDifferenceForValidSubtractPayload() {
        given()
            .contentType("application/json")
            .body("""
                {
                  "firstOperand": 10.25,
                  "secondOperand": 5.75
                }
                """)
            .when()
            .post("/api/calc/subtract")
            .then()
            .statusCode(200)
            .body("difference", is(4.50f));
    }

    @Test
    void shouldReturnProductForValidMultiplyPayload() {
        given()
            .contentType("application/json")
            .body("""
                {
                  "multiplier": 10.25,
                  "multiplicand": 5.75
                }
                """)
            .when()
            .post("/api/calc/multiply")
            .then()
            .statusCode(200)
            .body("product", is(58.9375f));
    }

    @Test
    void shouldReturnQuotientForValidDividePayload() {
        given()
            .contentType("application/json")
            .body("""
                {
                  "dividend": 10.25,
                  "divisor": 2.5
                }
                """)
            .when()
            .post("/api/calc/divide")
            .then()
            .statusCode(200)
            .body("quotient", is(4.1f));
    }

    @Test
    void shouldUseDefaultRoundingContextWhenDividePayloadOmitsIt() {
        given()
            .contentType("application/json")
            .body("""
                {
                  "dividend": 1,
                  "divisor": 3
                }
                """)
            .when()
            .post("/api/calc/divide")
            .then()
            .statusCode(200)
            .body("quotient", is(0.33f));
    }

    @Test
    void shouldUseExplicitRoundingContextForDividePayload() {
        given()
            .contentType("application/json")
            .body("""
                {
                  "dividend": 1,
                  "divisor": 3,
                  "roundingContext": {
                    "scale": 3,
                    "roundingMode": "HALF_UP"
                  }
                }
                """)
            .when()
            .post("/api/calc/divide")
            .then()
            .statusCode(200)
            .body("quotient", is(0.333f));
    }

    @Test
    void shouldUseDefaultRoundingModeWhenDividePayloadOmitsIt() {
        given()
            .contentType("application/json")
            .body("""
                {
                  "dividend": 1,
                  "divisor": 3,
                  "roundingContext": {
                    "scale": 4
                  }
                }
                """)
            .when()
            .post("/api/calc/divide")
            .then()
            .statusCode(200)
            .body("quotient", is(0.3333f));
    }

    @Test
    void shouldUseDefaultScaleWhenDividePayloadOmitsIt() {
        given()
            .contentType("application/json")
            .body("""
                {
                  "dividend": 1,
                  "divisor": 3,
                  "roundingContext": {
                    "roundingMode": "DOWN"
                  }
                }
                """)
            .when()
            .post("/api/calc/divide")
            .then()
            .statusCode(200)
            .body("quotient", is(0.33f));
    }

    @Test
    void shouldReturnBadRequestWhenDivideRoundingScaleIsNegative() {
        given()
            .contentType("application/json")
            .body("""
                {
                  "dividend": 1,
                  "divisor": 3,
                  "roundingContext": {
                    "scale": -1,
                    "roundingMode": "HALF_UP"
                  }
                }
                """)
            .when()
            .post("/api/calc/divide")
            .then()
            .statusCode(400);
    }

    @Test
    void shouldReturnBadRequestWhenDivideRoundingModeIsUnknown() {
        given()
            .contentType("application/json")
            .body("""
                {
                  "dividend": 1,
                  "divisor": 3,
                  "roundingContext": {
                    "scale": 2,
                    "roundingMode": "UNKNOWN"
                  }
                }
                """)
            .when()
            .post("/api/calc/divide")
            .then()
            .statusCode(400);
    }

    @Test
    void shouldReturnBadRequestWhenDivideRoundingIsUnnecessaryButRequired() {
        given()
            .contentType("application/json")
            .body("""
                {
                  "dividend": 1,
                  "divisor": 3,
                  "roundingContext": {
                    "scale": 2,
                    "roundingMode": "UNNECESSARY"
                  }
                }
                """)
            .when()
            .post("/api/calc/divide")
            .then()
            .statusCode(400);
    }

    @Test
    void shouldReturnBadRequestWhenFirstOperandIsMissing() {
        given()
            .contentType("application/json")
            .body("""
                {
                  "secondOperand": 5.75
                }
                """)
            .when()
            .post("/api/calc/sum")
            .then()
            .statusCode(400);
    }

    @Test
    void shouldReturnBadRequestWhenSubtractFirstOperandIsMissing() {
        given()
            .contentType("application/json")
            .body("""
                {
                  "secondOperand": 5.75
                }
                """)
            .when()
            .post("/api/calc/subtract")
            .then()
            .statusCode(400);
    }

    @Test
    void shouldReturnBadRequestWhenMultiplierIsMissing() {
        given()
            .contentType("application/json")
            .body("""
                {
                  "multiplicand": 5.75
                }
                """)
            .when()
            .post("/api/calc/multiply")
            .then()
            .statusCode(400);
    }

    @Test
    void shouldReturnBadRequestWhenDividendIsMissing() {
        given()
            .contentType("application/json")
            .body("""
                {
                  "divisor": 2.5
                }
                """)
            .when()
            .post("/api/calc/divide")
            .then()
            .statusCode(400);
    }

    @Test
    void shouldReturnBadRequestWhenSubtractSecondOperandIsMissing() {
        given()
            .contentType("application/json")
            .body("""
                {
                  "firstOperand": 10.25
                }
                """)
            .when()
            .post("/api/calc/subtract")
            .then()
            .statusCode(400);
    }

    @Test
    void shouldReturnBadRequestWhenMultiplicandIsMissing() {
        given()
            .contentType("application/json")
            .body("""
                {
                  "multiplier": 10.25
                }
                """)
            .when()
            .post("/api/calc/multiply")
            .then()
            .statusCode(400);
    }

    @Test
    void shouldReturnBadRequestWhenDivisorIsMissing() {
        given()
            .contentType("application/json")
            .body("""
                {
                  "dividend": 10.25
                }
                """)
            .when()
            .post("/api/calc/divide")
            .then()
            .statusCode(400);
    }

    @Test
    void shouldReturnBadRequestWhenSubtractFirstOperandIsNull() {
        given()
            .contentType("application/json")
            .body("""
                {
                  "firstOperand": null,
                  "secondOperand": 5.75
                }
                """)
            .when()
            .post("/api/calc/subtract")
            .then()
            .statusCode(400);
    }

    @Test
    void shouldReturnBadRequestWhenMultiplierIsNull() {
        given()
            .contentType("application/json")
            .body("""
                {
                  "multiplier": null,
                  "multiplicand": 5.75
                }
                """)
            .when()
            .post("/api/calc/multiply")
            .then()
            .statusCode(400);
    }

    @Test
    void shouldReturnBadRequestWhenDividendIsNull() {
        given()
            .contentType("application/json")
            .body("""
                {
                  "dividend": null,
                  "divisor": 2.5
                }
                """)
            .when()
            .post("/api/calc/divide")
            .then()
            .statusCode(400);
    }

    @Test
    void shouldReturnBadRequestWhenSecondOperandIsNull() {
        given()
            .contentType("application/json")
            .body("""
                {
                  "firstOperand": 10.25,
                  "secondOperand": null
                }
                """)
            .when()
            .post("/api/calc/sum")
            .then()
            .statusCode(400);
    }

    @Test
    void shouldReturnBadRequestWhenSubtractSecondOperandIsNull() {
        given()
            .contentType("application/json")
            .body("""
                {
                  "firstOperand": 10.25,
                  "secondOperand": null
                }
                """)
            .when()
            .post("/api/calc/subtract")
            .then()
            .statusCode(400);
    }

    @Test
    void shouldReturnBadRequestWhenMultiplicandIsNull() {
        given()
            .contentType("application/json")
            .body("""
                {
                  "multiplier": 10.25,
                  "multiplicand": null
                }
                """)
            .when()
            .post("/api/calc/multiply")
            .then()
            .statusCode(400);
    }

    @Test
    void shouldReturnBadRequestWhenDivisorIsNull() {
        given()
            .contentType("application/json")
            .body("""
                {
                  "dividend": 10.25,
                  "divisor": null
                }
                """)
            .when()
            .post("/api/calc/divide")
            .then()
            .statusCode(400);
    }

    @Test
    void shouldReturnBadRequestWhenPayloadTypeIsInvalid() {
        given()
            .contentType("application/json")
            .body("""
                {
                  "firstOperand": "abc",
                  "secondOperand": 1.25
                }
                """)
            .when()
            .post("/api/calc/sum")
            .then()
            .statusCode(400);
    }

    @Test
    void shouldReturnBadRequestWhenSubtractPayloadTypeIsInvalid() {
        given()
            .contentType("application/json")
            .body("""
                {
                  "firstOperand": "abc",
                  "secondOperand": 1.25
                }
                """)
            .when()
            .post("/api/calc/subtract")
            .then()
            .statusCode(400);
    }

    @Test
    void shouldReturnBadRequestWhenMultiplyPayloadTypeIsInvalid() {
        given()
            .contentType("application/json")
            .body("""
                {
                  "multiplier": "abc",
                  "multiplicand": 1.25
                }
                """)
            .when()
            .post("/api/calc/multiply")
            .then()
            .statusCode(400);
    }

    @Test
    void shouldReturnBadRequestWhenDividePayloadTypeIsInvalid() {
        given()
            .contentType("application/json")
            .body("""
                {
                  "dividend": "abc",
                  "divisor": 2.5
                }
                """)
            .when()
            .post("/api/calc/divide")
            .then()
            .statusCode(400);
    }

    @Test
    void shouldReturnBadRequestWhenDivisorIsZero() {
        given()
            .contentType("application/json")
            .body("""
                {
                  "dividend": 10.25,
                  "divisor": 0
                }
                """)
            .when()
            .post("/api/calc/divide")
            .then()
            .statusCode(400);
    }

    @Test
    void shouldExposeOpenApiContractForCalcSum() {
        given()
            .when()
            .get("/q/openapi")
            .then()
            .statusCode(200)
            .body(notNullValue())
            .body(containsString("/api/calc/sum"));
    }

    @Test
    void shouldExposeOpenApiContractForCalcSubtract() {
        given()
            .when()
            .get("/q/openapi")
            .then()
            .statusCode(200)
            .body(notNullValue())
            .body(containsString("/api/calc/subtract"));
    }

    @Test
    void shouldExposeOpenApiContractForCalcMultiply() {
        given()
            .when()
            .get("/q/openapi")
            .then()
            .statusCode(200)
            .body(notNullValue())
            .body(containsString("/api/calc/multiply"));
    }

    @Test
    void shouldExposeOpenApiContractForCalcDivide() {
        given()
            .when()
            .get("/q/openapi")
            .then()
            .statusCode(200)
            .body(notNullValue())
            .body(containsString("/api/calc/divide"));
    }
}
