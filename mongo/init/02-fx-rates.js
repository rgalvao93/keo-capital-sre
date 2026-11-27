const database = db.getSiblingDB("keo-capital");

if (!database.getCollectionNames().includes("fx_rates")) {
  database.createCollection("fx_rates");
}

database.fx_rates.createIndex({ from: 1, to: 1, validFrom: -1 });
// O índice _id já é criado automaticamente e é único por definição.
// Recriá-lo com { unique: true } é inválido no MongoDB 7 e faz o entrypoint
// do container abortar (o que derruba o serviço no `docker compose up --wait`).

const now = new Date();
database.fx_rates.insertMany([
  { _id: "USD-BRL", from: "USD", to: "BRL", rate: 5.0, spread: 0.005, source: "manual", validFrom: now, validUntil: null, updatedAt: now, updatedBy: "ops@keo" },
  { _id: "USD-CAD", from: "USD", to: "CAD", rate: 1.35, spread: 0.005, source: "manual", validFrom: now, validUntil: null, updatedAt: now, updatedBy: "ops@keo" },
  { _id: "USD-EUR", from: "USD", to: "EUR", rate: 0.92, spread: 0.005, source: "manual", validFrom: now, validUntil: null, updatedAt: now, updatedBy: "ops@keo" }
]);
