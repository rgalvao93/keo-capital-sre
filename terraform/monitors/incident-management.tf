# Incident Management Monitors — Keo Capital

module "mttr_sev1" {
  source = "../modules/monitor"

  name    = "MTTR SEV-1 - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_1d):avg:keo.incidents.mttr{severity:sev1} > 1800"
  message = "MTTR de incidentes SEV-1 acima de 30 minutos. Revisar processo de resposta a incidentes. @slack-alerts-sre"

  thresholds = {
    critical = 1800
    warning  = 1500
  }

  tags = ["team:sre", "severity:high"]
}

module "mttd" {
  source = "../modules/monitor"

  name    = "MTTD - Mean Time to Detection - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_1d):avg:keo.incidents.mttd{*} > 300"
  message = "MTTD acima de 5 minutos. Melhorar cobertura de monitors e alertas. @slack-alerts-sre"

  thresholds = {
    critical = 300
    warning  = 240
  }

  tags = ["team:sre", "severity:high"]
}

module "alert_fatigue" {
  source = "../modules/monitor"

  name    = "Alert Fatigue - False Positive Rate - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_7d):avg:keo.alerts.false_positive_rate{*} > 0.1"
  message = "Taxa de falsos positivos acima de 10%. Revisar thresholds dos monitors. @slack-alerts-sre"

  thresholds = {
    critical = 0.1
    warning  = 0.08
  }

  tags = ["team:sre", "severity:medium"]
}