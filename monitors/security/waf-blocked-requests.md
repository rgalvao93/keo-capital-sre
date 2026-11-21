# Monitor: WAF/Firewall — Bloqueios e Padrões de Ataque

## Descrição
Monitora bloqueios do WAF/firewall e padrões de ataque (SQL injection, XSS, etc.) contra o portal B2B.

## Tipo
Metric Alert

## Query
```
avg(last_5m):sum:keo.waf.blocked_requests{*}.as_rate() > 10
```

## Thresholds
- **Warning**: 5 req/s bloqueadas
- **Critical**: 10 req/s bloqueadas

## Tags
```
service:waf
team:sre
compliance:security
severity:high
```

## Notificação
- **Canal**: Slack #alerts-security
- **On-call**: PagerDuty (SEV-2)
- **Mensagem**: "WAF bloqueando requisições em alta taxa. Possível ataque em andamento. Verificar logs do firewall."

## Terraform
```hcl
resource "datadog_monitor" "waf_blocked_requests" {
  name    = "WAF Blocked Requests - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):sum:keo.waf.blocked_requests{*}.as_rate() > 10"
  message = <<-EOT
    WAF bloqueando requisições em alta taxa.
    Possível ataque em andamento. Verificar logs do firewall.
    @slack-alerts-security
    @pagerduty-sre
  EOT

  thresholds = {
    critical = 10
    warning  = 5
  }

  tags = ["service:waf", "team:sre", "compliance:security", "severity:high"]
}
```