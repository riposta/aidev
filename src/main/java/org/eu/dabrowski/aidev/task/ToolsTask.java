package org.eu.dabrowski.aidev.task;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.eu.dabrowski.aidev.model.aidevs.TaskResponse;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ToolsTask extends AbstractTask {
    private static String TASK_NAME = "tools";

    private static String SYSTEM_TEXT = """
            I Decide whether the task should be added to the ToDo list or to the calendar (if time is provided) and return the corresponding JSON.\s
             I always use YYYY-MM-DD format for dates. If task has provided duration, and not date I classify as ToDo
            ###
            For example:
            Q: Jutro mam spotkanie z Marianem
            A: {{"tool":"Calendar","desc":"Spotkanie z Marianem","date":"2024-04-10"}}
                            
            Q: Przypomnij mi, że mam kupić mleko
            A: {{"tool":"ToDo","desc":"Kup mleko" }}
            \s
            Context:###:
            Today is: {date}
                """;

    public ToolsTask(ObjectMapper objectMapper) {
        super();
    }


    @Override
    @SneakyThrows
    Object compute(Object object) {
        TaskResponse taskResponse = (TaskResponse) object;
        String question = taskResponse.getQuestion();

        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(SYSTEM_TEXT);
        Message systemMessage = systemPromptTemplate.createMessage(Map.of("date", LocalDate.now().toString()));

        Prompt prompt = new Prompt(List.of(new UserMessage(question), systemMessage));
        Generation result = super.getChatClient().call(prompt).getResult();

        return objectMapper.readTree(result.getOutput().getContent());
    }

    private List<String> jsonNodeToList(JsonNode jsonNode) {
        List<String> list = new ArrayList<>();
        if (jsonNode.isArray()) {
            for (JsonNode node : jsonNode) {
                list.add(node.asText());
            }
        }
        return list;
    }

    @Override
    public boolean accept(String taskName) {
        return taskName.equals(TASK_NAME);
    }
}
