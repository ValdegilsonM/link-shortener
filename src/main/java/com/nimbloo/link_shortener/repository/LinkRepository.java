package com.nimbloo.link_shortener.repository;

import com.nimbloo.link_shortener.entity.Link;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class LinkRepository {

    private final DynamoDbEnhancedClient enhancedClient;
    private DynamoDbTable<Link> linkTable;

    // Injeção de dependência: O Spring entrega o client que configuramos anteriormente
    public LinkRepository(DynamoDbEnhancedClient enhancedClient) {
        this.enhancedClient = enhancedClient;
    }

    @PostConstruct
    public void init() {
        // Aponta para a tabela chamada "links" e usa a estrutura da nossa classe Link
        this.linkTable = enhancedClient.table("links", TableSchema.fromBean(Link.class));

        // Tenta criar a tabela no DynamoDB Local.
        // Se ela já existir (quando reiniciarmos a API), ele vai dar um erro silencioso e continuar.
        try {
            linkTable.createTable();
        } catch (Exception e) {
            // Na vida real, a infraestrutura criaria a tabela via Terraform, mas para teste local, isso ajuda muito!
        }
    }

    // Salva ou atualiza um Link
    public void save(Link link) {
        linkTable.putItem(link);
    }

    // Busca um Link pelo seu código único
    public Link findByCode(String code) {
        Key key = Key.builder().partitionValue(code).build();
        return linkTable.getItem(key);
    }

    // Retorna todos os links (O teste pede paginação, implementaremos isso com mais detalhes no futuro se necessário)
    public List<Link> findAll() {
        return linkTable.scan().items().stream().collect(Collectors.toList());
    }
}
