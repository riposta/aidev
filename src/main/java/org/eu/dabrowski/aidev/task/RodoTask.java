package org.eu.dabrowski.aidev.task;

import lombok.SneakyThrows;
import org.eu.dabrowski.aidev.model.aidevs.TaskResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

@Component
public class RodoTask extends AbstractTask {


    private static String TASK_NAME = "rodo";

    private static String USER_PROMPT = "Tell me information about yourself. Important thing is to use placeholders %imie%, %nazwisko%, %zawod% and %miasto% instead you real data (in any place of your answer). Do your task strcitly following the instructions.\n" +
            "\n" +
            "Response in Polish language. don not repeat any kind information. Response in concisly way.";
    @Override
    @SneakyThrows
    Object compute(TaskResponse taskResponse) {

        return USER_PROMPT;
    }


    @Override
    public boolean accept(String taskName) {
        return taskName.equals(TASK_NAME);
    }


}
