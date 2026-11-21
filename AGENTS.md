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

## Referências rápidas

- `README.md` — visão geral do projeto e estrutura.
- `docs/levantamento/empresa.md` — levantamento completo da Keo Capital.
- `docs/slos/slos.md` — SLOs/SLIs por produto.
- `docs/arquitetura/tags.md` — convenção de tags e métricas.
- `docs/arquitetura/apm-tracing.md` — APM e distributed tracing.
- `docs/incident-management.md` — processo de gestão de incidentes.
- `docs/integracoes/integracoes.md` — integrações externas.
- `docs/dashboards/dashboards.md` — especificações de dashboards.