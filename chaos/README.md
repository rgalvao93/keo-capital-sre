# Chaos Engineering — Keo Capital

## Objetivo
Validar resiliência dos serviços críticos (pagamentos, crédito, cross-border, travel card) através de experimentos controlados de injeção de falha, garantindo que runbooks e SLOs funcionam sob condições adversas.

---

## Metodologia

### Princípios de Segurança
1. **Blast Radius Controlado** — Experimentar em um namespace/pod isolado ou em staging antes de prod
2. **Abort Criteria** — Se SLO de qualquer serviço crítico cair abaixo de warning threshold, interromper imediatamente
3. **Rollback Automático** — Experimentos têm TTL (ex: 5min) e revertem automaticamente
4. **Observabilidade** — Dashboards de SLO, error budget e burn rate monitorados em tempo real durante experimento
5. **Game Day Cadence** — Executar experimentos planejados quinzenalmente com squad presente

### Processo Game Day
```
1. Planejamento (2 semanas antes)
   - Selecionar experimento do backlog
   - Definir hipóteses e métricas de sucesso
   - Preparar runbooks de rollback
   - Agendar com squads afetadas

2. Preparação (1 dia antes)
   - Deploy do experimento em staging/k8s
   - Validar que monitoramento (Datadog) capta a falha
   - Dry-run sem injeção real

3. Execução (Dia do Game Day)
   - Squad completa presente (on-call + devs + SRE)
   - Iniciar experimento via Chaos Mesh / script
   - Observar dashboards SLO + runbook em ação
   - Anotar tempo até detecção, MTTR, ação tomada

4. Pós-Game Day (1 dia depois)
   - Retrospectiva
   - Atualizar runbooks se necessário
   - Fechar ou criar issues de melhorias
   - Registrar no runbook da incidente associada
```

---

## Ferramentas

| Ambiente | Ferramenta | Uso |
|---|---|---|
| **Kubernetes (prod/staging)** | [Chaos Mesh](https://chaos-mesh.org/) | PodChaos (kill/pause), NetworkChaos (latency/loss/partition), TimeChaos |
| **Docker Compose (local)** | Scripts shell + `toxiproxy` (opcional) | `docker stop`, `docker pause`, latency injection via proxy |
| **CI/CD** | GitHub Actions + `chaos-mesh` job | Validar experimentos no pipeline (opcional) |

---

## Experimentos Catalogados

| ID | Experimento | Alvo | Tipo | Blast Radius | Runbook Associado |
|---|---|---|---|---|---|
| CE-01 | `payment-api-pod-kill` | payment-api | PodChaos (kill) | 1 pod (replica=3) | payment-service-unavailable |
| CE-02 | `mongodb-latency` | mongodb | NetworkChaos (delay) | mongodb pod | working-capital-processing-delay |
| CE-03 | `kafka-partition-failure` | kafka | NetworkChaos (partition) | 1 broker | cross-border-transaction-failure |
| CE-04 | `mastercard-latency` | payment-api → mastercard | NetworkChaos (delay) | payment-api pod | mastercard-network-unavailable |
| CE-05 | `payment-api-cpu-stress` | payment-api | StressChaos (cpu) | 1 pod | authorization-latency-degradation |

---

## Estrutura de Arquivos

```
chaos/
├── README.md                 # Este arquivo
├── experiments/
│   ├── 01-payment-api-pod-kill.yaml      # PodChaos: kill payment-api pod
│   ├── 02-mongodb-latency.yaml           # NetworkChaos: 100ms latency to MongoDB
│   ├── 03-kafka-partition.yaml           # NetworkChaos: partition kafka broker
│   ├── 04-mastercard-latency.yaml        # NetworkChaos: latency to external (via proxy)
│   └── 05-payment-api-cpu-stress.yaml    # StressChaos: CPU pressure
└── scripts/
    ├── local-pod-kill.sh         # docker stop payment-api (local)
    ├── local-latency-inject.sh   # toxiproxy latency injection (local)
    └── rollback.sh               # rollback generic
```

---

## Como Executar

### Kubernetes (staging/prod) — Chaos Mesh
```bash
# Instalar Chaos Mesh no cluster (uma vez)
helm repo add chaos-mesh https://charts.chaos-mesh.org
helm install chaos-mesh chaos-mesh/chaos-mesh -n chaos-mesh --create-namespace

# Aplicar experimento
kubectl apply -f chaos/experiments/01-payment-api-pod-kill.yaml

# Observar
kubectl get podchaos -n chaos-mesh
kubectl describe podchaos payment-api-pod-kill -n chaos-mesh

# Rollback manual (se necessário)
kubectl delete -f chaos/experiments/01-payment-api-pod-kill.yaml
```

### Docker Compose (local) — Scripts
```bash
# Subir stack
make up

# Injetar falha: matar payment-api
./chaos/scripts/local-pod-kill.sh

# Observar logs/alertas Datadog (runbook disparado)

# Restaurar
docker compose up -d payment-api
```

> **Nota**: Para latency injection local, adicione `toxiproxy` como sidecar no docker-compose.yml e configure via `chaos/scripts/local-latency-inject.sh`.

---

## Métricas de Sucesso por Experimento

| Experimento | Hipótese | Métrica de Sucesso |
|---|---|---|
| CE-01 | Killing 1 pod não impacta SLO | Error budget consumption < 10%; MTTR < 2min |
| CE-02 | 100ms MongoDB latency não degrada P95 > 5s | Disbursement P95 < 5s; burn rate < 2 |
| CE-03 | Kafka partition recovery automático | Cross-border success rate ≥ 99%; MTTR < 1min |
| CE-04 | Mastercard latency < 500ms não afeta P99 | Authorization P99 < 300ms; burn rate < 2 |
| CE-05 | CPU stress não causa OOM | No pod restarts; auth success rate ≥ 99.5% |

---

## Integração com Runbooks

Cada experimento referencia um runbook existente em `runbooks/`. Durante Game Day, o on-call deve seguir o runbook e anotar desvios.

### Runbooks Associados
- `runbooks/payment-service-unavailable.md`
- `runbooks/card-authorization-failure.md`
- `runbooks/working-capital-processing-delay.md`
- `runbooks/travel-card-creation-failure.md`
- `runbooks/cross-border-transaction-failure.md`
- `runbooks/mastercard-network-unavailable.md`
- `runbooks/authorization-latency-degradation.md`

---

## Roadmap

- [ ] Adicionar `toxiproxy` ao docker-compose.yml para latency injection local
- [ ] Integrar Chaos Mesh no GitHub Actions (validar experimentos em PR)
- [ ] Dashboard Datadog específico para "Chaos Experiment Tracking"
- [ ] Expandir para StressChaos (memory, io) e TimeChaos
- [ ] Automatizar relatório pós-Game Day (template no Confluence/Notion)

---

## Referências
- Chaos Mesh: https://chaos-mesh.org/docs/
- Datadog SLO Burn Rate: https://docs.datadoghq.com/observability/slo/burn_rate_alerts/
- Runbooks Keo Capital: `runbooks/`
- SLOs: `docs/slos/slos.md`, `terraform/slos.tf`, `terraform/burn-rate.tf`