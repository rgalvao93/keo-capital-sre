# SLOs e SLIs — Keo Capital

## Definições

- **SLI (Service Level Indicator)**: Métrica que mede o comportamento do serviço
- **SLO (Service Level Objective)**: Meta de disponibilidade/performance
- **Error Budget**: Quantidade de erros permitidos dentro do SLO

---

## 1. Autorização de Cartão (Card Authorization)

### SLI
- **Taxa de sucesso**: % de autorizações aprovadas com sucesso (excluindo recusas por saldo/limite)
- **Latência**: Tempo entre requisição e resposta do processador

### SLO
| Métrica | SLO | Janela |
|---|---|---|
| Taxa de sucesso | ≥ 99.5% | 30 dias |
| Latência P99 | < 300ms | 30 dias |
| Latência P95 | < 200ms | 30 dias |

### Error Budget
- 0.5% de falhas técnicas permitidas (excluindo recusas de negócio)
- Exemplo: 10.000 transações/dia → 50 falhas técnicas/dia permitidas

### Monitors Datadog
```
keo.payments.authorization.success_rate >= 99.5
keo.payments.authorization.latency_p99 < 300
```

---

## 2. Disponibilidade do Serviço de Pagamentos (Payment Service Availability)

### SLI
- % de requisições bem-sucedidas (HTTP 2xx) vs total

### SLO
| Métrica | SLO | Janela |
|---|---|---|
| Disponibilidade | ≥ 99.9% | 30 dias |
| Taxa de erro (5xx) | < 0.1% | 30 dias |

### Error Budget
- 0.1% de indisponibilidade permitida
- Exemplo: 30 dias → ~43 minutos de downtime permitido

---

## 3. Working Capital — Workeo

### SLI
- **Tempo de processamento**: Tempo entre solicitação e disbursement
- **Taxa de sucesso**: % de solicitações processadas com sucesso

### SLO
| Métrica | SLO | Janela |
|---|---|---|
| Tempo de processamento (P95) | < 5s | 30 dias |
| Taxa de sucesso | ≥ 99.0% | 30 dias |
| Tempo de análise de crédito (P95) | < 10s | 30 dias |

---

## 4. Keo Travel — Criação de Cartão

### SLI
- **Tempo de criação**: Tempo entre solicitação e cartão disponível
- **Taxa de sucesso**: % de cartões criados com sucesso

### SLO
| Métrica | SLO | Janela |
|---|---|---|
| Tempo de criação (P95) | < 3s | 30 dias |
| Taxa de sucesso | ≥ 99.5% | 30 dias |

---

## 5. Cross-border — Transações Internacionais

### SLI
- **Taxa de sucesso**: % de transações cross-border concluídas
- **Latência**: Tempo de processamento incluindo câmbio

### SLO
| Métrica | SLO | Janela |
|---|---|---|
| Taxa de sucesso | ≥ 99.0% | 30 dias |
| Latência P99 | < 5s | 30 dias |
| Taxa de erro de câmbio | < 0.5% | 30 dias |

---

## 6. Dependências Externas

### Mastercard Network
| Métrica | SLO | Janela |
|---|---|---|
| Disponibilidade | ≥ 99.9% | 30 dias |
| Latência P99 | < 200ms | 30 dias |
| Timeout rate | < 0.1% | 30 dias |

### Bancos Parceiros (Disbursement)
| Métrica | SLO | Janela |
|---|---|---|
| Disponibilidade | ≥ 99.5% | 30 dias |
| Latência P95 | < 3s | 30 dias |

---

## 7. Infraestrutura

### SLI
- CPU, memória, disco, rede por serviço

### SLO
| Métrica | SLO | Janela |
|---|---|---|
| CPU utilization (P95) | < 80% | 7 dias |
| Memory utilization (P95) | < 85% | 7 dias |
| Disk utilization | < 90% | 7 dias |
| Error rate (5xx) | < 0.1% | 7 dias |

---

## 8. Alertas de Burn Rate (Error Budget Consumption)

### Conceito
Burn rate mede a velocidade de consumo do error budget. Burn rate 1.0 = consumindo o budget exatamente no pace do SLO; burn rate 2.0 = consumindo 2x mais rápido que o permitido (esgotaria o budget em metade da janela).

### Implementação
Os alertas de burn rate estão definidos em `terraform/burn-rate.tf` como `datadog_monitor` do tipo `slo alert`, com query:

```
error_budget("<slo_id>").over("30d").burn_rate() > 2
```

| SLO | Burn rate threshold | Janela | Ação |
|---|---|---|---|
| Card Authorization Success Rate | > 2 | 30d | Ver runbook card-authorization-failure |
| Payment Service Availability | > 2 | 30d | Ver runbook payment-service-unavailable |
| Cross-border Transaction Success Rate | > 2 | 30d | Ver runbook cross-border-transaction-failure |
| Workeo Disbursement Success Rate | > 2 | 30d | Ver runbook working-capital-processing-delay |
| Keo Travel Card Creation Success Rate | > 2 | 30d | Ver runbook travel-card-creation-failure |

### Referências
- `terraform/slos.tf` — SLOs monitor-based e metric-based
- `terraform/burn-rate.tf` — alertas de burn rate
- Datadog: https://docs.datadoghq.com/observability/slo/burn_rate_alerts/

---

## Convenção de Tags

```
env:prod|staging|dev
region:latam|canada|us|eu
service:<nome-do-servico>
team:sre|payments|credit|travel
product:workeo|keo-travel|cross-border|supplier-payments
```