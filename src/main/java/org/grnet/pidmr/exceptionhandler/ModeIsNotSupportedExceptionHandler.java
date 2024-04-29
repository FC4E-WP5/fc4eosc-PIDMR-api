package org.grnet.pidmr.exceptionhandler;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.grnet.pidmr.dto.InformativeResponse;
import org.grnet.pidmr.exception.ModeIsNotSupported;
import org.jboss.logging.Logger;


@Provider
public class ModeIsNotSupportedExceptionHandler implements ExceptionMapper<ModeIsNotSupported> {

    private static final Logger LOG = Logger.getLogger(ModeIsNotSupportedExceptionHandler.class);

    @Override
    public Response toResponse(ModeIsNotSupported e) {

        LOG.error("Mode is not supported exception", e);

        var response = new InformativeResponse();
        response.code = 400;
        response.message = e.getMessage();

        return Response.status(400).entity(response).build();
    }
}
