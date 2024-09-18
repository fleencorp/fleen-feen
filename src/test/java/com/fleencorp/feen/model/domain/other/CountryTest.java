package com.fleencorp.feen.model.domain.other;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class CountryTest{

    @Test
    void ensure_country_is_null(){
        //GIVEN
        Country country = null;

        //ASSERT
        assertNull(country);
    }

    @Test
    void ensure_country_title_is_not_null(){
        //GIVEN
        Country country = new Country();
        country.setTitle("Nigeria");


        //ASSERT
        assertNotNull(country);
        assertNotNull(country.getTitle());
    }

    @Test
    void ensure_country_country_id_not_null(){
        //GIVEN
        Country country = new Country();
        country.setCountryId(1L);

        //ASSERT
        assertNotNull(country);
        assertNotNull(country.getCountryId());
    }


    @Test
    void ensure_country_objects_are_not_equal(){
        //GIVEN
        Country country1 = new Country(1L, "Nigeria","NG");
        Country country2 = new Country(2L, "Canada","CA");

        //ASSERT
        assertNotEquals(country1, country2);
    }

    @Test
    void ensure_country_titles_are_not_equal(){
        //GIVEN
        Country country1 = new Country();
        country1.setTitle("Nigeria");
        Country country2 = new Country();
        country2.setTitle("Canada");

        //ASSERT
        assertNotEquals(country1.getTitle(), country2.getTitle());
    }

    @Test
    void ensure_country_codes_are_not_equal(){
        //GIVEN
        Country country1 = new Country();
        country1.setCode("NG");
        Country country2 = new Country();
        country2.setCode("CA");

        //ASSERT
        assertNotEquals(country1.getCode(), country2.getCode());
    }

    @Test
    void ensure_country_country_id_are_not_equal(){
        //GIVEN
        Country country1 = new Country();
        country1.setCountryId(1L);
        Country country2 = new Country();
        country2.setCountryId(2L);

        //ASSERT
        assertNotEquals(country1.getCountryId(), country2.getCountryId());
    }

    @Test
    void ensure_country_objects_are_equal(){
        //GIVEN
        Country country1 = new Country(1L, "Nigeria","NG");
        Country sameCountry = new Country(1L, "Nigeria","NG");

        //ASSERT
        assertEquals(country1.getTitle(), sameCountry.getTitle());
        assertEquals(country1.getCountryId(), sameCountry.getCountryId());
        assertEquals(country1.getCode(), sameCountry.getCode());
    }

    @Test
    void ensure_country_codes_are_equal(){
        //GIVEN
        Country country1 = new Country();
        country1.setCode("NG");
        Country sameCountry = new Country();
        sameCountry.setCode("NG");

        //ASSERT
        assertEquals(country1.getCode(), sameCountry.getCode());
    }

    @Test
    void ensure_country_country_ids_are_equal(){
        //GIVEN
        Country country1 = new Country();
        country1.setCountryId(1L);
        Country sameCountry = new Country();
        sameCountry.setCountryId(1L);

        //ASSERT
        assertEquals(country1.getCountryId(), sameCountry.getCountryId());
    }

    @Test
    void ensure_country_titles_are_equal(){
        //GIVEN
        Country country1 = new Country();
        country1.setTitle("Nigeria");
        Country sameCountry = new Country();
        sameCountry.setTitle("Nigeria");

        //ASSERT
        assertEquals(country1.getTitle(), sameCountry.getTitle());
    }

    @Test
    void ensure_country_is_not_null(){
        // GIVEN
        Country country1 = new Country(1L, "Nigeria","NG");
        Country country2 = new Country(2L, "Canada","CA");

        //ASSERT
        assertNotEquals(country1, null);
        assertNotEquals(country2, null);
    }


}
