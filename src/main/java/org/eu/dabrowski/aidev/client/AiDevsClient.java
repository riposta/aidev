package org.eu.dabrowski.aidev.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.eu.dabrowski.aidev.configuration.FeignClientConfiguration;
import org.eu.dabrowski.aidev.model.aidevs.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;



@FeignClient(value = "aidevs-client", url = "${client.aidevs.url}", configuration = FeignClientConfiguration.class)
public interface AiDevsClient {

    @PostMapping(value = "/token/{task}")
    TokenResponse getToken(@PathVariable String task, @RequestBody TokenRequest tokenRequest);

    @PostMapping(value = "/task/{token}")
    TaskResponse getTask(@PathVariable String token);

    @PostMapping(value = "/answer/{token}")
    AnswerResponse postAnswer(@PathVariable String token, @RequestBody AnswerRequest answerRequest);

}