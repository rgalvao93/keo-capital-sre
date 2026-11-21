# Security Monitors — Keo Capital

# Log alerts require "Track logs" enabled in the organization
# Uncomment when log tracking is enabled

module "fraud_detection" {
  source = "./modules/monitor"

  name    = "Fraud Detection - Suspicious Transactions - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):avg:keo.fraud.suspicious_transaction_rate{*} > 0.05"
  message = "Padrão suspeito de transação detectado (possível fraude). Verificar logs e bloquear cliente se necessário. @slack-alerts-security @pagerduty-sre"

  threshold_critical = 0.05
  threshold_warning  = 0.03

  tags = ["service:payment-api", "team:sre", "compliance:pci-dss", "severity:critical"]
}

module "waf_blocked_requests" {
  source = "./modules/monitor"

  name    = "WAF Blocked Requests - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):sum:keo.waf.blocked_requests{*}.as_rate() > 10"
  message = "WAF bloqueando requisições em alta taxa. Possível ataque em andamento. Verificar logs do firewall. @slack-alerts-security @pagerduty-sre"

  threshold_critical = 10
  threshold_warning  = 5

  tags = ["service:waf", "team:sre", "compliance:security", "severity:high"]
}