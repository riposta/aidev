package org.eu.dabrowski.aidev.task;

import com.fasterxml.jackson.databind.JsonNode;
import com.jayway.jsonpath.JsonPath;
import lombok.SneakyThrows;
import org.eu.dabrowski.aidev.client.OpenAiModerationApiClient;
import org.eu.dabrowski.aidev.model.aidevs.TaskResponse;
import org.eu.dabrowski.aidev.model.openapi.ModerationRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HelloApiTask extends AbstractTask {

    private static String TASK_NAME = "helloapi";


    @Override
    @SneakyThrows
    Object compute(TaskResponse taskResponse) {

        return taskResponse.getCookie();
    }


    @Override
    public boolean accept(String taskName) {
        return taskName.equals(TASK_NAME);
    }
}
