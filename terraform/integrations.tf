# External integrations — PagerDuty, Slack and Jira
# Reference: docs/integracoes/integracoes.md

# --- PagerDuty ---

resource "datadog_integration_pagerduty" "this" {
  count     = var.pagerduty_subdomain != "" ? 1 : 0
  subdomain = var.pagerduty_subdomain
  api_token = var.pagerduty_api_token
}

resource "datadog_integration_pagerduty_service_object" "services" {
  for_each     = var.pagerduty_services
  service_name = each.key
  service_key  = each.value
}

# --- Slack ---

resource "datadog_integration_slack_channel" "channels" {
  for_each     = var.slack_account_name != "" ? toset(var.slack_channels) : toset([])
  account_name = var.slack_account_name
  channel_name = each.value

  display {
    message      = true
    mute_buttons = true
    notified     = true
    snapshot     = true
    tags         = true
  }
}
