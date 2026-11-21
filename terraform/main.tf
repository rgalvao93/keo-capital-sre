# Root module - Datadog monitors are defined in terraform/monitors/*.tf
# Use the reusable module in terraform/modules/monitor/

# Example:
# module "card_auth_success_rate" {
#   source = "./modules/monitor"
#   name    = "Card Authorization Success Rate - Keo Capital"
#   type    = "metric alert"
#   query   = "avg(last_5m):avg:keo.payments.authorization.success_rate{*} < 99.5"
#   message = "Taxa de sucesso de autorização de cartão abaixo de 99.5%. @slack-alerts-payments @pagerduty-sre"
#   thresholds = {
#     critical = 99.5
#     warning  = 99.0
#   }
#   tags = ["service:payment-api", "team:payments", "severity:critical"]
# }