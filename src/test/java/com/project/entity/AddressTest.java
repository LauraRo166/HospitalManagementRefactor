package com.project.entity;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Address embeddable entity.
 * Principle: FIRST | Pattern: AAA
 */
class AddressTest {

    @Test
    void testAddressIsCreatedWithBothFields() {
        // Arrange
        String residential = "123 Main St";
        String permanent = "456 Oak Ave";

        // Act
        Address address = new Address(residential, permanent);

        // Assert
        assertEquals("123 Main St", address.getResidentialAddress());
        assertEquals("456 Oak Ave", address.getPermanentAddress());
    }

    @Test
    void testAddressIsCreatedWithNullPermanent() {
        // Arrange & Act
        Address address = new Address("123 Main St", null);

        // Assert
        assertEquals("123 Main St", address.getResidentialAddress());
        assertNull(address.getPermanentAddress());
    }

    @Test
    void testAddressToStringContainsBothAddresses() {
        // Arrange
        Address address = new Address("Residential St", "Permanent Ave");

        // Act
        String result = address.toString();

        // Assert
        assertTrue(result.contains("Residential St"));
        assertTrue(result.contains("Permanent Ave"));
    }
}
