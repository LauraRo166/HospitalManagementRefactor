package com.project.entity;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Login entity.
 * Principle: FIRST | Pattern: AAA
 */
class LoginTest {

    @Test
    void testLoginIsCreatedWithAllFields() {
        // Arrange & Act
        Login login = new Login("EMP101", "doctor", "emp101user", "pass1234");

        // Assert
        assertEquals("EMP101", login.getId());
        assertEquals("doctor", login.getRole());
        assertEquals("emp101user", login.getUsername());
        assertEquals("pass1234", login.getPassword());
    }

    @Test
    void testLoginIsCreatedWithNullId() {
        // Arrange & Act
        Login login = new Login(null, "administrator", "root123", "root1234");

        // Assert
        assertNull(login.getId());
        assertEquals("administrator", login.getRole());
        assertEquals("root123", login.getUsername());
        assertEquals("root1234", login.getPassword());
    }

    @Test
    void testLoginIsCreatedWithNullPassword() {
        // Arrange & Act
        Login login = new Login("EMP101", "doctor", "emp101user", null);

        // Assert
        assertEquals("EMP101", login.getId());
        assertNull(login.getPassword());
    }

    @Test
    void testLoginToStringContainsAllFields() {
        // Arrange
        Login login = new Login("EMP101", "doctor", "emp101user", "pass1234");

        // Act
        String result = login.toString();

        // Assert
        assertTrue(result.contains("EMP101"));
        assertTrue(result.contains("doctor"));
        assertTrue(result.contains("emp101user"));
    }

    @Test
    void testLoginRolesAreStoredCorrectly() {
        // Arrange
        Login admin = new Login("EMP100", "administrator", "admin1", "pass");
        Login doctor = new Login("EMP101", "doctor", "doc1", "pass");
        Login receptionist = new Login("EMP105", "receptionist", "rec1", "pass");

        // Assert
        assertEquals("administrator", admin.getRole());
        assertEquals("doctor", doctor.getRole());
        assertEquals("receptionist", receptionist.getRole());
    }
}
