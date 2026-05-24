package org.soujava.demo;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/calc")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CalcResource {

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
}
