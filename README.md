# 💰 EconoMe | Financial Systems

[![Build Status](https://img.shields.io/badge/Azure_DevOps-Build_Passing-success?logo=azure-pipelines)](https://econome.azurewebsites.net/EconoMe)
[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)](https://www.oracle.com/java/)
[![Coverage](https://img.shields.io/badge/Coverage-JaCoCo-blueviolet?logo=testing-library)](https://econome.azurewebsites.net/EconoMe)
[![Deployment](https://img.shields.io/badge/Cloud-Azure_App_Service-blue?logo=microsoft-azure)](https://econome.azurewebsites.net/EconoMe)

**EconoMe** es una plataforma de gestión financiera personal robusta diseñada para el control transaccional de ingresos, egresos, deudas y préstamos. Más allá de la funcionalidad de usuario, este repositorio representa un ciclo de vida de desarrollo de software (SDLC) moderno, automatizado y gestionado bajo metodologías ágiles de grado industrial.

---

## 🛠 Tech Stack & Arquitectura

| Capa | Tecnología |
| :--- | :--- |
| **Lenguaje** | Java 21 (JDK 1.21) |
| **Framework Web** | Jakarta EE / Servlets |
| **Persistencia** | Hibernate (ORM) / JPA |
| **Base de Datos** | PostgreSQL (Supabase) |
| **Infraestructura** | Microsoft Azure App Service |

---

## 🖼️ Visual Overview

<p align="center">
  <img width="1711" height="1282" alt="Screenshot 2026-05-01 190721" src="https://github.com/user-attachments/assets/d364d835-6f3e-4bed-81c5-e16bbac49fea" /> 
  <br>
  <em>Figura 1: Interfaz principal de la aplicación enfocada en la usabilidad financiera.</em>
</p>

<p align="center">
  <img width="1555" height="854" alt="Screenshot 2026-05-01 191636" src="https://github.com/user-attachments/assets/12b8e75c-643d-457a-8f58-6207fe094d6f" />
  <br>
  <em>Figura 1: Manejo de cuentas.</em>
</p>

---

## 🚀 DevOps: CI/CD Pipeline & Quality Gates

El núcleo del proyecto es un pipeline de **Azure DevOps** que garantiza la integridad del código mediante validaciones automáticas y pruebas de cobertura.

<p align="center">
  <img width="2219" height="353" alt="image" src="https://github.com/user-attachments/assets/ba54ed5b-1105-453f-a106-b2aaa69fd34d" />
  <br>
  <em>Figura 2: Ejecución exitosa de integración continua en Azure Pipelines.</em>
</p>

### Flujo de Automatización e Ingeniería de Calidad
Nuestro archivo `azure-pipelines.yml` orquesta un proceso riguroso:

1. **Build & Unit Testing:** Compilación mediante Maven 4 y ejecución de pruebas unitarias.
2. **Code Coverage (JaCoCo):** Análisis de cobertura. El pipeline publica reportes detallados para asegurar el respaldo de pruebas en cada funcionalidad.
3. **Artifact Staging:** Empaquetado automático en formato `.war` y preparación del artefacto `drop`.
4. **Continuous Deployment:** Despliegue automatizado hacia **Azure App Service** tras la validación de calidad.

---

## 📈 Gestión Ágil (Agile Management)

Utilizamos **Scrum** gestionado a través de **Azure Boards**. El desarrollo se estructuró en Sprints con trazabilidad total desde el requerimiento hasta el cierre.

<p align="center">
  <img width="2222" height="766" alt="image" src="https://github.com/user-attachments/assets/b88dd4bd-448a-478f-8639-841f0e9fdbfc" />
  <br>
  <em>Figura 3: Sprint Board con trazabilidad de Historias de Usuario y tareas técnicas.</em>
</p>

* **Sprints Documentados:** Planificación detallada de cada iteración (ej. Sprint 1: Cuentas y Movimientos).
* **Gestión de Tareas:** Flujos de trabajo profesionales incluyendo "Definition of Ready" (DoR) y "Definition of Done" (DoD).

---

## 📦 Módulos y Características del Sistema

La aplicación está estructurada en módulos funcionales diseñados para cubrir todas las necesidades financieras:

* **📊 Dashboard:** Vista general y métricas clave del estado financiero actual.
* **🏦 Cuentas:** Administración y agrupación de diferentes fuentes de dinero.
* **💸 Movimientos:** Registro transaccional detallado de ingresos y gastos.
* **📜 Obligaciones Financieras:** Seguimiento de compromisos de pago, deudas y préstamos.
* **⏰ Recordatorios:** Sistema de alertas para pagos recurrentes (ej. suscripciones, servicios).
* **🛒 Listas de Compra:** Planificación de gastos próximos para evitar compras impulsivas.
* **📉 Resumen Financiero:** Análisis consolidado y reportes para la toma de decisiones.

## 👥 Engineering Team

<p align="center">
  <img width="2238" height="669" alt="image" src="https://github.com/user-attachments/assets/d87e23fe-1aa8-4d92-b99c-2f09cb1e0307" />
  <br>
  <em>Figura 4: Documentación oficial del proyecto y equipo responsable.</em>
</p>

---
<p align="center">
  <em>Este proyecto fue desarrollado bajo estándares de Ingeniería de Software, priorizando la automatización y la calidad continua.</em>
</p>
