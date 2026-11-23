const database = db.getSiblingDB("keo-capital");

if (!database.getCollectionNames().includes("kyc_verifications")) {
  database.createCollection("kyc_verifications");
}

database.kyc_verifications.createIndex({ customerId: 1, createdAt: -1 });
database.kyc_verifications.createIndex({ applicationId: 1 }, { unique: true });
database.kyc_verifications.createIndex({ status: 1 });

database.kyc_verifications.insertMany([
  {
    customerId: "CUST-1",
    applicationId: "app-001",
    provider: "kyc-provider-x",
    status: "APPROVED",
    riskLevel: "LOW",
    checks: [{ name: "document", result: "PASS", score: 0.98 }],
    providerRequestId: "req-001",
    latencyMs: 412,
    errorCode: null,
    createdAt: new Date()
  }
]);
