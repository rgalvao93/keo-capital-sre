# Runbook: Degradação de Aprovação de Crédito

## Severidade
**SEV-2** (Alto) — Afeta aprovação de crédito e concessão de working capital

## Sintomas
- Alerta: `keo.credit.approval.success_rate < 85%` (taxa de aprovação abaixo do esperado)
- Alerta: `keo.credit.rejection.rate > 20%`
- Aumento de rejeições de crédito sem mudança no perfil de clientes
- Dashboard Workeo mostrando queda em aprovações
- Clientes reportando que não estão conseguindo crédito

## Impacto
- Clientes não conseguem crédito aprovado
- Perda de revenue em working capital
- Experiência degradada
- Possível impacto em clientes B2B dependentes de crédito

## Diagnóstico

### 1. Verificar métricas de aprovação

```bash
# Datadog: Logs Explorer → service:credit-service → status:error
# Verificar taxa de aprovação vs rejeição nas últimas horas
```

- [ ] Taxa de aprovação atual vs baseline (esperado ~85%)
- [ ] Distribuição de credit score dos aplicantes
- [ ] Volume de aplicações de crédito

### 2. Verificar credit-service

- [ ] Verificar logs de erro do credit-service
- [ ] Verificar se há exceções na avaliação de crédito
- [ ] Verificar se o modelo de scoring está funcionando
- [ ] Verificar conexões com MongoDB
- [ ] Verificar pods e recursos (CPU/memória)

### 3. Verificar dependências

- [ ] Serviço de KYC (validação de cliente)
- [ ] Serviço de análise de risco
- [ ] Integrações com bureaus de crédito (se aplicável)

### 4. Verificar causas prováveis

- [ ] Mudança recente no modelo de scoring
- [ ] Mudança nos thresholds de aprovação
- [ ] Problema com dados de entrada (credit score null/inválido)
- [ ] Deploy recente no credit-service
- [ ] Problema de infraestrutura

## Ações de Mitigação

### Se problema no modelo de scoring
1. Reverter para modelo anterior (se disponível)
2. Ajustar thresholds temporariamente
3. Investigar dados de entrada

### Se problema de dados
1. Verificar integridade dos dados de credit score
2. Corrigir pipeline de dados
3. Reprocessar aplicações afetadas

### Se problema interno
1. Reiniciar pods de credit-service
2. Verificar e corrigir código
3. Rollback do último deploy

### Se problema de infraestrutura
1. Escalar pods de credit-service
2. Verificar conexões com banco de dados
3. Verificar rede/DNS

## Escalonamento

| Tempo | Ação |
|---|---|
| 0-15 min | SRE on-call investiga |
| 15-30 min | Escalar para tech lead de credit |
| 30+ min | Escalar para Head of Technology |

## Comunicação

- **Slack**: #incidents-credit
- **Clientes**: Notificar se > 1 hora

## Post-mortem

Após resolução:
1. Criar post-mortem no Jira
2. Identificar causa raiz
3. Definir ações corretivas
4. Atualizar este runbook se necessário

## Métricas a Monitorar Durante Incidente

```
keo.credit.approval
keo.credit.rejection
keo.credit.approval.success_rate
keo.working_capital.disbursement.success_rate
keo.working_capital.disbursement.latency_p95
```