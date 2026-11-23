# Monitor: Outbox Pending Age

## Métrica
`keo.outbox.pending.age` (segundos desde createdAt do PENDING mais antigo)

## Threshold
- Warning: > 60s
- Critical: > 300s

## Ação
Verificar publisher agendado e conectividade com Kafka.