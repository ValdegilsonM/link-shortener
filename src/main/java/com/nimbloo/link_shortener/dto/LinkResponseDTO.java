package com.nimbloo.link_shortener.dto;

import com.nimbloo.link_shortener.entity.Link;
import java.time.Instant;

public record LinkResponseDTO(
        String code,
        String originalUrl,
        Integer clicks,
        Boolean active,
        Instant createdAt,
        Instant expiresAt
) {
    // Método auxiliar prático para converter a Entidade do banco neste DTO
    public static LinkResponseDTO fromEntity(Link link) {
        return new LinkResponseDTO(
                link.getCode(),
                link.getOriginalUrl(),
                link.getClicks(),
                link.getActive(),
                link.getCreatedAt(),
                link.getExpiresAt()
        );
    }
}