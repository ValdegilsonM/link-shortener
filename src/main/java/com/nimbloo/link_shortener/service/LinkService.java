package com.nimbloo.link_shortener.service;

import com.nimbloo.link_shortener.entity.Link;
import com.nimbloo.link_shortener.repository.LinkRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.security.SecureRandom;
import java.util.List;

@Service
public class LinkService {

    private final LinkRepository repository;
    
    // Todos os caracteres permitidos para o nosso código aleatório (Base62)
    private static final String ALPHANUMERIC = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private final SecureRandom random = new SecureRandom();

    public LinkService(LinkRepository repository) {
        this.repository = repository;
    }

    public Link createLink(String originalUrl, String alias, Instant expiresAt) {
        // Validação 1: URL deve começar com http ou https
        if (originalUrl == null || (!originalUrl.startsWith("http://") && !originalUrl.startsWith("https://"))) {
            throw new IllegalArgumentException("URL inválida. O esquema deve ser http ou https.");
        }

        // Validação 2: Data de expiração não pode ser no passado
        if (expiresAt != null && expiresAt.isBefore(Instant.now())) {
            throw new IllegalArgumentException("A data de expiração não pode estar no passado.");
        }

        // Se o usuário mandou um alias, usamos ele. Se não, geramos o código aleatório.
        String code;
        if (alias != null && !alias.trim().isEmpty()) {
            if (repository.findByCode(alias) != null) {
                throw new IllegalArgumentException("Este alias já está em uso.");
            }
            code = alias;
        } else {
            code = generateRandomCode(6); // Gera um código de 6 caracteres
        }

        Link link = new Link();
        link.setCode(code);
        link.setOriginalUrl(originalUrl);
        link.setCreatedAt(Instant.now());
        link.setExpiresAt(expiresAt);

        repository.save(link);
        return link;
    }

    // Busca o link e já registra o clique se for válido (para o redirecionamento)
    public Link getLinkForRedirect(String code) {
        Link link = repository.findByCode(code);

        if (link == null) {
            throw new RuntimeException("Link não encontrado.");
        }

        // Valida se está inativo ou expirado (não deve redirecionar)
        if (!link.getActive() || (link.getExpiresAt() != null && link.getExpiresAt().isBefore(Instant.now()))) {
            throw new RuntimeException("Link expirado ou inativo.");
        }

        // Conta o clique e salva no banco
        link.setClicks(link.getClicks() + 1);
        repository.save(link);

        return link;
    }
    
    // Busca os detalhes sem contar clique
    public Link getLinkDetails(String code) {
        return repository.findByCode(code);
    }

    public List<Link> getAllLinks() {
        return repository.findAll();
    }

    // Desativa um link
    public void disableLink(String code) {
        Link link = repository.findByCode(code);
        if (link != null) {
            link.setActive(false);
            repository.save(link);
        }
    }

    // Lógica para gerar a string aleatória
    private String generateRandomCode(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(ALPHANUMERIC.length());
            sb.append(ALPHANUMERIC.charAt(randomIndex));
        }
        return sb.toString();
    }
}