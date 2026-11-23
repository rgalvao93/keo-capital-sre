# Terraform Configuration for Datadog — Keo Capital

## Provider Configuration

```hcl
terraform {
  required_providers {
    datadog = {
      source  = "DataDog/datadog"
      version = "~> 3.0"
    }
  }
}

provider "datadog" {
  api_key = var.datadog_api_key
  app_key = var.datadog_app_key
  api_url = var.datadog_api_url
}

variable "datadog_api_key" {
  description = "Datadog API Key"
  type        = string
  sensitive   = true
}

variable "datadog_app_key" {
  description = "Datadog APP Key"
  type        = string
  sensitive   = true
}

variable "datadog_api_url" {
  description = "Datadog API URL"
  type        = string
  default     = "https://api.datadoghq.com/"
}
```

## Local credentials

Copy `terraform.tfvars.example` to `terraform.tfvars` and replace the
placeholder values with your Datadog API key and application key:

```sh
cp terraform.tfvars.example terraform.tfvars
```

Terraform automatically loads `terraform.tfvars`. The `.gitignore` excludes
`*.tfvars` files in this directory; never commit real credentials. If a key
has been shared in chat or another public place, revoke it and create a new one.

Slack channels are only provisioned when `slack_account_name` is configured.
Synthetic tests that send `POST` requests are disabled by default; enable them
with `enable_post_synthetics = true` only when the configured API URL is safe
for repeated test requests.

## Module Structure

```
terraform/
├── main.tf              # Root module
├── variables.tf         # Input variables
├── outputs.tf           # Output values
├── providers.tf         # Provider configuration
├── monitors/            # Monitor definitions
│   ├── business.tf
│   ├── application.tf
│   ├── infrastructure.tf
│   ├── security.tf
│   ├── external-dependencies.tf
│   └── incident-management.tf
├── dashboards/          # Dashboard definitions
│   ├── transaction-overview.tf
│   ├── infrastructure-health.tf
│   ├── slo-compliance.tf
│   ├── incident-management.tf
│   ├── workeo.tf
│   ├── keo-travel.tf
│   ├── cross-border.tf
│   └── external-dependencies.tf
├── slos/                # SLO definitions
│   └── slos.tf
└── modules/             # Reusable modules
    ├── monitor/
    │   ├── main.tf
    │   ├── variables.tf
    │   └── outputs.tf
    └── dashboard/
        ├── main.tf
        ├── variables.tf
        └── outputs.tf
```

## Reusable Monitor Module

```hcl
# modules/monitor/main.tf
resource "datadog_monitor" "this" {
  name    = var.name
  type    = var.type
  query   = var.query
  message = var.message

  thresholds = var.thresholds

  tags = var.tags

  notify_no_data    = var.notify_no_data
  no_data_timeframe = var.no_data_timeframe
  renotify_interval = var.renotify_interval
}

# modules/monitor/variables.tf
variable "name" {
  type = string
}

variable "type" {
  type = string
}

variable "query" {
  type = string
}

variable "message" {
  type = string
}

variable "thresholds" {
  type = map(number)
}

variable "tags" {
  type    = list(string)
  default = []
}

variable "notify_no_data" {
  type    = bool
  default = true
}

variable "no_data_timeframe" {
  type    = number
  default = 10
}

variable "renotify_interval" {
  type    = number
  default = 60
}
```

## Example Usage

```hcl
# monitors/business.tf
module "card_auth_success_rate" {
  source = "../modules/monitor"

  name    = "Card Authorization Success Rate - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):avg:keo.payments.authorization.success_rate{*} < 99.5"
  message = "Taxa de sucesso de autorização de cartão abaixo de 99.5%. @slack-alerts-payments @pagerduty-sre"

  thresholds = {
    critical = 99.5
    warning  = 99.0
  }

  tags = ["service:payment-api", "team:payments", "severity:critical"]
}
```

## CI/CD (GitHub Actions)

```yaml
# .github/workflows/terraform.yml
name: Terraform Datadog

on:
  push:
    branches: [main]
    paths: ['terraform/**']
  pull_request:
    branches: [main]
    paths: ['terraform/**']

jobs:
  terraform:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: hashicorp/setup-terraform@v3
      - run: terraform init
        working-directory: terraform
      - run: terraform plan
        working-directory: terraform
        env:
          TF_VAR_datadog_api_key: ${{ secrets.DATADOG_API_KEY }}
          TF_VAR_datadog_app_key: ${{ secrets.DATADOG_APP_KEY }}
      - run: terraform apply -auto-approve
        if: github.ref == 'refs/heads/main'
        working-directory: terraform
        env:
          TF_VAR_datadog_api_key: ${{ secrets.DATADOG_API_KEY }}
          TF_VAR_datadog_app_key: ${{ secrets.DATADOG_APP_KEY }}
```