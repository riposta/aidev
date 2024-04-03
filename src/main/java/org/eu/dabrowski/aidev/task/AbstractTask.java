package org.eu.dabrowski.aidev.task;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eu.dabrowski.aidev.client.AiDevsClient;
import org.eu.dabrowski.aidev.model.aidevs.AnswerRequest;
import org.eu.dabrowski.aidev.model.aidevs.AnswerResponse;
import org.eu.dabrowski.aidev.model.aidevs.TaskResponse;
import org.eu.dabrowski.aidev.model.aidevs.TokenRequest;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.config.Task;
import org.springframework.stereotype.Component;

@Slf4j
@Data
public abstract class AbstractTask {
    @Autowired
    protected AiDevsClient aiDevsClient;
    @Autowired
    protected OpenAiChatClient chatClient;
    @Autowired
    protected ObjectMapper objectMapper;
    @Value("${client.aidevs.apiKey}")
    protected String aiDevsApiKey;

    protected AbstractTask(){
    }

    public void process(String taskName){
        var tokenResponse = aiDevsClient.getToken(taskName, TokenRequest.builder().apikey(aiDevsApiKey).build());
        TaskResponse taskResponse = aiDevsClient.getTask(tokenResponse.getToken());
        log.info("Task compute input {}", taskResponse);
        Object taskOutput = compute(taskResponse);
        log.info("Task compute output {}", taskOutput);
        JsonNode answerResponse = aiDevsClient.postAnswer(tokenResponse.getToken(), AnswerRequest.builder().answer(taskOutput).build());
        log.info("Task answer response {}", answerResponse);

    }

    abstract Object compute(Object taskResponse);

    public abstract boolean accept(String taskName);
}
