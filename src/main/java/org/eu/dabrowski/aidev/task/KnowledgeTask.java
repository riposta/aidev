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

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Component
public class KnowledgeTask extends AbstractTask {
    private static String TASK_NAME = "knowledge";

    private static String DB1_API = "http://api.nbp.pl/api/exchangerates/tables/A/";
    private static String DB2_API = "https://restcountries.com/v3.1/all?fields=name,population";

    private static String SYSTEM_TEXT = """
            Answer the question from the prompt based on data below. 
            Only return the answer based on the knowledge below.
            Please answer in Polish.\n###\n
            """;

    public KnowledgeTask(ObjectMapper objectMapper) {
        super();
    }


    @Override
    @SneakyThrows
    Object compute(Object object) {
        TaskResponse taskResponse = (TaskResponse) object;
        String contentDB1 = fetchContentFromUrl(DB1_API);
        String contentDB2 = fetchContentFromUrl(DB2_API);



        Prompt prompt = new Prompt(List.of(new UserMessage(taskResponse.getQuestion()),
                new SystemMessage(SYSTEM_TEXT + contentDB1 + "\n###\n" + contentDB2)));
        Generation result = super.getChatClient().call(prompt).getResult();
        return result.getOutput().getContent();
    }


    public String fetchContentFromUrl(String url) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
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
