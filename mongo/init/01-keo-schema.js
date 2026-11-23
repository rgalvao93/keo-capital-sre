const database = db.getSiblingDB("keo-capital");

const collections = [
  "customers",
  "cards",
  "credit_applications",
  "transactions"
];

collections.forEach((name) => {
  if (!database.getCollectionNames().includes(name)) {
    database.createCollection(name);
  }
});

database.customers.createIndex({ region: 1, status: 1 });
database.customers.createIndex({ products: 1 });
database.cards.createIndex({ customerId: 1, status: 1 });
database.cards.createIndex({ product: 1 });
database.credit_applications.createIndex({ customerId: 1, createdAt: -1 });
database.credit_applications.createIndex({ status: 1 });
database.transactions.createIndex({ customerId: 1, createdAt: -1 });
database.transactions.createIndex({ type: 1, status: 1, createdAt: -1 });
database.transactions.createIndex({ product: 1, region: 1 });

const regions = ["latam", "canada", "us", "eu"];
const products = ["keo-travel", "workeo", "cross-border", "supplier-payments"];

for (let i = 0; i < 1000; i++) {
  const id = "CUST-" + i;
  database.customers.updateOne(
    { _id: id },
    {
      $set: {
        name: "Customer " + i,
        status: "ACTIVE",
        region: regions[i % regions.length],
        type: i % 10 === 0 ? "ENTERPRISE" : "INDIVIDUAL",
        products: products.filter((_, index) => (i + index) % 3 !== 0),
        createdAt: new Date(),
        updatedAt: new Date()
      }
    },
    { upsert: true }
  );
}
