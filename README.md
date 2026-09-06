# Nimbloo · Serviço de Links Curtos

Este repositório contém a solução para o desafio técnico de Engenharia de Software Júnior da Nimbloo. O projeto é um serviço full-stack para encurtamento, rastreamento e gerenciamento de links.

---

## 🚀 Tecnologias Utilizadas

* **Backend:** Java 21, Spring Boot 3
* **Banco de Dados:** Amazon DynamoDB (via DynamoDB Local)
* **Frontend:** React, TypeScript, Vite, Axios
* **Infraestrutura:** Docker, Docker Compose, Nginx (Servidor Web Frontend)
* **Testes:** JUnit 5, Mockito

---

## ⚙️ Como Rodar o Projeto

A aplicação inteira (Banco de Dados, Backend e Frontend) foi conteinerizada para facilitar a avaliação.

1. Certifique-se de ter o **Docker** e o **Docker Compose** rodando na sua máquina.
2. Navegue até a pasta raiz do projeto (onde está o arquivo `docker-compose.yml`).
3. Execute o comando abaixo para construir e subir todos os serviços:
   ```bash
   docker compose up -d --build

Acessos:

Interface Web (Frontend): Acesse http://localhost:5173

API REST (Backend): Rodando internamente em http://localhost:8080

Nota: O banco de dados roda com a flag -inMemory. Ao derrubar os contêineres, os dados de teste serão apagados.

🧠 Decisões Arquiteturais e Trade-offs
Escolha do Banco de Dados (DynamoDB): Optei pelo DynamoDB devido à natureza de chave-valor dos links curtos. Encurtadores exigem leituras em altíssima velocidade para os redirecionamentos, cenário onde o DynamoDB é ideal.

Geração do Código Curto: Utilizei um algoritmo combinando UUID e Base62 para gerar códigos alfanuméricos enxutos. Isso garante URLs limpas e um espaço de colisão praticamente nulo.

Imutabilidade e Segurança: Utilizei records do Java 21 para transitar os DTOs entre as camadas, garantindo imutabilidade (thread-safety) e um código mais limpo.

Frontend SPA e Nginx: O Frontend foi construído como Single Page Application. Configurei o Nginx no Docker para interceptar chamadas diretas (ex: localhost:5173/meulink) e direcionar para o index.html, permitindo que o React gerencie a rota de redirecionamento corretamente, sem erros 404.

⚠️ Limitações Conhecidas
Contagem Síncrona: O redirecionamento atualiza os cliques no banco de forma síncrona. Em um cenário de alto tráfego concorrente, isso pode gerar Race Conditions (Atualização Perdida) e se tornar um gargalo de escrita.

Listagem sem Paginação: A API retorna a listagem através do método scan() do DynamoDB. Varrendo toda a tabela, isso se torna custoso e lento conforme a base de dados cresce.

🚀 O Que Eu Faria Com Mais Tempo
Filas para Cliques (SQS / Mensageria): Moveria a lógica de incremento de cliques para uma fila assíncrona, resolvendo problemas de concorrência e liberando o usuário instantaneamente durante o redirecionamento.

Otimização de Banco de Dados:

Criaria um GSI (Global Secondary Index) para permitir buscas e paginação eficientes, evitando o uso do scan().

Ativaria o TTL (Time To Live) nativo do DynamoDB para apagar automaticamente registros com data de expiração ultrapassada, sem consumir processamento do backend.

Cache em Memória (Redis): Adicionaria uma camada de cache no endpoint de redirecionamento para evitar bater no DynamoDB repetidas vezes em links virais.

🤖 Uso de IA e Integridade
Durante o desenvolvimento deste projeto, utilizei Inteligência Artificial (Gemini) como ferramenta de estudo, pair programming e auxílio na configuração. A IA foi utilizada principalmente para:

Configurar e orquestrar a infraestrutura com o Docker e Nginx.

Configurar o DynamoDbEnhancedClient da AWS no Spring Boot.

Discutir abordagens de arquitetura, trade-offs e tratamento de concorrência (cenários adversos).

Entender a fundo os padrões modernos do Java 21, como a utilização de records.

Todo o código gerado foi minuciosamente revisado, testado e compreendido por mim para atender aos requisitos.