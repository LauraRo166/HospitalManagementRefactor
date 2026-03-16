package com.project.entity;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.Date;

/**
 * Unit tests for the Opd entity.
 * Principle: FIRST | Pattern: AAA
 */
class OpdTest {

    @Test
    void testOpdIsCreatedWithAllFields() {
        // Arrange & Act
        Opd opd = new Opd("PID001", "EMP101", 1);

        // Assert
        assertEquals("PID001", opd.getPid());
        assertEquals("EMP101", opd.getDoctorId());
        assertEquals(1, opd.getStatus());
    }

    @Test
    void testOpdStatusPendingConstantEqualsOne() {
        // Assert
        assertEquals(1, Opd.PENDING);
    }

    @Test
    void testOpdStatusDoneConstantEqualsZero() {
        // Assert
        assertEquals(0, Opd.DONE);
    }

    @Test
    void testOpdStatusCanBeChanged() {
        // Arrange
        Opd opd = new Opd("PID001", "EMP101", Opd.PENDING);

        // Act
        opd.setStatus(Opd.DONE);

        // Assert
        assertEquals(Opd.DONE, opd.getStatus());
    }

    @Test
    void testOpdVisitDateCanBeSet() {
        // Arrange
        Opd opd = new Opd("PID001", "EMP101", 1);
        Date now = new Date();

        // Act
        opd.setVisitDate(now);

        // Assert
        assertEquals(now, opd.getVisitDate());
    }

    @Test
    void testOpdVisitDateIsNullByDefault() {
        // Arrange & Act
        Opd opd = new Opd("PID001", "EMP101", 1);

        // Assert
        assertNull(opd.getVisitDate());
    }

    @Test
    void testOpdToStringContainsKeyFields() {
        // Arrange
        Opd opd = new Opd("PID001", "EMP101", 1);

        // Act
        String result = opd.toString();

        // Assert
        assertTrue(result.contains("PID001"));
        assertTrue(result.contains("EMP101"));
    }

    @Test
    void testOpdStatusTransitionFromPendingToPrinting() {
        // Arrange
        Opd opd = new Opd("PID001", "EMP101", Opd.PENDING);

        // Act — simulate prescription ready (status=2 per convention)
        opd.setStatus(2);

        // Assert
        assertEquals(2, opd.getStatus());
    }

    @Test
    void testOpdStatusTransitionFromPrintingToDone() {
        // Arrange
        Opd opd = new Opd("PID001", "EMP101", 2);

        // Act
        opd.setStatus(Opd.DONE);

        // Assert
        assertEquals(Opd.DONE, opd.getStatus());
    }
}
