# 🏛️ Architectural Smells Analysis

> **April 19, 2026** | Course: CSDT_M — Software Quality and Technical Debt

---

## 📋 Table of Contents

- [Analysis Goal & Method](#-analysis-goal--method)
- [Architectural Smells Identified](#-architectural-smells-identified)
- [Summary Matrix](#-summary-matrix)
- [Refactoring Roadmap](#-refactoring-roadmap)
- [References](#-references)

---

## 🎯 Analysis Goal & Method

This report documents the main **architectural smells** identified in the project.
The goal is not to list isolated code-level issues, but to highlight **structural decisions and recurring dependency patterns** that negatively affect maintainability, testability, deployability, and future evolution.

### Scope of the inspection

The analysis was performed through:

1. **Manual inspection of the current structure**
2. **Review of controllers, DAOs, configuration files, and previous weekly reports**
3. **Cross-checking findings with architectural smell literature and quality analysis tools**
4. **IA validation of identified smells**

## 🚨 Architectural Smells Identified


### 1. Layer Violation / Missing Application Service Layer — 🔴 CRITICAL

**Category:** Layering / Modularity
**Quality attributes affected:** Maintainability, testability, modifiability

#### Description

Controllers are directly coordinating persistence operations, session handling, dashboard composition, and business flow decisions.
This means the web layer is not limited to HTTP request handling; instead, it behaves as an application layer and, in some cases, partially as a business layer as well.

#### Evidence

`LoginController` directly depends on three DAOs and composes dashboard data itself:

```java
@Autowired
LoginDao dao;

@Autowired
PatientPrescriptionDao dao1;

@Autowired
UsersInSystemDao dao2;
```

```java
String userId = dao.validate(l1);
HttpSession session = request.getSession();
session.setAttribute("userInfo", l);

mv.setViewName("welcome");
mv.addObject("prescriptionsCount", dao1.prescriptionPrintCount());
mv.addObject("users_count", dao2.getUsersInSystem());
```

`AddEmployeeController` also creates domain objects and directly delegates persistence to a DAO without any intermediate service boundary:

```java
Name n1 = new Name(firstName, middleName, lastName);
Address a1 = new Address(residentialAddress, permanentAddress);
Employee e1 = new Employee(null, n1, birthdate, gender, email, mobileNo, adharNo,
        country, state, city, a1, role, qualification, specialization);

boolean b = dao.add(e1);
```

#### Why this is an architectural smell

When controllers directly orchestrate persistence and business decisions:

* request-handling logic becomes tightly coupled to business flow
* use cases cannot be reused outside HTTP endpoints
* transaction boundaries become scattered
* unit testing becomes difficult because behavior depends on web, session, and database concerns at the same time

This is a strong indicator of a **layer violation** and of a **missing application service layer**.

#### Consequences

* Controllers become large orchestration points
* Business rules are duplicated or scattered
* Refactoring a use case affects web endpoints directly
* Test isolation is reduced
* Future API evolution (REST, mobile, async jobs) becomes harder

#### Recommendation

Introduce an explicit **service layer** with components such as:

* `AuthenticationService`
* `DashboardService`
* `EmployeeService`
* `EmployeeSearchService`

The intended dependency direction should be:

```text
Controller -> Service -> DAO/Repository -> Database
```

Controllers should only:

* validate and map HTTP input
* call one application service
* map the result to a view/response

---

### 2. Hub-like Dependency centered on `LoginDao` — 🔴 CRITICAL

**Category:** Dependency smell / Hub-like dependency / Concern overload
**Quality attributes affected:** Maintainability, analyzability, change isolation

#### Description

`LoginDao` is not used only for login persistence.
It is also injected into controllers and DAOs as an informal logging component (`infoLog`) and therefore becomes a central dependency that many unrelated components rely on.

#### Evidence

`LoginController`:

```java
@Autowired
LoginDao dao;
```

`AddEmployeeController`:

```java
@Autowired
LoginDao infoLog;
```

`SearchEmployeeController`:

```java
@Autowired
LoginDao infoLog;
```

`AddEmployeeDao`:

```java
@Autowired
LoginDao infoLog;
```

Inside `LoginDao` itself, there is also self-injection:

```java
@Autowired
LoginDao infoLog;
```

And the same class contains both authentication behavior and an auxiliary logging method:

```java
@Transactional
public String validate(Login l) { ... }

public void logActivities(String s) {
    //System.out.println("@" + s);
}
```

#### Why this is an architectural smell

A component that becomes a dependency hub for unrelated responsibilities increases coupling across the system.
Here, `LoginDao` acts as:

* authentication DAO
* pseudo-logger
* cross-cutting utility
* indirect infrastructure helper

That means authentication concerns are coupled with tracing/logging concerns, and many modules now depend on a component that should have had a much narrower responsibility.

#### Consequences

* High fan-in around a single component
* Low cohesion inside `LoginDao`
* Changes in authentication or logging strategy ripple across many files
* Dependency graph becomes harder to reason about
* Replacing logging with a real mechanism requires edits in multiple components

#### Recommendation

Split the responsibilities:

* Keep `LoginDao` only for authentication-related persistence
* Replace `logActivities(...)` with a proper logging approach (`Logger`, SLF4J, Logback, etc.)
* Remove self-injection from `LoginDao`
* Avoid using a DAO as a cross-cutting infrastructure component

A better structure would be:

```text
Controller/DAO -> Logger
Controller -> AuthenticationService -> LoginDao
```

---

### 3. Business Logic inside the Persistence Layer — 🔴 CRITICAL

**Category:** Concern overload / Improper transaction boundary
**Quality attributes affected:** Maintainability, modifiability, reuse

#### Description

Several DAOs are not limited to persistence.
For example, `AddEmployeeDao` not only stores the employee but also performs onboarding-related business behavior.

#### Evidence

Inside `AddEmployeeDao.add(...)`, the DAO:

* sets the joining date
* activates the employee
* hashes a password
* creates a related `Login`
* updates the `IdGenerate` table
* persists multiple objects

```java
Date date = new Date();
e.setJoiningDate(date);
e.setStatus(1);

session.save(e);

String password = BCrypt.hashpw(e.getAdharNo() + "", BCrypt.gensalt());
Login l = new Login(id, role, username, password);
session.save(l);

Query q1 = session.createQuery(" from IdGenerate");
IdGenerate temp = (IdGenerate) q1.uniqueResult();
int eid = temp.getEid();
eid++;
q1 = session.createQuery("update IdGenerate set eid= :i");
q1.setParameter("i", eid);
q1.executeUpdate();
```

#### Why this is an architectural smell

This is no longer a simple DAO.
It contains a full **employee onboarding workflow** mixed with persistence details.

That creates a structural problem:

* the persistence layer owns business policy
* business rules cannot be reused without the DAO
* responsibilities are grouped by convenience instead of architectural role

This is a form of **concern overload** and an indicator that transaction/application logic is implemented at the wrong layer.

#### Consequences

* Poor separation of concerns
* Low cohesion inside DAOs
* Harder reuse of onboarding rules
* Difficult testing because business and persistence are mixed
* Future changes to employee creation impact low-level database components directly

#### Recommendation

Move the orchestration to a dedicated service, for example:

* `EmployeeOnboardingService`

That service should:

1. validate the request
2. create the employee
3. generate credentials
4. update required counters/sequences
5. delegate persistence to the corresponding DAOs/repositories

The DAO should only persist and query data.

---

### 4. Scattered Functionality in Employee Search Flow — 🟠 HIGH

**Category:** Scattered functionality / Duplication across use-case flow
**Quality attributes affected:** Maintainability, consistency, evolvability

#### Description

The employee search use case is fragmented into multiple almost-identical controller methods.
The flow for “search, detect absence, return details view or search view” is repeated several times.

#### Evidence

`SearchEmployeeController` contains four similar methods:

* `searchName(...)`
* `searchId(...)`
* `searchMobileNo(...)`
* `searchAadharNo(...)`

All of them follow the same structure:

```java
Employee e1 = dao.searchX(...);
try {
    if (e1.getEid() != null) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("administrator/EmployeeDetailsView");
        mv.addObject("employee", e1);
        return mv;
    }
} catch (NullPointerException e) {
    ModelAndView mv = new ModelAndView();
    mv.setViewName("administrator/SearchEmployeeView");
    mv.addObject("status", "false");
    return mv;
}
return null;
```

#### Why this is an architectural smell

This is more than simple duplication.
It shows that the search use case lacks a stable abstraction and is implemented as multiple scattered controller flows.

When a use case is copied into several endpoints:

* behavior consistency becomes fragile
* changes require touching many methods
* feature evolution tends toward shotgun surgery
* the application flow is not centralized in a reusable component

#### Consequences

* Same bug may appear in several endpoints
* Same refactor must be repeated multiple times
* Response behavior can diverge over time
* Search rules are not expressed in one place

#### Recommendation

Create a reusable search application flow:

* `EmployeeSearchService`
* `EmployeeSearchCriteria` DTO
* shared response-building/helper method

Possible direction:

```text
Controller -> EmployeeSearchService -> SearchEmployeeDao
```

This centralizes search semantics and keeps controllers thin.

---

### 5. Hard-Coded Infrastructure Configuration / Environment Coupling — 🟠 HIGH

**Category:** Configuration smell / Deployment coupling
**Quality attributes affected:** Deployability, portability, security

#### Description

The infrastructure configuration is tightly bound to a local environment inside `springMVC-servlet.xml`.

#### Evidence

The datasource is configured with concrete local values:

```xml
<property name="jdbcUrl" value="jdbc:mysql://localhost:3306/hospital" />
<property name="user" value="root" />
<property name="password" value="" />
```

The application also scans the whole `com.project` package:

```xml
<ctx:component-scan base-package="com.project"></ctx:component-scan>
```

#### Why this is an architectural smell

Hard-coded infrastructure values couple the architecture to a specific execution environment.
This makes the system less portable and complicates CI/CD, cloud deployment, containerization, and secure configuration management.

At the architectural level, this means the deployment view is not separated from the code/configuration view.

#### Consequences

* Environment changes require code/config changes in source-controlled files
* Local assumptions leak into deployment architecture
* Security risk from embedded credentials
* Reduced portability across machines and stages
* Harder automation in pipelines

#### Recommendation

Externalize infrastructure configuration:

* environment variables
* Spring property files by profile
* secret management
* separate dev/test/prod configuration

For example:

```text
application-dev.properties
application-test.properties
application-prod.properties
```

Or environment-based XML/property placeholders if the current stack must remain XML-based.

---

## 📊 Summary

| ID    | Architectural Smell                     | Severity    | Main Evidence                                                | Main Risk                                            |
| ----- | --------------------------------------- | ----------- | ------------------------------------------------------------ | ---------------------------------------------------- |
| AS-01 | Missing service layer / layer violation | 🔴 Critical | Controllers call DAOs directly and orchestrate business flow | Tight coupling between web, session, and persistence |
| AS-02 | Hub-like dependency around `LoginDao`   | 🔴 Critical | `LoginDao` used for auth + pseudo logging across modules     | Ripple effects and low cohesion                      |
| AS-03 | Business logic inside DAOs              | 🔴 Critical | `AddEmployeeDao` performs onboarding workflow                | Wrong transaction boundary and poor reuse            |
| AS-04 | Scattered search functionality          | 🟠 High     | Four duplicated search flows in `SearchEmployeeController`   | Inconsistency and shotgun surgery                    |
| AS-05 | Hard-coded infrastructure configuration | 🟠 High     | Local DB URL/user/password in XML config                     | Low portability and weak deployment architecture     |

---

## 📚 References

1. SonarCloud — [https://sonarcloud.io/](https://sonarcloud.io/)
2. The Open Group — [https://www.opengroup.org/architecture/togaf7-doc/arch/p4/comp/clists/syseng.htm](https://www.opengroup.org/architecture/togaf7-doc/arch/p4/comp/clists/syseng.htm)
3. Microsoft Patterns & Practices — [https://docs.microsoft.com/en-us/previous-versions/msp-n-p/ff647464(v=pandp.10)?redirectedfrom=MSDN](https://docs.microsoft.com/en-us/previous-versions/msp-n-p/ff647464%28v=pandp.10%29?redirectedfrom=MSDN)

