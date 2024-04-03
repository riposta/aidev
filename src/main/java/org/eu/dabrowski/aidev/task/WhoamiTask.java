package org.eu.dabrowski.aidev.task;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eu.dabrowski.aidev.client.FileClient;
import org.eu.dabrowski.aidev.model.aidevs.AnswerRequest;
import org.eu.dabrowski.aidev.model.aidevs.TaskResponse;
import org.eu.dabrowski.aidev.model.aidevs.TokenRequest;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class WhoamiTask extends AbstractTask {
    private static String TASK_NAME = "whoami";

    private static String SYSTEM_TEXT = """
            Please return the name if you now what person describes prompt. Please return only the name.
            """;


    @Override
    @SneakyThrows
    public void process(String taskName) {
        var tokenResponse = aiDevsClient.getToken(taskName, TokenRequest.builder().apikey(aiDevsApiKey).build());
        boolean process = true;
        String msg = "";
        while (process) {
            try {
                TaskResponse taskResponse = aiDevsClient.getTask(tokenResponse.getToken());
                log.info("Task compute input {}", taskResponse);
                msg += taskResponse.getHint();
                Object taskOutput = compute(msg);
                log.info("Task compute output {}", taskOutput);
                JsonNode answerResponse = aiDevsClient.postAnswer(tokenResponse.getToken(), AnswerRequest.builder().answer(taskOutput).build());
                log.info("Task answer response {}", answerResponse);
                process = false;
            } catch (Exception e) {
                Thread.sleep(1000);

            }
        }

    }


    @Override
    Object compute(Object object) {
        String msg = (String) object;
        Prompt prompt = new Prompt(List.of(new UserMessage(msg), new SystemMessage(SYSTEM_TEXT)));
        Generation result = super.getChatClient().call(prompt).getResult();
        return result.getOutput().getContent();
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
