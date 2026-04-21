# 🚀 Developer Productivity: SPACE Framework

> **Week 5 — March 22, 2026** | Course: CSDT_M — Software Quality and Technical Debt

**📌 Related Analysis:** [📊 Static Analysis](./15-03-2026-StaticAnalysis.md) | [🧪 Testing Debt](./15-03-2026-TestingDebt.md) | [📋 First Delivery 2026](../CSDT_PrimeraEntrega2026.md)

---

## 📋 Table of Contents

- [Introduction to SPACE Analysis](#-introduction-to-space-analysis)
- [Multidimensional Analysis (S.P.A.C.E.)](#-multidimensional-analysis-space)
  - [1. Satisfaction and well-being (S)](#1-satisfaction-and-well-being-s)
  - [2. Performance (P)](#2-performance-p)
  - [3. Activity (A)](#3-activity-a)
  - [4. Communication and Collaboration (C)](#4-communication-and-collaboration-c)
  - [5. Efficiency and Flow (E)](#5-efficiency-and-flow-e)
- [General Improvement Opportunities](#-general-improvement-opportunities)
- [Conclusions](#-conclusions)

---

## 🚀 Introduction to SPACE Analysis

This document evaluates developer productivity and Developer Experience (DevEx) while working on the refactoring of the **Hospital Management System (HMS)** using the **SPACE Framework**.

Given the academic context of the project (CSDT_M), where a team of 4 people (César, Juan, Laura, Maria Paula) has faced legacy code with high technical debt, the analysis focuses on perception metrics, system/process behaviors, and how the project's state has affected each dimension, both positively and negatively.

---

## 🔍 Multidimensional Analysis (S.P.A.C.E.)

### 1. Satisfaction and well-being (S)
*How developers feel about their work, their team, tool usage, and culture.*

**🟢 Positive Observations**
- **Satisfaction Through Evolution:** The team has achieved tangible improvements in code that initially had 0% coverage and multiple *code smells*. Increasing coverage or implementing SonarCloud provides a sense of efficacy and accomplishment.
- **Modern Tools:** Integration of static analysis tools (SonarCloud) and modern testing frameworks (JUnit 5, Mockito) improves the development experience by providing *automated feedback*, reducing frustration from manual error hunting.

**🔴 Negative Observations (Challenges)**
- **High Cognitive Load:** The original project contained "God Classes", tight coupling, and methods with up to 18 parameters. Reading, understanding, and modifying this code generates enormous mental effort for developers.
- **Burnout Risk from Debt:** Identifying and prioritizing such extensive and complex technical debt can feel overwhelming to the team and temporarily impact overall well-being (eNPS).

**📊 Measurable Metrics**
- **eNPS Frequency:** Measure team satisfaction iteration by iteration.
- **Complexity Perception:** Simple survey on *"How difficult was it to implement feature X this week due to legacy code?"*

---

### 2. Performance (P)
*Value generated toward the end customer / Outcomes.*

**🟢 Positive Observations**
- **Conscious Technical Debt Reduction:** The team not only adds functional value but also structural value. Deep analysis of architectural debt, testing debt, and code cleanliness (Clean Code) translates into a more robust project long-term.
- **Precise Failure Identification:** The Static Analysis report has enabled direct identification of security vulnerabilities and reliability issues (Bugs, Code Smells, Duplications).

**🔴 Negative Observations (Challenges)**
- **Affected Lead Time:** Time needed to ship changes has been hindered in early stages because deep refactoring (such as decoupling controllers and DAO) consumes time that initially doesn't provide visible new functionality to the "user".

**📊 Measurable Metrics**
- **Technical Debt Reduction (SonarCloud):** Measure the decrease in estimated technical debt days/hours.
- **Code Smell Density:** Number of smells per 1K lines of code before and after refactoring.

---

### 3. Activity (A)
*Actions or completed results by developers.*

**🟢 Positive Observations**
- **High Volume of Refactoring Activity:** Generation of unit tests for DAOs and Entities, configurations in `pom.xml`, and detailed continuous reports each week has been evidenced.
- **Pipeline Implementation (Coming Soon):** As progress is made toward CI/CD and automated analysis, continuous pipeline execution is guaranteed, increasing measurable activity (useful commits, automated builds).

**🔴 Negative Observations (Challenges)**
- **Repetitive Manual Work Initially:** Lack of unit tests at project start meant activity concentrated on "manual testing" (lost hours as reported in Testing Debt) rather than real value-adding code/refactoring.

**📊 Measurable Metrics**
- **Number of Unit Tests Created:** Incremental iteration by iteration.
- **Issues Resolved in SonarCloud:** Weekly count of reliability fixes.
- **Number of merged commits or PRs.*

---

### 4. Communication and Collaboration (C)
*How teams communicate and work collaboratively in a fluid manner.*

**🟢 Positive Observations**
- **High Quality Documentation (Discoverability):** Weekly reports (Code Smells, Clean Code, Testing Debt) are extremely detailed and function as an excellent asynchronous medium. Any team member (or new member) has immediate context thanks to the consolidated README index.
- **Consensus on Improvements:** The team worked on unified delivery and cross-referenced Testing findings with Static Analysis.

**🔴 Negative Observations (Challenges)**
- **Evident Lack of Formal Code Reviews:** It was identified in the "Processes" diagnosis that there was no formal code review flow. This can create bottlenecks and knowledge silos.

**📊 Measurable Metrics**
- **Documentation Quality (Perception):** Ease of finding answers to problems by viewing the README.
- **PR Resolution Time (PR Merge Time):** (Once a Pull Requests/Code Reviews workflow is established).

---

### 5. Efficiency and Flow (E)
*Ability to complete work with minimal interruptions or delays.*

**🟢 Positive Observations**
- **Moving Toward "Flow State":** By introducing JUnit 5 and isolating dependencies with Mockito, developers no longer require a mounted database or lengthy processes to test. *"Time to local validation"* dropped from minutes (manual) to seconds (<5ms automated).

**🔴 Negative Observations (Challenges)**
- **Rework Due to Coupling:** To accomplish a simple test, the developer encountered multiple blockers (e.g., inability to inject mocks due to private fields tightly coupled to DAO). These obstacles break the *Flow* and demand constant rework just to understand how a class works before intervening.
- **Hidden Waits:** Error masking through `catch(Exception e)` hindered quick *debugging*, creating time waste.

**📊 Measurable Metrics**
- **Time to Validate a Change Locally (Feedback Loop):** Before hours of manual testing, now seconds.
- **Rework Frequency:** Times a class must be structurally modified **just** to make it testable for a test.

---

## 📈 General Improvement Opportunities

Based on the SPACE framework and accumulated technical evidence:

1. **Implement CI and *Branching Strategy*:** To improve **Activity** and **Efficiency**, configure GitHub Actions to run SonarCloud and Surefire unit tests on each *Push/PR*.
2. **Establish Code Review Policies (Checklists):** Promote **Communication and Collaboration**. Each Pull Request should require approval from at least one other team member and must include associated tests.
3. **Developer Flow Tracking:** Foster uninterrupted refactoring blocks (without distractions). Measure how decoupling is progressively making test inclusion faster and reducing *lead time*.
4. **Weekly Surveys ("Check-Ins"):** Actively measure **Satisfaction** with short questions like: *"Do you feel this week's Login refactoring improved the system's flexibility?"*

---

## 🎯 Conclusions

Using the SPACE framework doesn't simply aim to measure how many lines of code the team generates (a misleading metric). It seeks to understand that **the technical impact of debt is not only on the product, but on the health and work rhythm of developers.**

As the team pays down Testing Debt and addresses Static Analysis findings, it progressively moves from an initial state of *high friction, manual rework, and frustration*, toward a development flow that is *automated, validated, documented, and satisfactory*.
