package org.eu.dabrowski.aidev.task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import lombok.SneakyThrows;
import org.eu.dabrowski.aidev.client.AiDevsClient;
import org.eu.dabrowski.aidev.client.OpenAiModerationApiClient;
import org.eu.dabrowski.aidev.model.aidevs.TaskResponse;
import org.eu.dabrowski.aidev.model.openapi.ModerationRequest;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ModerationTask extends AbstractTask {
    @Value("${spring.ai.openai.api-key}")
    private String openAiKey;

    private static String TASK_NAME = "moderation";
    private final OpenAiModerationApiClient moderationApiClient;


    public ModerationTask(OpenAiModerationApiClient moderationApiClient) {
        super();
        this.moderationApiClient = moderationApiClient;
    }

    @Override
    @SneakyThrows
    Object compute(TaskResponse taskResponse) {
        List<Integer> responseList = new ArrayList<>();
        for (String line : jsonNodeToList(taskResponse.getInput())) {
            JsonNode response = moderationApiClient.postModeration("Bearer " + openAiKey, ModerationRequest.builder()
                    .input(line).build());
            Boolean result = JsonPath.read(getObjectMapper().writeValueAsString(response), "$.results[0].flagged");
            if(result.booleanValue()){
                responseList.add(1);
            }else{
                responseList.add(0);
            }
        }
        return objectMapper.readTree(objectMapper.writeValueAsString(responseList));
    }

    private List<String> jsonNodeToList(JsonNode jsonNode) {
        List<String> list = new ArrayList<>();
        if (jsonNode.isArray()) {
            for (JsonNode node : jsonNode) {
                list.add(node.asText());
            }
        }
        return list;
    }

    @Override
    public boolean accept(String taskName) {
        return taskName.equals(TASK_NAME);
    }
}
