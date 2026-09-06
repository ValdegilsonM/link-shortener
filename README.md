# Nimbloo · Serviço de Links Curtos

Serviço full-stack de encurtamento, redirecionamento e gerenciamento de links, desenvolvido como solução para o teste técnico de **Engenheiro(a) de Software Júnior da Nimbloo**.

A aplicação permite criar links curtos com expiração opcional e alias personalizado, redirecionar para a URL original, acompanhar a quantidade de cliques e desativar links.

O projeto foi desenvolvido com foco em **simplicidade, clareza de código e facilidade de execução local**, considerando o timebox proposto no desafio.

---

## 📌 Funcionalidades

### Backend

- Criação de links curtos
- Geração automática de códigos alfanuméricos
- Alias personalizado opcional
- Data de expiração opcional
- Validação de URLs HTTP/HTTPS
- Validação de data de expiração
- Redirecionamento HTTP 302
- Contagem de cliques
- Consulta dos detalhes de um link
- Desativação de links
- Listagem dos links cadastrados
- Tratamento centralizado de exceções utilizando `ProblemDetail`

### Frontend

- Formulário para criação de links
- Alias personalizado opcional
- Expiração opcional
- Exibição do link gerado
- Botão para copiar o link
- Listagem dos links cadastrados
- Exibição de quantidade de cliques
- Status do link:
  - Ativo
  - Expirado
  - Desativado
- Estados explícitos de loading, erro e lista vazia

---

## 🛠️ Stack

### Backend

- Java 21
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Validation
- AWS SDK for Java
- DynamoDB Enhanced Client
- Maven
- JUnit 5
- Mockito

### Frontend

- React 19
- TypeScript
- Vite
- Axios
- Nginx

### Infraestrutura

- Docker
- Docker Compose
- DynamoDB Local

> **Nota sobre o Spring Boot:** o enunciado do teste solicita Spring Boot 3. Optei por utilizar Spring Boot 4.1.1 para estudar a versão mais recente da família Spring Boot durante a implementação. A decisão é deliberada e está registrada aqui para evitar qualquer ambiguidade durante a avaliação.

---

# 🚀 Como executar

## Pré-requisitos

É necessário ter instalado:

- Docker
- Docker Compose

Não é necessário instalar Java, Maven, Node.js ou DynamoDB localmente para executar a aplicação utilizando Docker Compose.

## Executar

Na raiz do projeto:

```bash
docker compose up -d --build
```

Após a inicialização:

| Serviço | URL |
|---|---|
| Frontend | http://localhost:5173 |
| Backend | http://localhost:8080 |
| DynamoDB Local | http://localhost:8000 |

Para acompanhar os logs:

```bash
docker compose logs -f
```

Para encerrar:

```bash
docker compose down
```

Como o DynamoDB Local está configurado com armazenamento em memória, os dados são perdidos quando o container do banco é recriado.

---

# 🔌 API

## Criar link

```http
POST /api/v1/links
Content-Type: application/json
```

Exemplo:

```json
{
  "url": "https://www.example.com",
  "alias": "exemplo",
  "expiresAt": "2026-12-31T23:59:59Z"
}
```

Os campos `alias` e `expiresAt` são opcionais.

Resposta:

```http
201 Created
```

```json
{
  "code": "exemplo",
  "originalUrl": "https://www.example.com",
  "clicks": 0,
  "active": true,
  "createdAt": "2026-09-06T20:00:00Z",
  "expiresAt": "2026-12-31T23:59:59Z"
}
```

---

## Redirecionar

```http
GET /{code}
```

Exemplo:

```http
GET /exemplo
```

Quando o link estiver ativo e dentro do prazo de validade, a API responde:

```http
302 Found
Location: https://www.example.com
```

O clique é registrado durante o processamento do redirecionamento.

---

## Listar links

```http
GET /api/v1/links
```

Retorna os links cadastrados juntamente com seus respectivos status e quantidade de cliques.

> **Limitação atual:** a implementação atual realiza um `Scan` da tabela DynamoDB e ainda não possui paginação. Esse é um dos principais pontos que eu evoluiria em uma próxima versão.

---

## Consultar detalhes

```http
GET /api/v1/links/{code}
```

Retorna os dados do link e a quantidade atual de cliques.

---

## Desativar link

```http
DELETE /api/v1/links/{code}
```

Desativa o link sem removê-lo fisicamente do banco.

Depois de desativado, uma tentativa de acesso ao código não realiza o redirecionamento.

---

# 🏗️ Arquitetura

A aplicação foi organizada em camadas simples:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
DynamoDB
```

### Controller

Responsável por:

- receber requisições HTTP;
- delegar regras para o service;
- montar as respostas HTTP;
- realizar o redirecionamento.

### Service

Concentra as regras de negócio relacionadas aos links:

- validação;
- criação do código;
- expiração;
- verificação de status;
- contagem de cliques;
- desativação.

### Repository

Responsável pela comunicação com o DynamoDB através do `DynamoDbEnhancedClient`.

### Entity

Representa o documento armazenado na tabela `links`.

A chave de partição utilizada é o próprio código curto:

```text
code
```

Isso permite que a operação principal do sistema — encontrar um link pelo código — seja realizada diretamente pela chave.

---

# 🗄️ Modelagem do DynamoDB

A tabela utilizada é:

```text
links
```

Com `code` como chave de partição.

Estrutura conceitual:

| Campo | Descrição |
|---|---|
| `code` | Código curto / chave primária |
| `original_url` | URL original |
| `created_at` | Data de criação |
| `expires_at` | Data de expiração |
| `clicks` | Quantidade de cliques |
| `is_active` | Indica se o link está ativo |

A escolha do DynamoDB está relacionada ao padrão de acesso principal da aplicação: consultar um link diretamente pelo código curto.

---

# 🔑 Geração do código

Quando nenhum alias é informado, a aplicação gera um código aleatório de 6 caracteres utilizando um alfabeto Base62:

```text
0123456789
ABCDEFGHIJKLMNOPQRSTUVWXYZ
abcdefghijklmnopqrstuvwxyz
```

A geração utiliza `SecureRandom`.

Exemplo:

```text
a8K2xP
```

A estratégia evita códigos sequenciais previsíveis e mantém o identificador curto.

---

# ⚠️ Validações

Atualmente são tratados principalmente:

### URL

A URL deve utilizar:

```text
http://
```

ou

```text
https://
```

### Expiração

Quando informada, a data de expiração não pode estar no passado.

### Alias

O alias informado não pode estar em uso.

Erros de regra de negócio são centralizados através de `@RestControllerAdvice` e retornados utilizando o padrão `ProblemDetail`.

---

# 🧪 Testes

Os testes atuais cobrem principalmente as regras de negócio do serviço.

Cenários implementados:

- criação de link com sucesso;
- URL com esquema inválido;
- data de expiração no passado.

Exemplo:

```bash
./mvnw test
```

Ou, caso o Maven esteja instalado:

```bash
mvn test
```

> A suíte ainda pode ser ampliada para cobrir redirecionamento, expiração, desativação, alias duplicado, geração de código e cenários de concorrência.

---

# 🤔 Decisões e trade-offs

## DynamoDB

Escolhi DynamoDB por ser adequado ao padrão de acesso principal do sistema: buscar um registro diretamente através de uma chave curta.

Para o escopo do desafio, essa abordagem permite manter a infraestrutura simples e evitar a necessidade de um ORM.

## Código aleatório

Optei por gerar códigos Base62 de 6 caracteres utilizando `SecureRandom`.

O objetivo foi manter os links curtos sem utilizar identificadores sequenciais facilmente previsíveis.

## Records

Utilizei Java Records nos DTOs por serem objetos simples de transporte de dados e não precisarem de mutabilidade.

Isso reduz código boilerplate e deixa explícita a natureza imutável desses objetos.

## Redirecionamento HTTP 302

O endpoint de redirecionamento utiliza `302 Found`.

Isso mantém o comportamento compatível com um redirecionamento temporário e evita tratar a URL curta como um redirecionamento permanente pelo navegador.

## Frontend separado

O frontend foi desenvolvido como uma SPA React/TypeScript e servido pelo Nginx.

O Nginx também possui fallback para `index.html`, permitindo que a aplicação React processe as rotas do frontend.

---

# ⚠️ Limitações conhecidas

O projeto foi deliberadamente mantido pequeno para respeitar o escopo e o timebox do desafio.

As principais limitações conhecidas são:

### 1. Listagem sem paginação

A listagem atual utiliza `Scan` no DynamoDB.

Isso funciona para o volume pequeno esperado no teste, mas não escala bem para uma tabela grande.

### 2. Contagem de cliques síncrona

O incremento de cliques acontece durante o redirecionamento.

Em alta concorrência, múltiplas leituras seguidas de gravações podem provocar atualizações perdidas.

### 3. Ausência de autenticação

A aplicação não possui autenticação ou autorização.

Isso foi deixado fora do escopo porque o próprio desafio não exige autenticação completa.

### 4. Ausência de multi-tenancy

A aplicação atualmente não separa links por cliente.

Essa funcionalidade seria necessária para uma utilização real como serviço interno multi-cliente.

### 5. Persistência local

O DynamoDB Local utiliza armazenamento em memória no ambiente Docker Compose.

Consequentemente, os dados são destinados apenas ao ambiente de demonstração.

### 6. Configuração local

Algumas configurações atualmente estão voltadas para execução local e deveriam ser externalizadas em um ambiente de produção.

---

# 🚀 O que eu faria com mais uma semana

Se o projeto fosse evoluído para um ambiente mais próximo de produção, eu priorizaria:

### 1. Paginação no DynamoDB

Utilizaria a paginação nativa do DynamoDB através de `LastEvaluatedKey` em vez de realizar `Scan` completo.

### 2. Contagem assíncrona de cliques

Migraria o registro de cliques para uma fila, por exemplo:

```text
Redirect
   ↓
SQS
   ↓
Consumer
   ↓
Update click count
```

Isso reduziria a latência do redirecionamento e permitiria tratar melhor o volume de acessos.

### 3. Cache

Adicionaria Redis ou cache em memória para reduzir consultas ao DynamoDB no caminho crítico do redirecionamento.

### 4. Multi-tenant

Adicionaria uma API Key por cliente e modelaria o acesso para que cada cliente pudesse visualizar e gerenciar apenas seus próprios links.

### 5. Rate limiting

Implementaria rate limiting por cliente/API Key para evitar abuso da API.

### 6. Observabilidade

Adicionaria:

- Spring Boot Actuator;
- Micrometer;
- métricas de latência;
- quantidade de redirecionamentos;
- quantidade de links expirados;
- taxa de erros;
- métricas do DynamoDB.

### 7. Infraestrutura como código

Para um ambiente real, a criação da tabela e demais recursos seria transferida para Terraform ou CloudFormation, evitando que a aplicação seja responsável por criar sua própria infraestrutura.

---

# 🤖 Uso de Inteligência Artificial

Durante o desenvolvimento utilizei **Gemini** como ferramenta de estudo, pair programming e apoio técnico.

A IA foi utilizada principalmente para:

- discutir decisões de arquitetura;
- estudar alternativas de implementação;
- auxiliar na configuração do Docker e Nginx;
- auxiliar na configuração do DynamoDB Enhanced Client;
- discutir tratamento de concorrência;
- estudar recursos modernos do Java 21.

Todo código utilizado no projeto foi revisado, testado e compreendido antes de ser mantido na solução final.

A responsabilidade pela implementação e pelas decisões apresentadas neste repositório é minha.

---

# 📋 Requisitos do desafio

| Requisito | Status |
|---|---|
| Java 21 | ✅ |
| Spring Boot 3 | ⚠️ Utilizado Spring Boot 4.1.1 |
| DynamoDB | ✅ |
| API REST | ✅ |
| Criação de link | ✅ |
| Redirecionamento | ✅ |
| Expiração | ✅ |
| Desativação | ✅ |
| Contagem de cliques | ✅ |
| Validação de URL | ⚠️ Validação básica |
| Validação de expiração | ✅ |
| Testes automatizados | ✅ |
| Docker Compose | ✅ |
| React + TypeScript | ✅ |
| Loading | ✅ |
| Erro | ✅ |
| Lista vazia | ✅ |
| Paginação | ❌ |
| Multi-tenant | ❌ |
| Cache | ❌ |

---

# 📄 Estrutura do projeto

```text
link-shortener/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/nimbloo/link_shortener/
│   │   │       ├── config/
│   │   │       ├── controller/
│   │   │       ├── dto/
│   │   │       ├── entity/
│   │   │       ├── exception/
│   │   │       ├── repository/
│   │   │       └── service/
│   │   └── resources/
│   │       └── application.yaml
│   └── test/
│       └── java/
├── nimbloo-web/
│   ├── src/
│   │   ├── components/
│   │   ├── App.tsx
│   │   └── main.tsx
│   ├── Dockerfile
│   └── nginx.conf
├── DockerFile
├── docker-compose.yml
├── pom.xml
└── README.md
```

---

## 👨‍💻 Contexto

Projeto desenvolvido como exercício técnico e de aprendizado para o desafio de **Engenheiro(a) de Software Júnior da Nimbloo**.

O objetivo principal foi construir uma solução pequena, funcional e explicável, priorizando clareza das decisões técnicas em vez de adicionar complexidade desnecessária.