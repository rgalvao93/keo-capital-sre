const database = db.getSiblingDB("keo-capital");

if (!database.getCollectionNames().includes("audit_events")) {
  database.createCollection("audit_events");
}

database.audit_events.createIndex({ customerId: 1, createdAt: -1 });
database.audit_events.createIndex({ eventType: 1, createdAt: -1 });
database.audit_events.createIndex({ "actor.id": 1, createdAt: -1 });

// Seed de exemplo para demos
database.audit_events.insertMany([
  {
    eventType: "CREDIT_LIMIT_CHANGE",
    actor: { id: "ops@keo", type: "USER", ip: "10.0.0.1" },
    resource: { type: "CREDIT_APPLICATION", id: "app-001" },
    customerId: "CUST-1",
    before: { creditLimit: 10000 },
    after: { creditLimit: 20000 },
    panTouched: false,
    outcome: "SUCCESS",
    correlationId: "corr-001",
    service: "credit-service",
    region: "latam",
    createdAt: new Date()
  },
  {
    eventType: "PAN_ACCESS",
    actor: { id: "payment-api", type: "SERVICE", ip: "10.0.0.2" },
    resource: { type: "CARD", id: "card-001" },
    customerId: "CUST-1",
    before: null,
    after: null,
    panTouched: true,
    outcome: "SUCCESS",
    correlationId: "corr-002",
    service: "payment-api",
    region: "latam",
    createdAt: new Date()
  }
]);
