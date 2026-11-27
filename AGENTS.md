# AGENTS.md

Keo Capital SRE — projeto de observabilidade e gestão de incidentes com Datadog para a Keo Capital (fintech B2B de crédito, pagamentos e working capital, STO:KEOC).

## Estrutura do repositório

- `monitors/` — definições de monitors Datadog por camada (business, application, infrastructure, security, external-dependencies, incident-management). Cada monitor é um arquivo Markdown com query, thresholds, tags e bloco Terraform HCL.
- `runbooks/` — runbooks operacionais por cenário de incidente.
- `docs/` — documentação: levantamento da empresa, SLOs, dashboards, arquitetura (tags, APM/tracing), gestão de incidentes, integrações.
- `terraform/` — IaC para Datadog (providers, módulos reutilizáveis, GitHub Actions CI/CD).
- `dashboards/`, `alerts/`, `scripts/`, `modules/` — diretórios auxiliares (atualmente vazios ou em desenvolvimento).

## Convenções importantes

- **Tags obrigatórias**: `env`, `region`, `service`, `team`. Tags de negócio: `product`, `severity`, `compliance`. Ver `docs/arquitetura/tags.md`.
- **Naming de métricas**: `keo.<domínio>.<ação>.<métrica>` (ex.: `keo.payments.authorization.success_rate`).
- **Naming de serviços**: `<domínio>-<função>` (ex.: `payment-api`, `credit-service`).
- **Thresholds de SLO**: autorização de cartão ≥ 99.5%, disponibilidade de pagamentos ≥ 99.9%, working capital P95 < 5s, Keo Travel card creation P95 < 3s, cross-border ≥ 99.0%.
- **Severidades**: SEV-1 (crítico, 5 min resposta), SEV-2 (alto, 15 min), SEV-3 (médio, 30 min), SEV-4 (baixo, 1 hora).

## Issues e workflow

- Issues vivem no GitHub Issues deste repositório; operações via `gh` CLI.
- Issues fechadas: #1 a #10 (todas as camadas de observabilidade implementadas).
- Novos runbooks/monitors devem seguir o padrão dos arquivos existentes em `monitors/` e `runbooks/`.

## Dependências e compatibilidade

Matriz de versões do stack Java (não alterar isoladamente — ver `pom.xml`):

| Componente | Versão | Observação |
|---|---|---|
| Spring Boot (parent) | 3.5.16 | Base do projeto (série 3.x) |
| springdoc-openapi | 2.10.1 | Série 2.x (compatível com Spring Boot 3.x). **3.x exige Spring Boot 4.x** — incompatível |
| MongoDB Java Driver | 5.5.2 | Gerenciado pelo Spring Boot (não sobrescrever `mongodb.version`) |
| Micrometer | gerenciado pelo Spring Boot | versão gerenciada pelo Spring Boot |
| Java | 21 | — |

Regras para não regredir:

- **Nunca** subir o `springdoc-openapi-starter-webmvc-ui` para 3.x enquanto o Spring Boot estiver na série 3.x. O springdoc 3.x puxa artefatos do Spring Boot 4.x e quebra o contexto (erro `EndpointCondition must be used on @Bean methods`).
- **Não sobrescrever** a propriedade `mongodb.version`: o Spring Boot 3.5 importa o BOM `org.mongodb:mongodb-driver-bom` usando essa propriedade; um valor antigo (ex.: 4.11.0) quebra a resolução. Deixe o Spring Boot gerenciar.
- O `maven-enforcer-plugin` (no `pom.xml`) **falha o build** se aparecer qualquer `org.springframework.boot:*:4.x` ou `org.springdoc:*:3.x` na árvore de dependências.
- O `.github/dependabot.yml` ignora bumps *major* de Spring Boot e springdoc; bumps do stack Spring são agrupados em um único PR.
- Cada serviço tem um `*ApplicationTests` com `@SpringBootTest` (`contextLoads`) que valida a subida do contexto; o CI também roda um **smoke test** (`docker compose up --wait` + health checks em `/actuator/health`).
- O `blockchain-mock` não usa Redis; seu health check de Redis está desabilitado (`management.health.redis.enabled=false`) porque o módulo `common` traz o starter transitivamente.
- Os scripts de `mongo/init/` só rodam quando o volume `mongodb_data` está **vazio**. Se um script falhar, o entrypoint do Mongo aborta e o container sai — os serviços passam a falhar com `UnknownHostException: mongodb`. Para reproduzir o CI, teste com `docker compose down -v` antes do `up`.

## Referências rápidas

- `README.md` — visão geral do projeto e estrutura.
- `docs/levantamento/empresa.md` — levantamento completo da Keo Capital.
- `docs/slos/slos.md` — SLOs/SLIs por produto.
- `docs/arquitetura/tags.md` — convenção de tags e métricas.
- `docs/arquitetura/apm-tracing.md` — APM e distributed tracing.
- `docs/incident-management.md` — processo de gestão de incidentes.
- `docs/integracoes/integracoes.md` — integrações externas.
- `docs/dashboards/dashboards.md` — especificações de dashboards.