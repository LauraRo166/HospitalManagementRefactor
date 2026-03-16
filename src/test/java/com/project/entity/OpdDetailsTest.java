package com.project.entity;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the OpdDetails entity.
 * Principle: FIRST | Pattern: AAA
 */
class OpdDetailsTest {

    @Test
    void testOpdDetailsIsCreatedWithAllFields() {
        // Arrange & Act
        OpdDetails details = new OpdDetails(
                "Fever, Headache",
                "Viral Flu",
                "Paracetamol 500mg",
                "Drink warm water",
                "Avoid cold drinks",
                "Blood test",
                "2026-03-20",
                500);

        // Assert
        assertEquals("Fever, Headache", details.getSymptoms());
        assertEquals("Viral Flu", details.getDiagnosis());
        assertEquals("Paracetamol 500mg", details.getMedicinesDose());
        assertEquals("Drink warm water", details.getDos());
        assertEquals("Avoid cold drinks", details.getDonts());
        assertEquals("Blood test", details.getInvestigations());
        assertEquals("2026-03-20", details.getFollowupDate());
        assertEquals(500, details.getFees());
    }

    @Test
    void testOpdDetailsOpdIdCanBeSet() {
        // Arrange
        OpdDetails details = new OpdDetails("Cough", "Cold", "Syrup", "", "", "", "", 200);

        // Act
        details.setOpdid(42);

        // Assert
        assertEquals(42, details.getOpdid());
    }

    @Test
    void testOpdDetailsDefaultConstructorInitializesFieldsToNull() {
        // Arrange & Act
        OpdDetails details = new OpdDetails();

        // Assert
        assertNull(details.getSymptoms());
        assertNull(details.getDiagnosis());
        assertNull(details.getMedicinesDose());
        assertEquals(0, details.getFees());
    }

    @Test
    void testOpdDetailsFeesCanBeUpdated() {
        // Arrange
        OpdDetails details = new OpdDetails("Fever", "Flu", "Med", "", "", "", "", 300);

        // Act
        details.setFees(750);

        // Assert
        assertEquals(750, details.getFees());
    }

    @Test
    void testOpdDetailsToStringContainsKeyFields() {
        // Arrange
        OpdDetails details = new OpdDetails("Headache", "Migraine", "Ibuprofen", "", "", "", "2026-04-01", 1000);

        // Act
        String result = details.toString();

        // Assert
        assertTrue(result.contains("Headache"));
        assertTrue(result.contains("Migraine"));
        assertTrue(result.contains("Ibuprofen"));
        assertTrue(result.contains("1000"));
    }
}
