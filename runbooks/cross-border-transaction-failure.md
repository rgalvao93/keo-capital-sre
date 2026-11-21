# Runbook: Erro de Câmbio (Cross-border)

## Severidade
**SEV-2** (Alto)

## Sintomas
- Alerta: `keo.crossborder.fx.error_rate > 0.5%`
- Alerta: `keo.external.fx.availability < 99.5%`
- Transações cross-border falhando com erro de câmbio
- Dashboard Cross-border mostrando queda

## Impacto
- Transações internacionais não processadas
- Clientes não conseguem fazer pagamentos cross-border
- Experiência degradada

## Diagnóstico

### 1. Verificar provedor de câmbio
- [ ] Verificar status do provedor de FX
- [ ] Verificar se há manutenção programada
- [ ] Verificar conectividade com API de câmbio

### 2. Verificar cross-border-fx
- [ ] Verificar logs de erro
- [ ] Verificar se há transações travadas
- [ ] Verificar cache de taxas

### 3. Verificar infraestrutura
- [ ] Verificar pods de cross-border-fx
- [ ] Verificar conexões com banco de dados

## Ações de Mitigação

### Se problema no provedor de FX
1. Verificar status do provedor
2. Tentar retry com backoff
3. Considerar provedor alternativo (se disponível)

### Se problema interno
1. Reiniciar pods de cross-border-fx
2. Limpar cache de taxas
3. Verificar e corrigir código

## Escalonamento

| Tempo | Ação |
|---|---|
| 0-15 min | SRE on-call investiga |
| 15-30 min | Escalar para tech lead de payments |
| 30+ min | Escalar para Head of Technology |

## Comunicação

- **Slack**: #incidents-payments
- **Clientes**: Notificar se > 1 hora

## Métricas a Monitorar Durante Incidente

```
keo.crossborder.fx.error_rate
keo.external.fx.availability
keo.crossborder.transaction.success_rate
keo.crossborder.transaction.volume
```