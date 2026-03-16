# 📊 CSDT — First Delivery 2026

## Quality Models: SonarCloud Analysis

> **Project:** HospitalManagementRefactor
> **Organization:** Escuela Colombiana de Ingeniería CSDT
> **Analysis Date:** March 15, 2026
> **Branch Analyzed:** `master`
> **Lines of Code:** ~7.5k (JSP, Java)

**📌 Related Analysis:** [🧪 Testing Debt & Unit Testing Implementation](./15-03-2026-TestingDebt.md) | [📋 First Delivery 2026](../CSDT_PrimeraEntrega2026.md)

---

## 🧰 Tool Used: SonarCloud

### What is SonarCloud?

[SonarCloud](https://sonarcloud.io) is the **100% online** version of SonarQube. It performs static code analysis directly from GitHub repositories without needing to install any local server.

### Why Did We Choose SonarCloud?

| Criterion                 | Detail                                                   |
| ------------------------ | --------------------------------------------------------- |
| 🌐 **Online**            | No installation required, connects directly to GitHub |
| 🆓 **Free**              | For public projects like ours                   |
| ☕ **Java Support**      | Deep analysis of Java, Spring MVC, JSP and Maven        |
| 📊 **Visual Dashboard**  | Clear metrics exportable for documentation            |
| 🔁 **Integrated CI/CD**   | Runs automatically on each push                   |
| 🔍 **Quality Model** | Implements the **SQALE** model for technical debt         |

### Quality Model Implemented

SonarCloud is based on the **SQALE model (Software Quality Assessment based on Lifecycle Expectations)**, which evaluates software quality across five dimensions:

| Dimension | Description |
|---|---|
| Reliability | Stable behavior under normal and error conditions |
| Security | Protection against vulnerabilities |
| Maintainability | Ease of modifying the code |
| Coverage | Percentage of code covered by tests |
| Duplications | Level of duplicated code |

---

## 📈 Analysis Results

> First analysis executed on **March 15, 2026 at 18:48** on the `master` branch — 7.5k lines of code (JSP + Java).

---

### Project Summary

![Summary SonarCloud](imgs/static-analysis/sonarcloud-summary.png)

> **📋 Description:** Main view of the project in SonarCloud after the first analysis. The general status of the three main dimensions is observed: **Security (A)**, **Reliability (D)** and **Maintainability (A)**. The Quality Gate appears as `Not computed` since it's the first scan. Also highlighted are 127 unreviewed Security Hotspots, a duplication of **39.7%** over 8.9k lines, and coverage not yet configured.

---

### Project Health Dashboard (Overview)

![Overview Dashboard SonarCloud](imgs/static-analysis/sonarcloud-overiew-general.png)
>
> **📋 Description:** _Project Health Dashboard_ view with consolidated global metrics: **549 open issues** in total, **39.7%** duplication with no changes in the last 30 days, coverage with no data available and Quality Gate not calculated. This panel is the entry point to evaluate the overall health of the repository at a glance.

---

### Security Snapshot & Security Hotspots

![Security Snapshot SonarCloud](imgs/static-analysis/sonarcloud-overview-security.png)
>
> **📋 Description:** Security panel divided into two sections. The first shows the **Security Rating A** with 0 confirmed active vulnerabilities. The second shows the **Security Hotspot Snapshot** with 127 identified hotspots, all in "To Review" status (100%), resulting in a **Security Review Rating E** — the worst possible rating for this metric.

---

### Reliability & Maintainability Snapshot

![Reliability y Maintainability SonarCloud](imgs/static-analysis/sonarcloud-overview-reliability-maintanibility.png)
>
> **📋 Description:** Snapshot of the two most representative dimensions of the analysis. **Reliability** obtains Rating **D** with 338 issues distributed in 27% High and 73% Medium severity. **Maintainability** obtains Rating **A** despite having 408 issues, with only one data point available on March 15 — first analysis recorded.

---

### Project Summary Card

![Tarjeta resumen SonarCloud](imgs/static-analysis/sonarcloud-general-analysis.png)
>
> **📋 Description:** Compact card of the `HospitalManagementRefactor` project in the listing of the _Escuela Colombiana de Ingeniería CSDT_ organization. Shows all consolidated ratings: Security **A**, Reliability **D**, Maintainability **A**, Hotspots Reviewed **E (0.0%)** and Duplications **39.7%**. Date of last analysis: 15/03/2026 at 18:48.

---

### Detailed Issues View

![Issues detallados SonarCloud](imgs/static-analysis/sonarcloud-issues.png)
>
> **📋 Description:** Issues panel with active filters by Software Quality and Severity. **549 issues** are listed with an estimated effort of **6 days and 3 hours**. Visible issues: _"Remove this commented out code"_ (pom.xml L15), _"Remove this field injection and use constructor injection instead"_ (EditLoginDetailsController.java L21, L24), _"Replace @RequestMapping with @PostMapping"_ (L47), _"Define a constant instead of duplicating literal 'userInfo'"_ (L53) and _"Make Login serializable or don't store it in the session"_ (L62). Distribution: **114 High**, **330 Medium**, **106 Low**, no Blockers.

---

## 🔗 Relationship with Previous Findings

SonarCloud results **confirm and quantify** problems identified in previous weeks:

| Week               | Manual Finding                      | SonarCloud Confirmation                                  |
| -------------------- | ------------------------------------ | -------------------------------------------------------- |
| Week 1 - Code Smells  | Tight Coupling, Field Injection      | ✅ 338 Reliability issues, field injection detected     |
| Week 1 - Code Smells  | Magic Numbers / Duplicated literals | ✅ "Define a constant" - Critical issues                 |
| Week 1 - Code Smells  | Poor Exception Handling              | ✅ Bugs in null handling in session                     |
| Week 2 - Clean Code   | DRY Violation                        | ✅ 39.7% duplication confirmed                       |
| Week 2 - Clean Code   | Outdated Spring annotations   | ✅ Replace `@RequestMapping` with specific annotations |
| Week 3 - Testing Debt | No unit tests                | ✅ Coverage: no data available                       |

---

## 🧠 Complementary AI Analysis

To complement the static analysis of SonarCloud, we used **Claude (Anthropic)** as an AI assistant to:

1. **Interpret detected issues** and relate them to known technical debt patterns
2. **Propose concrete refactorings** for the highest impact issues
3. **Prioritize** which issues to address first based on severity and effort

**Key Finding with AI:** The AI identified that the **338 Reliability issues** are mostly a consequence of an original architectural decision: using **field injection** instead of **constructor injection** in all controllers and DAOs. Correcting this pattern systematically would simultaneously reduce Reliability and Maintainability issues, and would enable writing unit tests (since constructor injection facilitates mocking).

---

## 💡 Conclusions

1. **SonarCloud is a powerful and accessible tool** for academic projects. Integration with GitHub was straightforward and the results of the first analysis were immediate and very informative.

2. **The biggest problem with the project is Reliability (Rating D)**: 338 issues, 91 of High severity, concentrated mainly in architectural patterns such as field injection and lack of serialization.

3. **The 39.7% duplication is alarming** and reflects the absence of DRY principles in the original design. Refactoring JSP views with templates or fragments would drastically reduce this number.

4. **The absence of test coverage** is not only a technical debt in itself, but prevents SonarCloud from calculating the Quality Gate correctly, which means the team does not have complete visibility of the quality status.

5. **SonarCloud findings are consistent with manual analysis** conducted in previous weeks, which validates the team's approach and demonstrates that automated static analysis and manual analysis complement each other effectively.

6. **The Quality Gate "Not computed"** will change in the next analysis. It is recommended that the team configure a custom Quality Gate that includes coverage and duplication thresholds for future deliveries.

---

## 🔗 Resources

- 🔗 [SonarCloud — HospitalManagementRefactor Project](https://sonarcloud.io)
- 🔗 [GitHub Repository](https://github.com/Escuela-Colombiana-de-Ingenieria-CSDT/HospitalManagementRefactor)
- 📖 [SonarCloud Documentation](https://docs.sonarcloud.io)
- 📖 [SQALE Model](https://www.sonarsource.com/docs/CognitiveComplexity.pdf)
- 📖 [CWE — Common Weakness Enumeration](https://cwe.mitre.org)
- 📖 [Top 40 Static Code Analysis Tools](https://www.softwaretestinghelp.com/tools/top-40-static-code-analysis-tools/)

---

## 🔗 Related

- **Previous:** [🧪 Testing Debt & Unit Testing Implementation](./15-03-2026-TestingDebt.md)
- **Summary:** [📋 First Delivery 2026](../CSDT_PrimeraEntrega2026.md)
- **Home:** [📚 Main Project README](../README.md)

---

_[⬆ Back to top](#-csdt--first-delivery-2026)_

**CSDT_M — Software Quality and Technical Debt | Escuela Colombiana de Ingeniería | 2026**

