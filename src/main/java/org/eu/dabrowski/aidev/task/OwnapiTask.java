package org.eu.dabrowski.aidev.task;

import lombok.SneakyThrows;
import org.eu.dabrowski.aidev.controler.OwnapiRequest;
import org.eu.dabrowski.aidev.controler.OwnapiResponse;
import org.eu.dabrowski.aidev.model.aidevs.TaskResponse;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OwnapiTask extends AbstractTask {

    private static String TASK_NAME = "ownapi";

    @Value("${address}")
    protected String address;

    private static String SYSTEM_TEXT = """
            Answer the question from prompt.
            """;


    @Override
    @SneakyThrows
    Object compute(Object object) {
        TaskResponse taskResponse = (TaskResponse) object;

        return address;
    }

    public OwnapiResponse getResponse(OwnapiRequest ownapiRequest) {

        Prompt prompt = new Prompt(List.of(new UserMessage(ownapiRequest.getQuestion()),
                new SystemMessage(SYSTEM_TEXT)));
        Generation result = super.getChatClient().call(prompt).getResult();
        return OwnapiResponse.builder().reply(result.getOutput().getContent()).build();
    }


    @Override
    public boolean accept(String taskName) {
        return taskName.equals(TASK_NAME);
    }
}
