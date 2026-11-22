# Chaos Scripts — Local (Docker Compose)

Scripts para injeção de falhas no ambiente local (Docker Compose).

| Script | Experiment | Uso |
|---|---|---|
| `local-pod-kill.sh` | CE-01 | `./local-pod-kill.sh` |
| `local-latency-inject.sh` | CE-02 | `./local-latency-inject.sh <container> <latency-ms>` (requer toxiproxy) |
| `rollback.sh` | Qualquer | `./rollback.sh <container>` |

Todos os scripts assumem o stack `make up` rodando. Use `docker compose ps` para descobrir nomes dos containers.