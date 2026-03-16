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
import com.project.entity.Name;
import com.project.entity.Opd;
import com.project.entity.Patient;

/**
 * Unit tests for PatientPrescriptionDao.
 * Principle: FIRST | Pattern: AAA
 */
@ExtendWith(MockitoExtension.class)
class PatientPrescriptionDaoTest {

    @Mock
    private SessionFactory sf;

    @Mock
    private Session session;

    @Mock
    private Query query;

    @Mock
    private LoginDao infoLog;

    @InjectMocks
    private PatientPrescriptionDao dao;

    @BeforeEach
    void setUp() {
        when(sf.getCurrentSession()).thenReturn(session);
    }

    // --- prescriptionPrintCount tests ---

    @Test
    void testPrescriptionPrintCountReturnsCorrectCount() {
        // Arrange
        when(session.createQuery("from Opd where status= :s")).thenReturn(query);
        when(query.setParameter("s", 2)).thenReturn(query);

        List<Opd> opdList = new ArrayList<>();
        opdList.add(new Opd("PID001", "EMP101", 2));
        opdList.add(new Opd("PID002", "EMP101", 2));
        opdList.add(new Opd("PID003", "EMP102", 2));
        when(query.list()).thenReturn(opdList);

        // Act
        int count = dao.prescriptionPrintCount();

        // Assert
        assertEquals(3, count);
    }

    @Test
    void testPrescriptionPrintCountReturnsZeroWhenNoPrescriptions() {
        // Arrange
        when(session.createQuery("from Opd where status= :s")).thenReturn(query);
        when(query.setParameter("s", 2)).thenReturn(query);
        when(query.list()).thenReturn(new ArrayList<>());

        // Act
        int count = dao.prescriptionPrintCount();

        // Assert
        assertEquals(0, count);
    }

    @Test
    void testPrescriptionPrintCountReturnsZeroOnException() {
        // Arrange
        when(session.createQuery("from Opd where status= :s")).thenReturn(query);
        when(query.setParameter("s", 2)).thenReturn(query);
        when(query.list()).thenThrow(new RuntimeException("DB error"));

        // Act
        int count = dao.prescriptionPrintCount();

        // Assert
        assertEquals(0, count);
    }

    // --- getPatientName tests ---

    @Test
    void testGetPatientNameReturnsFormattedFullName() {
        // Arrange
        when(session.createQuery("from Patient where pid= :s")).thenReturn(query);
        when(query.setParameter("s", "PID001")).thenReturn(query);

        Patient patient = mock(Patient.class);
        Name name = new Name("John", "Michael", "Doe");
        when(patient.getName()).thenReturn(name);
        when(query.uniqueResult()).thenReturn(patient);

        // Act
        String result = dao.getPatientName("PID001");

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("John"));
        assertTrue(result.contains("Michael"));
        assertTrue(result.contains("Doe"));
    }

    @Test
    void testGetPatientNameReturnsNullWhenPatientNotFound() {
        // Arrange
        when(session.createQuery("from Patient where pid= :s")).thenReturn(query);
        when(query.setParameter("s", "INVALID")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(null);

        // Act — will throw NPE internally, caught and returns null
        String result = dao.getPatientName("INVALID");

        // Assert
        assertNull(result);
    }

    @Test
    void testGetPatientNameReturnsNullOnException() {
        // Arrange
        when(session.createQuery("from Patient where pid= :s")).thenReturn(query);
        when(query.setParameter("s", "PID001")).thenReturn(query);
        when(query.uniqueResult()).thenThrow(new RuntimeException("DB error"));

        // Act
        String result = dao.getPatientName("PID001");

        // Assert
        assertNull(result);
    }

    // --- getPrescriptionList tests ---

    @Test
    @SuppressWarnings("unchecked")
    void testGetPrescriptionListReturnsCorrectData() {
        // Arrange
        Query query2 = mock(Query.class);

        when(session.createQuery("from Opd where status= :s")).thenReturn(query);
        when(query.setParameter("s", 2)).thenReturn(query);

        Opd opd1 = mock(Opd.class);
        when(opd1.getPid()).thenReturn("PID001");
        when(opd1.getOpdId()).thenReturn(10);

        List<Opd> opdList = new ArrayList<>();
        opdList.add(opd1);
        when(query.list()).thenReturn(opdList);

        // For inner patient query
        when(session.createQuery("from Patient where pid= :i")).thenReturn(query2);
        when(query2.setParameter("i", "PID001")).thenReturn(query2);
        Patient patient = mock(Patient.class);
        when(patient.getName()).thenReturn(new Name("John", "M", "Doe"));
        when(query2.uniqueResult()).thenReturn(patient);

        // Act
        List<String[]> result = dao.getPrescriptionList();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("PID001", result.get(0)[0]);
        assertTrue(result.get(0)[1].contains("John"));
        assertEquals("10", result.get(0)[2]);
    }

    @Test
    void testGetPrescriptionListReturnsNullOnException() {
        // Arrange
        when(session.createQuery("from Opd where status= :s")).thenReturn(query);
        when(query.setParameter("s", 2)).thenReturn(query);
        when(query.list()).thenThrow(new RuntimeException("DB error"));

        // Act
        List<String[]> result = dao.getPrescriptionList();

        // Assert
        assertNull(result);
    }
}
