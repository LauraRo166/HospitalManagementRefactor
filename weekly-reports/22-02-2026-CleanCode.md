# 🧹 Clean Code & XP Practices

> **Week 2 — February 22, 2026** | Course: CSDT_M — Software Quality and Technical Debt

---

## 📋 Table of Contents

- [Authentication & Administration Module](#authentication--administration-module)
- [Patient & Reception Module](#patient--reception-module)
- [Doctors & Entities Module](#doctors--entities-module)

---

## Analysis Structure

We analyzed the project into three groups of modules, each of which was assigned based on **3 aspects**.

1. **Clean Code Characteristics** — Which of the 8 characteristics are met or violated
2. **Programming Principles** — Which YAGNI / KISS / DRY / SOLID principles are violated
3. **XP Practices** — Which practices would improve the module's code quality

---

## Authentication & Administration Module

> **Assigned files:** `LoginController`, `EditLoginDetailsController`, `LogOutController`, `administrator/*` (~20 files)

---


### 1.1 Clean Code Characteristics

---

<details open>
<summary><strong>01. Focused Code — ❌ NOT MET</strong></summary>

**Principle:** Clean code does one thing. Bad code does too many things.

**Violation in `LoginController.validate()`:**

```java
@Controller
public class LoginController {
    @Autowired LoginDao dao;
    @Autowired PatientPrescriptionDao dao1;  // why does LoginController need prescription logic?
    @Autowired UsersInSystemDao dao2;        // why does LoginController count system users?

    public ModelAndView validate(String role, String username, String password,
                                  HttpServletRequest request) {
        // 1. Validates credentials via DAO
        // 2. Creates and populates HttpSession
        // 3. Fetches prescription metrics for receptionist dashboard
        // 4. Fetches user count for admin dashboard
        // 5. Logs sensitive data (username, password, role)
        // 6. Decides which view to render based on role
    }
}
```

This single method simultaneously handles authentication, session management, role-based dashboard preparation, metric fetching, logging, and view routing — at least 5 distinct responsibilities.

**Violation in `AddEmployeeController.add()`:**

Constructs `Name` and `Address` objects, builds an `Employee` entity, persists it via DAO, logs the result, and resolves the response view — all in one method.

**Violation in `EditEmployeeController`:**

Contains two distinct use cases: editing the profile of the currently logged-in user (using session data) and generic admin editing of any employee — both crammed into the same controller with overlapping logic.

**Recommendation:** Extract an `AuthenticationService` that encapsulates credential validation and session creation. Introduce a `DashboardService` that, given a role, builds the appropriate model. Leave `LoginController` responsible only for mapping the HTTP request to the service call and returning the view.

</details>

---

<details>
<summary><strong>02. Boy Scout Rule — ❌ NOT MET</strong></summary>

**Principle:** Always leave the code cleaner than you found it.

**Evidence in `SearchEmployeeController.java`:**

```java
import java.io.FileOutputStream;       // never used
import javax.servlet.http.HttpServletResponse;  // never used
import javax.servlet.RequestDispatcher;         // never used
```

Unused imports are left in place across multiple controller files, indicating that dead code is never cleaned up when files are modified.

**Evidence across `administrator/*` controllers:**

Logging calls that existed for debugging purposes remain in production code:

```java
infoLog.logActivities("in LoginController-validate: username=" + username
    + " password=" + password + " role=" + role);  // logs plaintext password
```

Sensitive data such as passwords are logged in plain text, and these log lines have never been removed or sanitized even as the code evolved.

**Recommendation:** Remove all unused imports. Replace plaintext credential logging with a sanitized audit trail. Adopt a rule that every PR touching a file must remove at least one piece of dead code.

</details>

---

<details>
<summary><strong>03. Understandable (KISS & YAGNI) — ❌ NOT MET</strong></summary>

**Principle:** Code should be simple and self-explanatory.

**Evidence — Long parameter list in `AddEmployeeController.add()` and `EditEmployeeController.edit()`:**

```java
public ModelAndView add(
    @RequestParam("firstName") String firstName,
    @RequestParam("middleName") String middleName,
    @RequestParam("lastName") String lastName,
    @RequestParam("gender") String gender,
    @RequestParam("email") String email,
    @RequestParam("mobileNo") Long mobileNo,
    @RequestParam("adharNo") Long adharNo,
    @RequestParam("country") String country,
    @RequestParam("state") String state,
    @RequestParam("city") String city,
    // ... ~15 total parameters
)
```

Like the `AddPatientController` in the reception module, these methods receive every form field as a separate `@RequestParam`, making them untestable and fragile.

**Evidence — Exception used as flow control:**

```java
// LoginController.validate()
if (!userId.equals(null)) {
    // ... success path
} else {
    throw new Exception();  // used to signal "not found", not an exceptional case
}

// ShowAllEmployeeDetailsController
if (!l.equals(null)) {
    // success
} else {
    throw new Exception();
}
```

Using `throw new Exception()` as a substitute for an `if/return` is both misleading and harder to follow. Worse, `!userId.equals(null)` is an incorrect null check — calling `.equals()` on a potentially null reference will itself throw a `NullPointerException`.

**Recommendation:** Replace `@RequestParam` lists with a `@ModelAttribute EmployeeFormDTO`. Replace exception-as-flow-control with explicit null checks and typed return paths. Rename `validate()` to `loginAndBuildDashboard()` to reflect its actual behavior.

</details>

---

<details>
<summary><strong>04. Scalable (SOLID, OOP) — ❌ NOT MET</strong></summary>

**Principle:** Code must support future growth through proper design.

**Evidence — Role-based dashboard tightly coupled to `LoginController`:**

```java
// LoginController.validate() — adding a new role requires editing this method
mv.addObject("prescriptionsCount", dao1.prescriptionPrintCount()); // only for receptionist
mv.addObject("users_count", dao2.getUsersInSystem().size());       // only for admin
mv.setViewName("welcome");  // same view for all roles
```

Adding a new role (e.g., `doctor` or `nurse`) requires directly modifying `LoginController.validate()` to inject the correct metrics for that role. The controller is not closed for modification.

**Evidence — New employee search criteria require copy-pasting methods:**

`SearchEmployeeController` has four nearly identical methods (`searchName`, `searchId`, `searchMobileNo`, `searchAadharNo`). Adding a fifth search criterion means duplicating another method.

**Recommendation:** Introduce a `DashboardFactory` or Strategy pattern mapping roles to model-builder components. Replace the four search methods with a single parameterized `search(String criterion, String value)` method backed by a `EmployeeSearchService`.

</details>

---

<details>
<summary><strong>05. No Duplication (DRY) — ❌ NOT MET</strong></summary>

**Principle:** Don't Repeat Yourself.

**Evidence — Repeated DAO result handling pattern across all administrator controllers:**

```java
// Appears in AddEmployeeController, EditEmployeeController,
// DeleteEmployeeController, and ShowAllEmployeeDetailsController:
boolean result = dao.add(employee);
infoLog.logActivities("in XController: got=" + result);
if (result) {
    mv.setViewName("successPage");
} else {
    throw new Exception();
}
```

This exact structure is copy-pasted with minimal variation across every mutating operation in the module.

**Evidence — Repeated search-and-render pattern in `SearchEmployeeController`:**

```java
// Repeated 4 times with different DAO method calls:
try {
    Employee e1 = dao.searchByX(...);
    if (e1.getEid() != null) {
        mv.addObject("employee", e1);
        mv.setViewName("administrator/EmployeeDetailsView");
    }
} catch (NullPointerException e) {
    mv.addObject("status", false);
    mv.setViewName("administrator/SearchEmployeeView");
}
```

**Evidence — `LoginDao` injected as an activity logger in every controller:**

```java
@Autowired LoginDao infoLog;  // present in 10+ controllers as a logging workaround
```

| Duplication | Locations |
|---|---|
| DAO result → log → success/fail pattern | `AddEmployeeController`, `EditEmployeeController`, `DeleteEmployeeController`, `ShowAllEmployeeDetailsController` |
| NullPointerException-based search render | `SearchEmployeeController.searchName/searchId/searchMobileNo/searchAadharNo` |
| `LoginDao infoLog` logging boilerplate | All 10+ controllers in this module |

**Recommendation:** Extract a private helper `renderEmployeeOrNotFound(Employee e, ModelAndView mv)`. Create a shared `OperationResultHandler.handleResult(boolean ok, ModelAndView mv)`. Introduce a dedicated `AuditService` to replace the `LoginDao`-as-logger workaround.

</details>

---

<details>
<summary><strong>06. Abstraction — ❌ NOT MET</strong></summary>

**Principle:** Classes and methods should be short and operate at a single abstraction level.

**Evidence — `LoginController.validate()` mixes three abstraction levels:**

```java
public ModelAndView validate(String role, String username, String password,
                              HttpServletRequest request) {
    // Level 1: Infrastructure — HTTP session manipulation
    HttpSession session = request.getSession();
    session.setAttribute("userInfo", loginObj);

    // Level 2: Domain — credential validation and role interpretation
    Login userId = dao.validate(username, password);

    // Level 3: Persistence — metrics queries (N queries for dashboard)
    List users = dao2.getUsersInSystem();
    for (Login l : users) {
        infoLog.logActivities("user: " + l.getUsername());  // logging inside iteration
    }
    int count = dao1.prescriptionPrintCount();
}
```

Infrastructure (HTTP/session), domain (roles, auth), and persistence (metrics) all collapse into a single 50+ line method.

**Recommendation:** Extract `AuthenticationService.login(username, password)` → returns a `LoginResult`. Extract `DashboardModelBuilder.buildFor(role)` → returns a populated `Map<String, Object>`. The controller then becomes 5–8 lines that wire these together.

</details>

---

<details>
<summary><strong>07. Testable (F.I.R.S.T.) — ❌ NOT MET</strong></summary>

**Principle:** Code must have unit tests that are Fast, Independent, Repeatable, Self-Validating, and Timely.

**Evidence:**
- No test files exist anywhere in the project
- `LoginController.validate()` requires mocks for `HttpServletRequest`, `HttpSession`, and three separate DAOs to test the simplest login scenario
- The use of `throw new Exception()` for control flow makes verifying business outcomes through tests awkward — tests must catch generic exceptions instead of checking meaningful return values
- Plaintext password logging means any test that exercises the login path produces security-sensitive output in test logs
- `SearchEmployeeController` catches `NullPointerException` to detect "not found" — this makes it impossible to distinguish a programming error from a legitimate empty result in a test

**Recommendation:** Introduce JUnit 5 + Mockito. Extract `AuthenticationService` so it can be tested without HTTP context. Replace NullPointerException-based flow with `Optional<Employee>` returns so tests can assert on meaningful values.

</details>

---

<details>
<summary><strong>08. Principle of Least Surprise — ❌ NOT MET</strong></summary>

**Principle:** A method should behave exactly as its name suggests.

**Violations:**

- `LoginController.validate()` — "validate" implies a boolean check; the method actually creates sessions, fetches dashboard metrics, and routes to views.
- `EditEmployeeController` — mixing logged-in user's own profile edit with admin editing of any employee in the same controller is unexpected.
- `SearchEmployeeController` — catching `NullPointerException` to handle "employee not found" means a programming error (e.g., a null pointer in unrelated code) would silently render the "not found" view instead of surfacing a bug.

```java
// Surprise: NullPointerException is used as a "not found" signal
try {
    Employee e1 = dao.searchByName(name);
    if (e1.getEid() != null) { ... }
} catch (NullPointerException e) {
    mv.addObject("status", false);  // silently swallows real bugs
}
```

**Recommendation:** Rename `validate()` to `authenticate()` or `login()`. Have DAOs return `Optional<Employee>` to avoid NullPointerException-based flow. Separate self-profile editing from admin CRUD into distinct controllers.

</details>

---

### 1.2 Programming Principles Violations

---

<details open>
<summary><strong>YAGNI — You Aren't Gonna Need It ❌ VIOLATED</strong></summary>

**Evidence:**

1. **Unused imports accumulate across files** — `FileOutputStream`, `HttpServletResponse`, and `RequestDispatcher` are imported in `SearchEmployeeController` but never referenced. These suggest features were started and abandoned without cleanup.

2. **Full entity list loaded just for a count** in `LoginController.validate()`:

```java
List users = dao2.getUsersInSystem();
for (Login l : users) {
    infoLog.logActivities("system user: " + l);
}
mv.addObject("users_count", users.size());
```

All `Login` entities are loaded into memory and iterated solely to log them and then count with `.size()`. A `COUNT(*)` SQL query would suffice.

3. **Logging of data that is never acted upon** — multiple controllers log intermediate DAO results that are only used for debugging and serve no production purpose.

**Conclusion:** Dead imports and unnecessary data loading indicate features were partially implemented or never cleaned up, adding noise and memory overhead with no benefit.

</details>

---

<details>
<summary><strong>KISS — Keep It Simple, Stupid ❌ VIOLATED</strong></summary>

**Evidence:**

1. **15+ parameter controller methods** — `AddEmployeeController.add()` and `EditEmployeeController.edit()` accept every form field as a separate `@RequestParam`, identical to the violation found in the Patient module.

2. **Incorrect null check used throughout:**

```java
// !userId.equals(null) will throw NullPointerException if userId IS null
if (!userId.equals(null)) { ... }

// Correct form:
if (userId != null) { ... }
```

This pattern appears in `LoginController` and `ShowAllEmployeeDetailsController`, introducing a latent bug.

3. **Exception as control flow instead of conditional logic:**

```java
} else {
    throw new Exception();  // used where a simple return or if/else would do
}
```

This forces callers to wrap invocations in try/catch for what are normal business outcomes (login failed, employee not found).

**Conclusion:** Multiple methods in this module are unnecessarily complex due to incorrect idioms and long parameter lists that standard Spring features (form binding, Optional) would eliminate.

</details>

---

<details>
<summary><strong>DRY — Don't Repeat Yourself ❌ VIOLATED</strong></summary>

| Duplication | Locations |
|---|---|
| `result → log → success/fail` pattern | `AddEmployeeController.add()`, `EditEmployeeController.edit()`, `DeleteEmployeeController.delete()`, `ShowAllEmployeeDetailsController.showEmployeeDetailsViewMethod()` |
| Search + NullPointerException render | `SearchEmployeeController.searchName/searchId/searchMobileNo/searchAadharNo` |
| `@Autowired LoginDao infoLog` logging boilerplate | Every controller in this module (10+ classes) |
| Plaintext credential logging | `LoginController.validate()`, `EditLoginDetailsController` |

**Conclusion:** The most pervasive duplication is the CRUD result-handling pattern repeated verbatim in every mutating controller. A single shared helper method or template would eliminate all four copies simultaneously.

</details>

---

<details>
<summary><strong>SOLID — S: Single Responsibility Principle ❌ VIOLATED</strong></summary>

| Class | Responsibilities Found |
|---|---|
| `LoginController` | Credential validation, session creation, role routing, dashboard metric fetching (2 DAOs), activity logging, view resolution |
| `EditEmployeeController` | Self-profile editing (session-based) + generic admin employee editing (two distinct use cases) |
| `SearchEmployeeController` | Four search criteria implementations + NullPointerException-based not-found handling + view rendering |
| `AddEmployeeController` | Request parsing, entity construction (`Name`, `Address`, `Employee`), DAO orchestration, logging, view resolution |

</details>

---

<details>
<summary><strong>SOLID — O: Open/Closed Principle ❌ VIOLATED</strong></summary>

**Evidence:** Adding a new user role (e.g., `nurse`) requires modifying `LoginController.validate()` to add a new metric-loading branch. Adding a new employee search criterion requires copying an existing method in `SearchEmployeeController`. Neither class is closed for modification; both are open in the wrong direction.

**Recommendation:** A `DashboardStrategy` per role and a `EmployeeSearchSpecification` abstraction would allow new roles and search criteria to be added without touching existing code.

</details>

---

<details>
<summary><strong>SOLID — I: Interface Segregation Principle ❌ VIOLATED</strong></summary>

No interfaces are defined for any DAO or service in this module. All controllers depend directly on concrete DAO classes (`LoginDao`, `AddEmployeeDao`, `EditEmployeeDao`, etc.), coupling consumers to every method on each DAO regardless of which ones they actually need.

</details>

---

<details>
<summary><strong>SOLID — D: Dependency Inversion Principle ❌ VIOLATED</strong></summary>

**Evidence:**

```java
// LoginController depends directly on three concrete DAOs
@Autowired LoginDao dao;
@Autowired PatientPrescriptionDao dao1;   // cross-module concrete dependency
@Autowired UsersInSystemDao dao2;
@Autowired LoginDao infoLog;              // same class used for two different purposes
```

`LoginController` depends on a concrete DAO from the **Patient & Reception module** (`PatientPrescriptionDao`), creating a cross-module coupling with no interface boundary. `LoginDao` is simultaneously used for authentication and as an activity logger — two responsibilities conflated in one concrete class.

**Recommendation:** Introduce `AuthenticationService`, `DashboardService`, and `AuditService` interfaces. Inject these into controllers. The cross-module coupling to `PatientPrescriptionDao` would disappear inside `DashboardService`'s receptionist implementation.

</details>

---

### 1.3 XP Practices Recommendations

---

<details open>
<summary><strong>1. Refactoring 🔴 High Priority</strong></summary>

**Specific actions for this module:**

| # | Refactoring | Target | Risk |
|---|---|---|---|
| 1 | Introduce `EmployeeFormDTO` to replace 15+ parameter methods | `AddEmployeeController.add()`, `EditEmployeeController.edit()` | Low |
| 2 | Extract `AuthenticationService` with `login(username, password)` | `LoginController.validate()` | High |
| 3 | Extract `DashboardModelBuilder` (strategy per role) | `LoginController.validate()` | Medium |
| 4 | Replace NullPointerException-based "not found" with `Optional<Employee>` | `SearchEmployeeController` (4 methods) | Low |
| 5 | Replace `!x.equals(null)` with `x != null` | `LoginController`, `ShowAllEmployeeDetailsController` | Low |
| 6 | Extract shared `handleCrudResult(boolean, ModelAndView)` helper | All 4 mutating controllers | Low |
| 7 | Introduce `AuditService` to decouple logging from `LoginDao` | All 10+ controllers | Medium |
| 8 | Separate self-edit and admin-edit into distinct controllers | `EditEmployeeController` | Medium |

</details>

---

<details>
<summary><strong>2. Test-Driven Development (TDD) 🔴 High Priority</strong></summary>

**Recommended first tests for this module:**

- `AuthenticationService.login()`: test successful login, wrong password, and unknown user — all without `HttpServletRequest`
- `DashboardModelBuilder`: assert that a `receptionist` role produces a model with `prescriptionsCount` and no `users_count`, and vice versa for `admin`
- `SearchEmployeeController`: test each search method with Mockito-mocked `EmployeeService`, asserting the correct view and model attributes for both found and not-found cases — without relying on `NullPointerException`
- `AddEmployeeController.add()`: verify `employeeService.add(EmployeeFormDTO)` is called with a correctly assembled object

</details>

---

<details>
<summary><strong>3. Pair Programming & Collective Code Ownership 🟡 Medium Priority</strong></summary>

The same bad practices (exception-as-flow-control, `LoginDao` as logger, identical CRUD patterns) appear identically across all controllers in this module, suggesting they propagated from a single author's style without cross-review. Pair programming during the initial implementation of `LoginController` — the most complex class — would likely have surfaced the SRP and DIP violations before they were replicated everywhere.

**Recommendation:** Schedule a pairing session specifically to refactor `LoginController.validate()` as a shared learning exercise, then apply the same pattern to `administrator/*` controllers collectively.

</details>

---

<details>
<summary><strong>4. Simple Design 🟡 Medium Priority</strong></summary>

Applying to this module specifically:
- Replace 15+ parameter methods with `@ModelAttribute EmployeeFormDTO` form binding
- Replace the `throw new Exception()` control-flow anti-pattern with explicit conditionals and typed results
- Remove all plaintext password logging immediately — this is a security issue, not just a code quality issue
- Remove all unused imports across `administrator/*`

</details>

---

<details>
<summary><strong>5. Coding Standard 🟡 Medium Priority</strong></summary>

**Issues specific to this module:**
- DAO fields named `dao`, `dao1`, `dao2` in `LoginController` provide no semantic information — rename to `loginDao`, `prescriptionDao`, `usersInSystemDao`
- `infoLog` as a variable name for a `LoginDao` used for logging is misleading — the type name does not indicate logging intent
- `!x.equals(null)` should be replaced with `x != null` as a team-wide standard
- Unused imports must be removed as part of the coding standard, enforced via Checkstyle or IDE inspection profiles

</details>

---

---
## Patient & Reception Module

> **Assigned files:** `receptionist/*`, `opd/*` (~15 files)
>
> **Analyzed:** `AddPatientController`, `AddPatientDao`, `SearchPatientDao`, `PatientPrescriptionDao`, `DeleteOpdController`, `DeleteOpdDao`, `OpdDetailsDao`

---

### 2.1 Clean Code Characteristics

---

<details open>
<summary><strong>01. Focused Code — ❌ NOT MET</strong></summary>

**Principle:** Clean code does one thing. Bad code does too many things.

**Violation in `AddPatientController.java`:**

```java
@Controller
public class AddPatientController {
    @Autowired AddPatientDao dao;
    @Autowired PatientPrescriptionDao dao1;  // why does AddPatient need prescription logic?
    @Autowired LoginDao infoLog;

    public ModelAndView view() { ... }    // renders the form
    public ModelAndView add(...) { ... }  // processes form + builds Patient + persists
}
```

The `add()` method simultaneously handles: reading 16 request parameters, constructing `Name` and `Address` objects, building a `Patient` entity, persisting it via DAO, and managing the response view. This is at least 4 distinct responsibilities in a single method.

**Violation in `PatientPrescriptionDao.java`:**
This DAO manages both prescription counting AND patient name lookup. It also contains a hidden N+1 query inside `getPrescriptionList()`.

**Recommendation:** Extract a dedicated `PatientService` that handles entity construction and orchestration. Split prescription-related logic into its own `PrescriptionDao` or service.

</details>

---

<details>
<summary><strong>02. Boy Scout Rule — ❌ NOT MET</strong></summary>

**Principle:** Always leave the code cleaner than you found it.

**Evidence in `AddPatientController.java`:**

```java
// Entire try/catch block commented out in add() method:
//try{
//  ...
//}
/*}
catch(Exception e) {
    infoLog.logActivities("in AddPatientController-add: " + e);
    mv.setViewName("failure");
    mv.addObject("error", e);
    return mv;
}*/
```

```java
// Inline comment left mid-logic:
//else
//{   throw new Exception();  }
```

The commented-out error handling in `add()` means the method **has no exception handling at all**. If anything fails during patient registration, the controller silently returns an empty `ModelAndView()`.

**Recommendation:** Remove all commented-out code. Either implement proper exception handling or document explicitly why it was removed.

</details>

---

<details>
<summary><strong>03. Understandable (KISS & YAGNI) — ❌ NOT MET</strong></summary>

**Principle:** Code should be simple and self-explanatory.

**Evidence — 16-parameter method signature in `AddPatientController.java:64`:**

```java
public ModelAndView add(
    @RequestParam("firstName") String firstName,
    @RequestParam("middleName") String middleName,
    @RequestParam("lastName") String lastName,
    @RequestParam("birthdate") String birthdate,
    @RequestParam("gender") String gender,
    @RequestParam("email") String email,
    @RequestParam("mobileNo") Long mobileNo,
    @RequestParam("adharNo") Long adharNo,
    @RequestParam("country") String country,
    @RequestParam("state") String state,
    @RequestParam("city") String city,
    @RequestParam("residentialAddress") String residentialAddress,
    @RequestParam("permanentAddress") String permanentAddress,
    @RequestParam("bloodGroup") String bloodGroup,
    @RequestParam("chronicDiseases") String chronicDiseases,
    @RequestParam("medicineAllergy") String medicineAllergy,
    @RequestParam("doctorId") String doctorId)
```

16 parameters in a single method is a textbook Long Parameter List smell. The method is impossible to call in tests without enormous boilerplate.

**Evidence — Raw array for doctor data (`AddPatientDao.java`):**

```java
String[] temp = new String[4];
temp[0] = e.getEid();       // what is index 0?
temp[1] = e.getName().getFirstName();
temp[2] = e.getName().getMiddleName();
temp[3] = e.getName().getLastName();
```

**Recommendation:** Replace the 16-parameter method with a `@RequestBody PatientRegistrationDTO` (for REST) or a `@ModelAttribute Patient` form binding. Replace `String[]` with a typed `DoctorSummaryDTO`.

</details>

---

<details>
<summary><strong>04. Scalable (SOLID, OOP) — ❌ NOT MET</strong></summary>

**Principle:** Code must support future growth through proper design.

**Evidence — Magic status integers scattered across OPD DAOs:**

```java
// DeleteOpdDao.java — status=1 means "pending", never documented
query("delete from Opd where pid= :id AND status=1")
query("update Opd set status=2 where pid= :id AND status=1")
query("update Opd set status=0 where pid= :id AND status=2")

// PatientPrescriptionDao.java — status=2 means "ready to print"
query("from Opd where status= :s").setParameter("s", 2)
```

The same magic integers appear across multiple DAOs with no shared definition. Adding a new status requires hunting down all occurrences manually.

**Recommendation:** Define an `OpdStatus` enum with `DONE=0`, `PENDING=1`, `PRINTING=2` and reference it everywhere.

</details>

---

<details>
<summary><strong>05. No Duplication (DRY) — ❌ NOT MET</strong></summary>

**Principle:** Don't Repeat Yourself.

**Evidence — Patient name resolution duplicated in at least 3 places:**

```java
// PatientPrescriptionDao.java — getPrescriptionList()
String name = p.getName().getFirstName() + " " + p.getName().getMiddleName()
              + " " + p.getName().getLastName();

// PatientPrescriptionDao.java — getPatientName()
String str = p.getName().getFirstName() + " " + p.getName().getMiddleName()
             + " " + p.getName().getLastName();

// OpdDetailsDao.java — searchPatientName() (same pattern via HQL + concatenation)
```

**Evidence — `searchDoctorAssigned()` duplicated in both `SearchPatientDao` and `OpdDetailsDao`:**

```java
// Both classes implement the exact same HQL query and name-concatenation loop
Query q1 = session.createQuery(
    "select name.firstName,name.lastName from Employee where eid= :id");
```

**Recommendation:** Create a shared utility method `PersonNameUtils.fullName(Name name)` and a `EmployeeQueryHelper` for the repeated doctor search logic.

</details>

---

<details>
<summary><strong>06. Abstraction — ❌ NOT MET</strong></summary>

**Principle:** Classes and methods should be short and operate at a single abstraction level.

**Evidence — `getPrescriptionList()` in `PatientPrescriptionDao.java` mixes:**

```java
public List<String[]> getPrescriptionList() {
    // Level 1: query all OPD records with status=2
    Query q1 = session.createQuery("from Opd where status= :s");

    for (Opd o : l1) {
        String[] temp = new String[3];
        temp[0] = o.getPid();

        // Level 2: inner query per iteration (N+1 problem)
        q1 = session.createQuery("from Patient where pid= :i");
        Patient p = (Patient) q1.uniqueResult();

        // Level 3: string formatting/concatenation
        temp[1] = p.getName().getFirstName() + " " + ... + " " + ...;
        temp[2] = "" + o.getOpdId();
    }
}
```

Three abstraction levels (data access, in-loop querying, string formatting) collapse into one method. This also produces an **N+1 query** per OPD record.

**Recommendation:** Extract inner query into `findPatientById(String pid)`, extract name formatting into `PatientNameUtils`, and replace the N+1 with a single JOIN query.

</details>

---

<details>
<summary><strong>07. Testable (F.I.R.S.T.) — ❌ NOT MET</strong></summary>

**Principle:** Code must have unit tests that are Fast, Independent, Repeatable, Self-Validating, and Timely.

**Evidence:**
- No test files exist in the project
- `AddPatientController.add()` has 16 parameters — impossible to test without full request mocking
- All DAOs inject `LoginDao` as a logging dependency, requiring Spring context setup to test anything
- The N+1 query in `getPrescriptionList()` would be undetectable without integration tests
- `AddPatientDao.add()` has commented-out exception handling, meaning failures silently return `true`

**Recommendation:** Introduce JUnit 5 + Mockito, create a `PatientService` that can be tested without a database, and write at least one unit test per business method.

</details>

---

<details>
<summary><strong>08. Principle of Least Surprise — ⚠️ PARTIALLY MET</strong></summary>

**Partial violation in `AddPatientController.java`:**

```java
// view() retrieves the form
// add() processes the form — OK so far

// But add() also queries prescriptions count with no documented reason:
mv.addObject("prescriptionsCount", dao1.prescriptionPrintCount());  // for receptionist only
```

A method named `add()` is not expected to also query prescription counts. The comment `// for receptionist only` implies the caller context is a known workaround.

**Violation — `SearchPatientDao.searchDoctorAssigned()`:**  
A class named `SearchPatientDao` should not contain a method that searches for doctors. This violates what the name implies.

**Recommendation:** Move doctor-related queries out of `SearchPatientDao`. Document why `prescriptionsCount` is needed in the add flow, or pass it through a shared session attribute.

</details>

---

### 2.2 Programming Principles Violations

---

<details open>
<summary><strong>YAGNI — You Aren't Gonna Need It ❌ VIOLATED</strong></summary>

**Evidence:**

1. **Manual prescription count via loop** in `PatientPrescriptionDao.prescriptionPrintCount()`:

```java
List<Opd> temp = (List<Opd>) q1.list();
int i = 0;
for (Opd o : temp) {
    i++;   // manually counting what .size() does in one call
}
return i;
```

Loading all `Opd` entity objects into memory just to count them is unnecessary. SQL `COUNT(*)` would be far simpler.

2. **Unused variable declarations** — `int i = 0, j = 0;` in `AddPatientDao.getDoctors()` where `j` is never used.

3. **`@Controller` import in `PatientPrescriptionDao`** — the DAO class imports `org.springframework.stereotype.Controller` but uses `@Component`. The unused import adds noise.

**Conclusion:** There are multiple instances where code exists to solve problems that either don't exist or are solved trivially by existing APIs.

</details>

---

<details>
<summary><strong>KISS — Keep It Simple, Stupid ❌ VIOLATED</strong></summary>

**Evidence:**

1. **16-parameter controller method** (`AddPatientController.add()`) — instead of binding a form object, all fields are received as individual `@RequestParam` strings.

2. **Manual ID counter** in `AddPatientDao.add()`:

```java
// 7 lines to do what @GeneratedValue(strategy = AUTO) does in 0 lines
Query q1 = session.createQuery("from IdGenerate");
IdGenerate temp = (IdGenerate) q1.uniqueResult();
int pid = temp.getPid();
pid++;
q1 = session.createQuery("update IdGenerate set pid= :i");
q1.setParameter("i", pid);
q1.executeUpdate();
```

This also introduces a race condition: two concurrent requests could read the same `pid` before either increments it.

3. **N+1 query inside a loop** (`PatientPrescriptionDao.getPrescriptionList()`): fetching each patient with a separate query per OPD record instead of a single JOIN query.

**Conclusion:** Several sections of this module add significant complexity for results that standard Spring/Hibernate/SQL features would provide with minimal code.

</details>

---

<details>
<summary><strong>DRY — Don't Repeat Yourself ❌ VIOLATED</strong></summary>

| Duplication | Locations |
|---|---|
| Full-name string concatenation | `PatientPrescriptionDao.getPrescriptionList()`, `PatientPrescriptionDao.getPatientName()`, `OpdDetailsDao.searchPatientName()` |
| `searchDoctorAssigned()` HQL + loop | `SearchPatientDao`, `OpdDetailsDao` (identical implementation) |
| Magic number `status=2` (printing) | `PatientPrescriptionDao`, `DeleteOpdDao` |
| `LoginDao infoLog` injection boilerplate | Every DAO and Controller (8+ classes in this module) |

**Conclusion:** This module has the highest concentration of DRY violations in the project. The doctor name resolution logic alone is copied verbatim in two separate DAOs.

</details>

---

<details>
<summary><strong>SOLID — S: Single Responsibility Principle ❌ VIOLATED</strong></summary>

| Class | Responsibilities Found |
|---|---|
| `AddPatientController` | Request parsing, entity building, DAO orchestration, view resolution, prescription count retrieval |
| `PatientPrescriptionDao` | Prescription listing, prescription counting, patient name lookup |
| `SearchPatientDao` | Patient search by 4 criteria + doctor name resolution |
| `DeleteOpdDao` | OPD deletion + prescription print state management + prescription-done confirmation |

</details>

---

<details>
<summary><strong>SOLID — O: Open/Closed Principle ❌ VIOLATED</strong></summary>

**Evidence:** Adding a new OPD status (e.g., "cancelled") requires editing `DeleteOpdDao`, `PatientPrescriptionDao`, and `OpdDetailsDao` directly — all of which hardcode the integer values 0, 1, 2 in query strings.

The system is not closed for modification when it comes to status management.

</details>

---

<details>
<summary><strong>SOLID — I: Interface Segregation Principle ❌ VIOLATED</strong></summary>

No interfaces are defined for any DAO in the `receptionist/*` or `opd/*` packages. All controllers depend directly on concrete DAO classes, violating ISP by coupling consumers to all DAO methods regardless of which ones they need.

</details>

---

<details>
<summary><strong>SOLID — D: Dependency Inversion Principle ❌ VIOLATED</strong></summary>

**Evidence:**

```java
// AddPatientController directly depends on concrete implementations
@Autowired AddPatientDao dao;               // concrete DAO
@Autowired PatientPrescriptionDao dao1;     // concrete DAO
@Autowired LoginDao infoLog;               // concrete logging workaround
```

No interface exists between any controller and its DAOs in this module. Unit testing is impossible without spinning up a Spring application context with a real database.

</details>

---

### 2.3 XP Practices Recommendations

---

<details open>
<summary><strong>1. Refactoring 🔴 High Priority</strong></summary>

**Specific actions for this module:**

| # | Refactoring | Target | Risk |
|---|---|---|---|
| 1 | Introduce `PatientRegistrationDTO` to replace 16-parameter method | `AddPatientController.add()` | Low |
| 2 | Replace `String[]` with `DoctorSummaryDTO` | `AddPatientDao.getDoctors()` | Low |
| 3 | Extract `PersonNameUtils.fullName()` helper | `PatientPrescriptionDao`, `OpdDetailsDao` | Low |
| 4 | Merge duplicate `searchDoctorAssigned()` into one shared DAO | `SearchPatientDao`, `OpdDetailsDao` | Medium |
| 5 | Replace manual ID counter with `@GeneratedValue` | `AddPatientDao.add()` | Medium |
| 6 | Replace N+1 in `getPrescriptionList()` with a JOIN query | `PatientPrescriptionDao` | Medium |
| 7 | Introduce `OpdStatus` enum and replace all magic `status=0/1/2` | All OPD DAOs | Low |
| 8 | Introduce `PatientService` to remove business logic from controller | `AddPatientController` | High |

</details>

---

<details>
<summary><strong>2. Test-Driven Development (TDD) 🔴 High Priority</strong></summary>

**Recommended first tests for this module:**

- `SearchPatientDao`: test each of the 4 search methods (`searchName`, `searchId`, `searchMobileNo`, `searchAdharNo`) with Mockito-mocked `SessionFactory`
- `PatientPrescriptionDao.prescriptionPrintCount()`: assert it returns `q1.list().size()` (after refactoring the loop)
- `AddPatientController.add()`: test that `dao.add(patient)` is called with the correctly constructed `Patient` entity

</details>

---

<details>
<summary><strong>3. Simple Design 🟡 Medium Priority</strong></summary>

Applying to this module specifically:
- Replace the 16-parameter `add()` method with form binding (`@ModelAttribute`)
- Replace count-via-loop with `COUNT(*)` SQL aggregate
- Remove commented-out code and restore proper exception handling in `AddPatientController.add()`

</details>

---

<details>
<summary><strong>4. Coding Standard 🟡 Medium Priority</strong></summary>

**Issues specific to this module:**
- DAO fields named `dao`, `dao1` provide no semantic information — should be `addPatientDao`, `prescriptionDao`
- Unused import `org.springframework.stereotype.Controller` in `PatientPrescriptionDao.java`
- `int i = 0, j = 0;` should be declared at the point of first use; `j` should be removed entirely

</details>

---

## Doctors & Entities Module

> **Assigned files:** `doctor/*`, `entity/*`, `PersonalInfoController`, `UsersInSystemDao` (~15 files)

---

### 3.1 Clean Code Characteristics

> ⏳ *Pending*

---

### 3.2 Programming Principles Violations

> ⏳ *Pending*

---

### 3.3 XP Practices Recommendations

> ⏳ *Pending*

---

*[⬆ Back to top](#-clean-code--xp-practices)*

---

*CSDT_M — Software Quality and Technical Debt | February 2026*
