# Synthetics HTTP API Tests — Jornadas Críticas
# https://registry.terraform.io/providers/datadog/datadog/latest/docs/resources/synthetics_test

resource "datadog_synthetics_test" "payment_api_health" {
  name      = "Keo Capital - Payment API Health"
  type      = "api"
  subtype   = "http"
  status    = "live"
  message   = "Payment API health check failed. Verify if all pods are running and if MongoDB/Kafka are healthy."
  locations = [var.synthetics_location]

  request_definition {
    method = "GET"
    url    = var.payment_api_url
  }

  request_headers = {}

  assertion {
    type     = "statusCode"
    operator = "is"
    target   = "200"
  }

  options_list {
    tick_every           = 300
    min_failure_duration = 0
    min_location_failed  = 0
  }

  tags = ["service:payment-api", "slo:synthetics", "env:prod"]
}

resource "datadog_synthetics_test" "payment_api_card_authorization" {
  name      = "Keo Capital - Card Authorization (Payment API)"
  type      = "api"
  subtype   = "http"
  status    = "live"
  message   = "Card authorization failed or returned error."
  locations = [var.synthetics_location]

  request_definition {
    method = "POST"
    url    = var.payment_api_url
    body   = <<JSON
{"customerId":"CUST-SYNTH-001","region":"latam","product":"keo-travel","amount":100.0}
JSON
  }

  request_headers = {
    "Content-Type" = "application/json"
  }

  count = var.enable_post_synthetics ? 1 : 0

  assertion {
    type     = "statusCode"
    operator = "is"
    target   = "200"
  }

  options_list {
    tick_every           = 300
    min_failure_duration = 0
    min_location_failed  = 0
  }

  tags = ["service:payment-api", "slo:synthetics", "env:prod"]
}

resource "datadog_synthetics_test" "payment_api_card_create" {
  name      = "Keo Capital - Card Creation (Payment API)"
  type      = "api"
  subtype   = "http"
  status    = "live"
  message   = "Card creation failed or returned error."
  locations = [var.synthetics_location]

  request_definition {
    method = "POST"
    url    = var.payment_api_url
    body   = <<JSON
{"customerId":"CUST-SYNTH-002","region":"latam","product":"keo-travel"}
JSON
  }

  request_headers = {
    "Content-Type" = "application/json"
  }

  count = var.enable_post_synthetics ? 1 : 0

  assertion {
    type     = "statusCode"
    operator = "is"
    target   = "200"
  }

  options_list {
    tick_every           = 300
    min_failure_duration = 0
    min_location_failed  = 0
  }

  tags = ["service:payment-api", "slo:synthetics", "env:prod"]
}

resource "datadog_synthetics_test" "payment_api_disbursement" {
  name      = "Keo Capital - Disbursement (Payment API)"
  type      = "api"
  subtype   = "http"
  status    = "live"
  message   = "Disbursement failed or returned error."
  locations = [var.synthetics_location]

  request_definition {
    method = "POST"
    url    = var.payment_api_url
    body   = <<JSON
{"customerId":"CUST-SYNTH-003","region":"latam","product":"workeo","amount":5000.0}
JSON
  }

  request_headers = {
    "Content-Type" = "application/json"
  }

  count = var.enable_post_synthetics ? 1 : 0

  assertion {
    type     = "statusCode"
    operator = "is"
    target   = "200"
  }

  options_list {
    tick_every           = 300
    min_failure_duration = 0
    min_location_failed  = 0
  }

  tags = ["service:payment-api", "slo:synthetics", "env:prod"]
}