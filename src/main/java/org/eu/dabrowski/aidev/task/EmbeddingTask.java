package org.eu.dabrowski.aidev.task;

import lombok.SneakyThrows;
import org.eu.dabrowski.aidev.model.aidevs.TaskResponse;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmbeddingTask extends AbstractTask {
    @Autowired
    private EmbeddingClient embeddingClient;

    private static String TEXT = "Hawaiian pizza";
    private static String TASK_NAME = "embedding";
    @Override
    @SneakyThrows
    Object compute(Object object) {
        EmbeddingResponse embeddingResponse = embeddingClient.call(
                new EmbeddingRequest(List.of(TEXT),
                        OpenAiEmbeddingOptions.builder()
                                .withModel("text-embedding-ada-002")
                                .build()));

        return objectMapper.readTree(objectMapper.writeValueAsString(embeddingResponse.getResult().getOutput()));
    }

    @Override
    public boolean accept(String taskName) {
        return taskName.equals(TASK_NAME);
    }
}
