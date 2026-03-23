# 🚀 Productividad de Desarrolladores: Framework SPACE

> **Week 5 — Marzo 22, 2026** | Curso: CSDT_M — Software Quality and Technical Debt

**📌 Relacionado a:** [📊 Análisis Estático](./15-03-2026-StaticAnalysis.md) | [🧪 Testing Debt](./15-03-2026-TestingDebt.md) | [📋 Primera Entrega](../CSDT_PrimeraEntrega2026.md)

---

## 📋 Tabla de Contenidos

- [Introducción al Análisis SPACE](#-introducción-al-análisis-space)
- [Análisis Multidimensional (S.P.A.C.E.)](#-análisis-multidimensional-space)
  - [1. Satisfaction and well-being (S)](#1-satisfaction-and-well-being-s)
  - [2. Performance (P)](#2-performance-p)
  - [3. Activity (A)](#3-activity-a)
  - [4. Communication and Collaboration (C)](#4-communication-and-collaboration-c)
  - [5. Efficiency and Flow (E)](#5-efficiency-and-flow-e)
- [Oportunidades de Mejora Generales](#-oportunidades-de-mejora-generales)
- [Conclusiones](#-conclusiones)

---

## 🚀 Introducción al Análisis SPACE

En este documento se evalúa la productividad y la experiencia de los desarrolladores (DevEx) trabajando en el refactor del sistema **Hospital Management System (HMS)** utilizando el **Framework SPACE**.

Dado el contexto académico del proyecto (CSDT_M), donde un equipo de 4 personas (César, Juan, Laura, Maria Paula) se ha enfrentado a un código legado con alta deuda técnica, el análisis se enfoca en las métricas de percepción, comportamientos de sistema/proceso y cómo el estado del proyecto ha afectado cada dimensión, tanto de forma positiva como negativa.

---

## 🔍 Análisis Multidimensional (S.P.A.C.E.)

### 1. Satisfaction and well-being (S)
*Cómo se sienten los desarrolladores en su trabajo, con su equipo, uso de herramientas y cultura.*

**🟢 Puntos Positivos**
- **Satisfacción por Evolución:** El equipo ha logrado mejoras tangibles en un código que inicialmente tenía 0% de cobertura y múltiples *code smells*. Subir la cobertura o implementar SonarCloud aporta un sentido de eficacia y logro.
- **Herramientas Modernas:** La integración de herramientas de análisis estático (SonarCloud) y frameworks modernos de testing (JUnit 5, Mockito) mejora la experiencia de desarrollo al brindar *feedback* automatizado, reduciendo la frustración de buscar errores manualmente.

**🔴 Puntos Negativos (Desafíos)**
- **Alta Carga Cognitiva:** El proyecto original contaba con "God Classes", acoplamiento fuerte y métodos con hasta 18 parámetros. Leer, entender y modificar ese código genera un esfuerzo mental enorme para los desarrolladores.
- **Riesgo de *Burnout* por Deuda:** Identificar y priorizar una deuda técnica tan extensa y compleja puede resultar abrumador para el equipo e impactar momentáneamente el bienestar general (eNPS).

**📊 Métricas Identificables**
- **Frecuencia del eNPS:** Medir la satisfacción del equipo iteración a iteración.
- **Percepción de Complejidad:** Encuesta simple sobre "¿Qué tan difícil fue implementar X funcionalidad esta semana por culpa del código heredado?".

---

### 2. Performance (P)
*Valor generado hacia el cliente final / Outcomes.*

**🟢 Puntos Positivos**
- **Reducción Consciente de Deuda Técnica:** El equipo no solo añade valor funcional, sino estructural. El análisis profundo de deuda arquitectónica, de pruebas y de limpieza (Clean Code) se traduce en un proyecto más robusto a largo plazo.
- **Identificación Precisa de Fallos:** El reporte de Static Analysis ha permitido identificar directamente vulnerabilidades de seguridad y problemas de fiabilidad (Bugs, Code Smells, Duplications).

**🔴 Puntos Negativos (Desafíos)**
- ***Lead Time* Afectado:** El tiempo necesario para lanzar cambios se ha visto obstaculizado en etapas tempranas porque la refactorización profunda (como desacoplar controladores y DAO) consume tiempo que inicialmente no aporta una funcionalidad nueva visible para el "usuario".

**📊 Métricas Identificables**
- **Reducción de Deuda Técnica (SonarCloud):** Medir la disminución de días/horas de deuda técnica estimada.
- **Densidad de Code Smells:** Cantidad de smells por cada 1K líneas de código antes y después del refactor.

---

### 3. Activity (A)
*Acciones o resultados finalizados por los desarrolladores.*

**🟢 Puntos Positivos**
- **Alto Volumen de Actividad de Refactor:** Se ha evidenciado la generación de pruebas unitarias para DAOs y Entities, configuraciones en el `pom.xml`, y reportes continuos detallados cada semana.
- **Implementación de Pipelines (Pronto):** Al avanzar hacia CI/CD y análisis automatizados se garantiza la ejecución continua de pipelines, incrementando la actividad medible (commits útiles, builds automatizados).

**🔴 Puntos Negativos (Desafíos)**
- **Trabajo Manual Repetitivo Inicial:** La falta de pruebas unitarias al inicio del proyecto implicaba que la actividad se concentraba en "Testing manual" (horas perdidas como se reporta en el Testing Debt) y no en codificación/refactor real de valor.

**📊 Métricas Identificables**
- **Número de Pruebas Unitarias Creadas:** Incremental iteración a iteración.
- **Issues Resueltos en SonarCloud:** Conteo semanal de correcciones de fiabilidad.
- **Número de commits o PRs *mergeados*.**

---

### 4. Communication and Collaboration (C)
*Cómo los equipos se comunican y trabajan colaborativamente de manera fluida.*

**🟢 Puntos Positivos**
- **Alta Calidad en Documentación (Discoverability):** Los reportes semanales (Code Smells, Clean Code, Testing Debt) son extremadamente detallados y funcionan como un excelente medio asíncrono. Cualquier miembro del equipo (o miembro nuevo) tiene contexto inmediato gracias al índice consolidado del README.
- **Consenso en Mejoras:** El equipo trabajó en la entrega unificada y cruzó hallazgos de Testing con Static Analysis.

**🔴 Puntos Negativos (Desafíos)**
- **Falta Evidenciada de Code Reviews Formales:** Se identificó en el diagnóstico de "Procesos" que no había un flujo formal de revisión de código. Esto puede crear cuellos de botella e islas de conocimiento.

**📊 Métricas Identificables**
- **Calidad de Documentación (Percepción):** Facilidad de encontrar la respuesta a un problema viendo el README.
- **Tiempo de Resolución de PRs (PR Merge Time):** (Una vez se establezca un flujo de *Pull Requests*/Code Reviews).

---

### 5. Efficiency and Flow (E)
*Capacidad de finalizar el trabajo con mínimas interrupciones o retrasos.*

**🟢 Puntos Positivos**
- **Hacia el "Flow State":** Al introducir JUnit 5 y aislar dependencias con Mockito, los desarrolladores ya no requieren una base de datos montada ni largos procesos para probar. El *"time to local validation"* bajó de minutos (manuales) a segundos (<5ms automático).

**🔴 Puntos Negativos (Desafíos)**
- **Reprocesos por Acoplamiento:** Para lograr hacer una simple prueba, el desarrollador se encontró con múltiples bloqueos (Ej. no poder inyectar mocks por campos privados acoplados al DAO). Estos obstáculos rompen el *Flow* y exigen retrabajo constante solo para entender cómo funciona la clase antes de intervenirla.
- **Esperas Ocultas:** El enmascaramiento de errores por `catch(Exception e)` dificultó el *debugging* rápido, generando desperdicio de tiempo.

**📊 Métricas Identificables**
- **Tiempo en Validar un Cambio Localmente (Feedback Loop):** Antes horas de testing manual, ahora segundos.
- **Frecuencia de Retrabajo:** Veces que una clase debe ser modificada estructuralmente **solo** para poder hacerla testeable de cara a una prueba.

---

## 📈 Oportunidades de Mejora Generales

Basado en el framework SPACE y la evidencia técnica acumulada:

1. **Implementar CI y *Branching Strategy*:** Para mejorar **Activity** y **Efficiency**, configurar GitHub Actions para correr SonarCloud y Surefire unit tests con cada *Push/PR*.
2. **Establecer Políticas de Code Review (Checklists):** Promover la **Communication and Collaboration**. Cada Pull Request debe requerir la aprobación de al menos otro integrante y debe incluir pruebas asociadas.
3. **Tracking del "Developer Flow":** Fomentar bloques ininterrumpidos de refactor (sin distracciones). Medir cómo el desacoplamiento está haciendo progresivamente más rápida la inclusión de nuevas pruebas y reduciendo el *lead time*.
4. **Encuestas Semanales ("Check-Ins"):** Medir activamente la **Satisfaction** con preguntas cortas como: *"¿Sientes que el refactor del Login de esta semana mejoró la flexibilidad del sistema?"*.

---

## 🎯 Conclusiones

El uso del marco SPACE no busca medir simplemente cuántas líneas de código genera el equipo (una métrica engañosa). Busca entender que **el impacto técnico de la deuda no es solo en el producto, sino en la salud y el ritmo de trabajo de los desarrolladores.** 

Al ir pagando el Testing Debt y solventando hallazgos de Static Analysis, el equipo se mueve progresivamente desde un estado inicial de *alta fricción, retrabajo manual y frustración*, hacia un flujo de desarrollo *automatizado, validado, documentado y satisfactorio*.
