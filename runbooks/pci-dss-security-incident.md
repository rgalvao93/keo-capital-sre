# Runbook: Incidente de Segurança PCI-DSS

## Severidade
**SEV-1** (Crítico)

## Sintomas
- Alerta: `keo.waf.blocked_requests > 10/s`
- Alerta: `keo.fraud.suspicious_transaction_rate > 5%`
- Alerta: Acesso anômalo a dados de cartão (PAN)
- Possível violação de dados

## Impacto
- Violação de compliance PCI-DSS
- Risco de multas e perda de certificação
- Risco de vazamento de dados de cartão

## Diagnóstico

### 1. Avaliar escopo
- [ ] Identificar quais dados foram acessados
- [ ] Identificar quem acessou
- [ ] Verificar se houve exfiltração de dados
- [ ] Verificar logs de auditoria

### 2. Contenção
- [ ] Bloquear acesso suspeito
- [ ] Isolar sistemas afetados
- [ ] Preservar evidências (logs, snapshots)

### 3. Investigação
- [ ] Analisar logs de acesso
- [ ] Verificar audit trail
- [ ] Identificar vetor de ataque

## Ações de Mitigação

### Imediato
1. Isolar sistemas afetados
2. Bloquear IPs/contas suspeitas
3. Preservar logs e evidências
4. Notificar time de segurança e compliance

### Após contenção
1. Investigar causa raiz
2. Implementar correções
3. Notificar autoridades (se necessário)
4. Atualizar procedimentos

## Escalonamento

| Tempo | Ação |
|---|---|
| 0-5 min | SRE + Security investigam |
| 5-15 min | Escalar para Head of Technology |
| 15-30 min | Escalar para CEO/CISO |
| 30+ min | Ativar plano de resposta a incidentes de segurança |

## Comunicação

- **Slack**: #alerts-security
- **Time de segurança**: Notificar imediatamente
- **Compliance**: Notificar em até 1 hora
- **Autoridades**: Notificar se necessário (LGPD/GDPR)

## Métricas a Monitorar Durante Incidente

```
keo.waf.blocked_requests
keo.fraud.suspicious_transaction_rate
keo.payments.authorization.volume
```