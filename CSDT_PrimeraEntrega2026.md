# 📋 CSDT — First Delivery 2026

## Quality Analysis: Testing Debt & Static Analysis

> **Project:** Hospital Management System Refactor  
> **Course:** CSDT_M — Software Quality and Technical Debt  
> **Period:** February - March 2026  
> **Focus:** Technical Debt in Testing and Static Code Analysis

---

## 🎯 Objective of This Delivery

Consolidation of two critical analyses on the code quality of the Hospital Management System:

1. **🧪 Testing Debt** — Identification of testing debt practices and testability analysis
2. **📊 Static Analysis** — Automated code quality evaluation using SonarCloud under the SQALE model

---

## 📚 Documents Included

### 1. 🧪 Testing Debt Analysis

**Focus:** Identification of testing debt practices, analysis of existing code testability, and recommendations for test suite implementation.

**Topics Covered:**
- Absence of automated test suite (0% coverage)
- Tight coupling between layers (controllers-DAO)
- Generic exception handling
- Long parameter lists
- Lack of input validation
- Testing implementation roadmap proposal

**📎 [View full document](./weekly-reports/15-03-2026-TestingDebt.md)**

---

### 2. 📊 Static Analysis with SonarCloud

**Focus:** Automated code quality evaluation using SonarCloud under the SQALE model (Software Quality Assessment based on Lifecycle Expectations).

**Topics Covered:**
- SonarCloud configuration on GitHub repository
- Analysis of 5 quality dimensions: Reliability, Security, Maintainability, Coverage, Duplications
- Cyclomatic complexity metrics
- Rule violations
- Estimated technical debt

**📎 [View full document](./weekly-reports/15-03-2026-StaticAnalysis.md)**

---

## 🔗 Relationship Between Analyses

Both documents complement each other:

- **Testing Debt** identifies **why** code is difficult to test
- **Static Analysis** measures **how problematic** the code is from multiple perspectives

Together, they provide a complete view of the project's current quality.

---

## 📈 Consolidated Metrics

| Metric | Value | Status |
|--------|-------|--------|
| Test Coverage | 0% | 🔴 Critical |
| Technical Debt | To calculate | ⚠️ Analysis |
| Duplications | To identify | ⚠️ Analysis |
| Reliability Rating | To evaluate | ⚠️ Analysis |

---

## 🚀 Next Phases

Based on these initial analyses, the following phases will include:

- **Phase 2:** Implementation of unit test suite
- **Phase 3:** Refactoring to reduce coupling
- **Phase 4:** Architecture improvement
- **Phase 5:** Process optimization

---

## 📖 Recommended Readings Index

Navigate between analyses:

- 🧪 [Testing Debt & Unit Testing Implementation](./weekly-reports/15-03-2026-TestingDebt.md)
- 📊 [Quality Models: SonarCloud Analysis](./weekly-reports/15-03-2026-StaticAnalysis.md)
- 📚 [Main Project README](./README.md)

---

_[⬆ Back to top](#-csdt--first-delivery-2026)_

---

**CSDT_M — Software Quality and Technical Debt | 2026**



