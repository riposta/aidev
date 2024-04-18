package org.eu.dabrowski.aidev.task;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.eu.dabrowski.aidev.model.aidevs.TaskResponse;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Md2htmlTask extends AbstractTask {
    private static String TASK_NAME = "md2html";

    private static String SYSTEM_TEXT = """
                md2html
                """;

    public Md2htmlTask(ObjectMapper objectMapper) {
        super();
    }


    @Override
    @SneakyThrows
    Object compute(Object object) {
        TaskResponse taskResponse = (TaskResponse) object;
        Prompt prompt = new Prompt(List.of(new UserMessage(taskResponse.getInput().asText()), new SystemMessage(SYSTEM_TEXT)));
        Generation result = super.getChatClient().call(prompt).getResult();
        return result.getOutput().getContent();
    }



    @Override
    public boolean accept(String taskName) {
        return taskName.equals(TASK_NAME);
    }
}
