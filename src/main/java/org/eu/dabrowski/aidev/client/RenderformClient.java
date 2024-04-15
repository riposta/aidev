package org.eu.dabrowski.aidev.client;

import com.fasterxml.jackson.databind.JsonNode;
import dabrowski.eu.org.aidevs.renderform.model.RenderRenderRequest;
import dabrowski.eu.org.aidevs.renderform.model.RenderRenderResponse;
import org.eu.dabrowski.aidev.configuration.FeignClientConfiguration;
import org.eu.dabrowski.aidev.model.aidevs.AnswerRequest;
import org.eu.dabrowski.aidev.model.aidevs.TaskResponse;
import org.eu.dabrowski.aidev.model.aidevs.TokenRequest;
import org.eu.dabrowski.aidev.model.aidevs.TokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;


@FeignClient(value = "renderform-client", url = "${client.renderform.url}", configuration = FeignClientConfiguration.class)
public interface RenderformClient {

    @PostMapping(value = "/render")
    RenderRenderResponse render(@RequestHeader("x-api-key") String key, @RequestBody RenderRenderRequest request);


}