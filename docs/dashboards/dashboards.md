# Dashboards — Keo Capital

## Dados atualmente disponíveis

Os dashboards com prefixo `[DEV]` consultam telemetria observada pelo Datadog
APM nos serviços em `env:dev` e métricas reais do Datadog Agent. As requisições
APM recentes incluem tráfego do `traffic-simulator`; são dados observados de
execuções de desenvolvimento, não tráfego de clientes em produção.

Todos os dashboards usam uma janela padrão de seis horas para incluir traces
recentes, mesmo quando não houve tráfego nos últimos minutos.

Não há métricas de negócio recentes suficientes para calcular aprovação de
crédito, criação de cartões, MTTR ou disponibilidade por provedor. Por isso,
os dashboards mostram sinais APM disponíveis e identificam explicitamente as
métricas que não equivalem a SLOs ou indicadores de negócio.

## 1. Dashboard de Transações (Transaction Overview)

### Widgets
| Widget | Tipo | Métrica |
|---|---|---|
| Volume de Transações | Timeseries | `keo.payments.authorization.volume` |
| Taxa de Sucesso | Gauge | `keo.payments.authorization.success_rate` |
| Latência P99 | Timeseries | `keo.payments.authorization.latency_p99` |
| Transações por Região | Toplist | `keo.payments.authorization.volume{region}` |
| Transações por Produto | Pie Chart | `keo.payments.authorization.volume{product}` |
| Taxa de Erro | Timeseries | `keo.payments.service.error_rate` |

### Tags
- `env:prod`
- `region:*`
- `product:*`

---

## 2. Dashboard de Infraestrutura (Infrastructure Health)

### Widgets
| Widget | Tipo | Métrica |
|---|---|---|
| CPU Utilization | Timeseries | `system.cpu.user`, `system.cpu.system` |
| Memory Utilization | Timeseries | `system.mem.used / system.mem.total` |
| Disk Utilization | Gauge | `system.disk.used / system.disk.total` |
| Network Latency | Timeseries | `trace.http.request.duration{service:service-mesh}` |
| Host Availability | Status | `system.uptime` |
| Error Rate 5xx | Timeseries | `http.requests{status:5xx}` |

### Tags
- `env:prod`
- `service:*`

---

## 3. Dashboard de SLOs (SLO Compliance)

### Widgets
| Widget | Tipo | Métrica |
|---|---|---|
| SLO: Autorização de Cartão | SLO Widget | `keo.payments.authorization.success_rate >= 99.5` |
| SLO: Disponibilidade Pagamentos | SLO Widget | `keo.payments.service.success_rate >= 99.9` |
| SLO: Workeo Disbursement | SLO Widget | `keo.working_capital.disbursement.latency_p95 < 5` |
| SLO: Keo Travel Card Creation | SLO Widget | `keo.travel.card_creation.latency_p95 < 3` |
| SLO: Cross-border Transactions | SLO Widget | `keo.crossborder.transaction.success_rate >= 99.0` |
| Error Budget Remaining | Timeseries | Error budget por SLO |

### Tags
- `env:prod`
- `product:*`

---

## 4. Dashboard de Incidentes (Incident Management)

### Widgets
| Widget | Tipo | Métrica |
|---|---|---|
| MTTR (SEV-1) | Timeseries | `keo.incidents.mttr{severity:sev1}` |
| MTTD | Timeseries | `keo.incidents.mttd` |
| Incidentes por Severidade | Pie Chart | `keo.incidents.count{severity}` |
| Alert Fatigue | Gauge | `keo.alerts.false_positive_rate` |
| Incidentes por Serviço | Toplist | `keo.incidents.count{service}` |
| Incidentes Abertos | Table | Incidentes ativos |

### Tags
- `team:*`
- `severity:*`

---

## 5. Dashboard Workeo (Working Capital)

### Widgets
| Widget | Tipo | Métrica |
|---|---|---|
| Taxa de Aprovação de Crédito | Gauge | `keo.credit.approval_rate{product:workeo}` |
| Tempo de Processamento (P95) | Timeseries | `keo.working_capital.disbursement.latency_p95` |
| Volume de Solicitações | Timeseries | `keo.working_capital.request.volume` |
| Disbursement por Banco | Toplist | `keo.working_capital.disbursement.volume{bank}` |
| Taxa de Sucesso de Disbursement | Gauge | `keo.working_capital.disbursement.success_rate` |
| Análise de Crédito (P95) | Timeseries | `keo.credit.evaluation.latency_p95` |

### Tags
- `env:prod`
- `product:workeo`
- `region:*`

---

## 6. Dashboard Keo Travel (Corporate Travel)

### Widgets
| Widget | Tipo | Métrica |
|---|---|---|
| Criação de Cartão (P95) | Timeseries | `keo.travel.card_creation.latency_p95` |
| Taxa de Sucesso de Criação | Gauge | `keo.travel.card_creation.success_rate` |
| Volume de Transações | Timeseries | `keo.travel.transaction.volume` |
| Gastos por Categoria | Pie Chart | `keo.travel.transaction.volume{category}` |
| Cartões Ativos | Gauge | `keo.travel.card.active_count` |
| Taxa de Erro | Timeseries | `keo.travel.service.error_rate` |

### Tags
- `env:prod`
- `product:keo-travel`
- `region:*`

---

## 7. Dashboard Cross-border

### Widgets
| Widget | Tipo | Métrica |
|---|---|---|
| Taxa de Sucesso de Transações | Gauge | `keo.crossborder.transaction.success_rate` |
| Latência de Transação (P99) | Timeseries | `keo.crossborder.transaction.latency_p99` |
| Volume por Região | Toplist | `keo.crossborder.transaction.volume{region}` |
| Taxa de Erro de Câmbio | Timeseries | `keo.crossborder.fx.error_rate` |
| Latência Mastercard (P99) | Timeseries | `keo.external.mastercard.latency_p99` |
| Timeout Rate Mastercard | Timeseries | `keo.external.mastercard.timeout_rate` |

### Tags
- `env:prod`
- `product:cross-border`
- `region:*`

---

## 8. Dashboard de Dependências Externas

### Widgets
| Widget | Tipo | Métrica |
|---|---|---|
| Mastercard Availability | Status | `keo.external.mastercard.availability` |
| Mastercard Latency (P99) | Timeseries | `keo.external.mastercard.latency_p99` |
| Mastercard Timeout Rate | Timeseries | `keo.external.mastercard.timeout_rate` |
| Bank Availability | Status | `keo.external.bank.availability` |
| Bank Disbursement Latency | Timeseries | `keo.external.bank.disbursement.latency_p95` |
| FX Provider Availability | Status | `keo.external.fx.availability` |
| KYC Provider Availability | Status | `keo.external.kyc.availability` |

### Tags
- `env:prod`
- `dependency:*`