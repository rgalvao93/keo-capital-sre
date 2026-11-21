# Runbook: Falha de Disbursement (Working Capital)

## Severidade
**SEV-2** (Alto)

## Sintomas
- Alerta: `keo.external.bank.availability < 99.5%`
- Alerta: `keo.working_capital.disbursement.latency_p95 > 5s`
- Clientes reportando que working capital não está sendo disbursed
- Dashboard Workeo mostrando queda em disbursement

## Impacto
- Clientes não recebem working capital
- Experiência degradada
- Possível perda de clientes

## Diagnóstico

### 1. Verificar status dos bancos parceiros
- [ ] Verificar status page dos bancos
- [ ] Verificar se há manutenção programada
- [ ] Verificar conectividade com APIs bancárias

### 2. Verificar credit-service
- [ ] Verificar logs de erro do credit-service
- [ ] Verificar status do disbursement queue
- [ ] Verificar se há transações travadas

### 3. Verificar infraestrutura
- [ ] Verificar pods de credit-service
- [ ] Verificar conexões com banco de dados
- [ ] Verificar connection pool

## Ações de Mitigação

### Se problema nos bancos parceiros
1. Tentar retry com backoff
2. Comunicar clientes sobre delay
3. Verificar se há banco alternativo disponível

### Se problema interno
1. Reiniciar pods de credit-service
2. Reprocessar transações travadas
3. Verificar e corrigir código

## Escalonamento

| Tempo | Ação |
|---|---|
| 0-15 min | SRE on-call investiga |
| 15-30 min | Escalar para tech lead de credit |
| 30+ min | Escalar para Head of Technology |

## Comunicação

- **Slack**: #incidents-credit
- **Clientes**: Notificar se > 1 hora

## Métricas a Monitorar Durante Incidente

```
keo.external.bank.availability
keo.external.bank.disbursement.latency_p95
keo.working_capital.disbursement.success_rate
keo.working_capital.disbursement.volume
```