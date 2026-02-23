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

> ⏳ *Pending*

---

### 1.2 Programming Principles Violations

> ⏳ *Pending*

---

### 1.3 XP Practices Recommendations

> ⏳ *Pending*

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
