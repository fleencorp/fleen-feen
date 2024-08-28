package com.fleencorp.feen.service.impl.common;

import com.fleencorp.base.model.view.search.SearchResultView;
import com.fleencorp.feen.exception.user.CountryNotFoundException;
import com.fleencorp.feen.mapper.CountryMapper;
import com.fleencorp.feen.model.domain.other.Country;
import com.fleencorp.feen.model.request.search.CountrySearchRequest;
import com.fleencorp.feen.model.response.country.CountryResponse;
import com.fleencorp.feen.model.response.other.CountAllResponse;
import com.fleencorp.feen.repository.common.CountryRepository;
import com.fleencorp.feen.service.common.CountryService;
import com.fleencorp.feen.service.impl.cache.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.fleencorp.base.util.FleenUtil.toSearchResult;
import static com.fleencorp.feen.mapper.CountryMapper.toCountryResponses;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

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
  private final CacheService cacheService;

  /**
   * Constructs a {@link CountryServiceImpl} instance with the specified repository and cache service.
   *
   * @param repository The repository used for accessing country data.
   * @param cacheService The service used for caching country data.
   */
  public CountryServiceImpl(
      final CountryRepository repository,
      final CacheService cacheService) {
    this.repository = repository;
    this.cacheService = cacheService;
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

  /**
   * Retrieves a list of all countries from the repository and maps them to a list of {@link CountryResponse}
   * objects. This method is typically used to fetch and cache country data.
   *
   * @return A {@link List} of {@link CountryResponse} objects representing all countries.
   */
  private List<CountryResponse> getCountries() {
    return CountryMapper.toCountryResponses(repository.findAll());
  }

  /**
   * Saves the provided list of {@link CountryResponse} objects to the cache. If the provided list is {@code null}
   * or empty, it retrieves the countries from the data source and caches them instead.
   *
   * @param countries A {@link List} of {@link CountryResponse} objects to be cached. If this list is {@code null}
   *                  or empty, the method will fetch the countries from the data source using {@link #getCountries()}.
   */
  protected void saveCountriesToCache(List<CountryResponse> countries) {
    if (isNull(countries) || countries.isEmpty()) {
      countries = getCountries();
    }
    countries.forEach(country -> cacheService.set(getCountryCacheKey(country.getTitle()), country));
  }

  /**
   * Handles the {@link ApplicationReadyEvent} event to save the list of countries to the cache when the
   * application starts up. This method retrieves the countries from the data source and stores them in the cache.
   * This method is automatically invoked by the Spring framework when the application is fully initialized.
   */
  @EventListener(ApplicationReadyEvent.class)
  public void saveCountriesToCacheOnStartup() {
    List<CountryResponse> countries = getCountries();
    saveCountriesToCache(countries);
  }

  /**
   * Scheduled task that refreshes the cache with country data every 12 hours.
   * This method is triggered based on a cron expression which schedules it to run at the start of every 12th hour.
   * It calls {@link #saveCountriesToCache(List)} with {@code null} to update the cache with the latest
   * list of countries retrieved from the data source.
   */
  @Scheduled(cron = "0 0 */12 * * *")
  private void saveCountriesToCache() {
    saveCountriesToCache(null);
  }

  /**
   * Retrieves a {@link CountryResponse} from the cache based on the provided country title.
   * The method uses the title to generate a cache key and retrieves the corresponding {@link CountryResponse}
   * from the cache. If no data is found in the cache for the generated key, the result will be {@code null}.
   *
   * @param title The title of the country for which the cached response is to be retrieved.
   * @return The {@link CountryResponse} for the given title if it exists in the cache;
   *         {@code null} if no cached data is found for the specified key.
   */
  protected CountryResponse getCountryFromCache(final String title) {
    return cacheService.get(getCountryCacheKey(title), CountryResponse.class);
  }

  /**
   * Retrieves the country code for a given country title from the cache.
   * This method looks up the country title in the cache using {@link #getCountryFromCache(String)}.
   * If a matching {@link CountryResponse} is found, the country code is returned as an {@link Optional}.
   * If no matching country is found in the cache, an empty {@link Optional} is returned.
   *
   * @param title The title of the country for which the code is to be retrieved.
   * @return An {@link Optional} containing the country code if found;
   *         {@link Optional#empty()} if no country with the given title is found in the cache.
   */
  @Override
  public Optional<String> getCountryCodeByTitle(final String title) {
    CountryResponse country = getCountryFromCache(title);
    if (nonNull(country)) {
      return Optional.of(country.getCode());
    }
    return Optional.empty();
  }

  /**
   * Generates the key used for caching country data. This key is used to store and retrieve country data from
   * the cache. The generated key is a constant string "COUNTRY:::", which can be used to uniquely identify
   * country-related cache entries.
   *
   * @return A {@link String} representing the key used for country data in the cache.
   */
  private String getCountryCacheKey(final String title) {
    return "COUNTRY:::".concat(title);
  }
}
