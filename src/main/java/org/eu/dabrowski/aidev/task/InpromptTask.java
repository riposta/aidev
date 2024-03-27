package org.eu.dabrowski.aidev.task;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.TextNode;
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
public class InpromptTask extends AbstractTask {
    private static String TASK_NAME = "inprompt";

    private static String SYSTEM_TEXT_FIRST = """
                As a detective, you need to get information from text 
                 ###
                Return only the person name from question below. Prepare it in Polish.
                """;

    private static String SYSTEM_TEXT_SECOND = """
                As a detective, you need to get information from text 
                 ###
                Answer the question in prompt based on the text below. Prepare it in Polish.
                """;

    public InpromptTask(ObjectMapper objectMapper) {
        super();
    }


    @Override
    @SneakyThrows
    Object compute(TaskResponse taskResponse) {
        Prompt prompt = new Prompt(List.of(new UserMessage(taskResponse.getQuestion()), new SystemMessage(SYSTEM_TEXT_FIRST)));
        Generation result = super.getChatClient().call(prompt).getResult();


        String content = "";
        if (taskResponse.getInput().isArray()) {
            for (JsonNode node : taskResponse.getInput()) {
                if(node instanceof TextNode && ((TextNode) node)
                        .asText().matches(".*"+result.getOutput().getContent()+".*")){
                    content += node.asText();
                }
            }
        }

        prompt = new Prompt(List.of(new UserMessage(taskResponse.getQuestion()), new SystemMessage(SYSTEM_TEXT_SECOND + content)));
        result = super.getChatClient().call(prompt).getResult();


        return result.getOutput().getContent();
    }


    @Override
    public boolean accept(String taskName) {
        return taskName.equals(TASK_NAME);
    }
}
