const database = db.getSiblingDB("keo-capital");

if (!database.getCollectionNames().includes("outbox_events")) {
  database.createCollection("outbox_events");
}

database.outbox_events.createIndex({ status: 1, nextAttemptAt: 1 });
database.outbox_events.createIndex({ aggregateId: 1 });
database.outbox_events.createIndex({ createdAt: -1 });

// TTL para SENT: 7 dias
database.outbox_events.createIndex(
  { sentAt: 1 },
  { expireAfterSeconds: 7 * 24 * 60 * 60, partialFilterExpression: { status: "SENT" } }
);

database.outbox_events.insertMany([
  {
    aggregateType: "TRANSACTION",
    aggregateId: "a1b2c3",
    eventType: "AUTHORIZATION",
    topic: "payment-events",
    payload: { transactionId: "txn-001", amount: 100.0, currency: "USD" },
    status: "PENDING",
    attempts: 0,
    lastError: null,
    nextAttemptAt: new Date(),
    createdAt: new Date(),
    sentAt: null
  }
]);
