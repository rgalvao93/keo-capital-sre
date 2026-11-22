.PHONY: help up down logs build test verify clean perf chaos-apply chaos-reset chaos-local tf-init tf-fmt tf-validate tf-plan tf-apply

JAVA_HOME ?= /usr/lib/jvm/java-21-openjdk-amd64
MVN = JAVA_HOME=$(JAVA_HOME) mvn -B

help: ## Show available targets
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "  %-18s %s\n", $$1, $$2}'

up: ## Start local stack with docker-compose
	docker compose up -d

down: ## Stop local stack
	docker compose down

logs: ## Follow docker-compose logs
	docker compose logs -f

build: ## Compile all modules
	$(MVN) clean compile

test: ## Run unit tests
	$(MVN) test

verify: ## Full build (compile + tests + package + JaCoCo check)
	$(MVN) clean verify

clean: ## Remove build artifacts
	$(MVN) clean

perf: ## Run k6 performance tests (requires k6 and stack up)
	k6 run tests/performance/authorization.js

chaos-apply: ## Apply chaos experiment (Kubernetes / Chaos Mesh)
	kubectl apply -f chaos/experiments/$(EXPERIMENT).yaml

chaos-reset: ## Delete chaos experiment (rollback)
	kubectl delete -f chaos/experiments/$(EXPERIMENT).yaml

chaos-local: ## Run local Docker chaos experiment (ex.: make chaos-local CONTAINER=keo-payment-api)
	./chaos/scripts/local-pod-kill.sh $(CONTAINER)

tf-init: ## Initialize Terraform
	cd terraform && terraform init -backend=false -input=false

tf-fmt: ## Format Terraform files
	cd terraform && terraform fmt -recursive

tf-validate: ## Validate Terraform configuration
	cd terraform && terraform validate

tf-plan: ## Terraform plan
	cd terraform && terraform plan

tf-apply: ## Terraform apply
	cd terraform && terraform apply -auto-approve
