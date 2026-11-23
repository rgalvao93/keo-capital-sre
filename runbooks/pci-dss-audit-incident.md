# Runbook: Incidente de Auditoria (PCI-DSS)

## Severidade
**SEV-2** (Alto)

## Sintomas
- Pico de `eventType=PAN_ACCESS` por ator
- Taxa de `outcome=DENIED` elevada em `audit_events`
- Alerta de anomalia de acesso a PAN

## Diagnóstico
1. Consultar `audit_events` filtrando `panTouched=true` e `createdAt` recente
2. Agrupar por `actor.id` para identificar origem
3. Verificar se há `ROLE_CHANGE` recente não autorizado
4. Correlacionar com deploys recentes (GitHub)

## Ações
- Revogar credenciais do ator suspeito
- Bloquear acesso temporário se necessário
- Notificar time de segurança e compliance
- Preservar logs (retenção 7 anos PCI-DSS)