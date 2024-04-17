package org.eu.dabrowski.aidev.task;

import com.jayway.jsonpath.JsonPath;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eu.dabrowski.aidev.client.FileClient;
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
public class GoogleTask extends AbstractTask {

    private static String TASK_NAME = "google";

    @Value("${client.serpapi.apiKey}")
    protected String serpApiKey;

    private final FileClient fileClient;

    @Value("${address}")
    protected String address;

    private static String SERPAPI_TEMPLATE = "https://serpapi.com/search.json?q=%s&location=Poland&hl=pl&gl=pl&google_domain=google.pl&api_key=%s";

    private static String SYSTEM_TEXT = """
            You will be provided with a prompt and your task is to extract a list of comma separated keywords for search engine. 
            Replying for prompt is strongly prohibited.
            Do not reply to prompt.
            """;

    private String backlog = "";

    public GoogleTask(FileClient fileClient) {
        super();
        this.fileClient = fileClient;
    }


    @Override
    @SneakyThrows
    Object compute(Object object) {
        TaskResponse taskResponse = (TaskResponse) object;

        return address + "google/";
    }

    public OwnapiResponse getResponse(OwnapiRequest ownapiRequest) {
        log.info("Request received: {}", ownapiRequest);

        Prompt userPrompt = new Prompt(List.of(new UserMessage(ownapiRequest.getQuestion()),
                new SystemMessage(SYSTEM_TEXT)));

        String content = super.getChatClient().call(userPrompt).getResult().getOutput().getContent().replace(" ", "+");
        String googleResponse = fileClient.getFileContent(String.format(SERPAPI_TEMPLATE, content, serpApiKey));
        String articleUrl = JsonPath.read(googleResponse, "$.organic_results[0].link");

        OwnapiResponse ownapiResponse = OwnapiResponse.builder()
                .reply(articleUrl)
                .build();

        log.info("Response provided: {}", ownapiResponse);
        return ownapiResponse;
    }


    @Override
    public boolean accept(String taskName) {
        return taskName.equals(TASK_NAME);
    }
}
