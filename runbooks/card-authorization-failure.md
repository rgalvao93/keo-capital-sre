# Runbook: Falha de Autorização de Cartão

## Severidade
**SEV-1** (Crítico) — Afeta diretamente transações de clientes

## Sintomas
- Alerta: `keo.payments.authorization.success_rate < 99.5%`
- Alerta: `keo.payments.authorization.latency_p99 > 300ms`
- Clientes reportando falhas de pagamento
- Dashboard de transações mostrando queda abrupta

## Impacto
- Transações de cartão recusadas
- Perda de revenue
- Experiência degradada para clientes B2B

## Diagnóstico

### 1. Verificar se é problema interno ou externo

```bash
# Verificar status do Mastercard Network
# Datadog: External Dependencies Dashboard

# Verificar logs de erro recentes
# Datadog: Logs Explorer → service:payment-api → status:error
```

### 2. Verificar integração com Mastercard

- [ ] Verificar latência de resposta do Mastercard
- [ ] Verificar taxa de timeout
- [ ] Verificar erros de autenticação/API
- [ ] Verificar se há mudanças recentes no código (deploy)

### 3. Verificar infraestrutura

- [ ] CPU/Memória dos pods de payment-api
- [ ] Conexões com banco de dados
- [ ] Status do Redis (cache de autorização)
- [ ] Lag de filas (Kafka/RabbitMQ)

### 4. Verificar dependências internas

- [ ] Serviço de crédito (limite de crédito)
- [ ] Serviço de KYC (validação de cliente)
- [ ] Serviço de câmbio (se cross-border)

## Ações de Mitigação

### Se problema no Mastercard Network
1. Ativar fallback para processador alternativo (se disponível)
2. Comunicar clientes sobre degradação
3. Monitorar recovery do Mastercard

### Se problema interno
1. Rollback do último deploy (se aplicável)
2. Escalar pods de payment-api
3. Reiniciar serviço se necessário
4. Verificar conexões com banco de dados

### Se problema de infraestrutura
1. Escalar recursos (CPU/memória)
2. Verificar rede/DNS
3. Verificar load balancer

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
keo.external.mastercard.latency_p99
keo.external.mastercard.timeout_rate
```