package org.grnet.pidmr.exceptionhandler;

import org.grnet.pidmr.dto.InformativeResponse;
import org.jboss.logging.Logger;

import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ServerErrorExceptionMapper implements ExceptionMapper<ServerErrorException> {

    private static final Logger LOG = Logger.getLogger(ServerErrorExceptionMapper.class);

    @Override
    public Response toResponse(ServerErrorException e) {

        LOG.error("Server Error", e);

        InformativeResponse response = new InformativeResponse();
        response.message = e.getMessage();
        response.code = e.getResponse().getStatus();
        return Response.status(e.getResponse().getStatus()).entity(response).build();
    }
}
