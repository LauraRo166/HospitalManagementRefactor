package com.project.entity;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.Date;

/**
 * Unit tests for the Patient entity.
 * Principle: FIRST | Pattern: AAA
 */
class PatientTest {

    private Patient createSamplePatient() {
        Name name = new Name("John", "M", "Doe");
        Address address = new Address("123 Main St", "456 Oak Ave");
        return new Patient(name, "1990-05-15", "Male", "john@mail.com",
                9876543210L, 123456789012L, "India", "Maharashtra",
                "Mumbai", address, "O+", "None", "None", "EMP101");
    }

    @Test
    void testPatientIsCreatedWithAllFields() {
        // Arrange & Act
        Patient patient = createSamplePatient();

        // Assert
        assertEquals("John", patient.getName().getFirstName());
        assertEquals("M", patient.getName().getMiddleName());
        assertEquals("Doe", patient.getName().getLastName());
        assertEquals("1990-05-15", patient.getBirthdate());
        assertEquals("Male", patient.getGender());
        assertEquals("john@mail.com", patient.getEmailID());
        assertEquals(9876543210L, patient.getMobileNo());
        assertEquals(123456789012L, patient.getAdharNo());
        assertEquals("India", patient.getCountry());
        assertEquals("Maharashtra", patient.getState());
        assertEquals("Mumbai", patient.getCity());
        assertEquals("O+", patient.getBloodGroup());
        assertEquals("None", patient.getChronicDiseases());
        assertEquals("None", patient.getMedicineAllergy());
        assertEquals("EMP101", patient.getDoctorId());
    }

    @Test
    void testPatientEmbeddedNameIsAccessible() {
        // Arrange & Act
        Patient patient = createSamplePatient();

        // Assert
        assertNotNull(patient.getName());
        assertEquals("John", patient.getName().getFirstName());
        assertEquals("Doe", patient.getName().getLastName());
    }

    @Test
    void testPatientEmbeddedAddressIsAccessible() {
        // Arrange & Act
        Patient patient = createSamplePatient();

        // Assert
        assertNotNull(patient.getAddress());
        assertEquals("123 Main St", patient.getAddress().getResidentialAddress());
        assertEquals("456 Oak Ave", patient.getAddress().getPermanentAddress());
    }

    @Test
    void testPatientRegistrationDateCanBeSet() {
        // Arrange
        Patient patient = createSamplePatient();
        Date now = new Date();

        // Act
        patient.setRegistrationDate(now);

        // Assert
        assertEquals(now, patient.getRegistrationDate());
    }

    @Test
    void testPatientRegistrationDateIsNullByDefault() {
        // Arrange & Act
        Patient patient = createSamplePatient();

        // Assert
        assertNull(patient.getRegistrationDate());
    }

    @Test
    void testPatientDoctorIdCanBeChanged() {
        // Arrange
        Patient patient = createSamplePatient();

        // Act
        patient.setDoctorId("EMP999");

        // Assert
        assertEquals("EMP999", patient.getDoctorId());
    }

    @Test
    void testPatientToStringContainsKeyFields() {
        // Arrange
        Patient patient = createSamplePatient();

        // Act
        String result = patient.toString();

        // Assert
        assertTrue(result.contains("John"));
        assertTrue(result.contains("Male"));
        assertTrue(result.contains("O+"));
        assertTrue(result.contains("EMP101"));
    }
}
