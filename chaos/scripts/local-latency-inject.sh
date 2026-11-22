#!/usr/bin/env bash
#
# Chaos Engineering — Local (Docker Compose)
# Experimento CE-02: injeta latência de 100ms no tráfego do payment-api para mongodb.
# Requer o serviço 'toxiproxy' no docker-compose.yml (roadmap).
#
# Uso:
#   ./chaos/scripts/local-latency-inject.sh <target-container> <latency-ms>
#
set -euo pipefail

TARGET="${1:-keo-mongodb}"
LATENCY="${2:-100ms}"

echo "[chaos] Atenção: injecao de latencia local requer Toxiproxy sidecar no stack."
echo "[chaos] Equivalente Docker puro: nao aplicavel sem proxy de rede."
echo "[chaos] Use com Chaos Mesh no Kubernetes (veja chaos/experiments/02-mongodb-latency.yaml)"
echo "[chaos] Target: ${TARGET} | Latency: ${LATENCY}"
