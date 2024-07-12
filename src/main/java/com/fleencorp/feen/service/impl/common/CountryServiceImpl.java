package com.fleencorp.feen.service.impl.common;

import com.fleencorp.feen.exception.user.CountryNotFoundException;
import com.fleencorp.feen.model.domain.other.Country;
import com.fleencorp.feen.model.response.other.CountAllResponse;
import com.fleencorp.feen.repository.common.CountryRepository;
import com.fleencorp.feen.service.common.CountryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
* Implementation of the {@link CountryService} interface.
*
* <p>This class provides the concrete implementations of the methods defined in the {@link CountryService}
* interface for managing Country entities. It uses a repository to perform CRUD operations on Country entities
* and includes methods to count all countries and check if a country exists by its unique identifier.</p>
*
* @author Yusuf Alamu Musa
* @version 1.0
*/
@Slf4j
@Service
public class CountryServiceImpl implements CountryService {

  private final CountryRepository repository;

  public CountryServiceImpl(final CountryRepository repository) {
    this.repository = repository;
  }

  /**
  * Retrieves a Country entity by its unique identifier.
  *
  * <p>This method attempts to find a Country entity in the repository using the provided ID. If the Country is not found,
  * a {@link CountryNotFoundException} is thrown.</p>
  *
  * @param id the unique identifier of the Country to be retrieved
  * @return the Country entity associated with the specified ID
  * @throws CountryNotFoundException if no Country is found with the specified ID
  */
  @Override
  public Country getCountry(final Long id) {
    return repository
            .findById(id)
            .orElseThrow(() -> new CountryNotFoundException(id));
  }

  /**
  * Counts the total number of Country entities in the repository.
  *
  * <p>This method retrieves the total count of Country entities from the repository and returns it wrapped in a
  * {@link CountAllResponse} DTO.</p>
  *
  * @return a {@link CountAllResponse} containing the total count of Country entities
  */
  @Override
  public CountAllResponse countAll() {
    final long total = repository.count();
    return CountAllResponse.of(total);
  }

  /**
  * Checks if a Country entity exists in the repository by its unique identifier.
  *
  * <p>This method attempts to find a Country entity in the repository using the provided ID and returns true if
  * the Country is found, otherwise returns false.</p>
  *
  * @param id the unique identifier of the Country to be checked
  * @return true if the Country exists, false otherwise
  */
  @Override
  public boolean isCountryExists(final Long id) {
    return repository
            .findById(id)
            .isPresent();
  }
}
