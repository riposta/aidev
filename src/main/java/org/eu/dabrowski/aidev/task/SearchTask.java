package org.eu.dabrowski.aidev.task;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import lombok.SneakyThrows;
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
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class SearchTask extends AbstractTask {
    private static String TASK_NAME = "search";

    private static String SYSTEM_TEXT = """
                Return url only from prompt;
                """;

    private final FileClient fileClient;
    private final VectorStore vectorStore;
    public SearchTask(ObjectMapper objectMapper, FileClient fileClient, VectorStore vectorStore) {
        super();
        this.fileClient = fileClient;
        this.vectorStore = vectorStore;
    }


    @Override
    @SneakyThrows
    Object compute(Object object) {
        TaskResponse taskResponse = (TaskResponse) object;
        String url = taskResponse.getMsg().replaceFirst(".*- ", "");
        String content = fileClient.getFileContent(url);
        ByteArrayResource resource = new ByteArrayResource(content.getBytes(StandardCharsets.UTF_8));
        JsonReader jsonReader = new JsonReader(resource,
                "title", "url", "info", "date");
        List<Document> documents = jsonReader.get();
        vectorStore.add(documents);
        String vectorResult = vectorStore.similaritySearch(SearchRequest.query(taskResponse.getQuestion()))
                .stream().findAny().get().getContent();
        Prompt prompt = new Prompt(List.of(new UserMessage(vectorResult), new SystemMessage(SYSTEM_TEXT)));
        Generation result = super.getChatClient().call(prompt).getResult();
        return result.getOutput().getContent();
    }


    @Override
    public boolean accept(String taskName) {
        return taskName.equals(TASK_NAME);
    }
}
