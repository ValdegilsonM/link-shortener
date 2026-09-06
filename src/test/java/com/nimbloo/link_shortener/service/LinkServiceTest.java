package com.nimbloo.link_shortener.service;

import com.nimbloo.link_shortener.entity.Link;
import com.nimbloo.link_shortener.repository.LinkRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LinkServiceTest {

    @Mock
    private LinkRepository repository;

    @InjectMocks
    private LinkService service;

    @Test
    void deveCriarLinkComSucesso_CaminhoFeliz() {
        // Arrange (Preparação)
        String url = "https://nimbloo.com.br";
        String alias = "seguro123";
        Instant expiresAt = Instant.now().plus(1, ChronoUnit.DAYS); // Expira amanhã

        // Act (Ação)
        Link result = service.createLink(url, alias, expiresAt);

        // Assert (Verificação)
        assertNotNull(result);
        assertEquals(alias, result.getCode());
        assertEquals(url, result.getOriginalUrl());
        
        // Verifica se o método save do banco de dados foi chamado exatamente 1 vez
        verify(repository, times(1)).save(any(Link.class));
    }

    @Test
    void deveLancarErro_QuandoUrlNaoTiverHttp() {
        // Arrange
        String url = "ftp://nimbloo.com.br"; // Esquema inválido

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.createLink(url, null, null);
        });

        assertEquals("URL inválida. O esquema deve ser http ou https.", exception.getMessage());
        
        // Verifica se o método save do banco NUNCA foi chamado
        verify(repository, never()).save(any(Link.class));
    }

    @Test
    void deveLancarErro_QuandoDataExpiracaoForNoPassado() {
        // Arrange
        String url = "https://nimbloo.com.br";
        Instant pastDate = Instant.now().minus(1, ChronoUnit.DAYS); // Ontem

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.createLink(url, null, pastDate);
        });

        assertEquals("A data de expiração não pode estar no passado.", exception.getMessage());
        verify(repository, never()).save(any(Link.class));
    }
}