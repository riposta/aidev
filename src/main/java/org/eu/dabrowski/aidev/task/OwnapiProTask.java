package org.eu.dabrowski.aidev.task;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eu.dabrowski.aidev.controler.OwnapiRequest;
import org.eu.dabrowski.aidev.controler.OwnapiResponse;
import org.eu.dabrowski.aidev.model.aidevs.TaskResponse;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class OwnapiProTask extends AbstractTask {

    private static String TASK_NAME = "ownapipro";

    private static String QUESTION_RESPONSE = "question";

    private static String SENTENCE_RESPONSE = "sentence";

    @Value("${address}")
    protected String address;

    private static String SORT_SYSTEM_TEXT = """
            I need to categorize if prompt is question or sentence.
            Possible answers:
            question
            sentence
            """;
    private static String SYSTEM_TEXT = """
            I need to answer the prompt.
            If prompt is a question I need to answer, If prompt is a sentence just confirm it.
            I can use data knowledge based provided below:
            ###
            {backlog}
            """;

    private String backlog = "";


    @Override
    @SneakyThrows
    Object compute(Object object) {
        TaskResponse taskResponse = (TaskResponse) object;

        return address + "pro/";
    }

    public OwnapiResponse getResponse(OwnapiRequest ownapiRequest) {
        log.info("Request received: {}", ownapiRequest);

        Prompt prompt = new Prompt(List.of(new UserMessage(ownapiRequest.getQuestion()),
                new SystemMessage(SORT_SYSTEM_TEXT)));
        Generation result = super.getChatClient().call(prompt).getResult();
        if(result.getOutput().getContent().equals(SENTENCE_RESPONSE)){
            backlog = backlog + "\n" + ownapiRequest.getQuestion();
        }
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(SORT_SYSTEM_TEXT);
        Prompt userPrompt = new Prompt(List.of(new UserMessage(ownapiRequest.getQuestion()),
                systemPromptTemplate.createMessage(Map.of("backlog", backlog))));

        OwnapiResponse ownapiResponse = OwnapiResponse.builder()
                .reply(super.getChatClient().call(userPrompt).getResult().getOutput().getContent())
                .build();

        log.info("Response provided: {}", ownapiResponse);
        return ownapiResponse;
    }


    @Override
    public boolean accept(String taskName) {
        return taskName.equals(TASK_NAME);
    }
}
