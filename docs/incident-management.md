# Gestão de Incidentes e On-call — Keo Capital

## Severidades

| Severidade | Descrição | Tempo de Resposta | Exemplo |
|---|---|---|---|
| **SEV-1** | Crítico — Impacto direto em transações/revenue | 5 min | Autorização de cartão indisponível |
| **SEV-2** | Alto — Degradação significativa | 15 min | Latência de autorização > 500ms |
| **SEV-3** | Médio — Degradação parcial | 30 min | Taxa de erro > 1% em um serviço |
| **SEV-4** | Baixo — Impacto mínimo | 1 hora | Alerta de infraestrutura não crítico |

## Rotação de On-call

### Times
| Time | Responsabilidade | Rotação |
|---|---|---|
| **SRE** | Infraestrutura, plataforma, observabilidade | Semanal |
| **Payments** | payment-api, cross-border-fx | Semanal |
| **Credit** | credit-service, working capital | Semanal |
| **Travel** | travel-card-service | Semanal |

### Escalonamento
```
0-5 min  → SRE on-call investiga
5-15 min → Escalar para tech lead do time afetado
15-30 min → Escalar para Head of Technology
30+ min  → Incidente SEV-1, ativar war room
```

## Processo de Incidente

### 1. Detecção
- Alerta do Datadog (monitor trigger)
- Report de cliente
- Monitoramento proativo

### 2. Triagem (0-5 min)
- Avaliar severidade
- Identificar serviço afetado
- Criar canal no Slack (#incidents-<servico>)

### 3. Investigação (5-30 min)
- Verificar dashboards e traces
- Consultar runbooks
- Identificar causa raiz

### 4. Mitigação
- Aplicar fix ou workaround
- Rollback se necessário
- Escalar se não resolvido

### 5. Resolução
- Confirmar recovery
- Atualizar status page
- Notificar stakeholders

### 6. Post-mortem (24-48h após)
- Criar post-mortem no Jira
- Identificar causa raiz
- Definir ações corretivas
- Atualizar runbooks

## Templates de Post-mortem

### Campos Obrigatórios
1. **Título**: [SEV-X] Descrição breve
2. **Data/Hora**: Início e fim do incidente
3. **Duração**: Tempo total
4. **Impacto**: Clientes afetados, transações perdidas, revenue impact
5. **Causa Raiz**: O que causou o incidente
6. **Timeline**: Eventos em ordem cronológica
7. **Ações Corretivas**: O que será feito para evitar recorrência
8. **Lições Aprendidas**: O que aprendemos

## Métricas de Incidentes

### KPIs
| Métrica | Descrição | Meta |
|---|---|---|
| **MTTR** | Mean Time to Resolution | < 30 min (SEV-1) |
| **MTBF** | Mean Time Between Failures | > 30 dias |
| **MTTD** | Mean Time to Detection | < 5 min |
| **Alert Fatigue** | % de alertas que são falsos positivos | < 10% |

### Monitors Datadog
```
keo.incidents.mttr{severity:sev1} < 1800
keo.incidents.mttd < 300
keo.alerts.false_positive_rate < 0.1
```

## Integrações

### PagerDuty
- **SEV-1**: Page imediato para on-call
- **SEV-2**: Page para on-call com delay de 5 min
- **SEV-3/4**: Notificação apenas (sem page)

### Slack
- **#alerts-infra**: Alertas de infraestrutura
- **#alerts-payments**: Alertas de pagamentos
- **#alerts-credit**: Alertas de crédito
- **#alerts-travel**: Alertas de viagem
- **#alerts-security**: Alertas de segurança
- **#incidents-<servico>**: Canal por incidente

### Jira
- Criar issue automaticamente para cada incidente SEV-1/SEV-2
- Template de post-mortem vinculado

## Runbooks Vinculados
- `runbooks/card-authorization-failure.md`
- `runbooks/mastercard-network-unavailable.md` (a criar)
- `runbooks/payment-service-unavailable.md` (a criar)
- `runbooks/working-capital-processing-delay.md` (a criar)
- `runbooks/travel-card-creation-failure.md` (a criar)
- `runbooks/cross-border-transaction-failure.md` (a criar)