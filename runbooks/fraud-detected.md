# Runbook: Alerta de Fraude Detectada

## Severidade
**SEV-2** (Alto)

## Sintomas
- Alerta: `keo.fraud.suspicious_transaction_rate > 5%`
- Múltiplas tentativas de autorização falhadas do mesmo cliente
- Padrões anômalos de transação

## Impacto
- Possível fraude em andamento
- Risco financeiro
- Necessidade de bloqueio de conta

## Diagnóstico

### 1. Verificar padrão de transação
- [ ] Identificar cliente(s) afetado(s)
- [ ] Verificar histórico de transações
- [ ] Verificar geolocalização das transações
- [ ] Verificar se há múltiplos cartões do mesmo cliente

### 2. Verificar logs de segurança
- [ ] Verificar logs de acesso
- [ ] Verificar tentativas de autenticação
- [ ] Verificar mudanças recentes na conta

## Ações de Mitigação

### Imediato
1. Bloquear cliente temporariamente
2. Notificar time de segurança
3. Preservar logs para investigação

### Após investigação
1. Se fraude confirmada: bloquear permanentemente
2. Se falso positivo: desbloquear e ajustar regras
3. Atualizar regras de fraud detection

## Escalonamento

| Tempo | Ação |
|---|---|
| 0-5 min | SRE on-call + Security investigam |
| 5-15 min | Escalar para Head of Technology |
| 15+ min | Ativar procedimento de fraude |

## Comunicação

- **Slack**: #alerts-security
- **Time de segurança**: Notificar imediatamente

## Métricas a Monitorar Durante Incidente

```
keo.fraud.suspicious_transaction_rate
keo.payments.authorization.success_rate
keo.payments.authorization.volume
```