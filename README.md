# Nimbloo · Serviço de Links Curtos

Este repositório contém a solução para o desafio técnico de Engenharia de Software Júnior da Nimbloo. O projeto é um serviço interno para encurtamento, rastreamento e gerenciamento de links.

---

## 🚀 Tecnologias Utilizadas

* **Backend:** Java 21, Spring Boot 3
* **Banco de Dados:** Amazon DynamoDB (via DynamoDB Local / Docker)
* **Frontend:** React, TypeScript, Vite, Axios
* **Testes:** JUnit 5, Mockito

---

## ⚙️ Como Rodar o Projeto

Para facilitar a avaliação, o banco de dados foi conteinerizado. Siga os passos abaixo:

1. **Inicie o Banco de Dados (DynamoDB Local):**
Na raiz do projeto, execute o comando:
`docker compose up -d`

2. **Inicie o Backend (Spring Boot):**
Abra o projeto na sua IDE e execute a classe `LinkShortenerApplication.java` ou rode via Maven:
`./mvnw spring-boot:run`
A API estará disponível em `http://localhost:8080`.

3. **Inicie o Frontend (React):**
Em um novo terminal, navegue até a pasta `nimbloo-web` e execute:
`npm install`
`npm run dev`
A interface estará disponível em `http://localhost:5173`.

---

## 🧠 Decisões Arquiteturais e Trade-offs

* **Escolha do Banco de Dados (DynamoDB):** Optei pelo DynamoDB em vez do PostgreSQL devido à natureza de chave-valor dos links curtos. Encurtadores exigem leituras em altíssima velocidade para os redirecionamentos, cenário onde o DynamoDB brilha.
* **Geração do Código Curto:** Utilizei um algoritmo de Base62 com `SecureRandom` para gerar códigos alfanuméricos de 6 caracteres. Isso garante URLs limpas (sem caracteres especiais) e um espaço de colisão gigantesco.
* **Contagem de Cliques Síncrona:** Para manter o escopo pequeno e funcional dentro do prazo, a contagem de cliques atualiza o banco de dados de forma síncrona durante o redirecionamento. Em um cenário de alto tráfego, isso geraria gargalos.
* **Paginação:** A API retorna uma listagem simples através do método `scan()` do DynamoDB. A paginação real via `LastEvaluatedKey` foi deixada de fora do escopo inicial por complexidade de tempo.

---

## ⚠️ Limitações Conhecidas

* O redirecionamento atualiza o banco de forma síncrona. Em um pico de acessos (ex: envio em massa de SMS), o banco de dados pode se tornar um gargalo de escrita.
* O método `scan()` utilizado na listagem de links varre toda a tabela, o que se torna custoso e lento conforme a base de dados cresce.
* Não há cache implementado. Cada clique no link curto gera uma consulta direta ao banco de dados.

---

## 🚀 O Que Eu Faria Com Mais Uma Semana

* **Filas para Cliques (SQS):** Moveria a lógica de incremento de cliques para uma fila SQS assíncrona, liberando o usuário instantaneamente e processando a contagem em *background*.
* **Cache em Memória (Redis):** Adicionaria uma camada de cache no endpoint de redirecionamento (`GET /{code}`) para evitar bater no DynamoDB em links muito acessados.
* **Dockerização Completa:** Criaria arquivos `Dockerfile` multi-stage para o backend e frontend, unificando tudo em um único `docker-compose.yml` para rodar a aplicação inteira com apenas um comando.

---

## 🤖 Uso de IA

Durante o desenvolvimento deste projeto, utilizei Inteligência Artificial (Gemini) como ferramenta de estudo e *pair programming*. A IA foi utilizada principalmente para:
* Tirar dúvidas sobre a configuração do `DynamoDbEnhancedClient` da AWS no Spring Boot 3.
* Auxiliar na estruturação semântica do formulário React.
* Discutir abordagens de arquitetura (como a decisão entre Base62 vs UUID).
* Todo o código foi revisado, compreendido e adaptado por mim para atender estritamente aos requisitos do teste.