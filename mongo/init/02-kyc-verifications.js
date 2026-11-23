const database = db.getSiblingDB("keo-capital");

if (!database.getCollectionNames().includes("kyc_verifications")) {
  database.createCollection("kyc_verifications");
}

database.kyc_verifications.createIndex({ customerId: 1, createdAt: -1 });
database.kyc_verifications.createIndex({ applicationId: 1 }, { unique: true });
database.kyc_verifications.createIndex({ status: 1 });

const seed = [
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
  },
  {
    customerId: "CUST-123",
    applicationId: "demo-workeo-approved",
    provider: "kyc-provider-x",
    status: "APPROVED",
    riskLevel: "LOW",
    checks: [{ name: "document", result: "PASS", score: 0.98 }],
    providerRequestId: "KYC-DEMO-001",
    latencyMs: 412,
    errorCode: null,
    createdAt: new Date()
  },
  {
    customerId: "CUST-124",
    applicationId: "demo-workeo-rejected",
    provider: "kyc-provider-x",
    status: "REJECTED",
    riskLevel: "HIGH",
    checks: [{ name: "document", result: "FAIL", score: 0.42 }],
    providerRequestId: "KYC-DEMO-002",
    latencyMs: 681,
    errorCode: "DOCUMENT_UNVERIFIED",
    createdAt: new Date()
  }
];

seed.forEach((verification) => {
  database.kyc_verifications.updateOne(
    { applicationId: verification.applicationId },
    { $set: verification },
    { upsert: true }
  );
});
