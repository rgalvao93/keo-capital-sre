# Runbook: Atualização de Taxa de Câmbio (Cross-border)

## Severidade
**SEV-3** (Médio)

## Quando usar
- Taxa desatualizada (monitor `keo.fx.rate_age.seconds > 900` para par ativo)
- Necessidade de correção manual de taxa durante incidente

## Procedimento
1. Conectar ao Mongo: `mongo keo-capital`
2. Atualizar documento em `fx_rates`:
   ```js
   db.fx_rates.updateOne(
     { _id: "USD-BRL" },
     { $set: { rate: 5.1, updatedAt: new Date(), updatedBy: "ops@keo", source: "manual" } }
   )
   ```
3. Validar: `db.fx_rates.findOne({ _id: "USD-BRL" })`
4. Aguardar cache local expirar (até 60s) ou reiniciar cross-border-fx
5. Verificar monitor `keo.fx.rate_age.seconds` voltando ao normal

## Rollback
Restaurar valor anterior a partir do histórico/backup.