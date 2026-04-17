package com.project.dao.opd;

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

/**
 * Unit tests for DeleteOpdDao.
 * Principle: FIRST | Pattern: AAA
 */
@ExtendWith(MockitoExtension.class)
class DeleteOpdDaoTest {

    @Mock
    private SessionFactory sf;

    @Mock
    private Session session;

    @Mock
    private Query query;

    @Mock
    private LoginDao infoLog;

    @InjectMocks
    private DeleteOpdDao dao;

    @BeforeEach
    void setUp() {
        when(sf.getCurrentSession()).thenReturn(session);
    }

    // --- delete tests ---

    @Test
    void testDeleteReturnsOneWhenPatientDeletedSuccessfully() {
        // Arrange
        when(session.createQuery("delete from Opd where pid= :id AND status=1")).thenReturn(query);
        when(query.setParameter("id", "PID001")).thenReturn(query);
        when(query.executeUpdate()).thenReturn(1);

        // Act
        int result = dao.delete("PID001");

        // Assert
        assertEquals(1, result);
        verify(query).executeUpdate();
    }

    @Test
    void testDeleteReturnsZeroWhenNoMatchingPatient() {
        // Arrange
        when(session.createQuery("delete from Opd where pid= :id AND status=1")).thenReturn(query);
        when(query.setParameter("id", "INVALID")).thenReturn(query);
        when(query.executeUpdate()).thenReturn(0);

        // Act
        int result = dao.delete("INVALID");

        // Assert
        assertEquals(0, result);
    }

    @Test
    void testDeleteReturnsZeroOnException() {
        // Arrange
        when(session.createQuery("delete from Opd where pid= :id AND status=1")).thenReturn(query);
        when(query.setParameter("id", "PID001")).thenReturn(query);
        when(query.executeUpdate()).thenThrow(new RuntimeException("DB error"));

        // Act
        int result = dao.delete("PID001");

        // Assert
        assertEquals(0, result);
    }

    // --- prescriptionPrint tests ---

    @Test
    void testPrescriptionPrintUpdatesStatusToTwo() {
        // Arrange
        when(session.createQuery("update Opd set status=2 where pid= :id AND status=1")).thenReturn(query);
        when(query.setParameter("id", "PID001")).thenReturn(query);
        when(query.executeUpdate()).thenReturn(1);

        // Act — no return value, verify interaction
        dao.prescriptionPrint("PID001");

        // Assert
        verify(query).setParameter("id", "PID001");
        verify(query).executeUpdate();
    }

    // --- prescriptionPrintDone tests ---

    @Test
    void testPrescriptionPrintDoneUpdatesStatusToZero() {
        // Arrange
        when(session.createQuery("update Opd set status=0 where pid= :id AND status=2")).thenReturn(query);
        when(query.setParameter("id", "PID001")).thenReturn(query);
        when(query.executeUpdate()).thenReturn(1);

        // Act
        int result = dao.prescriptionPrintDone("PID001");

        // Assert
        assertEquals(1, result);
    }

    @Test
    void testPrescriptionPrintDoneReturnsZeroOnException() {
        // Arrange
        when(session.createQuery("update Opd set status=0 where pid= :id AND status=2")).thenReturn(query);
        when(query.setParameter("id", "PID001")).thenReturn(query);
        when(query.executeUpdate()).thenThrow(new RuntimeException("DB error"));

        // Act
        int result = dao.prescriptionPrintDone("PID001");

        // Assert
        assertEquals(0, result);
    }

    @Test
    void testDeleteOnlyAffectsPendingStatusRecords() {
        // Arrange — verify the query uses status=1 (PENDING)
        when(session.createQuery("delete from Opd where pid= :id AND status=1")).thenReturn(query);
        when(query.setParameter("id", "PID001")).thenReturn(query);
        when(query.executeUpdate()).thenReturn(1);

        // Act
        dao.delete("PID001");

        // Assert — the HQL string includes "status=1", confirming only pending records
        verify(session).createQuery("delete from Opd where pid= :id AND status=1");
    }
}
