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
}
