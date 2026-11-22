# Performance Testing — Keo Capital

Testes de carga usando [k6](https://k6.io).

## Pré-requisitos
- k6 instalado
- Stack local rodando (`make up`)

## Executar
```bash
k6 run tests/performance/authorization.js
```

## Configuração
- `BASE_URL`: URL do payment-api (default `http://localhost:8080`)

## Thresholds
- Latência P99 < 300ms
- Taxa de erro < 0.5%
EOF
echo done