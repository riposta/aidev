package org.eu.dabrowski.aidev.model.aidevs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Data;
import org.json.JSONPropertyName;

@Builder
@Data
public class TaskResponse {
    int code;
    String msg;
    JsonNode input;
    JsonNode blog;
    String answer;
    String cookie;
    String question;
    String hint;
    String hint1;
    String hint2;
    String hint3;
    String data;
    String url;
    String service;
    String image;
    String text;

    @JsonProperty("database #1")
    String database1;
    @JsonProperty("database #2")
    String database2;

    @JsonProperty("example for ToDo")
    String exampleTodo;

    @JsonProperty("example for Calendar")
    String exampleCalendar;
}

