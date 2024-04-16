package org.eu.dabrowski.aidev.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.eu.dabrowski.aidev.client.FileClient;
import org.eu.dabrowski.aidev.model.aidevs.TaskResponse;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.JsonReader;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Component
public class GnomeTask extends AbstractTask {
    private static String TASK_NAME = "gnome";


    private final FileClient fileClient;
    public GnomeTask(FileClient fileClient) {
        super();
        this.fileClient = fileClient;
    }


    @Override
    @SneakyThrows
    Object compute(Object object) {
        TaskResponse taskResponse = (TaskResponse) object;
        byte[] imageBytes = fileClient.getFileAsBytes(taskResponse.getUrl());
        Media media = new Media(MimeTypeUtils.IMAGE_PNG, imageBytes);
        Prompt prompt = new Prompt(List.of(new UserMessage(taskResponse.getMsg(), List.of(media))));
        Generation result = super.getChatClient().call(prompt).getResult();
        return result.getOutput().getContent();
    }


    @Override
    public boolean accept(String taskName) {
        return taskName.equals(TASK_NAME);
    }
}
