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

module "credit_limit_change_audit" {
  source = "./modules/monitor"

  name    = "Credit Limit Change Audit Trail - Keo Capital"
  type    = "log alert"
  query   = "logs(\"service:credit-service status:info \\\"credit limit changed\\\"\").rollup(\"count\").last(\"5m\") > 10"
  message = "Mudanças em limites de crédito detectadas. Verificar audit trail e autorização. @slack-alerts-security @pagerduty-sre"

  threshold_critical = 10
  threshold_warning  = 5

  tags = ["service:credit-service", "team:credit", "compliance:audit", "severity:high"]
}

module "pan_access_anomaly" {
  source = "./modules/monitor"

  name    = "PAN Access Anomaly - Keo Capital"
  type    = "log alert"
  query   = "logs(\"service:payment-api status:info \\\"PAN accessed\\\"\").rollup(\"count\").last(\"5m\") > 100"
  message = "Acesso anômalo a dados de cartão (PAN) detectado. Verificar logs de auditoria PCI-DSS. @slack-alerts-security @pagerduty-sre"

  threshold_critical = 100
  threshold_warning  = 50

  tags = ["service:payment-api", "team:sre", "compliance:pci-dss", "severity:critical"]
}