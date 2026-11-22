#!/usr/bin/env bash
#
# Chaos Engineering — Local (Docker Compose)
# Experimento CE-01: mata o container do payment-api para validar resiliência.
#
# Uso:
#   ./chaos/scripts/local-pod-kill.sh
#
# Pré-requisito: stack rodando (`make up`)
set -euo pipefail

CONTAINER="${1:-keo-payment-api}"

echo "[chaos] Injetando falha CE-01: pod kill de '${CONTAINER}'..."
docker stop "${CONTAINER}" || true

echo "[chaos] Aguarde 30s enquanto a falha está ativa (Observe Datadog SLOs + runbook)"
sleep 30

echo "[chaos] Restaurando '${CONTAINER}'..."
docker start "${CONTAINER}" || docker compose up -d "${CONTAINER}"

echo "[chaos] Finalizado. Verifique: MTTR, burn rate e runbook payment-service-unavailable.md"
