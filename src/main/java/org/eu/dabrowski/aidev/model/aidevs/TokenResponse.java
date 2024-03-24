package org.eu.dabrowski.aidev.model.aidevs;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TokenResponse {
    int code;
    String msg;
    String token;
}
