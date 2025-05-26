package com.fleencorp.feen.service.impl.common;

import com.fleencorp.base.model.view.search.SearchResult;
import com.fleencorp.feen.exception.common.CountryNotFoundException;
import com.fleencorp.feen.mapper.other.CountryMapper;
import com.fleencorp.feen.model.domain.other.Country;
import com.fleencorp.feen.model.request.search.CountrySearchRequest;
import com.fleencorp.feen.model.response.country.CountryResponse;
import com.fleencorp.feen.model.response.country.RetrieveCountryResponse;
import com.fleencorp.feen.model.response.other.CountAllResponse;
import com.fleencorp.feen.model.search.country.CountrySearchResult;
import com.fleencorp.feen.repository.common.CountryRepository;
import com.fleencorp.feen.service.common.CountryService;
import com.fleencorp.feen.service.impl.cache.CacheService;
import com.fleencorp.localizer.service.Localizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.fleencorp.base.util.FleenUtil.toSearchResult;
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
  private final CountryMapper countryMapper;
  private final Localizer localizer;

  /**
   * Constructs a new instance of the {@link CountryServiceImpl}.
   *
   * <p>This constructor initializes the service with the required dependencies:
   * {@link CountryRepository}, {@link CacheService}, and {@link Localizer}.</p>
   *
   * <p>These dependencies are injected to handle various operations such as
   * country repository interactions, caching operations, and localized responses
   * for user feedback.</p>
   *
   * @param repository the {@link CountryRepository} used for country data retrieval and management
   * @param cacheService the service for caching country data
   * @param countryMapper mapper for mapping country related info
   * @param localizer the service for creating localized responses
   */
  public CountryServiceImpl(
      final CountryRepository repository,
      final CacheService cacheService,
      final CountryMapper countryMapper,
      final Localizer localizer) {
    this.repository = repository;
    this.cacheService = cacheService;
    this.countryMapper = countryMapper;
    this.localizer = localizer;
  }

  /**
   * Finds countries based on the provided search request.
   *
   * @param searchRequest the request object containing search criteria and pagination information
   * @return a CountrySearchResult object containing a list of CountryResponse views and pagination metadata
 */
  @Override
  public CountrySearchResult findCountries(final CountrySearchRequest searchRequest) {
    // Retrieve a page of Country entities based on the search request.
    final Page<Country> page = repository.findMany(searchRequest.getPage());
    // Convert the list of Country entities to a list of CountryResponse views.
    final List<CountryResponse> countryResponses = countryMapper.toCountryResponses(page.getContent());
    // Create the search result
    final SearchResult searchResult = toSearchResult(countryResponses, page);
    // Create the search result
    final CountrySearchResult countrySearchResult = CountrySearchResult.of(searchResult);
    // Return a search result with the responses and pagination details
    return localizer.of(countrySearchResult);
  }

  /**
  * Retrieves a Country entity by its unique identifier.
  *
  * <p>This method attempts to find a Country entity in the repository using the provided ID. If the Country is not found,
  * a {@link CountryNotFoundException} is thrown.</p>
  *
  * @param countryId the unique identifier of the Country to be retrieved
  * @return the Country entity associated with the specified ID
  * @throws CountryNotFoundException if no Country is found with the specified ID
  */
  @Override
  public RetrieveCountryResponse getCountry(final Long countryId) throws CountryNotFoundException {
    // Find country based on ID or throw an exception if it can't be found
    final Country country = repository.findById(countryId)
      .orElseThrow(CountryNotFoundException.of(countryId));
    // Create the response
    final CountryResponse countryResponse = countryMapper.toCountryResponse(country);
    // Return a localized response containing details of country
    return localizer.of(RetrieveCountryResponse.of(countryResponse));
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
    return repository.findByCode(code)
      .orElseThrow(CountryNotFoundException.of(code));
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
    // Count the total number of countries in the repository
    final long total = repository.count();
    // Return a localized response containing the total count of countries
    return localizer.of(CountAllResponse.of(total));
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
    // Check if a country exist with a 3 letter code exists in the repository
    return repository.existsByCode(code);
  }

  /**
   * Retrieves a list of all countries from the repository and maps them to a list of {@link CountryResponse}
   * objects. This method is typically used to fetch and cache country data.
   *
   * @return A {@link List} of {@link CountryResponse} objects representing all countries.
   */
  private List<CountryResponse> getCountries() {
    return countryMapper.toCountryResponses(repository.findAll());
  }

  /**
   * Saves the provided list of {@link CountryResponse} objects to the cache. If the provided list is {@code null}
   * or empty, it retrieves the countries from the data source and caches them instead.
   *
   * @param countries A {@link List} of {@link CountryResponse} objects to be cached. If this list is {@code null}
   *                  or empty, the method will fetch the countries from the data source using {@link #getCountries()}.
   */
  protected void saveCountriesToCache(final List<CountryResponse> countries) {
    // If the list of countries is null or empty, retrieve the countries
    List<CountryResponse> newCountries = countries;
    if (isNull(countries) || countries.isEmpty()) {
      newCountries = getCountries();
    }

    // Filter out null country entries and save each valid country to the cache
    newCountries.stream()
      .filter(Objects::nonNull)
      .forEach(country -> cacheService.set(getCountryCacheKey(country.getTitle()), country));
  }

  /**
   * Handles the {@link ApplicationReadyEvent} event to save the list of countries to the cache when the
   * application starts up. This method retrieves the countries from the data source and stores them in the cache.
   * This method is automatically invoked by the Spring framework when the application is fully initialized.
   */
  @EventListener(ApplicationReadyEvent.class)
  public void saveCountriesToCacheOnStartup() {
    final List<CountryResponse> countries = getCountries();
    saveCountriesToCache(countries);
  }

  /**
   * Scheduled task that refreshes the cache with country data every 12 hours.
   * This method is triggered based on a cron expression which schedules it to run at the start of every 12th hour.
   * It calls {@link #saveCountriesToCache(List)} with {@code null} to update the cache with the latest
   * list of countries retrieved from the data source.
   */
  @Scheduled(cron = "0 0 */12 * * *", zone = "${application.timezone}")
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
  @Override
  public Optional<CountryResponse> getCountryFromCache(final String title) {
    // Retrieve the country from the cache using the generated cache key
    final CountryResponse country = cacheService.get(getCountryCacheKey(title), CountryResponse.class);
    // Check if the country was found in the cache and return it wrapped in an Optional
    if (nonNull(country)) {
      return Optional.of(country);
    }
    // Return an empty Optional if the country is not found
    return Optional.empty();
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
    // Retrieve the country response from the cache
    final Optional<CountryResponse> existingCountry = getCountryFromCache(title);
    // If the country is found, return the code as an Optional
    return existingCountry.map(CountryResponse::getCode);
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
