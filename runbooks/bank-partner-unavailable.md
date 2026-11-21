# Runbook: Indisponibilidade de Banco Parceiro

## Severidade
**SEV-2** (Alto)

## Sintomas
- Alerta: `keo.external.bank.availability < 99.5%`
- Disbursement de working capital falhando
- Dashboard de dependências mostrando banco offline

## Impacto
- Working capital não sendo disbursed
- Clientes afetados
- Experiência degradada

## Diagnóstico

### 1. Verificar status do banco
- [ ] Verificar status page do banco
- [ ] Verificar se há manutenção programada
- [ ] Verificar conectividade com API bancária

### 2. Verificar credit-service
- [ ] Verificar logs de erro
- [ ] Verificar se há transações travadas
- [ ] Verificar fila de disbursement

## Ações de Mitigação

### Se problema no banco
1. Comunicar clientes sobre delay
2. Tentar retry com backoff
3. Verificar se há banco alternativo

### Se problema interno
1. Reiniciar pods de credit-service
2. Reprocessar transações travadas

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
```