# Runbook: Falha de Criação de Cartão (Keo Travel)

## Severidade
**SEV-2** (Alto)

## Sintomas
- Alerta: `keo.travel.card_creation.latency_p95 > 3s`
- Alerta: `keo.travel.card_creation.success_rate < 99.5%`
- Clientes reportando que não conseguem criar cartões de viagem
- Dashboard Keo Travel mostrando queda em criação de cartões

## Impacto
- Clientes não conseguem criar cartões de viagem
- Transações de viagem não processadas
- Experiência degradada

## Diagnóstico

### 1. Verificar integração com Mastercard
- [ ] Verificar status do Mastercard Network
- [ ] Verificar se há erros de API na criação de cartão
- [ ] Verificar logs de erro do travel-card-service

### 2. Verificar travel-card-service
- [ ] Verificar pods e recursos
- [ ] Verificar conexões com banco de dados
- [ ] Verificar cache Redis

### 3. Verificar infraestrutura
- [ ] Verificar CPU/memória dos pods
- [ ] Verificar rede

## Ações de Mitigação

### Se problema no Mastercard
1. Verificar status do Mastercard
2. Comunicar clientes sobre delay
3. Tentar retry com backoff

### Se problema interno
1. Reiniciar pods de travel-card-service
2. Escalar pods se necessário
3. Verificar e corrigir código

## Escalonamento

| Tempo | Ação |
|---|---|
| 0-15 min | SRE on-call investiga |
| 15-30 min | Escalar para tech lead de travel |
| 30+ min | Escalar para Head of Technology |

## Comunicação

- **Slack**: #incidents-travel
- **Clientes**: Notificar se > 1 hora

## Métricas a Monitorar Durante Incidente

```
keo.travel.card_creation.latency_p95
keo.travel.card_creation.success_rate
keo.external.mastercard.availability
keo.external.mastercard.latency_p99
```