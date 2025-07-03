package org.grnet.pidmr.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.grnet.pidmr.dto.Validity;
import org.grnet.pidmr.service.utils.ElasticSearchClientProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@ApplicationScoped
public class ElasticSearchService {

    @Inject
    ElasticSearchClientProvider clientProvider;
    public void sendSingleDoc(Validity validity) throws Exception {
        RestClient client = clientProvider.getClient();
        ObjectMapper mapper = new ObjectMapper();

        // Serialize your document as JSON
        String json = mapper.writeValueAsString(validity);

        // POST to the index's _doc endpoint
        Request request = new Request("POST", "/demo-pidmr/_doc");
        request.setJsonEntity(json);

        // Optional: refresh index after insertion for immediate availability
        request.addParameter("refresh", "true");

        Response response = client.performRequest(request);
        String responseBody = new String(response.getEntity().getContent().readAllBytes());
        System.out.println("Response body:\n" + responseBody);
    }
}
