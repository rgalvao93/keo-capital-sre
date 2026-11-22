# External Dependencies Monitors — Keo Capital

module "mastercard_availability" {
  source = "./modules/monitor"

  name    = "Mastercard Network Availability - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):avg:keo.external.mastercard.availability{*} < 99.9"
  message = "Disponibilidade do Mastercard Network abaixo de 99.9%. Incidente crítico! Verificar status da rede Mastercard. @slack-alerts-payments @pagerduty-sre"

  threshold_critical = 99.9
  threshold_warning  = 99.95

  tags = ["service:payment-api", "team:payments", "dependency:mastercard", "severity:critical"]
}

module "mastercard_latency_p99" {
  source = "./modules/monitor"

  name    = "Mastercard Network Latency P99 - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):p99:keo.external.mastercard.latency{*} > 200"
  message = "Latência P99 do Mastercard Network acima de 200ms. Verificar integração e rede. @slack-alerts-payments @pagerduty-sre"

  threshold_critical = 200
  threshold_warning  = 150

  tags = ["service:payment-api", "team:payments", "dependency:mastercard", "severity:high"]
}

module "mastercard_timeout_rate" {
  source = "./modules/monitor"

  name    = "Mastercard Network Timeout Rate - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):avg:keo.external.mastercard.timeout_rate{*} > 0.1"
  message = "Timeout rate do Mastercard Network acima de 0.1%. Verificar rede e integração. @slack-alerts-payments @pagerduty-sre"

  threshold_critical = 0.1
  threshold_warning  = 0.05

  tags = ["service:payment-api", "team:payments", "dependency:mastercard", "severity:critical"]
}

module "bank_availability" {
  source = "./modules/monitor"

  name    = "Bank Partner Availability - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):avg:keo.external.bank.availability{*} < 99.5"
  message = "Disponibilidade dos bancos parceiros abaixo de 99.5%. Verificar conectividade e status dos bancos. @slack-alerts-credit @pagerduty-sre"

  threshold_critical = 99.5
  threshold_warning  = 99.7

  tags = ["service:working-capital-disbursement", "team:credit", "dependency:bank", "severity:high"]
}

module "bank_disbursement_latency" {
  source = "./modules/monitor"

  name    = "Bank Disbursement Latency P95 - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):p95:keo.external.bank.disbursement.latency{*} > 3"
  message = "Latência de disbursement P95 acima de 3s. Verificar bancos parceiros. @slack-alerts-credit @pagerduty-sre"

  threshold_critical = 3
  threshold_warning  = 2.5

  tags = ["service:working-capital-disbursement", "team:credit", "dependency:bank", "severity:high"]
}

module "fx_provider_availability" {
  source = "./modules/monitor"

  name    = "FX Provider Availability - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):avg:keo.external.fx.availability{*} < 99.5"
  message = "Disponibilidade do provedor de câmbio abaixo de 99.5%. Verificar conectividade e status do provedor. @slack-alerts-payments @pagerduty-sre"

  threshold_critical = 99.5
  threshold_warning  = 99.7

  tags = ["service:cross-border-fx", "team:payments", "dependency:fx-provider", "severity:high"]
}

module "kyc_provider_availability" {
  source = "./modules/monitor"

  name    = "KYC Provider Availability - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):avg:keo.external.kyc.availability{*} < 99.0"
  message = "Disponibilidade do provedor de KYC abaixo de 99.0%. Verificar conectividade e status do provedor. @slack-alerts-credit"

  threshold_critical = 99.0
  threshold_warning  = 99.5

  tags = ["service:credit-service", "team:credit", "dependency:kyc-provider", "severity:medium"]
}