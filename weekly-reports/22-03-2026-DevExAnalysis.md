# DevEx Analysis

> This analysis applies the **Developer Experience (DevEx)** framework to the HMS project. The framework examines three core dimensions:

1. **Feedback Loops**
2. **Cognitive Load**
3. **Flow State**

> to identify friction points and facilitators in the day-to-day development experience. The analysis is grounded in the four weekly reports completed so far: Code Smells, Clean Code, Testing Debt, and Static Analysis with SonarCloud.

---

## Dimension 1: Feedback Loops

> Feedback loops are the mechanisms that inform the developer about the quality of their work.

### Negative Points

> **Feedback loop still partially broken at the controller layer.**\
> Coverage is still limited to entities and DAOs in the patient/reception module. The 15+ controllers, the highest-risk layer, remain untested. No test covers `LoginController`, `AddEmployeeController`, or any other controller.

> **No CI/CD pipeline configured.**\
> The SonarCloud analysis is not configurated to be automatic on each commit, it was executed manually on March 15 over the `master` branch. The Quality Gate shows "Not computed", meaning the team has no automatic visibility of quality status on each push.

### Positive Points

> **SonarCloud is the most significant advancement for feedback loops.**\
> For the first time the project has real quantitative feedback: 549 issues, 338 Reliability issues (Rating D), 39.7% code duplication, 127 unreviewed security hotspots. Qualitative observations from previous weeks now have measurable numbers.

> **64 tests passing with `BUILD SUCCESS` in 10.9 seconds.**\
> This is the first real automated feedback cycle in the project. Previously, any change required manually starting the application and navigating the UI.

> **Testing debt documented with concrete cost metrics.**\
> The week 3 report estimates that manual testing consumes 50–70% of development time and wastes 3–4 hours per sprint, giving the team a solid basis for prioritization.

### Identifiable Metrics

| Metric                              | Current State        | Target              |
| ----------------------------------- | -------------------- | ------------------- |
| Automated tests                     | 64 tests, 0 failures | Controllers covered |
| Test suite execution time           | 10.9 seconds         | < 30 seconds        |
| Total issues (SonarCloud)           | 549                  | < 100               |
| Reliability Rating                  | D (338 issues)       | A or B              |
| Code duplication                    | 39.7%                | < 5%                |
| Security hotspots reviewed          | 0%                   | > 80%               |
| Coverage integrated with SonarCloud | Not configured       | >= 70%              |

### Improvement Opportunities

> Configure `jacoco-maven-plugin` and integrate it with SonarCloud so existing tests appear in the Quality Gate dashboard.
> Implement GitHub Actions to run `mvn test` and the SonarCloud analysis automatically on every push.
> Extend tests to the controller layer - the week 3 report already documents exactly why this requires prior refactoring of field injection into constructor injection.

---

## Dimension 2: Cognitive Load

> Cognitive load measures the mental effort required to understand, navigate, and modify the codebase.

### Negative Points

> **Field injection confirmed as the root cause of 338 Reliability issues.**\
> SonarCloud detected this pattern across all controllers and DAOs. The complementary AI analysis noted in the Static Analysis report identifies that fixing this pattern systematically would simultaneously reduce Reliability and Maintainability issues, and would unblock unit testing of controllers.

> **39.7% code duplication now confirmed.**\
> What was previously a qualitative observation ("DRY violations") now has a concrete number. Nearly 4 out of every 10 lines are duplicated, concentrated especially in JSP views.

> **Duplicated literals as active cognitive debt.**\
> SonarCloud raised issues like "Define a constant instead of duplicating literal 'userInfo'" across multiple files. Developers must remember the exact form of each literal instead of referencing a central constant.

> **549 open issues create noise without a clear action signal.**\
> Without a configured Quality Gate or prioritization policy, the developer looking at the SonarCloud dashboard has no clear indication of what to fix first.

### Positive Points

> **64 tests serve as executable documentation.**\
> Tests like `PatientTest`, `OpdTest`, and `LoginTest` document expected domain model behavior more precisely than any code comment.

> **12 code smells identified with 10 concrete refactoring proposals.**\
> The week 1 report gives the team a clear plan instead of facing an unmapped codebase. This significantly reduces the cognitive effort of deciding where to start.

> **Cross-referenced weekly reports reduce the need to remember past decisions.**\
> The Static Analysis report explicitly links to Testing Debt and to the consolidated delivery, creating a coherent project narrative.

### Identifiable Metrics

| Metric                             | Current State                   | Target                    |
| ---------------------------------- | ------------------------------- | ------------------------- |
| Field injection occurrences        | Across all controllers and DAOs | 0                         |
| Code duplication                   | 39.7%                           | < 5%                      |
| Open issues without prioritization | 549                             | Triaged with Quality Gate |
| Refactoring proposals documented   | 10                              | Applied progressively     |

### Improvement Opportunities

> Configure a custom Quality Gate in SonarCloud with clear thresholds. This turns 549 open issues into an actionable signal.
> Start refactoring from the highest-impact pattern: replacing field injection with constructor injection across all controllers. This single change reduces Reliability issues, enables mocking, and improves readability simultaneously.

---

## Dimension 3: Flow State

> Flow state is the developer's ability to maintain deep focus and continuous work without unnecessary interruptions. This dimension saw partial improvement, but key friction points remain.

### Negative Points

> **Development environment still not standardized.**\
> No `docker-compose`, no setup guide. The SonarCloud analysis was run manually over `master` rather than being triggered automatically. Each team member must configure their own environment independently.

> **Disconnected coverage breaks the feedback flow.**\
> The team wrote 64 tests but the Quality Gate remains "Not computed" because coverage is not integrated. There is completed work that does not reflect in the main dashboard - a concrete source of friction and demotivation.

> **Refactoring is still high risk.**\
> The Testing Debt report explicitly rates refactoring risk as "EXTREMELY HIGH" because controllers have no tests. This inhibits flow: the developer cannot modify confidently and must validate manually.

### Positive Points

> **Test cycle dropped from hours to 10.9 seconds** for the covered modules. For entities and patient DAOs, developers can now modify code and get immediate automated feedback.

> **SonarCloud with no local installation reduced setup friction.**\
> The team chose SonarCloud precisely because it requires no local server. This was a good DevEx decision.

### Identifiable Metrics

| Metric                            | Current State                         | Target               |
| --------------------------------- | ------------------------------------- | -------------------- |
| Test cycle time (covered modules) | 10.9 seconds                          | < 30 seconds         |
| Refactoring risk level            | Extremely high (controllers untested) | Low (>70% coverage)  |
| Quality Gate status               | Not computed                          | Passing              |
| Environment setup time (new dev)  | Estimated > 3 hours                   | < 30 min with Docker |

### Improvement Opportunities

> Integrate `jacoco` with SonarCloud to close the loop: the 64 existing tests should appear in the Quality Gate immediately, validating the work already done.
> Implement GitHub Actions to automate the SonarCloud analysis on every push, removing the manual trigger.

---

## Overall Summary

| Dimension      | State in Hospital Managment Project                                                                              |
| -------------- | ---------------------------------------------------------------------------------------------------------------- |
| Feedback Loops | Improved - 64 tests and SonarCloud metrics, but no CI/CD and coverage not integrated                             |
| Cognitive Load | Improved - problems quantified (549 issues, 39.7% duplication), but 549 open issues without clear prioritization |
| Flow State     | Partially improved - 10.9s test suite for covered modules, but controller refactoring still extremely risky      |
