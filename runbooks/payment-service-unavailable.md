# Runbook: Indisponibilidade do Payment Service

## Severidade
**SEV-1** (Crítico) — Afeta todas as transações de pagamento e autorização de cartão

## Sintomas
- Alerta: `keo.payments.authorization.success_rate < 99.5%` (ou sem dados)
- Alerta: pods de `payment-api` em `CrashLoopBackOff` ou `NotReady`
- Health check `/actuator/health` retornando 5xx ou timeout
- Clientes reportando que pagamentos não estão sendo processados
- Dashboard de transações mostrando queda abrupta a zero

## Impacto
- Autorização de cartão indisponível
- Processamento de pagamentos parado
- Criação de cartões de viagem bloqueada
- Perda de revenue
- Experiência degradada para clientes B2B

## Diagnóstico

### 1. Verificar status do serviço

```bash
# Verificar pods
kubectl get pods -l service=payment-api

# Verificar logs recentes
kubectl logs -l service=payment-api --tail=100

# Verificar health check
curl -s http://payment-api:8080/actuator/health
```

### 2. Verificar infraestrutura

- [ ] CPU/Memória dos pods de payment-api
- [ ] Conexões com MongoDB
- [ ] Conexões com Kafka (tópico payment-events)
- [ ] Status do Redis (cache de autorização)
- [ ] Lag de filas (Kafka/RabbitMQ)

### 3. Verificar dependências internas

- [ ] Serviço de crédito (limite de crédito)
- [ ] Serviço de KYC (validação de cliente)
- [ ] Serviço de câmbio (se cross-border)

### 4. Verificar causa raiz provável

- [ ] Deploy recente (rollback disponível?)
- [ ] Mudança de configuração
- [ ] Exaustão de recursos (OOMKilled)
- [ ] Problema de rede/DNS
- [ ] Falha no banco de dados

## Ações de Mitigação

### Se problema de infraestrutura
1. Escalar pods de payment-api (`kubectl scale deployment payment-api --replicas=N`)
2. Verificar e corrigir recursos (CPU/memória limits)
3. Reiniciar pods se necessário (`kubectl rollout restart deployment/payment-api`)

### Se problema de dependência (MongoDB/Kafka)
1. Verificar status do MongoDB e Kafka
2. Reiniciar conexões (restart dos pods)
3. Ativar circuit breaker se disponível

### Se problema de código/deploy
1. Rollback do último deploy
2. Verificar logs de erro para identificar exceção
3. Corrigir e redeployar

### Se OOMKilled
1. Aumentar memory limit do deployment
2. Investigar memory leak
3. Reiniciar pods

## Escalonamento

| Tempo | Ação |
|---|---|
| 0-5 min | SRE on-call investiga |
| 5-15 min | Escalar para tech lead de payments |
| 15-30 min | Escalar para Head of Technology |
| 30+ min | Incidente SEV-1, ativar war room |

## Comunicação

- **Slack**: #incidents-payments
- **Status page**: Atualizar se > 15 min
- **Clientes**: Notificar se > 30 min

## Post-mortem

Após resolução:
1. Criar post-mortem no Jira
2. Identificar causa raiz
3. Definir ações corretivas
4. Atualizar este runbook se necessário

## Métricas a Monitorar Durante Incidente

```
keo.payments.authorization.success_rate
keo.payments.authorization.latency_p99
keo.payments.authorization.volume
keo.payments.processing.success_rate
keo.payments.processing.latency_p99
keo.payments.card_creation.success_rate
```