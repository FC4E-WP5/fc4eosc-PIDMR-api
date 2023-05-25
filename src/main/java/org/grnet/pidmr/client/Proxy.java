package org.grnet.pidmr.client;

import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;

import javax.ws.rs.POST;
import javax.ws.rs.core.MediaType;

//@RegisterRestClient
//@Path("/")
public interface Proxy {

    @POST
    Response sendMultipart(@RestForm @PartType(MediaType.APPLICATION_FORM_URLENCODED) Parameters parameters);

    class Parameters {
        public String hdl;
        public String clear;
    }
}

