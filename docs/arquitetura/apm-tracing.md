# APM e Distributed Tracing — Keo Capital

## Visão Geral
Instrumentação de APIs críticas com Datadog APM para rastreamento de performance, erros e dependências.

## Serviços a Instrumentar

### 1. payment-api
**Endpoints críticos:**
- `POST /api/v1/card/authorize` — Autorização de cartão
- `POST /api/v1/card/create` — Criação de cartão virtual
- `GET /api/v1/card/{id}` — Consulta de cartão
- `POST /api/v1/payment/process` — Processamento de pagamento
- `GET /api/v1/payment/{id}/status` — Status de pagamento

**Dependências:**
- Mastercard Network (autorização)
- PostgreSQL (dados de cartão)
- Redis (cache de autorização)
- credit-service (validação de limite)

### 2. credit-service
**Endpoints críticos:**
- `POST /api/v1/credit/evaluate` — Avaliação de crédito
- `POST /api/v1/credit/approve` — Aprovação de crédito
- `GET /api/v1/credit/{id}/status` — Status de crédito
- `POST /api/v1/working-capital/request` — Solicitação de working capital
- `POST /api/v1/working-capital/disburse` — Disbursement

**Dependências:**
- PostgreSQL (dados de crédito)
- credit-scoring (modelo de scoring)
- bancos parceiros (disbursement)
- KYC provider (validação de cliente)

### 3. travel-card-service
**Endpoints críticos:**
- `POST /api/v1/travel/card/create` — Criação de cartão de viagem
- `GET /api/v1/travel/card/{id}` — Consulta de cartão
- `POST /api/v1/travel/transaction` — Transação de viagem
- `GET /api/v1/travel/report` — Relatório de gastos

**Dependências:**
- Mastercard Network (emissão de cartão)
- PostgreSQL (dados de viagem)
- Redis (cache de cartão)

### 4. cross-border-fx
**Endpoints críticos:**
- `POST /api/v1/fx/convert` — Conversão de câmbio
- `GET /api/v1/fx/rate` — Consulta de taxa
- `POST /api/v1/crossborder/transaction` — Transação cross-border

**Dependências:**
- Provedor de câmbio (FX provider)
- Mastercard Network
- PostgreSQL (dados de transação)

## Configuração do Datadog APM

### Variáveis de Ambiente
```bash
DD_SERVICE=payment-api
DD_ENV=prod
DD_VERSION=1.0.0
DD_AGENT_HOST=datadog-agent
DD_TRACE_AGENT_PORT=8126
DD_LOGS_INJECTION=true
DD_PROFILING_ENABLED=true
```

### Tags de Serviço
```
service:payment-api
env:prod
version:1.0.0
team:payments
```

## Monitors de Aplicação

### 1. Latência de Endpoint (P99)
```
avg(last_5m):p99:trace.http.request.duration{service:payment-api} > 500
```

### 2. Taxa de Erro por Endpoint
```
avg(last_5m):sum:trace.http.request.errors{service:payment-api}.as_rate() > 0.01
```

### 3. Throughput (Requests/sec)
```
avg(last_5m):sum:trace.http.request.hits{service:payment-api}.as_rate()
```

### 4. Database Query Latency
```
avg(last_5m):p99:trace.postgres.query.duration{service:payment-api} > 100
```

### 5. Redis Cache Hit Rate
```
avg(last_5m):avg:trace.redis.command.hits{service:payment-api} / avg:trace.redis.command.total{service:payment-api} < 0.8
```

## Distributed Tracing

### Fluxo de Autorização de Cartão
```
payment-api → credit-service (validação de limite)
           → Mastercard Network (autorização)
           → PostgreSQL (registro de transação)
           → Redis (cache de autorização)
```

### Fluxo de Criação de Cartão (Keo Travel)
```
travel-card-service → Mastercard Network (emissão)
                    → PostgreSQL (registro)
                    → Redis (cache)
```

### Fluxo de Working Capital (Workeo)
```
credit-service → credit-scoring (avaliação)
              → PostgreSQL (dados de crédito)
              → banco parceiro (disbursement)
```

## Error Tracking

### Grupos de Erro a Monitorar
1. **MastercardTimeout** — Timeout na comunicação com Mastercard
2. **DatabaseConnectionError** — Falha de conexão com PostgreSQL
3. **RedisConnectionError** — Falha de conexão com Redis
4. **CreditScoringError** — Erro no modelo de scoring
5. **FXProviderError** — Erro no provedor de câmbio
6. **BankDisbursementError** — Erro no disbursement bancário

## Profiling

### Hotspots a Monitorar
- Cálculo de limites de crédito (credit-service)
- Processamento de autorização (payment-api)
- Conversão de câmbio (cross-border-fx)
- Geração de relatórios (travel-card-service)