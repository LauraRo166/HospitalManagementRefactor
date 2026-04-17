# 🏥 Hospital Management System

## Technical Debt Analysis & Improvement

> Fork of [HospitalManagement](https://github.com/rid17pawar/HospitalManagement) by rid17pawar, focused on the progressive analysis, identification, and improvement of technical debt within the system.
>
> **Course:** CSDT_M — Software Quality and Technical Debt

---

## 📌 Project Description

A web-based system designed to replace traditional paper-based hospital workflows with a secure and efficient digital platform. Built with **Spring MVC** and **Hibernate** as the main frameworks, using **MySQL** as the database.

### Main features of the original system

- Patient and staff management
- Automatic PDF prescription generation
- OPD (Outpatient Department) queue management
- Role-based modules: Doctor, Receptionist, and Administrator
- Password encryption with BCrypt

---

## 🎯 Fork Objective

This fork has an academic purpose: **iterative analysis and improvement of the technical debt** present in the original project. Over 8 weeks, the team will identify code smells, propose and apply refactorings, implement tests, analyze static metrics, and improve the architecture and development process, documenting each advancement weekly.

---

## 👥 Team Members

<table align="center">
  <tr>
    <td align="center">
      <a href="https://github.com/AndresSu2342">
        <img src="https://github.com/AndresSu2342.png" width="100px;" alt="César Andrés Borray Suarez"/>
        <br />
        <sub><b>César Andrés Borray Suarez</b></sub>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/JuanEstebanMedina">
        <img src="https://github.com/JuanEstebanMedina.png" width="100px;" alt="Juan Esteban Medina Rivas"/>
        <br />
        <sub><b>Juan Esteban Medina Rivas</b></sub>
      </a>
    </td>

  </tr>
  <tr>
  <td align="center">
      <a href="https://github.com/LauraRo166">
        <img src="https://github.com/LauraRo166.png" width="100px;" alt="Laura Daniela Rodríguez Sánchez"/>
        <br />
        <sub><b>Laura Daniela Rodríguez Sánchez</b></sub>
      </a>
    </td>

  <td align="center">
      <a href="https://github.com/hakki17">
        <img src="https://github.com/hakki17.png" width="100px;" alt="Maria Paula Sánchez Macías"/>
        <br />
        <sub><b>Maria Paula Sánchez Macías</b></sub>
      </a>
    </td>
  </tr>
</table>

---

## 📚 Weekly Reports Index

| # | Topic | Report |
|:-:|-------|--------|
| 0 | 📄 Original README | [View report](./weekly-reports/11-02-2026-READMEoriginal.md) |
| 1 | 🚨 Code Smells & Refactoring Proposals | [View report](./weekly-reports/13-02-2026-CodeSmells.md) |
| 2 | 🧹 Clean Code & XP Practices | [View report](./weekly-reports/22-02-2026-CleanCode.md) |
| 3 | 🧪 Testing Debt | [View report](weekly-reports/15-03-2026-TestingDebt.md) |
| 4 | 📊 Static Analysis | [View report](./weekly-reports/15-03-2026-StaticAnalysis.md) |
| 5 | 🚀 DevEx & SPACE | *Coming soon* |
| 6 | ⚙️ Technical Debt in Processes | *Coming soon* |
| 7 | 🏛️ Technical Debt in Architecture | *Coming soon* |

---

### Index Detail

#### 0. 📄 Original README

> The original README from the forked project, preserved as a reference for the system's initial documentation.
>
> 📎 [11-02-2026-READMEoriginal.md](./weekly-reports/11-02-2026-READMEoriginal.md)

---

#### 1. 🚨 Code Smells & Refactoring Proposals

> Identification of the main code smells present in the system's source code (Tight Coupling, God Class, Poor Exception Handling, Magic Numbers, among others) and concrete refactoring proposals for each one.
>
> 📎 [13-02-2026-CodeSmells.md](./weekly-reports/13-02-2026-CodeSmells.md)

---

#### 2. 🧹 Clean Code & XP Practices

> Evaluation of all 8 Clean Code characteristics against the codebase, analysis of violated YAGNI/KISS/DRY/SOLID principles with real code examples, and recommendations of 8 XP practices to improve code quality.
>
> 📎 [22-02-2026-CleanCode.md](./weekly-reports/22-02-2026-CleanCode.md)

---

#### 3. 🧪 Testing Debt
> Comprehensive analysis of testing debt in the HMS project, identifying 5 critical testing debt practices:
> - Absence of automated test suite (0% coverage)
> - Tight coupling between controllers and DAO layer (unit testing impossible)
> - Generic exception handling masking error scenarios
> - Long parameter lists preventing effective testing
> - Lack of input validation causing security risks
>
> Includes detailed code examples, impact analysis, and implementation roadmap for unit testing.
>
> 📎 [28-02-2026-TestingDebt.md](weekly-reports/15-03-2026-TestingDebt.md)

---

#### 4. 📊 Static Analysis

> Static code analysis using tools such as SonarQube, Checkstyle, or SpotBugs. Report on quality metrics, cyclomatic complexity, and rule violations.
>
> 📎 [15-03-2026-staticAnalysis.md](./weekly-reports/15-03-2026-StaticAnalysis.md)

---

#### 5. 🚀 DevEx & SPACE

> Analysis of Developer Experience (DevEx) and the SPACE framework (Satisfaction, Performance, Activity, Communication, Efficiency) applied to the team and project.
>
> 📎 _Coming soon_

---

#### 6. ⚙️ Technical Debt in Processes

> Identification of technical debt in development processes: lack of CI/CD, absence of formal code reviews, dependency management, process documentation, etc.
>
> 📎 _Coming soon_

---

#### 7. 🏛️ Technical Debt in Architecture

> Structural analysis of the system's architecture: layer violations, absence of architectural patterns, circular dependencies, inter-module coupling, and improvement proposals.
>
> 📎 _Coming soon_

---

_[⬆ Back to top](#-hospital-management-system)_

---

**CSDT_M — Software Quality and Technical Debt | 2026**
