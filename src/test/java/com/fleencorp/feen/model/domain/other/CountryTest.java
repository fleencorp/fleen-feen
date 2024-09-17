package com.fleencorp.feen.model.domain.other;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class CountryTest {


    @Test
    void test_non_null_fields() {
        //GIVEN
        Country country1 = Country.builder()
          .countryId(111L)
          .title("Nigeria")
          .code("NG")
          .build();

        Country country2 = Country.builder()
          .countryId(222L)
          .title("Canada")
          .code("CA")
          .build();
        // ASSERT
        assertNotNull(country1.getTitle());
        assertNotNull(country1.getCode());
        assertNotNull(country2.getTitle());
        assertNotNull(country2.getCode());
    }

    @Test
    void test_nullability() {
        //GIVEN
        Country nullCountry = new Country();
        nullCountry.setCountryId(333L);

        //ASSERT
        assertNull(nullCountry.getTitle());
        assertNull(nullCountry.getCode());
    }

    @Test
    void test_equality() {
        //GIVEN
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

        //ASSERT
        assertEquals(sameCountry1, sameCountry2);
    }

    @Test
    void test_non_equality() {

        //GIVEN
        Country country1 = Country.builder()
          .countryId(111L)
          .title("Nigeria")
          .code("NG")
          .build();

        Country country2 = Country.builder()
          .countryId(222L)
          .title("Canada")
          .code("CA")
          .build();

        Country diffCountry = Country.builder()
                .countryId(1L)
                .title("Canada")
                .code("CA")
                .build();

        //ASSERT
        assertNotEquals(country1, country2);
        assertNotEquals(country1, diffCountry);
    }

    @Test
    void test_hash_code_consistency() {
        //GIVEN
        Country country1 = Country.builder()
          .countryId(111L)
          .title("Nigeria")
          .code("NG")
          .build();

        Country sameCountry = Country.builder()
                .countryId(111L)
                .title("Nigeria")
                .code("NG")
                .build();

        //ASSERT
        assertEquals(country1.hashCode(), sameCountry.hashCode());
    }

    @Test
    void test_inequality_with_null() {
        // GIVEN
        Country country1 = Country.builder()
          .countryId(111L)
          .title("Nigeria")
          .code("NG")
          .build();

        Country country2 = Country.builder()
          .countryId(222L)
          .title("Canada")
          .code("CA")
          .build();

        //ASSERT
        assertNotEquals(country1, null);
        assertNotEquals(country2, null);
    }

    @Test
    void test_inequality_with_other_object_types() {
        //GIVEN
        Country country1 = Country.builder()
          .countryId(111L)
          .title("Nigeria")
          .code("NG")
          .build();

        Country country2 = Country.builder()
          .countryId(222L)
          .title("Canada")
          .code("CA")
          .build();

        //ASSERT
        assertNotEquals(country1, new Object());
        assertNotEquals(country2, new Object());
    }
}
