package com.project.entity;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Name embeddable entity.
 * Principle: FIRST (Fast, Independent, Repeatable, Self-validating, Timely)
 * Pattern: AAA (Arrange, Act, Assert)
 */
class NameTest {

    @Test
    void testNameIsCreatedWithAllFields() {
        // Arrange
        String firstName = "John";
        String middleName = "Michael";
        String lastName = "Doe";

        // Act
        Name name = new Name(firstName, middleName, lastName);

        // Assert
        assertEquals("John", name.getFirstName());
        assertEquals("Michael", name.getMiddleName());
        assertEquals("Doe", name.getLastName());
    }

    @Test
    void testNameIsCreatedWithNullMiddleName() {
        // Arrange & Act
        Name name = new Name("Jane", null, "Smith");

        // Assert
        assertEquals("Jane", name.getFirstName());
        assertNull(name.getMiddleName());
        assertEquals("Smith", name.getLastName());
    }

    @Test
    void testNameIsCreatedWithEmptyStrings() {
        // Arrange & Act
        Name name = new Name("", "", "");

        // Assert
        assertEquals("", name.getFirstName());
        assertEquals("", name.getMiddleName());
        assertEquals("", name.getLastName());
    }

    @Test
    void testNameToStringContainsAllFields() {
        // Arrange
        Name name = new Name("John", "M", "Doe");

        // Act
        String result = name.toString();

        // Assert
        assertTrue(result.contains("John"));
        assertTrue(result.contains("M"));
        assertTrue(result.contains("Doe"));
    }

    @Test
    void testNameDefaultConstructorReturnsNullFields() {
        // Arrange & Act
        Name name = new Name();

        // Assert
        assertNull(name.getFirstName());
        assertNull(name.getMiddleName());
        assertNull(name.getLastName());
    }
}
