package com.nimbloo.link_shortener.controller;

import com.nimbloo.link_shortener.dto.LinkRequestDTO;
import com.nimbloo.link_shortener.dto.LinkResponseDTO;
import com.nimbloo.link_shortener.entity.Link;
import com.nimbloo.link_shortener.service.LinkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
public class LinkController {

    private final LinkService service;

    public LinkController(LinkService service) {
        this.service = service;
    }

    @PostMapping("/api/v1/links")
    public ResponseEntity<LinkResponseDTO> createLink(@RequestBody LinkRequestDTO request) {
        Link link = service.createLink(request.url(), request.alias(), request.expiresAt());
        return ResponseEntity.status(HttpStatus.CREATED).body(LinkResponseDTO.fromEntity(link));
    }

    @GetMapping("/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        Link link = service.getLinkForRedirect(code);
        return ResponseEntity.status(HttpStatus.FOUND) // Retorna HTTP 302 Found para redirecionar
                .location(URI.create(link.getOriginalUrl()))
                .build();
    }

    @GetMapping("/api/v1/links/{code}")
    public ResponseEntity<LinkResponseDTO> getLinkDetails(@PathVariable String code) {
        Link link = service.getLinkDetails(code);
        if (link == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(LinkResponseDTO.fromEntity(link));
    }

    @DeleteMapping("/api/v1/links/{code}")
    public ResponseEntity<Void> disableLink(@PathVariable String code) {
        service.disableLink(code);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/v1/links")
    public ResponseEntity<List<LinkResponseDTO>> listLinks() {
        // Retornando uma lista simples inicial
        List<Link> links = service.getAllLinks();
        return ResponseEntity.ok(links.stream().map(LinkResponseDTO::fromEntity).collect(Collectors.toList()));
    }
}