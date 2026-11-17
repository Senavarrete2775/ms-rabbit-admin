# Changelog

Formato basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.1.0/) y versionado [SemVer](https://semver.org/lang/es/).

## [Unreleased]

### Cambiado
- Las colas administradas por defecto se leen de `app.rabbitmq.managed-queues` (incluye las 3 colas y sus DLQ).
- Conexión al clúster RabbitMQ con varias direcciones.

### Agregado
- Dockerfile.
