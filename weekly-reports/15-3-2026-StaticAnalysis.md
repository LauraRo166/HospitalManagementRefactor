# 📊 CSDT — Primera Entrega 2024
## Modelos de Calidad: Análisis con SonarCloud

> **Proyecto:** HospitalManagementRefactor
> **Organización:** Escuela Colombiana de Ingeniería CSDT
> **Fecha de análisis:** 15 de marzo de 2026
> **Rama analizada:** `master`
> **Líneas de código:** ~7.5k (JSP, Java)

---

## 👥 Equipo

| Integrante | GitHub |
|---|---|
| César Andrés Borray Suarez | [@AndresSu2342](https://github.com/AndresSu2342) |
| Juan Esteban Medina Rivas | [@JuanEstebanMedina](https://github.com/JuanEstebanMedina) |
| Laura Daniela Rodríguez Sánchez | [@LauraRo166](https://github.com/LauraRo166) |
| Maria Paula Sánchez Macías | [@hakki17](https://github.com/hakki17) |

---

## 🧰 Herramienta Utilizada: SonarCloud

### ¿Qué es SonarCloud?

[SonarCloud](https://sonarcloud.io) es la versión **100% en la nube** de SonarQube, desarrollada por SonarSource. Permite realizar análisis estático de código directamente desde repositorios de GitHub sin necesidad de instalar ningún servidor local. Para proyectos públicos y académicos, es **gratuita**.

### ¿Por qué elegimos SonarCloud?

| Criterio | Detalle |
|---|---|
| 🌐 **Online** | No requiere instalación, se conecta directamente a GitHub |
| 🆓 **Gratuito** | Para proyectos públicos como el nuestro |
| ☕ **Soporte Java** | Análisis profundo de Java, Spring MVC, JSP y Maven |
| 📊 **Dashboard visual** | Métricas claras exportables para documentación |
| 🔁 **CI/CD integrado** | Se ejecuta automáticamente en cada push |
| 🔍 **Modelo de calidad** | Implementa el modelo **SQALE** para deuda técnica |

### Modelo de Calidad que implementa

SonarCloud se basa en el modelo **SQALE (Software Quality Assessment based on Lifecycle Expectations)**, que evalúa la calidad del software en cinco dimensiones:

| Dimensión | Descripción |
|---|---|
| Reliability | Comportamiento estable bajo condiciones normales y de error |
| Security | Protección contra vulnerabilidades |
| Maintainability | Facilidad para modificar el código |
| Coverage | Porcentaje de código cubierto por tests |
| Duplications | Nivel de código duplicado |

---

## 📈 Resultados del Análisis

### 🖥️ Dashboard General (Overview)

El primer análisis fue ejecutado el **15 de marzo de 2026 a las 18:48** sobre la rama `master`, arrojando los siguientes resultados consolidados:

| Métrica | Resultado | Rating |
|---|---|---|
| 🔒 Security | 0 issues | 🟢 **A** |
| 🐛 Reliability | 338 issues | 🟠 **D** |
| 🔧 Maintainability | 408 issues | 🟢 **A** |
| 🔥 Security Hotspots | 127 hotspots | 🔴 **E** (0% revisados) |
| 📋 Coverage | Sin datos | ➖ N/A |
| 🔁 Duplications | 39.7% | ⚠️ Alto |
| 📦 Total Issues | **549** | — |
| 🚦 Quality Gate | **Not computed** | — |

> ⚠️ El Quality Gate aparece como "Not computed" porque es el primer análisis. En el siguiente scan se generará el estado automáticamente.

---

### 🔒 Seguridad — Rating A ✅

El análisis detectó **0 vulnerabilidades de seguridad activas**, lo cual es positivo. Sin embargo, se identificaron **127 Security Hotspots**, todos pendientes de revisión (100% en estado "To Review").

```
Security Rating:         A  ✅
Security Issues:         0
Security Hotspots:     127  ⚠️ (To Review: 100%)
Security Review Rating:  E  🔴
```

Los hotspots no son vulnerabilidades confirmadas, sino zonas de código que **merecen revisión manual** por parte del equipo para determinar si representan un riesgo real. Un Security Review Rating de **E** indica que ninguno ha sido revisado aún — esto es deuda de seguridad.

**Acción recomendada:** Revisar los 127 hotspots para clasificarlos como "Safe" o "To Fix". Esto mejorará inmediatamente el Security Review Rating.

---

### 🐛 Confiabilidad (Reliability) — Rating D ⚠️

Este es el **área más crítica** del proyecto con **338 issues** de confiabilidad.

```
Reliability Rating: D  🟠
Total Issues:      338
  ├── High:         91  (27%)   🔴
  ├── Medium:      246  (73%)   🟠
  └── Low:           1  (0.3%)  🟡
```

La distribución muestra que el **73% son de severidad Media** y el **27% son de severidad Alta**, lo que indica problemas reales que podrían causar comportamientos inesperados en producción.

**Ejemplos detectados en el código (Issues reales):**

```java
// ⚠️ ISSUE: Make "Login" serializable or don't store it in the session
// Archivo: LoginController.java — L62 | Bug · Major | 20 min effort
// La clase Login se almacena en sesión HTTP pero no implementa Serializable
HttpSession session = request.getSession();
session.setAttribute("userInfo", l);  // ← Login debe ser Serializable
```

```java
// ⚠️ ISSUE: Remove this field injection and use constructor injection instead
// Archivo: EditLoginDetailsController.java — L21, L24 | Major
// La inyección de campo (@Autowired en atributo) dificulta las pruebas unitarias
@Autowired
LoginDao dao;  // ← Mejor: inyección por constructor
```

**Causa raíz:** El código original del proyecto forkado tiene aproximadamente **5 años de antigüedad** y fue desarrollado sin seguir las mejores prácticas modernas de Spring MVC.

---

### 🔧 Mantenibilidad (Maintainability) — Rating A ✅ con 408 issues

Aunque el rating es **A**, existen **408 code smells** que aumentan la deuda técnica del proyecto.

```
Maintainability Rating: A  🟢
Code Smells:          408
Estimated Debt:       ~6d 3h de trabajo correctivo
```

**Issues más frecuentes detectados:**

> Imagen Resultados!!!

| Issue | Cantidad | Esfuerzo |
|---|---|---|
| Remove commented out code | Múltiples | 5 min c/u |
| Replace `@RequestMapping` con `@PostMapping`/`@GetMapping` | Múltiples | 2 min c/u |
| Define a constant instead of duplicating literal | Varios | 8 min c/u |
| Remove this field injection (usar constructor injection) | Varios | 5 min c/u |

```java
// ⚠️ ISSUE: Replace @RequestMapping with @PostMapping
// Archivo: LoginController.java — L47 | Minor | 2 min effort
@RequestMapping(value="/dashboard.html", method = RequestMethod.POST)  // ← antes
@PostMapping(value="/dashboard.html")  // ← recomendado por SonarCloud
```

```java
// ⚠️ ISSUE: Define a constant instead of duplicating "userInfo" 3 times
// Archivo: pom.xml relacionados y controllers | Critical | 8 min effort
session.getAttribute("userInfo");  // repetido 3+ veces sin constante
```

---

### 📋 Cobertura (Coverage) — Sin datos ❌

SonarCloud no pudo calcular la cobertura de código porque **el proyecto no tiene pruebas unitarias configuradas** ni un reporte de cobertura generado (JaCoCo o similar).

```
Coverage: No data available
Estado:   Requiere configuración de JaCoCo + reporte XML
```

Esto es consistente con lo documentado en nuestra bitácora de **Testing Debt**: el proyecto original carece completamente de pruebas unitarias, lo cual es una deuda técnica significativa que se está abordando en esta misma entrega.

**Para activar Coverage en SonarCloud se requiere:**
1. Agregar plugin JaCoCo al `pom.xml`
2. Ejecutar `mvn test` con generación de reporte XML
3. Configurar el path del reporte en `sonar-project.properties`

---

### 🔁 Duplicaciones — 39.7% 🔴

> Imagen Resultados!!!

El nivel de duplicación es **extremadamente alto**: casi **4 de cada 10 líneas** de código están duplicadas en alguna parte del proyecto.

```
Duplications: 39.7%  🔴  (sobre 8.9k líneas analizadas)
Umbral aceptable: < 3%
```

Este hallazgo es coherente con los **Code Smells** ya identificados en semanas anteriores (Magic Numbers, código repetido en JSPs, lógica duplicada en DAOs). Las vistas JSP repiten estructuras de navegación en cada archivo sin reutilización, y varios DAOs repiten patrones idénticos de consulta Hibernate.

---

### 📦 Issues Totales por Severidad

> Imagen Resultados!!!

---

## 🔗 Relación con Hallazgos Previos

Los resultados de SonarCloud **confirman y cuantifican** los problemas identificados en semanas anteriores:

| Semana | Hallazgo Manual | Confirmación SonarCloud |
|---|---|---|
| Sem 1 — Code Smells | Tight Coupling, Field Injection | ✅ 338 Reliability issues, field injection detectado |
| Sem 1 — Code Smells | Magic Numbers / Literales duplicados | ✅ "Define a constant" — Critical issues |
| Sem 1 — Code Smells | Poor Exception Handling | ✅ Bugs de manejo de nulos en sesión |
| Sem 2 — Clean Code | Violación DRY | ✅ 39.7% de duplicación confirmada |
| Sem 2 — Clean Code | Anotaciones Spring desactualizadas | ✅ Replace `@RequestMapping` con anotaciones específicas |
| Sem 3 — Testing Debt | Sin pruebas unitarias | ✅ Coverage: sin datos disponibles |

---

## 🧠 Análisis con IA Complementario

Para complementar el análisis estático de SonarCloud, utilizamos **Claude (Anthropic)** como asistente de IA para:

1. **Interpretar los issues detectados** y relacionarlos con patrones de deuda técnica conocidos
2. **Proponer refactorizaciones** concretas para los issues de mayor impacto
3. **Priorizar** qué issues atacar primero según severidad y esfuerzo

**Hallazgo clave con IA:** La IA identificó que los **338 issues de Reliability** son en su mayoría consecuencia de una decisión arquitectónica original: usar **field injection** en lugar de **constructor injection** en todos los controladores y DAOs. Corregir este patrón de forma sistemática reduciría simultáneamente issues de Reliability y Maintainability, y habilitaría la escritura de pruebas unitarias (ya que el constructor injection facilita el mockeo).

---

## 📐 Comparación con Estándares de Calidad

| Métrica | Valor Actual | Estándar Recomendado | Estado |
|---|---|---|---|
| Security Rating | A | A | ✅ Cumple |
| Reliability Rating | D | A o B | ❌ Crítico |
| Maintainability Rating | A | A | ✅ Cumple |
| Coverage | 0% | ≥ 80% | ❌ Sin implementar |
| Duplications | 39.7% | < 3% | ❌ Crítico |
| Security Hotspots revisados | 0% | 100% | ❌ Pendiente |

---

## 🎯 Plan de Acción Propuesto

Basado en los resultados, se propone el siguiente orden de prioridades:

```
PRIORIDAD ALTA
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
1. Corregir field injection → constructor injection
   Impacto: reduce ~100+ issues de Reliability + habilita tests
   Esfuerzo: ~1 día de trabajo

2. Implementar pruebas unitarias + JaCoCo
   Impacto: activa la métrica de Coverage en SonarCloud
   Esfuerzo: en progreso (Sem 3)

PRIORIDAD MEDIA
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
3. Revisar los 127 Security Hotspots
   Impacto: mejora Security Review Rating de E a A/B
   Esfuerzo: ~4 horas de revisión manual

4. Eliminar código comentado y literales duplicados
   Impacto: reduce issues de Maintainability
   Esfuerzo: ~3 horas

PRIORIDAD BAJA
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
5. Modernizar anotaciones Spring (@PostMapping, etc.)
   Impacto: reduce issues menores de Maintainability
   Esfuerzo: ~1 hora
```

---

## 💡 Conclusiones

1. **SonarCloud es una herramienta poderosa y accesible** para proyectos académicos. La integración con GitHub fue sencilla y los resultados del primer análisis fueron inmediatos y muy informativos.

2. **El mayor problema del proyecto es la Reliability (Rating D)**: 338 issues, 91 de severidad Alta, concentrados principalmente en patrones arquitectónicos como field injection y falta de serialización.

3. **La duplicación del 39.7% es alarmante** y refleja la ausencia de principios DRY en el diseño original. Refactorizar las vistas JSP con templates o fragments reduciría drásticamente este número.

4. **La ausencia de cobertura de pruebas** no solo es una deuda técnica en sí misma, sino que impide que SonarCloud calcule el Quality Gate correctamente, lo que significa que el equipo no tiene visibilidad completa del estado de calidad.

5. **Los hallazgos de SonarCloud son consistentes con el análisis manual** realizado en las semanas anteriores, lo que valida el enfoque del equipo y demuestra que el análisis estático automatizado y el análisis manual se complementan eficazmente.

6. **El Quality Gate "Not computed"** cambiará en el próximo análisis. Se recomienda que el equipo configure un Quality Gate personalizado que incluya umbrales de coverage y duplicación para futuras entregas.

---

## 🔗 Recursos

- 🔗 [SonarCloud — Proyecto HospitalManagementRefactor](https://sonarcloud.io)
- 🔗 [Repositorio GitHub](https://github.com/Escuela-Colombiana-de-Ingenieria-CSDT/HospitalManagementRefactor)
- 📖 [Documentación SonarCloud](https://docs.sonarcloud.io)
- 📖 [Modelo SQALE](https://www.sonarsource.com/docs/CognitiveComplexity.pdf)
- 📖 [CWE — Common Weakness Enumeration](https://cwe.mitre.org)
- 📖 [Top 40 Static Code Analysis Tools](https://www.softwaretestinghelp.com/tools/top-40-static-code-analysis-tools/)

---

*CSDT_M — Software Quality and Technical Debt | Escuela Colombiana de Ingeniería | 2026*
