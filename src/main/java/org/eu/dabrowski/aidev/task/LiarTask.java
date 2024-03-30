package org.eu.dabrowski.aidev.task;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.groovy.util.Maps;
import org.eu.dabrowski.aidev.client.OpenAiModerationApiClient;
import org.eu.dabrowski.aidev.model.aidevs.AnswerRequest;
import org.eu.dabrowski.aidev.model.aidevs.AnswerResponse;
import org.eu.dabrowski.aidev.model.aidevs.TaskResponse;
import org.eu.dabrowski.aidev.model.aidevs.TokenRequest;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class LiarTask extends AbstractTask {

    private static String TASK_NAME = "liar";
    private final OpenAiModerationApiClient moderationApiClient;

    private static String QUESTION = "What is the color of strawberry?";

    private static String SYSTEM_TEXT = "Check if answer in user prompt is response for question '{question}'. \n" +
            "If yes reply 'YES' if not reply 'NO'";

    public LiarTask(OpenAiModerationApiClient moderationApiClient) {
        super();
        this.moderationApiClient = moderationApiClient;
    }

    @Override
    @SneakyThrows
    Object compute(TaskResponse taskResponse) {
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(SYSTEM_TEXT);
        Prompt prompt = new Prompt(List.of(new UserMessage(taskResponse.getAnswer()), systemPromptTemplate.createMessage(Map.of("question", QUESTION))));
        Generation result = super.getChatClient().call(prompt).getResult();
        return result.getOutput().getContent();
    }

    @Override
    public void process(String taskName) {
        var tokenResponse = aiDevsClient.getToken(taskName, TokenRequest.builder().apikey(aiDevsApiKey).build());
        TaskResponse taskResponse = aiDevsClient.sendQuestion(tokenResponse.getToken(), Maps.of("question", QUESTION));
        log.info("Task compute input {}", taskResponse);
        Object taskOutput = compute(taskResponse);
        log.info("Task compute output {}", taskOutput);
        JsonNode answerResponse = aiDevsClient.postAnswer(tokenResponse.getToken(), AnswerRequest.builder().answer(taskOutput).build());
        log.info("Task answer response {}", answerResponse);
    }

    @Override
    public boolean accept(String taskName) {
        return taskName.equals(TASK_NAME);
    }
}
