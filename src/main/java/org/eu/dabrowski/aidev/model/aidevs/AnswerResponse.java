package org.eu.dabrowski.aidev.model.aidevs;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AnswerResponse {
    int code;
    String msg;
    String note;
    String reply;
}
