package org.eu.dabrowski.aidev.task;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eu.dabrowski.aidev.client.FileClient;
import org.eu.dabrowski.aidev.model.aidevs.TaskResponse;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class ScraperTask extends AbstractTask {
    private final FileClient fileClient;
    private static String TASK_NAME = "scraper";

    private static String SYSTEM_TEXT = """
            As a blogger, your role is to write a paragraph based on topic described in a prompt.
             ###
            Prepare a only one short paragraph describing topic from prompt. Prepare it in Polish.
            """;

    public ScraperTask(ObjectMapper objectMapper, FileClient fileClient) {
        super();
        this.fileClient = fileClient;
    }



    @Override
    @SneakyThrows
    Object compute(Object object) {
        TaskResponse taskResponse = (TaskResponse) object;
        String url = taskResponse.getInput().asText().replace("\"", "");
        String content = fileClient.getFileContent(url);
        Prompt prompt = new Prompt(List.of(new UserMessage(content + "###" + taskResponse.getMsg() + "###"
                + taskResponse.getQuestion()
             )));
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
