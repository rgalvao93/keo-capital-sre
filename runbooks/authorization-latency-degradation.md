# Runbook: Degradação de Latência de Autorização

## Severidade
**SEV-2** (Alto) — Latência de autorização de cartão acima do SLO

## Sintomas
- Alerta: `keo.payments.authorization.latency_p99 > 300ms`
- Alerta: `keo.payments.authorization.latency_p95 > 200ms`
- Clientes reportando lentidão em pagamentos
- Dashboard de transações mostrando aumento de latência
- Timeouts ocasionais em autorização

## Impacto
- Experiência degradada para clientes B2B
- Possível aumento de timeouts e falhas
- Risco de violação de SLO (≥ 99.5% sucesso)
- Perda de revenue se clientes desistirem

## Diagnóstico

### 1. Verificar latência por componente

```bash
# Datadog: APM → service:payment-api → traces
# Verificar onde está o gargalo: DB, cache, Mastercard, Kafka
```

- [ ] Latência p50, p95, p99 de autorização
- [ ] Latência de chamadas ao MongoDB
- [ ] Latência de cache Redis (hit/miss ratio)
- [ ] Latência de integração com Mastercard
- [ ] Latência de publicação no Kafka

### 2. Verificar infraestrutura

- [ ] CPU/Memória dos pods de payment-api
- [ ] Conexões com banco de dados (connection pool)
- [ ] Status do Redis (cache de autorização)
- [ ] Lag de filas (Kafka/RabbitMQ)
- [ ] Rede/DNS

### 3. Verificar causas prováveis

- [ ] Cache miss alto (Redis)
- [ ] Banco de dados lento (queries sem índice)
- [ ] Integração com Mastercard lenta
- [ ] Concorrência alta (muitas requisições)
- [ ] Garbage collection (JVM)
- [ ] Deploy recente com regressão de performance

### 4. Verificar dependências internas

- [ ] Serviço de crédito (limite de crédito)
- [ ] Serviço de KYC (validação de cliente)
- [ ] Serviço de câmbio (se cross-border)

## Ações de Mitigação

### Se problema de cache (Redis)
1. Verificar hit ratio do cache
2. Aumentar TTL se apropriado
3. Escalar Redis
4. Investigar eviction policy

### Se problema de banco de dados
1. Verificar queries lentas (slow query log)
2. Verificar índices
3. Escalar banco de dados
4. Otimizar queries

### Se problema de integração Mastercard
1. Verificar latência do Mastercard Network
2. Ativar timeout mais agressivo
3. Verificar se há fallback disponível

### Se problema de concorrência/recursos
1. Escalar pods de payment-api
2. Aumentar connection pool
3. Verificar thread pool

### Se problema de JVM/GC
1. Verificar métricas de GC
2. Ajustar heap size
3. Reiniciar pods

### Se problema de código/deploy
1. Rollback do último deploy
2. Profiling para identificar gargalo
3. Corrigir e redeployar

## Escalonamento

| Tempo | Ação |
|---|---|
| 0-15 min | SRE on-call investiga |
| 15-30 min | Escalar para tech lead de payments |
| 30+ min | Escalar para Head of Technology |

## Comunicação

- **Slack**: #incidents-payments
- **Clientes**: Notificar se > 1 hora

## Post-mortem

Após resolução:
1. Criar post-mortem no Jira
2. Identificar causa raiz
3. Definir ações corretivas
4. Atualizar este runbook se necessário

## Métricas a Monitorar Durante Incidente

```
keo.payments.authorization.latency_p50
keo.payments.authorization.latency_p95
keo.payments.authorization.latency_p99
keo.payments.authorization.success_rate
keo.external.mastercard.latency_p99
keo.payments.authorization.volume
```