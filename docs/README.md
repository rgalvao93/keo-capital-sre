# Documentação — Keo Capital SRE

Índice da documentação do projeto.

## Visão geral
- [Apresentação executiva](apresentacao.md) — narrativa do projeto para stakeholders
- [Levantamento da empresa](levantamento/empresa.md) — contexto da Keo Capital

## SRE e observabilidade
- [SLOs e SLIs](slos/slos.md) — metas de disponibilidade e performance
- [Gestão de incidentes](incident-management.md) — severidades, on-call, post-mortem
- [Integrações externas](integracoes/integracoes.md) — PagerDuty, Slack, Jira, GitHub
- [Dashboards](dashboards/dashboards.md) — especificações dos dashboards

## Arquitetura
- [Tags e métricas](arquitetura/tags.md) — convenções de tags e naming
- [APM e tracing](arquitetura/apm-tracing.md) — distributed tracing

## Operação
- [Runbooks](../runbooks/) — procedimentos por cenário de incidente
- [Monitors](../monitors/) — definições de monitors por camada
- [Terraform](../terraform/) — IaC Datadog
