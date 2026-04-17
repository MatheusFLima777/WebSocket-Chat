# 💬 WebSocket Chat — Backend

Backend de uma aplicação de chat em tempo real construído com **Spring Boot 3**, utilizando WebSocket para comunicação bidirecional, autenticação via **Auth0 (JWT)**, persistência no **MongoDB** e cache/sessões no **Redis**.

---

## 🚀 Deploy

- **Backend:** [websocket-production-b4dd.up.railway.app](https://websocket-production-b4dd.up.railway.app)
- **Plataforma:** [Railway](https://railway.app)
- **Frontend:** [WebSocket-Chat_Front](https://github.com/MatheusFLima777/WebSocket-Chat_Front)

---

## 🛠️ Tecnologias

- **Java 17**
- **Spring Boot 3.2.2**
- **Spring WebSocket** — Comunicação em tempo real
- **Spring Data MongoDB** — Persistência de mensagens e usuários
- **Spring Data Redis** — Cache e controle de tickets de sessão
- **Auth0 (java-jwt + jwks-rsa)** — Validação de tokens JWT
- **Docker / Docker Compose** — Ambiente local
- **Gradle** — Build tool

---

## 📁 Estrutura do Projeto

```
WebSocket-Chat/
├── src/
│   └── main/
│       ├── java/com/cursochat/ws/
│       │   ├── controllers/     # Endpoints REST (ex: TicketController)
│       │   ├── services/        # Regras de negócio (ex: TicketService)
│       │   ├── config/          # Configurações de WebSocket, Redis, Auth0
│       │   └── ...
│       └── resources/
│           └── application.yml  # Configurações da aplicação
├── Dockerfile
├── docker-compose.yaml
├── build.gradle
└── settings.gradle
```

---

## ⚙️ Como funciona

1. O frontend solicita um **ticket** via `POST /v1/ticket` enviando o JWT no header `Authorization`
2. O backend valida o JWT contra as chaves públicas do **Auth0 (JWKS)**
3. Um ticket temporário é gerado e armazenado no **Redis**
4. O frontend usa o ticket para abrir uma conexão **WebSocket** em `/chat?ticket=...`
5. O backend valida o ticket e autentica a sessão WebSocket
6. Mensagens são trocadas em tempo real e persistidas no **MongoDB**

---

## 🔧 Variáveis de Ambiente

| Variável | Descrição |
|---|---|
| `PORT` | Porta da aplicação (padrão: `8080`) |
| `REDIS_URL` | URL de conexão com o Redis |
| `MONG_DB_URI` | URI de conexão com o MongoDB Atlas |
| `MONGODB_DATABASE` | Nome do banco de dados (padrão: `chatDb`) |
| `JWKS_URL` | URL das chaves públicas do Auth0 |

Exemplo de configuração no `application.yml`:

```yaml
server:
  port: ${PORT:8080}

spring:
  data:
    redis:
      url: ${REDIS_URL}
    mongodb:
      uri: ${MONG_DB_URI}
      database: ${MONGODB_DATABASE:chatDb}

  cache:
    type: redis
    redis:
      time-to-live: 600000

app:
  auth:
    jwks-url: ${JWKS_URL}
```

---

## 🐳 Rodando localmente com Docker

O projeto inclui um `docker-compose.yaml` que sobe o Redis e o MongoDB localmente:

```bash
docker-compose up -d
```

Serviços iniciados:
- **Redis** na porta `6379`
- **MongoDB** na porta `27017` (usuário: `root`, senha: `root`)

Em seguida, rode a aplicação:

```bash
./gradlew bootRun
```

A aplicação estará disponível em `http://localhost:8080`.

---

## 🐳 Build com Docker

```bash
docker build -t websocket-chat .
docker run -p 8080:8080 --env-file .env websocket-chat
```

---

## 📦 Dependências principais

```groovy
implementation 'org.springframework.boot:spring-boot-starter-websocket'
implementation 'org.springframework.boot:spring-boot-starter-data-redis'
implementation 'org.springframework.boot:spring-boot-starter-data-mongodb'
implementation 'com.auth0:java-jwt:4.4.0'
implementation 'com.auth0:jwks-rsa:0.22.0'
```

---

## 🔐 Autenticação

A validação de tokens é feita via **JWKS** do Auth0. Configure a variável `JWKS_URL` com o endpoint do seu tenant:

```
https://SEU_DOMINIO.us.auth0.com/.well-known/jwks.json
```

---

## 🌐 Repositório do Frontend

[WebSocket-Chat_Front](https://github.com/MatheusFLima777/WebSocket-Chat_Front)
