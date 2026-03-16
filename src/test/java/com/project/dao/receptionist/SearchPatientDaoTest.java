package com.project.dao.receptionist;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import com.project.entity.Patient;
import com.project.entity.Name;

/**
 * Unit tests for SearchPatientDao.
 * Principle: FIRST | Pattern: AAA
 */
@ExtendWith(MockitoExtension.class)
class SearchPatientDaoTest {

    @Mock
    private SessionFactory sf;

    @Mock
    private Session session;

    @Mock
    private Query query;

    @Mock
    private LoginDao infoLog;

    @InjectMocks
    private SearchPatientDao dao;

    @BeforeEach
    void setUp() {
        when(sf.getCurrentSession()).thenReturn(session);
    }

    // --- searchName tests ---

    @Test
    void testSearchNameReturnsPatientWhenFound() {
        // Arrange
        when(session.createQuery("from Patient where firstName= :f AND lastName= :l")).thenReturn(query);
        when(query.setParameter("f", "John")).thenReturn(query);
        when(query.setParameter("l", "Doe")).thenReturn(query);

        Patient expected = mock(Patient.class);
        when(query.uniqueResult()).thenReturn(expected);

        // Act
        Patient result = dao.searchName("John", "Doe");

        // Assert
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void testSearchNameReturnsNullWhenNotFound() {
        // Arrange
        when(session.createQuery("from Patient where firstName= :f AND lastName= :l")).thenReturn(query);
        when(query.setParameter("f", "Unknown")).thenReturn(query);
        when(query.setParameter("l", "Person")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(null);

        // Act
        Patient result = dao.searchName("Unknown", "Person");

        // Assert
        assertNull(result);
    }

    @Test
    void testSearchNameReturnsNullOnException() {
        // Arrange
        when(session.createQuery("from Patient where firstName= :f AND lastName= :l")).thenReturn(query);
        when(query.setParameter("f", "John")).thenReturn(query);
        when(query.setParameter("l", "Doe")).thenReturn(query);
        when(query.uniqueResult()).thenThrow(new RuntimeException("DB error"));

        // Act
        Patient result = dao.searchName("John", "Doe");

        // Assert
        assertNull(result);
    }

    // --- searchId tests ---

    @Test
    void testSearchIdReturnsPatientWhenFound() {
        // Arrange
        when(session.createQuery("from Patient where pid= :id")).thenReturn(query);
        when(query.setParameter("id", "PID001")).thenReturn(query);

        Patient expected = mock(Patient.class);
        when(query.uniqueResult()).thenReturn(expected);

        // Act
        Patient result = dao.searchId("PID001");

        // Assert
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void testSearchIdReturnsNullWhenNotFound() {
        // Arrange
        when(session.createQuery("from Patient where pid= :id")).thenReturn(query);
        when(query.setParameter("id", "INVALID")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(null);

        // Act
        Patient result = dao.searchId("INVALID");

        // Assert
        assertNull(result);
    }

    // --- searchMobileNo tests ---

    @Test
    void testSearchMobileNoReturnsPatientWhenFound() {
        // Arrange
        when(session.createQuery("from Patient where mobileNo= :no")).thenReturn(query);
        when(query.setParameter("no", 9876543210L)).thenReturn(query);

        Patient expected = mock(Patient.class);
        when(query.uniqueResult()).thenReturn(expected);

        // Act
        Patient result = dao.searchMobileNo(9876543210L);

        // Assert
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void testSearchMobileNoReturnsNullWhenNotFound() {
        // Arrange
        when(session.createQuery("from Patient where mobileNo= :no")).thenReturn(query);
        when(query.setParameter("no", 0000000000L)).thenReturn(query);
        when(query.uniqueResult()).thenReturn(null);

        // Act
        Patient result = dao.searchMobileNo(0000000000L);

        // Assert
        assertNull(result);
    }

    // --- searchAdharNo tests ---

    @Test
    void testSearchAdharNoReturnsPatientWhenFound() {
        // Arrange
        when(session.createQuery("from Patient where adharNo= :no")).thenReturn(query);
        when(query.setParameter("no", 123456789012L)).thenReturn(query);

        Patient expected = mock(Patient.class);
        when(query.uniqueResult()).thenReturn(expected);

        // Act
        Patient result = dao.searchAdharNo(123456789012L);

        // Assert
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void testSearchAdharNoReturnsNullOnException() {
        // Arrange
        when(session.createQuery("from Patient where adharNo= :no")).thenReturn(query);
        when(query.setParameter("no", 999999999999L)).thenReturn(query);
        when(query.uniqueResult()).thenThrow(new RuntimeException("DB error"));

        // Act
        Patient result = dao.searchAdharNo(999999999999L);

        // Assert
        assertNull(result);
    }

    // --- searchDoctorAssigned tests ---

    @Test
    void testSearchDoctorAssignedReturnsDoctorName() {
        // Arrange
        when(session.createQuery("select name.firstName,name.lastName from Employee where eid= :id")).thenReturn(query);
        when(query.setParameter("id", "EMP101")).thenReturn(query);

        Object[] nameFields = new Object[]{"Dr", "Smith"};
        when(query.uniqueResult()).thenReturn(nameFields);

        // Act
        String result = dao.searchDoctorAssigned("EMP101");

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Dr"));
        assertTrue(result.contains("Smith"));
    }

    @Test
    void testSearchDoctorAssignedReturnsNullOnException() {
        // Arrange
        when(session.createQuery("select name.firstName,name.lastName from Employee where eid= :id")).thenReturn(query);
        when(query.setParameter("id", "INVALID")).thenReturn(query);
        when(query.uniqueResult()).thenThrow(new RuntimeException("Not found"));

        // Act
        String result = dao.searchDoctorAssigned("INVALID");

        // Assert
        assertNull(result);
    }
}
