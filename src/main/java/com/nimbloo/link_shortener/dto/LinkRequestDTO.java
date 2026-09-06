package com.nimbloo.link_shortener.dto;

import java.time.Instant;

public record LinkRequestDTO(
        String url,
        String alias,
        Instant expiresAt
) {
}