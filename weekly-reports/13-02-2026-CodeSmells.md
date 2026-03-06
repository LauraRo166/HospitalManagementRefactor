# 🚨 Code Smells & Refactoring Proposals

> **Week 1 — February 13, 2026** | Course: CSDT_M — Software Quality and Technical Debt

---

## 📋 Table of Contents

- [Code Smells Identified](#-code-smells-identified)
- [Refactoring Proposals](#-refactoring-proposals)
- [Implementation Plan](#-implementation-plan)

---

## 🚨 Code Smells Identified

### Analysis Methodology

- Manual source code review
- Anti-pattern detection
- Architecture evaluation
- SOLID principles validation
- Spring MVC & Hibernate best practices

---

<details open>
<summary><strong>1. Tight Coupling</strong></summary>

**Location:** Multiple Controllers and DAOs

**Description:** Controllers are directly coupled to concrete implementations instead of interfaces.

```java
@Autowired
DeleteOpdDao dao1;

@Autowired
OpdDetailsDao dao2;

@Autowired
PatientPrescriptionDao dao3;
```

**Impact:**
- Difficult unit testing (mocks cannot be easily created)
- Violates Dependency Inversion Principle (SOLID)
- Low flexibility and maintainability
- Hard to replace implementations

</details>

---

<details>
<summary><strong>2. God Class</strong></summary>

**Location:** `LoginDao.java`

**Description:** The `LoginDao` class contains multiple unrelated responsibilities (validation, logging, self-injection).

```java
@Autowired
LoginDao infoLog;  // Self-injection — bad practice
```

**Impact:**
- Violates Single Responsibility Principle (SRP)
- Difficult to test and maintain
- Confusing class purpose

</details>

---

<details>
<summary><strong>3. Poor Exception Handling</strong></summary>

**Location:** Multiple Controllers and DAOs

```java
// Incorrect null check and generic exception
if (!e1.getEid().equals(null)) {
    ...
} else {
    throw new Exception();
}
```

```java
// Exception swallowed
catch (Exception e) {
    infoLog.logActivities("in DeleteOpdDao-delete: " + e);
    return 0;
}
```

**Impact:**
- Loss of error information
- Difficult debugging
- Poor production traceability
- Bad user experience

</details>

---

<details>
<summary><strong>4. Commented Out Code</strong></summary>

**Location:** `AddPatientDao.java`, `LoginDao.java`

```java
// try {
//     ...
// } catch (Exception e) {
//     return false;
// }
```

**Impact:**
- Code clutter
- Confusion for developers
- Version control should manage history, not source files

</details>

---

<details>
<summary><strong>5. Hardcoded Values / Magic Numbers</strong></summary>

**Location:** Multiple classes

```java
q1.setParameter("s", 0);
q1.setParameter("s", 1);

if (i == 1) { ... }
```

**Impact:**
- Not self-documenting
- Error-prone
- Difficult to maintain

</details>

---

<details>
<summary><strong>6. Missing DTOs</strong></summary>

**Location:** Entire system

**Description:** JPA entities are exposed directly to the presentation layer.

**Impact:**
- Possible `LazyInitializationException`
- Tight coupling between layers
- Exposure of sensitive data
- Difficult API versioning

</details>

---

<details>
<summary><strong>7. Lack of Proper Logging</strong></summary>

```java
public void logActivities(String s) {
    // System.out.println("@"+s);
}
```

**Impact:**
- No production logs
- No log levels (INFO, DEBUG, ERROR)
- Impossible debugging
- No external configuration

</details>

---

<details>
<summary><strong>8. Inconsistent Null Checking</strong></summary>

```java
if (!e1.getEid().equals(null))  // Incorrect
if (!patients.equals(null))    // Incorrect
```

**Impact:**
- Possible `NullPointerException`
- Confusing inverted logic

</details>

---

<details>
<summary><strong>9. Primitive DTOs / Array Misuse</strong></summary>

```java
String[] temp = new String[3];
temp[0] = ...
temp[1] = ...
temp[2] = ...
```

**Impact:**
- Not type-safe
- Hard to understand
- Index-based errors

</details>

---

<details>
<summary><strong>10. Potential N+1 Query Problem</strong></summary>

```java
for (Opd opd : l1) {
    temp[1] = dao2.searchDoctorAssigned(did);
}
```

**Impact:**
- Performance degradation
- Multiple unnecessary DB queries

</details>

---

<details>
<summary><strong>11. Unused Imports and Variables</strong></summary>

```java
import com.project.entity.Login;

int i = 0, j = 0;
```

**Impact:**
- Code clutter
- Reduced readability
- Poor code hygiene

</details>

---

<details>
<summary><strong>12. Missing Unit Tests</strong></summary>

**Observation:** No test files were found in the repository.

**Impact:**
- No quality guarantee
- Risky refactoring
- Hard to detect regressions

</details>

---

*[⬆ Back to top](#-code-smells--refactoring-proposals)*

---

## 🔧 Refactoring Proposals

**Planned Refactoring Techniques:**
- Extract Class
- Introduce Parameter Object
- Replace Conditional with Polymorphism
- Repository Pattern
- DTO Pattern
- Dependency Injection
- Global Exception Handling
- Bean Validation
- Query Optimization

---

<details open>
<summary><strong>1. Extract Interface</strong></summary>

**Apply to:** All DAOs

**Before:**
```java
@Autowired
DeleteOpdDao dao1;
```

**After:**
```java
public interface OpdService {
    int delete(String pid);
}

@Autowired
private OpdService opdService;
```

**Benefits:**
- Reduces coupling
- Improves testability with mocks
- Complies with Dependency Inversion Principle
- Encourages clean architecture
- Improves flexibility for future implementations

</details>

---

<details>
<summary><strong>2. Replace Magic Numbers with Constants</strong></summary>

**Before:**
```java
q1.setParameter("s", 0);
if(i == 1) { }
```

**After:**
```java
public class OpdStatus {
    public static final int DONE = 0;
    public static final int PENDING = 1;
    public static final int PRINTING = 2;
}

q1.setParameter("s", OpdStatus.DONE);
if(i == OperationResult.SUCCESS) { }
```

**Benefits:**
- Self-documenting code
- Improved readability
- Reduced risk of logical errors
- Centralized status management
- Easier future modifications

</details>

---

<details>
<summary><strong>3. Extract Method</strong></summary>

**Apply to:** Long DAO methods

**Before (`AddPatientDao.java`):**
```java
public boolean add(Patient p1) {
    // 30+ lines of code
    // Save patient
    // Increment ID
    // Multiple responsibilities
}
```

**After:**
```java
public boolean add(Patient p1) {
    savePatient(p1);
    incrementPatientId();
    return true;
}

private void savePatient(Patient p1) { /* ... */ }
private void incrementPatientId() { /* ... */ }
```

**Benefits:**
- Improved readability
- Single Responsibility compliance
- Better unit testing
- Easier debugging
- Encourages code reuse

</details>

---

<details>
<summary><strong>4. Introduce Service Layer</strong></summary>

**Before:**
```java
@Controller
public class DeleteOpdController {

    @Autowired DeleteOpdDao dao1;
    @Autowired OpdDetailsDao dao2;

    public ModelAndView delete(String pid) {
        dao1.delete(pid);
        dao2.opdQueue();
    }
}
```

**After:**
```java
@Controller
public class OpdController {

    @Autowired
    private OpdService opdService;

    public ModelAndView delete(String pid) {
        opdService.deleteOpd(pid);
    }
}

@Service
public class OpdServiceImpl implements OpdService {
    // Business logic here
}
```

**Benefits:**
- Clear separation of concerns
- Better layering (Controller → Service → DAO)
- Centralized business logic
- Improved maintainability
- Easier testing of business logic

</details>

---

<details>
<summary><strong>5. Replace Conditional Logic with Strategy Pattern</strong></summary>

**Apply to:** OPD state logic

**Description:** Replace large conditional blocks that depend on status with strategy classes.

```java
public interface OpdStateStrategy {
    void process(Opd opd);
}

public class PendingState implements OpdStateStrategy {
    public void process(Opd opd) {
        // Logic for pending state
    }
}
```

**Benefits:**
- Eliminates complex conditional blocks
- Open/Closed Principle compliance
- Easier extension for new states
- Cleaner and more scalable design
- Improved maintainability

</details>

---

<details>
<summary><strong>6. Introduce DTOs</strong></summary>

**Before:**
```java
mv.addObject("employee", e1); // Direct entity exposure
```

**After:**
```java
EmployeeDTO dto = employeeMapper.toDTO(e1);
mv.addObject("employee", dto);
```

**Benefits:**
- Prevents `LazyInitializationException`
- Avoids exposing sensitive data
- Better API versioning support
- Clear separation between layers
- Improved security and encapsulation

</details>

---

<details>
<summary><strong>7. Replace Custom Logging with SLF4J / Logback</strong></summary>

**Before:**
```java
infoLog.logActivities("in DeleteOpdDao-delete: got= " + pid);
```

**After:**
```java
private static final Logger logger =
    LoggerFactory.getLogger(DeleteOpdDao.class);

logger.debug("Deleting OPD with pid: {}", pid);
```

**Benefits:**
- Structured logging
- Log levels support (INFO, DEBUG, ERROR)
- External configuration capability
- Production-ready logging
- Better monitoring and observability

</details>

---

<details>
<summary><strong>8. Null Object Pattern</strong></summary>

**Before:**
```java
if(!e1.getEid().equals(null)) { }
```

**After:**
```java
if(e1 != null && StringUtils.isNotEmpty(e1.getEid())) { }
// Or use Optional<Employee>
```

**Benefits:**
- Prevents `NullPointerException`
- Cleaner validation logic
- Improved robustness
- More readable conditions
- Safer null handling strategy

</details>

---

<details>
<summary><strong>9. Repository Pattern</strong></summary>

**Description:** Introduce a repository layer between services and data access logic.

```java
public interface PatientRepository extends JpaRepository<Patient, Long> {
}
```

**Benefits:**
- Encapsulates persistence logic
- Improves abstraction
- Reduces boilerplate DAO code
- Better integration with Spring Data
- Cleaner separation of layers

</details>

---

<details>
<summary><strong>10. Global Exception Handler</strong></summary>

```java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ModelAndView handleNotFound(EntityNotFoundException ex) {
        // Centralized exception handling
    }
}
```

**Benefits:**
- Centralized error handling
- Cleaner controllers
- Consistent error responses
- Improved maintainability
- Better user experience

</details>

---

*[⬆ Back to top](#-code-smells--refactoring-proposals)*

---

## ✅ Implementation Plan

### Phase 1 — Low Risk
- Logging integration
- Constants classes
- Bean validation
- Code documentation

### Phase 2 — Service Refactoring
- Extract long methods
- DTO implementation
- Mapper creation

### Phase 3 — Architecture Improvements
- Repository pattern
- Global exception handlers
- Hibernate optimization

### Phase 4 — Testing & Security
- Unit tests
- Integration tests
- Security review
- External configuration

---

*[⬆ Back to top](#-code-smells--refactoring-proposals)*

---

*CSDT_M — Software Quality and Technical Debt | February 2026*
