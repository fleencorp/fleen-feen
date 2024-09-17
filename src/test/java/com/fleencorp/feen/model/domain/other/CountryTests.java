package com.fleencorp.feen.model.domain.other;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class CountryTests {

    private Country country1;
    private Country country2;

    @BeforeEach
    void setUp() {
        country1 = Country.builder()
                .countryId(111L)
                .title("Nigeria")
                .code("NG")
                .build();

        country2 = Country.builder()
                .countryId(222L)
                .title("Canada")
                .code("CA")
                .build();
    }

    @Test
    void testNonNullFields() {
        // Non-nullability: title and code should not be null
        assertNotNull(country1.getTitle());
        assertNotNull(country1.getCode());

        // Verify other instance
        assertNotNull(country2.getTitle());
        assertNotNull(country2.getCode());
    }

    @Test
    void testNullability() {
        // Set null to non-nullable fields and check
        Country nullCountry = new Country();
        nullCountry.setCountryId(333L);

        assertNull(nullCountry.getTitle());
        assertNull(nullCountry.getCode());
    }

    @Test
    void testEquality() {
        // Two countries with the same fields should be equal
        Country sameCountry1 = Country.builder()
                .countryId(111L)
                .title("Nigeria")
                .code("NG")
                .build();

        Country sameCountry2 = Country.builder()
                .countryId(111L)
                .title("Nigeria")
                .code("NG")
                .build();

        assertEquals(sameCountry1, sameCountry2);
    }

    @Test
    void testNonEquality() {
        // Two countries with different fields should not be equal
        assertNotEquals(country1, country2);

        // Countries with same ID but different titles should not be equal
        Country diffCountry = Country.builder()
                .countryId(1L)
                .title("Canada")
                .code("CA")
                .build();

        assertNotEquals(country1, diffCountry);
    }

    @Test
    void testHashCodeConsistency() {
        // Two equal objects should have the same hash code
        Country sameCountry = Country.builder()
                .countryId(111L)
                .title("Nigeria")
                .code("NG")
                .build();
        assertEquals(country1.hashCode(), sameCountry.hashCode());
    }

    @Test
    void testInequalityWithNull() {
        // A country should not be equal to null
        assertNotEquals(country1, null);
        assertNotEquals(country2, null);
    }

    @Test
    void testInequalityWithOtherObjectTypes() {
        // A country should not be equal to an object of a different type
        assertNotEquals(country1, new Object());
        assertNotEquals(country2, new Object());
    }
}
