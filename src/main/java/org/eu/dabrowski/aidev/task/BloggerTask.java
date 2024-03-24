package org.eu.dabrowski.aidev.task;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eu.dabrowski.aidev.client.AiDevsClient;
import org.eu.dabrowski.aidev.client.OpenAiModerationApiClient;
import org.eu.dabrowski.aidev.model.aidevs.TaskResponse;
import org.eu.dabrowski.aidev.model.openapi.ModerationRequest;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BloggerTask extends AbstractTask {
    private static String TASK_NAME = "blogger";

    private static String SYSTEM_TEXT = """
                As a blogger, your role is to write a paragraph based on topic described in a prompt.
                 ###
                Prepare a only one short paragraph describing topic from prompt. Prepare it in Polish.
                """;

    public BloggerTask(ObjectMapper objectMapper) {
        super();
    }


    @Override
    @SneakyThrows
    Object compute(TaskResponse taskResponse) {
        List<String> responseList = new ArrayList<>();


        for (String line : jsonNodeToList(taskResponse.getBlog())) {
            Prompt prompt = new Prompt(List.of(new UserMessage(line), new SystemMessage(SYSTEM_TEXT)));
            Generation result = super.getChatClient().call(prompt).getResult();
            responseList.add(result.getOutput().getContent());
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
