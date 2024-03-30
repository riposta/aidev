package org.eu.dabrowski.aidev.task;

import lombok.SneakyThrows;
import org.eu.dabrowski.aidev.model.aidevs.TaskResponse;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.openai.OpenAiAudioTranscriptionClient;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.openai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.openai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.io.Resource;

import java.util.List;

@Component
public class WhisperTask extends AbstractTask {
    @Autowired
    OpenAiAudioTranscriptionClient audioTranscriptionClient;

    @Autowired
    private RestTemplate restTemplate;

    private static String AUDIO_PATH = "https://tasks.aidevs.pl/data/mateusz.mp3";

    private static String TASK_NAME = "whisper";
    @Override
    @SneakyThrows
    Object compute(TaskResponse taskResponse) {
        var transcriptionOptions = OpenAiAudioTranscriptionOptions.builder()
                .withResponseFormat(OpenAiAudioApi.TranscriptResponseFormat.TEXT)
                .withTemperature(0f)
                .build();
        AudioTranscriptionResponse response = audioTranscriptionClient.call(new AudioTranscriptionPrompt(getResourceFromUrl(AUDIO_PATH), transcriptionOptions));
        return response.getResult().getOutput();
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
