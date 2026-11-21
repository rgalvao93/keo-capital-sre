# Levantamento da Keo Capital

## Informações Gerais

- **Nome**: Keo Capital AB (publ)
- **Ticker**: STO:KEOC (Nasdaq Stockholm)
- **Setor**: Fintech B2B — crédito, pagamentos e working capital
- **Mercados**: América Latina (desde 2020), Canadá, Américas
- **Site**: https://keocapital.com/
- **Investor Relations**: https://keocapital.com/

## Produtos e Soluções

### Workeo
Plataforma de working capital para empresas mid-market e enterprise. Fornece facilidades de crédito recorrente para gestão de estoque e capital de giro.

### Keo Travel
Cartões virtuais para gastos de viagens corporativas. Adicionado em 2022. Permite criação de cartões virtuais diretamente da plataforma.

### Supplier Payments
Pagamentos a fornecedores com cartões virtuais e working capital integrado. Permite que empresas paguem fornecedores com cartões virtuais enquanto acessam working capital.

### Corporate Travel Solutions
Gestão de gastos de viagem com cartões virtuais. Solução integrada para controle de despesas de viagem corporativa.

### Local Supplier Solutions
Soluções locais de pagamento a fornecedores. Adaptadas para mercados locais (América Latina, Canadá).

### Cross-border Card Program
Programa de cartão cross-border com parceria estratégica com Mastercard (NYSE: MA). Expandir o programa de cartão cross-border para novos mercados.

## Parcerias Estratégicas

- **Mastercard** — parceria estratégica para expandir o programa de cartão cross-border. Permite emissão de cartões em múltiplos países com suporte à rede Mastercard.

## Características do Negócio (Relevantes para SRE)

### Transações Financeiras em Tempo Real
- Autorização de cartão (Mastercard network)
- Processamento de pagamentos B2B
- Disbursement de working capital
- Conversão de câmbio cross-border

### Múltiplas Geografias
- América Latina (Brasil, México, etc.)
- Canadá
- Cross-border (múltiplos países)

### Integrações com Redes de Pagamento
- Mastercard (rede de cartões)
- Processadores de pagamento locais
- Bancos parceiros (disbursement)
- Provedores de câmbio

### Plataforma SaaS B2B
- Clientes enterprise (mid-market e large corporate)
- Múltiplos setores: manufatura, construção, logística, distribuição, serviços profissionais

### Dados Sensíveis
- Dados de cartão (PCI-DSS)
- Dados financeiros de clientes
- Dados de crédito

### Alta Disponibilidade Crítica
- Transações não podem falhar
- Autorização de cartão em tempo real
- Pagamentos a fornecedores com SLA

## Stack Tecnológico (Inferido)

Com base no tipo de negócio (fintech B2B com cartões e pagamentos), a stack provavelmente inclui:

- **APIs REST/GraphQL** para integração com clientes
- **Processadores de pagamento** (integração com Mastercard)
- **Bancos de dados** (PostgreSQL, possivelmente MongoDB)
- **Filas de mensagens** (Kafka, RabbitMQ) para processamento assíncrono
- **Cache** (Redis) para dados de autorização
- **Cloud** (AWS, GCP ou Azure)
- **Containers** (Kubernetes ou ECS)
- **API Gateway** para roteamento e rate limiting

## Requisitos de Observabilidade

### Métricas Críticas de Negócio
1. Taxa de sucesso de autorização de cartão (≥ 99.5%)
2. Latência de autorização (P99 < 300ms)
3. Volume de transações por região/produto
4. Taxa de aprovação de crédito por produto
5. Disponibilidade do serviço de pagamentos (≥ 99.9%)
6. Tempo de processamento de working capital (P95 < 5s)

### Dependências Externas Críticas
1. Mastercard Network (autorização de cartão)
2. Bancos parceiros (disbursement)
3. Provedores de câmbio (cross-border)
4. Provedores de identidade/KYC (onboarding)

### Compliance
- PCI-DSS (dados de cartão)
- Regulações financeiras locais (Brasil, Canadá, etc.)
- LGPD/GDPR (dados pessoais)