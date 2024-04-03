package org.eu.dabrowski.aidev.task;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.SneakyThrows;
import org.eu.dabrowski.aidev.model.aidevs.TaskResponse;
import org.springframework.ai.openai.OpenAiAudioTranscriptionClient;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.openai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.openai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

@Component
public class FunctionsTask extends AbstractTask {

    @Value("classpath:files/functions.json")
    private Resource functionsResource;

    @Autowired
    private RestTemplate restTemplate;

    private static String TASK_NAME = "functions";
    @Override
    @SneakyThrows
    Object compute(Object object) {

        return objectMapper.readTree(functionsResource.getContentAsString(StandardCharsets.UTF_8));
    }

    public Resource getResourceFromUrl(String fileUrl) {
        byte[] fileBytes = restTemplate.getForObject(fileUrl, byte[].class);
        return new ByteArrayResource(fileBytes);
    }

    @Override
    public boolean accept(String taskName) {
        return taskName.equals(TASK_NAME);
    }


}
