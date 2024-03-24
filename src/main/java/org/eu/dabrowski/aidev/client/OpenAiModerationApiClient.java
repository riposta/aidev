package org.eu.dabrowski.aidev.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.eu.dabrowski.aidev.configuration.FeignClientConfiguration;
import org.eu.dabrowski.aidev.model.aidevs.AnswerResponse;
import org.eu.dabrowski.aidev.model.aidevs.TaskResponse;
import org.eu.dabrowski.aidev.model.aidevs.TokenRequest;
import org.eu.dabrowski.aidev.model.aidevs.TokenResponse;
import org.eu.dabrowski.aidev.model.openapi.ModerationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@FeignClient(value = "moderation-api-client", url = "${client.openaimoderationapi.url}", configuration = FeignClientConfiguration.class)
public interface OpenAiModerationApiClient {


    @PostMapping(value = "/moderations")
    JsonNode postModeration(@RequestHeader("Authorization") String token,
                                  @RequestBody ModerationRequest request);

}