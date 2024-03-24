package org.eu.dabrowski.aidev.model.openapi;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ModerationRequest {
    String input;
}
