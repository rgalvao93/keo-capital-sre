# Monitor: FX Rate Staleness

## Métrica
`keo.fx.rate_age.seconds` (tempo desde updatedAt)

## Threshold
- Warning: > 900s (15 min) para par ativo
- Critical: > 3600s

## Ação
Atualizar taxa via runbook fx-rate-update.md.