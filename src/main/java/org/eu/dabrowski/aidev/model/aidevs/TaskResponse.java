package org.eu.dabrowski.aidev.model.aidevs;

import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TaskResponse {
    int code;
    String msg;
    JsonNode input;
    JsonNode blog;
    String answer;
    String cookie;
}
