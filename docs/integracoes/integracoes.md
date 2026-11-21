# Integrações Externas — Keo Capital

## 1. PagerDuty / Opsgenie

### Configuração
- **Serviço**: Keo Capital SRE
- **Escalonamento**: Por severidade (SEV-1 a SEV-4)
- **Rotação**: Semanal por time

### Mapeamento de Severidades
| Datadog Severity | PagerDuty Urgency | Ação |
|---|---|---|
| Critical | High | Page imediato |
| High | High | Page com delay de 5 min |
| Medium | Low | Notificação apenas |
| Low | Low | Notificação apenas |

### Integração com Datadog
```yaml
# datadog-pagerduty-integration
service: keo-capital-sre
escalation_policy: sre-oncall
```

---

## 2. Slack

### Canais
| Canal | Propósito | Severidade |
|---|---|---|
| `#alerts-infra` | Alertas de infraestrutura | Medium+ |
| `#alerts-payments` | Alertas de pagamentos | Medium+ |
| `#alerts-credit` | Alertas de crédito | Medium+ |
| `#alerts-travel` | Alertas de viagem | Medium+ |
| `#alerts-security` | Alertas de segurança | High+ |
| `#alerts-sre` | Alertas de SRE/incidentes | Medium+ |
| `#incidents-<servico>` | Canal por incidente | SEV-1/SEV-2 |

### Configuração de Webhook
```bash
# Slack webhook URL (armazenar em secrets)
SLACK_WEBHOOK_URL=https://hooks.slack.com/services/XXX/YYY/ZZZ
```

### Formato de Mensagem
```
🚨 [SEV-1] Card Authorization Success Rate abaixo de 99.5%
Serviço: payment-api
Região: latam
Dashboard: https://app.datadoghq.com/dashboard/xxx
Runbook: https://github.com/rgalvao93/keo-capital-sre/runbooks/card-authorization-failure.md
```

---

## 3. Jira

### Configuração
- **Projeto**: KEO (Keo Capital)
- **Issue Type**: Incident
- **Prioridade**: Mapeada por severidade

### Automação
- Criar issue automaticamente para incidentes SEV-1/SEV-2
- Vincular post-mortem template
- Atualizar status quando incidente for resolvido

### Campos Obrigatórios
| Campo | Valor |
|---|---|
| Summary | [SEV-X] Descrição breve |
| Description | Detalhes do incidente + link para Datadog |
| Priority | Highest (SEV-1), High (SEV-2), Medium (SEV-3), Low (SEV-4) |
| Labels | `incident`, `datadog`, `<servico>` |
| Assignee | SRE on-call |

---

## 4. GitHub

### Configuração
- **Repositório**: rgalvao93/keo-capital-sre
- **Correlação**: Deploys com incidentes

### Automação
- Criar issue para cada incidente SEV-1/SEV-2
- Vincular PR de fix ao incidente
- Atualizar status quando PR for merged

### Webhook
```bash
# GitHub webhook para correlação de deploys
GITHUB_WEBHOOK_URL=https://api.github.com/repos/rgalvao93/keo-capital-sre
```

---

## 5. Datadog Integrations

### Integrations a Habilitar
| Integration | Propósito |
|---|---|
| AWS/GCP/Azure | Métricas de infraestrutura cloud |
| Kubernetes | Métricas de containers e pods |
| PostgreSQL | Database monitoring |
| Redis | Cache monitoring |
| Kafka | Message broker monitoring |
| Nginx/HAProxy | Load balancer monitoring |
| PagerDuty | Gestão de incidentes |
| Slack | Notificações |
| Jira | Tracking de incidentes |

### Configuração via Terraform
```hcl
resource "datadog_integration_aws" "keo_capital" {
  account_id = var.aws_account_id
  role_name  = "DatadogIntegrationRole"
  filter_tags = ["env:prod"]
}

resource "datadog_integration_slack" "keo_capital" {
  service_hooks = [
    {
      account_name = "keo-capital"
      url          = var.slack_webhook_url
    }
  ]
}
```