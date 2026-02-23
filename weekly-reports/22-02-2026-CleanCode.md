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

> ⏳ _Pending_

---

### 1.2 Programming Principles Violations

> ⏳ _Pending_

---

### 1.3 XP Practices Recommendations

> ⏳ _Pending_

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

| Duplication                              | Locations                                                                                                                      |
| ---------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------ |
| Full-name string concatenation           | `PatientPrescriptionDao.getPrescriptionList()`, `PatientPrescriptionDao.getPatientName()`, `OpdDetailsDao.searchPatientName()` |
| `searchDoctorAssigned()` HQL + loop      | `SearchPatientDao`, `OpdDetailsDao` (identical implementation)                                                                 |
| Magic number `status=2` (printing)       | `PatientPrescriptionDao`, `DeleteOpdDao`                                                                                       |
| `LoginDao infoLog` injection boilerplate | Every DAO and Controller (8+ classes in this module)                                                                           |

**Conclusion:** This module has the highest concentration of DRY violations in the project. The doctor name resolution logic alone is copied verbatim in two separate DAOs.

</details>

---

<details>
<summary><strong>SOLID — S: Single Responsibility Principle ❌ VIOLATED</strong></summary>

| Class                    | Responsibilities Found                                                                             |
| ------------------------ | -------------------------------------------------------------------------------------------------- |
| `AddPatientController`   | Request parsing, entity building, DAO orchestration, view resolution, prescription count retrieval |
| `PatientPrescriptionDao` | Prescription listing, prescription counting, patient name lookup                                   |
| `SearchPatientDao`       | Patient search by 4 criteria + doctor name resolution                                              |
| `DeleteOpdDao`           | OPD deletion + prescription print state management + prescription-done confirmation                |

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

| #   | Refactoring                                                         | Target                                    | Risk   |
| --- | ------------------------------------------------------------------- | ----------------------------------------- | ------ |
| 1   | Introduce `PatientRegistrationDTO` to replace 16-parameter method   | `AddPatientController.add()`              | Low    |
| 2   | Replace `String[]` with `DoctorSummaryDTO`                          | `AddPatientDao.getDoctors()`              | Low    |
| 3   | Extract `PersonNameUtils.fullName()` helper                         | `PatientPrescriptionDao`, `OpdDetailsDao` | Low    |
| 4   | Merge duplicate `searchDoctorAssigned()` into one shared DAO        | `SearchPatientDao`, `OpdDetailsDao`       | Medium |
| 5   | Replace manual ID counter with `@GeneratedValue`                    | `AddPatientDao.add()`                     | Medium |
| 6   | Replace N+1 in `getPrescriptionList()` with a JOIN query            | `PatientPrescriptionDao`                  | Medium |
| 7   | Introduce `OpdStatus` enum and replace all magic `status=0/1/2`     | All OPD DAOs                              | Low    |
| 8   | Introduce `PatientService` to remove business logic from controller | `AddPatientController`                    | High   |

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

> ---

#### ✅ Good Practices — Entities

<details open>
<summary><strong>Naming Conventions</strong></summary>

We found that class names are clear and correct, following the proper PascalCase format for the project context, with the exception of \_OpdRecord.java. Not only class names but each class has attributes that are correctly named using camelCase. Additionally, function names are totally understandable.

**Exception:** `_OpdRecord.java` violates the convention by starting with underscore.

</details>

---

<details>
<summary><strong>Format and Structure</strong></summary>

Although it's a small detail, all classes use the same indentation and follow an order at the level of class annotations, attributes, constructors, getters and setters, among others.

All entities maintain:

- Consistent indentation (4 spaces)
- Logical order: annotations → attributes → constructors → getters/setters → toString()
- Uniform vertical spacing between methods

</details>

---

<details>
<summary><strong>Small and Simple Methods</strong></summary>

In general we observed that the entity methods are all composed only of getters and setters. Therefore they comply with concise and quite simple methods. This also includes constructors.

</details>

---

<details>
<summary><strong>Correct Use of Value Objects</strong></summary>

`Name` and `Address` as `@Embeddable` avoid duplication and maintain cohesion.

</details>

---

#### ✅ Good Practices — Doctor Module

<details open>
<summary><strong>Naming Conventions at Method Level</strong></summary>

Method names are descriptive and correctly follow camelCase: `showHistoryList()`, `dopdQueue()`, `addPatientCase()`, `searchDoctorAssigned()`. These names clearly communicate their purpose without needing additional documentation.

</details>

---

<details>
<summary><strong>Separation of Responsibilities Between Layers</strong></summary>

There is a clear division between controllers and DAOs. Controllers handle HTTP and views, while DAOs take care of database operations. This separation facilitates maintenance and independent testing of each layer.

</details>

---

<details>
<summary><strong>Use of Spring Annotations</strong></summary>

All classes correctly use `@Controller`, `@Component`, `@Autowired`, `@Transactional`, and `@RequestMapping`. Transactions are well delimited in data access methods.

</details>

---

<details>
<summary><strong>Dependency Injection</strong></summary>

Field injection is consistently used with `@Autowired`, avoiding direct coupling between classes and facilitating testing with mocks.

</details>

---

#### ❌ Bad Practices — Entities

<details open>
<summary><strong>01. Absence of Validations (Bean Validation)</strong></summary>

Entities allow saving invalid data without any restrictions. Patient and Employee accept malformed emails, negative phone numbers or with incorrect format, and identity documents without any validation. OpdDetails allows negative medical fees. This lack of validation compromises the integrity of all system data.

**Examples:**

```java
// Patient.java (line 32)
private String emailID;  // We found it doesn't validate that the email has an "@" or restrict that there aren't "@%@" but perfectly there can be several errors because of this

// Employee.java (line 40)
private long mobileNo;   // Accepts negative numbers

// OpdDetails.java (line 16)
int fees;                // This also doesn't validate that negative numbers aren't accepted
```

</details>

---

<details>
<summary><strong>02. Duplicated Code (DRY)</strong></summary>

Patient.java and Employee.java repeat exactly the same attributes: emailID, mobileNo, adharNo, country, state, city and address. This duplication makes any change require modifying both classes, increasing the risk of inconsistencies.

**Duplicated attributes in both classes:**

```java
private String emailID;
private long mobileNo;
private long adharNo;
private String country;
private String state;
private String city;
private Address address;
```

</details>

---

<details>
<summary><strong>03. Obvious or Contradictory Comments</strong></summary>

Comments don't add value. \_OpdRecord explains in a comment with spelling errors why it's not an entity, when the class name should make it clear. Multiple attributes have comment "//optional" but the code doesn't reflect they are optional.

**Examples:**

```java
// _OpdRecord.java (line 3)
//Not annotated using @Entity bcoz we dont want to store its data in database

// Patient.java
private String specialization;  //optional
// Without @Nullable, the comment contradicts the implementation
```

</details>

---

<details>
<summary><strong>04. Single Responsibility (SRP) Poorly Implemented</strong></summary>

We found that in Opd.java mixes two responsibilities, the first is being a persistence entity and the second is defining system state constants. And another we observed is that in Login.java, the class combines authentication data with being a primary key. IdGenerate confuses its purpose between database entity and in-memory counter.

</details>

---

#### ❌ Bad Practices — Doctor Module

<details open>
<summary><strong>01. Inconsistent Class Names</strong></summary>

We found two classes that don't follow Java parameters for being a class, since they start with lowercase: patientObservePrescribeController and patientObservePrescribeDao. This breaks code consistency. They should be PatientObservePrescribeController and PatientObservePrescribeDao.

</details>

---

<details>
<summary><strong>02. Methods Too Long with Many Responsibilities</strong></summary>

PatientHistoryController.showHistoryList() has 31 lines and we found five different responsibilities: logging, session retrieval, database query, result validation, and view construction. The method PatientDopdDetailsController.view() also mixes around six responsibilities: logging, patient search, doctor search, validation, session management, and response construction.

**Example of method with multiple responsibilities:**

```java
// PatientHistoryController.java (lines 29-59)
public ModelAndView showHistoryList(HttpServletRequest request) {
    // Responsibility 1: Logging
    infoLog.logActivities("in PatientHistoryController-showHistoryList:");

    // Responsibility 2: Get session
    HttpSession session= request.getSession();
    String pid=(String) session.getAttribute("currentPatientId");

    // Responsibility 3: Query database
    List historyList=dao.showHistoryList(pid);

    // Responsibility 4: Validate result
    if(! historyList.equals(null))

    // Responsibility 5: Build view
    ModelAndView mv= new ModelAndView();
    mv.addObject("historyList",historyList);
    // ...
}
```

</details>

---

<details>
<summary><strong>03. Excess of Parameters in Methods</strong></summary>

The method addPatientCase() in patientObservePrescribeController receives 9 parameters: symptoms, diagnosis, medicinesDose, dos, donts, investigations, followupDate, fees, request. This makes the method difficult to invoke, maintain and test. We consider it should receive a DTO object that encapsulates all this data.

**Here's the example:**

```java
// patientObservePrescribeController.java (line 45)
public ModelAndView addPatientCase(
    @RequestParam("symptoms")String symptoms,
    @RequestParam("diagnosis")String diagnosis,
    @RequestParam("medicinesDose")String medicinesDose,
    @RequestParam("dos")String dos,
    @RequestParam("donts")String donts,
    @RequestParam("investigations")String investigations,
    @RequestParam("followupDate")String followupDate,
    @RequestParam("fees")int fees,
    HttpServletRequest request)
```

</details>

---

<details>
<summary><strong>04. Incorrect Error Handling</strong></summary>

Validations with `.equals(null)` are used which always return false because null is not an object. In PatientHistoryController line 41 and DopdDetailsController line 40 it validates `if(! historyList.equals(null))` when it should be `if(historyList != null)`. Additionally, generic exceptions are caught with `catch(Exception e)` without discriminating between different error types, which hides real problems.

**Examples of incorrect validation:**

```java
// PatientHistoryController.java (line 41)
if(! historyList.equals(null))  // Always false, null has no equals method

// DopdDetailsController.java (line 40)
if(! patients.equals(null))  // The same error

// Should be:
if(historyList != null && !historyList.isEmpty())
```

</details>

---

<details>
<summary><strong>05. Return Null Instead of Optional or Exceptions</strong></summary>

All DAOs return null when an error occurs: PatientHistoryDao lines 66 and 87, DopdDetailsDao line 53, patientObservePrescribeDao line 58. This forces controllers to constantly validate null and can cause cascading NullPointerException if any validation is forgotten.

**Pattern repeated in all DAOs:**

```java
// PatientHistoryDao.java (lines 63-67)
catch(Exception e) {
    infoLog.logActivities("in PatientHistoryDao-showHistoryList: "+e);
    return null;  // Hides the error
}

// DopdDetailsDao.java (lines 50-54)
catch(Exception e) {
    infoLog.logActivities("in DopdDetailsDao-dopdQueue: "+e);
    return null;  // Same pattern
}
```

</details>

---

<details>
<summary><strong>06. Variables with Cryptic Names</strong></summary>

DAOs use non-descriptive variable names: `q1`, `q2` for queries, `l1` for lists, `temp` for temporary arrays, `p1` for patients. These names could be quite confusing and don't communicate their purpose, thus demonstrating they are not clear at all.

**Examples:**

```java
// PatientHistoryDao.java (lines 41, 47, 52)
Query q1= session.createQuery("...");  // ❌ Query of what?
List l1=(List) q1.list();   // ❌ List of what?
String[] temp= new String[3];          // ❌ Temporary for what?
```

</details>

---

<details>
<summary><strong>07. Duplicated Code Between Controllers</strong></summary>

The view() methods in PatientDopdDetailsController (lines 27-60 and 63-96) are almost completely duplicated. Both search patient, search assigned doctor, validate, save to session and build the same view. The only difference is how they obtain the pid: one by POST parameter and another from session.

**Duplication of 40+ lines:**

```java
// Method 1 (lines 27-60): receives pid by parameter
Patient p1=dao.searchId(pid);
String doctorAssigned=dao.searchDoctorAssigned(p1.getDoctorId());
if(!(p1.getPid().equals(null)) && !(doctorAssigned.equals(null))) {
    session.setAttribute("currentPatientId", p1.getPid());
    // ... view construction
}

// Method 2 (lines 63-96): gets pid from session
String pid=(String)session.getAttribute("currentPatientId");
Patient p1=dao.searchId(pid);
String doctorAssigned=dao.searchDoctorAssigned(p1.getDoctorId());
if(!(p1.getPid().equals(null)) && !(doctorAssigned.equals(null))) {
    // ... SAME view construction
}
```

</details>

---

### 3.2 Programming Principles Violations

---

#### Entities

<details open>
<summary><strong>Single Responsibility Principle (SRP)</strong></summary>

Entities are assuming multiple responsibilities at the same time. Opd.java is both the persistence entity and the one that groups business constants. Login.java mixes authentication with system identity. IdGenerate confuses configuration with persistence. Each class should have a single reason to change.

</details>

---

<details>
<summary><strong>Open/Closed Principle (OCP)</strong></summary>

The states of Opd are hardcoded as numeric constants, and this would be an impediment to add new states to the existing ones, since it would be necessary to modify the code. An enum OpdStatus would have made extension possible without modifying the code.

</details>

---

<details>
<summary><strong>Don't Repeat Yourself (DRY)</strong></summary>

Patient and Employee duplicate 7 identical attributes. Changing the person structure would require modifying both classes. A Person base class would have avoided this duplication.

</details>

---

#### Doctor Module

<details open>
<summary><strong>Single Responsibility Principle (SRP)</strong></summary>

Controllers assume too many responsibilities simultaneously. PatientHistoryController.showHistoryList() handles logging, HTTP sessions, database queries, result validation, and view construction. PatientDopdDetailsController.view() additionally manages multiple searches and data transformations. Each method should delegate these responsibilities to specialized services.

</details>

---

<details>
<summary><strong>Don't Repeat Yourself (DRY)</strong></summary>

PatientDopdDetailsController has two almost identical methods (view and viewData) that duplicate 40+ lines of code. DAOs duplicate the error handling pattern with return null in each method. Magic numbers 0 and 1 for statuses repeat in multiple files without using the constants defined in Opd.

</details>

---

<details>
<summary><strong>Open/Closed Principle (OCP)</strong></summary>

Methods are coupled to concrete implementations. Changing the logging mechanism, session source, or response format requires modifying each controller. No abstraction exists to extend functionality without modifying existing code.

</details>

---

<details>
<summary><strong>KISS (Keep It Simple, Stupid)</strong></summary>

The validation `if(! patients.equals(null))` complicates unnecessarily something simple. The method with 9 parameters could be simplified with a DTO. Nested validations and generic exception handling add complexity without benefit.

</details>

---

### 3.3 XP Practices Recommendations

---

#### Entities

<details open>
<summary><strong>XP Practices for Entities</strong></summary>

| PRÁCTICA XP                       | BENEFICIO EN ENTIDADES                                                                  |
| --------------------------------- | --------------------------------------------------------------------------------------- |
| **Test-Driven Development (TDD)** | Validaciones garantizadas<br>Constraints verificados<br>Relaciones JPA probadas         |
| **Simple Design**                 | Eliminar IdGenerate<br>Usar secuencias BD estándar<br>\_OpdRecord → OpdRecordDTO        |
| **Refactoring Continuo**          | Extract Superclass (Person)<br>Replace Type Code with Enum<br>Introduce Bean Validation |
| **Collective Code Ownership**     | Convenciones documentadas<br>equals()/hashCode() estándar<br>Validaciones consistentes  |

</details>

---

#### Doctor Module

<details open>
<summary><strong>XP Practices for Doctor Module</strong></summary>

| PRÁCTICA XP                       | BENEFICIO EN MÓDULO DOCTORES                                                                                   |
| --------------------------------- | -------------------------------------------------------------------------------------------------------------- |
| **Test-Driven Development (TDD)** | Tests unitarios para lógica de negocio<br>Tests de integración para DAOs<br>Mocks para controllers             |
| **Simple Design**                 | Eliminar logging manual invasivo<br>Usar Optional en lugar de null<br>Reemplazar magic numbers con constantes  |
| **Refactoring Continuo**          | Extract Method para métodos largos<br>Extract Service Layer<br>Replace Parameter Object para 9 parámetros      |
| **Collective Code Ownership**     | Convenciones de naming documentadas<br>Manejo de errores estandarizado<br>Logging con AOP en lugar de manual   |
| **Continuous Integration**        | Análisis estático de código (SonarQube)<br>Cobertura de tests mínima<br>Validación de convenciones en pipeline |

</details>

---

_[⬆ Back to top](#-clean-code--xp-practices)_

---

_CSDT_M — Software Quality and Technical Debt | February 2026_
