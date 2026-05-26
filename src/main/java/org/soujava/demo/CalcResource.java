package org.soujava.demo;

import java.math.BigDecimal;
import java.math.RoundingMode;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/calc")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CalcResource {

    private static final int DEFAULT_DIVIDE_SCALE = 2;
    private static final RoundingMode DEFAULT_DIVIDE_ROUNDING_MODE = RoundingMode.HALF_UP;

    @Inject
    CalcService calcService;

    @POST
    @Path("/sum")
    public SumResponse sum(@Valid SumRequest request) {
        return new SumResponse(calcService.sum(request.firstOperand(), request.secondOperand()));
    }

    @POST
    @Path("/subtract")
    public SubtractResponse subtract(@Valid SubtractRequest request) {
        return new SubtractResponse(calcService.subtract(request.firstOperand(), request.secondOperand()));
    }

    @POST
    @Path("/multiply")
    public MultiplyResponse multiply(@Valid MultiplyRequest request) {
        return new MultiplyResponse(calcService.multiply(request.multiplier(), request.multiplicand()));
    }

    @POST
    @Path("/divide")
    public DivideResponse divide(@Valid DivideRequest request) {
        if (BigDecimal.ZERO.compareTo(request.divisor()) == 0) {
            throw new BadRequestException();
        }
        var roundingContext = request.roundingContext();
        var scale = roundingContext == null || roundingContext.scale() == null
            ? DEFAULT_DIVIDE_SCALE
            : roundingContext.scale();
        if (scale < 0) {
            throw new BadRequestException();
        }
        var roundingMode = roundingContext == null || roundingContext.roundingMode() == null
            ? DEFAULT_DIVIDE_ROUNDING_MODE
            : roundingContext.roundingMode();
        try {
            return new DivideResponse(calcService.divide(request.dividend(), request.divisor(), scale, roundingMode));
        } catch (ArithmeticException e) {
            throw new BadRequestException(e);
        }
    }

    @POST
    @Path("/power")
    public PowerResponse power(@Valid PowerRequest request) {
        return new PowerResponse(calcService.power(request.base(), request.exponent()));
    }
}
