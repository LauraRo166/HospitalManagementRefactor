package com.project.dao.receptionist;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.project.dao.LoginDao;
import com.project.entity.Employee;
import com.project.entity.IdGenerate;
import com.project.entity.Name;
import com.project.entity.Patient;
import com.project.entity.Address;

/**
 * Unit tests for AddPatientDao.
 * Principle: FIRST | Pattern: AAA
 * Uses Mockito to mock SessionFactory, Session, and Query to avoid database dependency.
 */
@ExtendWith(MockitoExtension.class)
class AddPatientDaoTest {

    @Mock
    private SessionFactory sf;

    @Mock
    private Session session;

    @Mock
    private Query query;

    @Mock
    private LoginDao infoLog;

    @InjectMocks
    private AddPatientDao dao;

    @BeforeEach
    void setUp() {
        lenient().when(sf.getCurrentSession()).thenReturn(session);
    }

    @Test
    void testAddPatientSavesPatientToSession() {
        // Arrange
        Name name = new Name("John", "M", "Doe");
        Address address = new Address("123 Main St", "456 Oak Ave");
        Patient patient = new Patient(name, "1990-01-01", "Male", "john@mail.com",
                9876543210L, 123456789012L, "India", "State", "City",
                address, "O+", "None", "None", "EMP101");

        when(session.createQuery(" from IdGenerate")).thenReturn(query);
        IdGenerate idGen = new IdGenerate();
        when(query.uniqueResult()).thenReturn(idGen);
        when(session.createQuery("update IdGenerate set pid= :i")).thenReturn(query);
        when(query.setParameter("i", 1)).thenReturn(query);
        when(query.executeUpdate()).thenReturn(1);

        // Act
        boolean result = dao.add(patient);

        // Assert
        assertTrue(result);
        verify(session).save(patient);
        assertNotNull(patient.getRegistrationDate());
    }

    @Test
    void testAddPatientSetsRegistrationDate() {
        // Arrange
        Patient patient = new Patient(new Name("Jane", "", "Doe"), "2000-01-01", "Female",
                "jane@mail.com", 1234567890L, 999999999999L, "India", "State",
                "City", new Address("St1", "St2"), "A+", "None", "None", "EMP101");

        when(session.createQuery(" from IdGenerate")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(new IdGenerate());
        when(session.createQuery("update IdGenerate set pid= :i")).thenReturn(query);
        when(query.setParameter(eq("i"), anyInt())).thenReturn(query);
        when(query.executeUpdate()).thenReturn(1);

        // Act
        dao.add(patient);

        // Assert
        assertNotNull(patient.getRegistrationDate());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testGetDoctorsReturnsListOfDoctorArrays() {
        // Arrange
        when(session.createQuery(" from Employee where role= :r AND status=:s")).thenReturn(query);
        when(query.setParameter("r", "doctor")).thenReturn(query);
        when(query.setParameter("s", 1)).thenReturn(query);

        List<Employee> doctors = new ArrayList<>();
        Employee doc = mock(Employee.class);
        Name docName = new Name("Dr", "A", "Smith");
        when(doc.getEid()).thenReturn("EMP101");
        when(doc.getName()).thenReturn(docName);
        doctors.add(doc);

        when(query.list()).thenReturn(doctors);

        // Act
        List<String[]> result = dao.getDoctors();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("EMP101", result.get(0)[0]);
        assertEquals("Dr", result.get(0)[1]);
        assertEquals("A", result.get(0)[2]);
        assertEquals("Smith", result.get(0)[3]);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testGetDoctorsReturnsEmptyListWhenNoDoctors() {
        // Arrange
        when(session.createQuery(" from Employee where role= :r AND status=:s")).thenReturn(query);
        when(query.setParameter("r", "doctor")).thenReturn(query);
        when(query.setParameter("s", 1)).thenReturn(query);
        when(query.list()).thenReturn(new ArrayList<>());

        // Act
        List<String[]> result = dao.getDoctors();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
