package org.eu.dabrowski.aidev.task;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import dabrowski.eu.org.aidevs.renderform.model.RenderRenderRequest;
import dabrowski.eu.org.aidevs.renderform.model.RenderRenderResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eu.dabrowski.aidev.client.FileClient;
import org.eu.dabrowski.aidev.client.RenderformClient;
import org.eu.dabrowski.aidev.model.aidevs.TaskResponse;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class MemeTask extends AbstractTask {
    private static String TASK_NAME = "meme";

    private static String TEMPLATE_ID = "lean-kangaroos-rejoice-tightly-1349";

    private final RenderformClient client;

    @Value("${client.renderform.apiKey}")
    protected String renderFormKey;

    public MemeTask(RenderformClient client) {
        super();
        this.client = client;
    }


    @Override
    @SneakyThrows
    Object compute(Object object) {
        TaskResponse taskResponse = (TaskResponse) object;

        RenderRenderResponse renderResponse = client.render(renderFormKey, RenderRenderRequest.builder().template(TEMPLATE_ID)
                .data(Map.of("IMAGE.src", taskResponse.getImage(), "TEXT.text", taskResponse.getText()))
                .build());
        return renderResponse.getHref();

    }


    @Override
    public boolean accept(String taskName) {
        return taskName.equals(TASK_NAME);
    }
}
