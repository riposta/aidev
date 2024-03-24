package org.eu.dabrowski.aidev.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eu.dabrowski.aidev.task.AbstractTask;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class InitializationService implements ApplicationRunner {
    @Value("${task}")
    private  String taskName;

    private final ApplicationContext context;

    @Override
    @SneakyThrows
    public void run(ApplicationArguments args) {

        Map<String, AbstractTask> beans = context.getBeansOfType(AbstractTask.class);
        for (Map.Entry<String, AbstractTask> entry : beans.entrySet()) {
            AbstractTask task = entry.getValue();
            if (task.accept(taskName)) {
                task.process(taskName);
            }
        }
    }
}