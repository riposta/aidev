package org.eu.dabrowski.aidev.task;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eu.dabrowski.aidev.client.FileClient;
import org.eu.dabrowski.aidev.model.aidevs.AnswerRequest;
import org.eu.dabrowski.aidev.model.aidevs.TaskResponse;
import org.eu.dabrowski.aidev.model.aidevs.TokenRequest;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.messages.Media;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class OptimaldbTask extends AbstractTask {
    private static String TASK_NAME = "optimaldb";

    private static String DATABASE_URL = "https://tasks.aidevs.pl/data/3friends.json";

    private final FileClient fileClient;
    private static String SYSTEM_TEXT = """
            You will be provided with a block of text, and your task is to extract a list of keywords from it. 
            Each line should have separate line of keyworlds starting from "-" character.
            """;

    public OptimaldbTask(FileClient fileClient) {
        super();
        this.fileClient = fileClient;
    }


    @Override
    @SneakyThrows
    Object compute(Object object) {

        String output = "";
        String fileContent = fileClient.getFileContent(DATABASE_URL);
        Iterator<Map.Entry<String, JsonNode>> fieldsIterator = objectMapper.readTree(fileContent).fields();
        log.info("Starting getting keyworlds...");
        while (fieldsIterator.hasNext()) {
            Map.Entry<String, JsonNode> field = fieldsIterator.next();
            ArrayNode arrayNode = (ArrayNode) field.getValue();
            String text = "";
            for (JsonNode childNode : arrayNode) {
                text += childNode.asText() + "\n";
            }
            Prompt prompt = new Prompt(List.of(new UserMessage(text), new SystemMessage(SYSTEM_TEXT)));
            Generation result = super.getChatClient().call(prompt).getResult();
            output += field.getKey() + ":\n" + result.getOutput().getContent() + "\n\n";
            log.info("Keyworlds for {}\n{}", field.getKey(), result.getOutput().getContent());
        }

        return output;
    }

    @Override
    public void process(String taskName){
        Object taskOutput = compute(null);
        var tokenResponse = aiDevsClient.getToken(taskName, TokenRequest.builder().apikey(aiDevsApiKey).build());
        TaskResponse taskResponse = aiDevsClient.getTask(tokenResponse.getToken());
        log.info("Task compute input {}", taskResponse);
        log.info("Task compute output {}", taskOutput);
        JsonNode answerResponse = aiDevsClient.postAnswer(tokenResponse.getToken(), AnswerRequest.builder().answer(taskOutput).build());
        log.info("Task answer response {}", answerResponse);

    }


    @Override
    public boolean accept(String taskName) {
        return taskName.equals(TASK_NAME);
    }
}
