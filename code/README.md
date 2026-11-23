# Keo Capital SRE — Código de Simulação

## Stack Tecnológica

| Componente | Tecnologia |
|---|---|
| **Backend** | Java 21 + Spring Boot 3.2 |
| **Banco de dados** | MongoDB 7.0 |
| **Message Broker** | Apache Kafka 7.5 |
| **Cache** | Redis 7.2 |
| **Observabilidade** | Datadog APM (dd-trace-java) |
| **Infra** | Docker Compose |
| **Blockchain** | Mock Service (simulação) |

## Estrutura do Projeto

```
code/
├── common/                    # Modelos compartilhados (Transaction, Card, CreditApplication)
├── payment-api/               # API de pagamentos (porta 8080)
├── credit-service/            # Serviço de crédito (porta 8081)
├── travel-card-service/       # Serviço de cartão de viagem (porta 8082)
├── cross-border-fx/           # Serviço de câmbio cross-border (porta 8083)
├── blockchain-mock/           # Mock de blockchain (porta 8084)
└── traffic-simulator/         # Simulador de tráfego (porta 8085)
```

## MongoDB

O banco `keo-capital` utiliza as seguintes coleções:

- `customers` — clientes, região, status e produtos contratados
- `cards` — cartões virtuais e físicos emitidos
- `credit_applications` — avaliações e solicitações de crédito
- `transactions` — pagamentos, autorizações, disbursements e transações internacionais

O schema, os índices e os dados iniciais dos clientes são aplicados por
[mongo/init/01-keo-schema.js](../mongo/init/01-keo-schema.js) em novas instalações.

## Como rodar

### 1. Baixar o Datadog Java Agent
```bash
wget -O dd-java-agent.jar https://dtdg.co/latest-java-tracer
```

### 2. Configurar variáveis de ambiente
```bash
export DD_API_KEY=sua_api_key
export DD_APP_KEY=sua_app_key
```

### 3. Subir a infraestrutura
```bash
docker-compose up -d
```

### 4. Build e rodar os serviços
```bash
mvn clean install -DskipTests
docker-compose up -d --build
```

### 5. Verificar health checks
```bash
curl http://localhost:8080/api/v1/health
curl http://localhost:8081/api/v1/health
curl http://localhost:8082/api/v1/health
curl http://localhost:8083/api/v1/health
curl http://localhost:8084/api/v1/blockchain/health
```

### 6. Testar endpoints
```bash
# Autorização de cartão
curl -X POST http://localhost:8080/api/v1/card/authorize \
  -H "Content-Type: application/json" \
  -d '{"customerId":"CUST-001","region":"latam","product":"keo-travel","amount":150.0}'

# Avaliação de crédito
curl -X POST http://localhost:8081/api/v1/credit/evaluate \
  -H "Content-Type: application/json" \
  -d '{"customerId":"CUST-001","region":"latam","product":"workeo","requestedAmount":5000.0}'

# Conversão de câmbio
curl -X POST http://localhost:8083/api/v1/fx/convert \
  -H "Content-Type: application/json" \
  -d '{"from":"USD","to":"BRL","amount":1000.0}'

# Transação blockchain
curl -X POST http://localhost:8084/api/v1/blockchain/execute \
  -H "Content-Type: application/json" \
  -d '{"type":"PAYMENT","amount":500.0,"from":"buyer","to":"supplier"}'
```

## Endpoints

### Payment API (8080)
- `POST /api/v1/card/authorize` — Autorização de cartão
- `POST /api/v1/card/create` — Criação de cartão virtual
- `GET /api/v1/card/{id}` — Consulta de cartão
- `POST /api/v1/payment/process` — Processamento de pagamento
- `GET /api/v1/payment/{id}/status` — Status de pagamento

### Credit Service (8081)
- `POST /api/v1/credit/evaluate` — Avaliação de crédito
- `POST /api/v1/credit/approve` — Aprovação de crédito
- `GET /api/v1/credit/{id}/status` — Status de crédito
- `POST /api/v1/working-capital/request` — Solicitação de working capital
- `POST /api/v1/working-capital/disburse` — Disbursement

### Travel Card Service (8082)
- `POST /api/v1/travel/card/create` — Criação de cartão de viagem
- `GET /api/v1/travel/card/{id}` — Consulta de cartão
- `POST /api/v1/travel/transaction` — Transação de viagem
- `GET /api/v1/travel/report` — Relatório de gastos

### Cross-border FX (8083)
- `POST /api/v1/fx/convert` — Conversão de câmbio
- `GET /api/v1/fx/rate` — Consulta de taxa
- `POST /api/v1/crossborder/transaction` — Transação cross-border

### Blockchain Mock (8084)
- `POST /api/v1/blockchain/execute` — Execução de transação blockchain
- `GET /api/v1/blockchain/status/{txHash}` — Status de transação

## Observabilidade

Todos os serviços estão instrumentados com Datadog APM (dd-trace-java) e enviam:
- Traces (APM)
- Métricas customizadas
- Logs

Os monitors estão configurados no Datadog (37 monitors) e os dashboards estão documentados em `docs/dashboards/dashboards.md`.