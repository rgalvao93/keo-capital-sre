import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '1m', target: 50 },
    { duration: '2m', target: 50 },
    { duration: '1m', target: 0 },
  ],
  thresholds: {
    http_req_duration: ['p(99)<300'],
    http_req_failed: ['rate<0.005'],
  },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export default function () {
  const payload = JSON.stringify({
    customerId: `CUST-${Math.floor(Math.random() * 100000)}`,
    region: 'latam',
    product: 'keo-travel',
    amount: 100 + Math.random() * 900,
  });

  const params = { headers: { 'Content-Type': 'application/json' } };
  const res = http.post(`${BASE_URL}/api/authorize`, payload, params);

  check(res, {
    'status is 200': (r) => r.status === 200,
    'latency < 300ms': (r) => r.timings.duration < 300,
  });

  sleep(0.1);
}
