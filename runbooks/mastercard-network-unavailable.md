# Runbook: Timeout de Mastercard Network

## Severidade
**SEV-1** (Crítico)

## Sintomas
- Alerta: `keo.external.mastercard.timeout_rate > 0.1%`
- Alerta: `keo.external.mastercard.latency_p99 > 200ms`
- Autorizações de cartão falhando com timeout
- Dashboard de dependências externas mostrando degradação

## Impacto
- Transações de cartão não processadas
- Clientes B2B não conseguem pagar
- Perda de revenue

## Diagnóstico

### 1. Verificar status do Mastercard
- [ ] Verificar status page do Mastercard
- [ ] Verificar se há comunicados de manutenção
- [ ] Verificar latência de resposta do Mastercard

### 2. Verificar rede e conectividade
- [ ] Verificar DNS resolution
- [ ] Verificar firewall/WAF rules
- [ ] Verificar VPN/tunnel (se aplicável)
- [ ] Verificar certificados TLS

### 3. Verificar infraestrutura interna
- [ ] Verificar pods de payment-api
- [ ] Verificar connection pool (exaustão?)
- [ ] Verificar CPU/memória dos pods
- [ ] Verificar logs de erro recentes

## Ações de Mitigação

### Se problema no Mastercard Network
1. Ativar fallback para processador alternativo (se disponível)
2. Comunicar clientes sobre degradação
3. Monitorar recovery do Mastercard
4. Considerar retry com backoff exponencial

### Se problema de rede/conectividade
1. Verificar e corrigir configuração de rede
2. Reiniciar pods de payment-api
3. Verificar firewall rules

### Se problema interno
1. Escalar pods de payment-api
2. Aumentar connection pool
3. Verificar e otimizar código (hotfix se necessário)

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

## Métricas a Monitorar Durante Incidente

```
keo.external.mastercard.availability
keo.external.mastercard.latency_p99
keo.external.mastercard.timeout_rate
keo.payments.authorization.success_rate
keo.payments.authorization.volume
```