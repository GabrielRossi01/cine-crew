![Imagem](https://drive.google.com/uc?export=view&id=1snK5aciVwga7VFbJNOPAeaXByAB5DPNB)

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=flat&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-6DB33F?style=flat&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-OAuth2%20%2B%20JWT-6DB33F?style=flat&logo=springsecurity&logoColor=white)](https://spring.io/projects/spring-security)
[![Spring AI](https://img.shields.io/badge/Spring%20AI-2.0.0-6DB33F?style=flat&logo=spring&logoColor=white)](https://spring.io/projects/spring-ai)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?style=flat&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-7-DC382D?style=flat&logo=redis&logoColor=white)](https://redis.io/)
[![Flyway](https://img.shields.io/badge/Flyway-Migrations-CC0200?style=flat&logo=flyway&logoColor=white)](https://flywaydb.org/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=flat&logo=docker&logoColor=white)](https://www.docker.com/)
[![Angular](https://img.shields.io/badge/Angular-21-DD0031?style=flat&logo=angular&logoColor=white)](https://angular.dev/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5-3178C6?style=flat&logo=typescript&logoColor=white)](https://www.typescriptlang.org/)
[![TailwindCSS](https://img.shields.io/badge/TailwindCSS-3-06B6D4?style=flat&logo=tailwindcss&logoColor=white)](https://tailwindcss.com/)

---

## Problemática

Organizar idas ao cinema com amigos gera atrito recorrente: é difícil centralizar a compra de ingressos, controlar quem já pagou sua parte (o clássico "manda o Pix depois" que nunca acontece) e manter o grupo engajado para os próximos rolês.

## Solução

O **CineCrew** centraliza toda essa organização em um único lugar: criação de clubes privados, controle de rachadinhas, ranking de engajamento e um feed de memórias vinculado a cada evento/filme assistido.

## Público-alvo

Grupos de amigos que frequentam o cinema com regularidade e querem substituir grupos de WhatsApp desorganizados por uma ferramenta dedicada: com histórico, transparência financeira e gamificação para manter todos engajados.

---

## Funcionalidades

| Módulo | Descrição |
|---|---|
| **Clubes** | Grupos privados com ingresso via link de convite (TTL controlado no Redis) |
| **Rachadinhas** | Registro de ingressos comprados e controle de quem já pagou sua parte |
| **Ranking** | Pontuação por engajamento (quem mais vai ao cinema, quem mais organiza) |
| **Feed de Memórias** | Photo posts vinculados ao evento/filme correspondente |
| **Lista de Desejos** | Filmes que o usuário quer assistir, com busca integrada ao TMDB |
| **Assistente Virtual (Cineco)** | Chat com IA (Spring AI + Gemini) para dúvidas sobre a plataforma |
| **Login com Google** | Autenticação via OAuth2, com emissão de JWT próprio |

## Resultados alcançados (parcial)

- API REST estruturada em camadas (`model`, `repository`, `service`, `controller`, `dto`, `mapper`) com nível 2/3 de maturidade Richardson
- Autenticação híbrida funcional: OAuth2 (Google) + JWT assinado com par de chaves RSA
- Persistência em PostgreSQL com 8 migrations versionadas via Flyway
- Cache distribuído em Redis com serialização JSON (Jackson 3) para rankings e dados do TMDB
- Integração externa com a API do TMDB via `RestClient`, isolada por Anti-Corruption Layer (DTOs próprios + client dedicado)
- Assistente conversacional funcional via Spring AI, usando o Gemini como modelo gratuito compatível com API OpenAI
- Containerização completa da infraestrutura (PostgreSQL + Redis) via Docker Compose
- Ambiente pronto para testes automatizados com Testcontainers (JUnit + Mockito)

## Em andamento

- Frontend em Angular consumindo a API (guards de rota, integração OAuth2, chat widget)
- Testes unitários e de integração para os módulos de Watchlist e Movies
- Deploy da aplicação em ambiente de produção

---

## Decisões arquiteturais

- **Camadas bem definidas**: `controller` → `service` → `repository`, sem lógica de negócio no controller. DTOs de entrada/saída separados em `dto/request` e `dto/response`, nunca expondo entidades JPA diretamente na API.
- **Entidades de junção explícitas** (`ClubMember`, `EventParticipant`, `WatchlistItem`) em vez de `@ManyToMany` implícito — facilita versionamento de schema via Flyway e permite atributos extras no relacionamento.
- **IDs sequenciais (`Long` + `SEQUENCE`)** em vez de UUID, priorizando performance de índice no PostgreSQL para este cenário.
- **Anti-Corruption Layer para APIs externas**: o pacote `client/tmdb` isola completamente o contrato do TMDB do domínio da aplicação — mudanças na API externa não afetam o restante do sistema.
- **RestClient em vez de WebClient**: a aplicação é MVC síncrona (não reativa), então o cliente HTTP moderno e não-reativo do Spring é a escolha coerente, evitando complexidade desnecessária de `Mono`/`Flux` fora do fluxo de IA.
- **Cache Redis com serialização JSON (Jackson 3)**: substitui a serialização Java padrão, com `PolymorphicTypeValidator` restrito ao pacote do projeto por segurança contra desserialização arbitrária.
- **JWT com chave RSA assimétrica**: emissão e validação de token sem depender de um segredo simétrico compartilhado, alinhado ao fluxo OAuth2 com Google.

## Documentação da API

A documentação interativa (Swagger/OpenAPI) é gerada automaticamente e fica disponível com a aplicação em execução:

- Swagger UI: `http://localhost:8080/docs`
- OpenAPI JSON: `http://localhost:8080/openapi`

Para o detalhamento de cada endpoint, consulte os controllers no repositório: [`src/main/java/br/com/cinecrew/cinecrew/controller`](https://github.com/GabrielRossi01/cine-crew/tree/main/src/main/java/br/com/cinecrew/cinecrew/controller).

## Conexão com o frontend (Angular)

O backend expõe uma API REST stateless consumida por um frontend em **Angular 21 + TypeScript + TailwindCSS** (repositório separado, em desenvolvimento). A comunicação segue o padrão:

1. Login via Google OAuth2 ou credenciais próprias → backend emite JWT
2. Frontend armazena o token e injeta via interceptor HTTP em todas as requisições subsequentes (`Authorization: Bearer <token>`)
3. Guards de rota (`CanActivate`) no Angular protegem páginas autenticadas, refletindo as regras de autorização já validadas no backend
4. CORS configurado no `SecurityConfig` para aceitar requisições da origem do frontend definida em `app.frontend-url`

---

## Rodando localmente

```bash
git clone https://github.com/GabrielRossi01/cine-crew.git
cd cine-crew
docker compose up -d      # sobe PostgreSQL + Redis
./gradlew bootRun          # aplicação em http://localhost:8080
```

Variáveis de ambiente necessárias estão descritas no `application.yaml`, com valores padrão para desenvolvimento local.

## Stack completa

**Backend**: Java 21, Spring Boot 4.1.0, Spring Web MVC, Spring Data JPA, Spring Data Redis, Spring Security (OAuth2 Client + Resource Server), Spring AI, PostgreSQL, Redis, Flyway, Docker Compose, JUnit 5, Mockito, Testcontainers.

**Frontend** (em desenvolvimento): Angular 21, TypeScript, TailwindCSS.
