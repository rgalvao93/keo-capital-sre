#!/usr/bin/env bash
#
# Chaos Engineering — Rollback de experimentos locais.
set -euo pipefail

SERVICE="${1:-keo-payment-api}"

echo "[rollback] Restaurando '${SERVICE}'..."
docker start "${SERVICE}" 2>/dev/null || docker compose up -d "${SERVICE}"
echo "[rollback] Concluído."
