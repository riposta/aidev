package org.eu.dabrowski.aidev.controler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.eu.dabrowski.aidev.task.OwnapiProTask;
import org.eu.dabrowski.aidev.task.OwnapiTask;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/")
@RestController
@RequiredArgsConstructor
public class OwnapiController {
    private final OwnapiTask task;
    private final OwnapiProTask taskPro;

    @PostMapping()
    public ResponseEntity<OwnapiResponse> answer(@RequestBody OwnapiRequest request) {
        return ResponseEntity.ok(task.getResponse(request));
    }

    @PostMapping("pro/")
    public ResponseEntity<OwnapiResponse> answerPro(@RequestBody OwnapiRequest request) {
        return ResponseEntity.ok(taskPro.getResponse(request));
    }
}
