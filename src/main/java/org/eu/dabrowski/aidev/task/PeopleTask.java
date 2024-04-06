package org.eu.dabrowski.aidev.task;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eu.dabrowski.aidev.client.FileClient;
import org.eu.dabrowski.aidev.model.aidevs.TaskResponse;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.JsonReader;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class PeopleTask extends AbstractTask {
    private static String TASK_NAME = "people";

    private static String NAME_SYSTEM_TEXT = """
            Return only name and surname from prompt. Change diminutive of the name to name. Replying to question is strongly prohibited.
            """;
    private static String SYSTEM_TEXT = """
            Return in Polish answer from question in prompt based on knowledge below\n###\n;
            """;

    private final FileClient fileClient;

    public PeopleTask(ObjectMapper objectMapper, FileClient fileClient) {
        super();
        this.fileClient = fileClient;
    }

    @SneakyThrows
    private JsonNode findDocumentBasedOnName(String question, String content) {
        while (true) {
            content.getBytes(StandardCharsets.UTF_8);
            ArrayNode arrayNode = (ArrayNode) objectMapper.readTree(content);
            Prompt prompt = new Prompt(List.of(new UserMessage(question), new SystemMessage(NAME_SYSTEM_TEXT)));
            Generation result = super.getChatClient().call(prompt).getResult();
            for (JsonNode jsonNode : arrayNode) {
                if ((jsonNode.get("imie").asText() + " " + jsonNode.get("nazwisko").asText())
                        .equals(result.getOutput().getContent())) {
                    return jsonNode;
                }
            }
        }
    }


    @Override
    @SneakyThrows
    Object compute(Object object) {
        TaskResponse taskResponse = (TaskResponse) object;
        String url = taskResponse.getData();
        String content = fileClient.getFileContent(url);
        JsonNode foundNode = findDocumentBasedOnName(taskResponse.getQuestion(), content);
        Prompt prompt = new Prompt(List.of(new UserMessage(taskResponse.getQuestion()), new SystemMessage(SYSTEM_TEXT + foundNode.toPrettyString())));
        Generation result = super.getChatClient().call(prompt).getResult();
        return result.getOutput().getContent();
    }


    @Override
    public boolean accept(String taskName) {
        return taskName.equals(TASK_NAME);
    }
}
