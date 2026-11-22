# Apresentação Executiva — Observabilidade e SRE na Keo Capital

## 1. Contexto

A Keo Capital é uma fintech B2B listada na Nasdaq Stockholm (STO:KEOC) com operações na América Latina, Canadá e Américas. O negócio depende de:

- **Workeo** — working capital para empresas mid-market e enterprise
- **Keo Travel** — cartões virtuais para gastos corporativos
- **Supplier Payments** — pagamentos a fornecedores
- **Cross-border Card Program** — parceria estratégica com a Mastercard

Cada produto tem uma cadeia crítica de dependências: APIs próprias, MongoDB, Kafka, Redis, Mastercard, bancos parceiros, provedores de KYC e câmbio. Uma falha em qualquer elo interrompe receita e expõe o negócio a risco regulatório (PCI-DSS).

## 2. O Problema

Antes deste projeto, a operação tinha:
- Alertas isolados e sem padronização de severidade
- Baixa visibilidade de SLOs e error budget
- Diagnóstico manual e lento (MTTR alto)
- Falta de correlação entre negócio, aplicação e dependências externas
- Risco de fadiga de alertas e de incidentes não detectados (MTTD alto)

## 3. A Solução: 7 Camadas de Observabilidade

```
7. Incident Management   → MTTR, MTTD, on-call, post-mortem
6. Security & Compliance → PCI-DSS, fraude, auditoria
5. External Dependencies → Mastercard, bancos, FX, KYC
4. Business/Transaction  → autorização, volume, SLOs
3. Application (APM)     → traces, erros, latência
2. Platform              → Kafka, Redis, API GW
1. Infrastructure        → compute, rede, storage
```

**Princípio:** começar pelo que gera receita (camada 4) e descer até a infraestrutura, garantindo que cada alerta tenha um runbook e um dono.

## 4. O que foi entregue

### Monitors (41)
- **Business:** taxa de sucesso de autorização, latência P99, disponibilidade, aprovação de crédito, disbursement, criação de cartão, cross-border, anomalia de volume
- **Application:** latência/erro por serviço, DB latency, Redis hit rate, throughput
- **External:** Mastercard, bancos, FX, KYC
- **Infrastructure:** CPU, memória, disco, rede, 5xx, host availability
- **Security:** fraude, WAF, PAN access, audit trail de limite de crédito
- **Incident Management:** MTTR, MTTD, fadiga de alertas

### SLOs (Terraform)
- Autorização de cartão: 99.5% / 30d
- Disponibilidade de pagamentos: 99.9% / 30d
- Working capital P95 < 5s
- Keo Travel card creation P95 < 3s
- Cross-border: 99.0% / 30d
- Mastercard: 99.9% / 30d
- Bancos parceiros: 99.5% / 30d

### Dashboards (8)
Transações, infraestrutura, SLO compliance, incidentes, Workeo, Keo Travel, cross-border e dependências externas — todos provisionados via Terraform.

### Runbooks (11)
Cada alerta crítico tem um runbook com sintomas, diagnóstico, mitigação, escalonamento, comunicação e métricas de acompanhamento.

### Integrações
PagerDuty (escalonamento por severidade), Slack (canais por domínio) e Jira (post-mortem), provisionados via Terraform.

### Automação e Qualidade
- Terraform modular (`modules/monitor`) para consistência dos monitors
- CI/CD com build, testes, Docker e validação de Terraform
- Testes unitários com JUnit 5 + Mockito e cobertura JaCoCo
- Microserviços Spring Boot simulando os domínios reais, com métricas Micrometer

## 5. Resultados Esperados

| Métrica | Antes | Depois |
|---|---|---|
| MTTD | Manual / alto | < 5 min |
| MTTR SEV-1 | Indefinido | < 30 min |
| Cobertura de SLOs | 0 | 7 SLOs |
| Alertas com runbook | Parcial | 100% dos críticos |
| Provisionamento | Manual | 100% via Terraform |

## 6. Decisões de Arquitetura (para discussão)

1. **SLOs monitor-based** — reaproveitam os monitors existentes e evitam duplicar queries; migrar para metric-based quando as métricas de numerator/denominator estiverem instrumentadas.
2. **Terraform modular** — um módulo `monitor` garante naming, tags e thresholds consistentes; reduz drift.
3. **Tags obrigatórias** (`env`, `region`, `service`, `team`) — permitem cortes por domínio em dashboards e SLOs.
4. **Runbook por alerta** — o alerta só é considerado pronto quando tem runbook e dono.
5. **Separação business/application** — o negócio enxerga impacto; a engenharia enxerga causa.

## 7. Próximos Passos

1. Testes de integração com Testcontainers (MongoDB/Kafka)
2. Migrar SLOs para metric-based com error budget burn rate
3. Synthetics para jornadas críticas (autorização, criação de cartão)
4. Correlação de deploys (GitHub Actions) com incidentes
5. FinOps: monitor de custo Datadog
6. Chaos engineering controlado nos fluxos de pagamento

## 8. Como Demonstrar ao Vivo

```bash
make up            # sobe stack local (MongoDB, Kafka, Redis, Datadog Agent, serviços)
make test          # testes unitários
make tf-validate   # valida Terraform
```

No Datadog:
1. Dashboard **Transaction Overview** → taxa de autorização e latência
2. Dashboard **SLO Compliance** → error budget dos 7 SLOs
3. Monitor de autorização → simular degradação e mostrar alerta + runbook
4. Incident Management → MTTR/MTTD e timeline
