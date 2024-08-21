package com.fleencorp.feen.service.impl.common;

import com.fleencorp.base.model.view.search.SearchResultView;
import com.fleencorp.feen.exception.user.CountryNotFoundException;
import com.fleencorp.feen.model.domain.other.Country;
import com.fleencorp.feen.model.request.search.CountrySearchRequest;
import com.fleencorp.feen.model.response.country.CountryResponse;
import com.fleencorp.feen.model.response.other.CountAllResponse;
import com.fleencorp.feen.repository.common.CountryRepository;
import com.fleencorp.feen.service.common.CountryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.fleencorp.base.util.FleenUtil.toSearchResult;
import static com.fleencorp.feen.mapper.CountryMapper.toCountryResponses;

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
   * Finds countries based on the provided search request.
   *
   * @param searchRequest the request object containing search criteria and pagination information
   * @return a SearchResultView object containing a list of CountryResponse views and pagination metadata
 */
  @Override
  public SearchResultView findCountries(final CountrySearchRequest searchRequest) {
    // Retrieve a page of Country entities based on the search request.
    final Page<Country> page = repository.findMany(searchRequest.getPage());
    // Convert the list of Country entities to a list of CountryResponse views.
    final List<CountryResponse> views = toCountryResponses(page.getContent());
    // Convert the list of views and the page metadata to a SearchResultView object.
    return toSearchResult(views, page);
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
   * Retrieves a Country entity by its unique identifier.
   *
   * <p>This method attempts to find a Country entity in the repository using the provided code. If the Country is not found,
   * a {@link CountryNotFoundException} is thrown.</p>
   *
   * @param code the unique identifier of the Country to be retrieved
   * @return the Country entity associated with the specified code
   * @throws CountryNotFoundException if no Country is found with the specified code
   */
  @Override
  public Country getCountryByCode(final String code) {
    return repository
      .findByCode(code)
      .orElseThrow(() -> new CountryNotFoundException(code));
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
  * <p>This method attempts to find a Country entity in the repository using the provided code and returns true if
  * the Country is found, otherwise returns false.</p>
  *
  * @param code the unique identifier of the Country to be checked
  * @return true if the Country exists, false otherwise
  */
  @Override
  public boolean isCountryExists(final String code) {
    return repository
      .existsByCode(code);
  }
}
