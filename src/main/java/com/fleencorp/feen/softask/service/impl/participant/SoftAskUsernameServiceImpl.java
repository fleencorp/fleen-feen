package com.fleencorp.feen.softask.service.impl.participant;

import com.fleencorp.feen.common.service.impl.cache.CacheService;
import com.fleencorp.feen.softask.model.domain.SoftAskUsername;
import com.fleencorp.feen.softask.repository.participant.SoftAskUsernameRepository;
import com.fleencorp.feen.softask.service.participant.SoftAskUsernameService;
import com.fleencorp.feen.user.service.UsernameService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

import static com.fleencorp.feen.common.util.common.LoggingUtil.logIfEnabled;
import static java.util.Objects.nonNull;

@Slf4j
@Service
public class SoftAskUsernameServiceImpl implements SoftAskUsernameService {

  private final CacheService cacheService;
  private final UsernameService usernameService;
  private final SoftAskUsernameRepository usernameRepository;

  private static final String USERNAME_CACHE_PREFIX = "sa::username:";
  private static final Duration CACHE_TTL = Duration.ofHours(1);

  public SoftAskUsernameServiceImpl(
      final CacheService cacheService,
      final SoftAskUsernameRepository usernameRepository,
      final UsernameService usernameService) {
    this.cacheService = cacheService;
    this.usernameRepository = usernameRepository;
    this.usernameService = usernameService;
  }

  @Override
  @Transactional
  public SoftAskUsername generateUsername(final Long softAskId, final Long userId) {
    final String username = usernameService.generateRandomUsername();
    final SoftAskUsername softAskUsername = SoftAskUsername.of(softAskId, userId, username);

    usernameRepository.save(softAskUsername);
    return softAskUsername;
  }

  /**
   * Retrieves an existing username associated with a given soft ask and user, or generates and assigns
   * a new one if none exists.
   *
   * <p>This method first checks whether the username is present in the cache for the specified
   * {@code softAskId} and {@code userId}. If found, the cached value is returned. Otherwise, it
   * queries the repository to determine if a username has already been assigned. If an existing
   * username is found, it is cached and returned. If no username exists, a new random username
   * is generated and persisted. In cases where persistence fails due to a race condition (for
   * example, another process assigning the same username concurrently), the method retries until
   * a unique username is successfully stored.</p>
   *
   * @param softAskId the identifier of the soft ask for which the username is retrieved or assigned
   * @param userId the identifier of the user associated with the soft ask
   * @return the existing or newly assigned username
   */
  @Override
  @Transactional
  public String getOrAssignUsername(final Long softAskId, final Long userId) {
    final String cacheKey = USERNAME_CACHE_PREFIX + softAskId + ":" + userId;
    final Object cachedUsername = cacheService.get(cacheKey);

    if (nonNull(cachedUsername)) {
      return String.valueOf(cachedUsername);
    }

    final Optional<String> existingUsername = usernameRepository.findUsernameBySoftAskIdAndUserId(softAskId, userId);
    if (existingUsername.isPresent()) {
      cacheUsername(cacheKey, existingUsername.get());
      return existingUsername.get();
    }

    while (true) {
      final String newUsername = usernameService.generateRandomUsername();
      try {
        final SoftAskUsername softAskUsername = SoftAskUsername.of(softAskId, userId, newUsername);
        usernameRepository.save(softAskUsername);

        cacheUsername(cacheKey, newUsername);
        return newUsername;
      } catch (final DataIntegrityViolationException ex) {
        logIfEnabled(log::isErrorEnabled, () -> log.debug("""
        Username is already in use and may be because of race conditions. Continue and try to generate a new username.
        Message: {}
        """, ex.getMessage()));
      }
    }
  }

  private void cacheUsername(final String cacheKey, final String newUsername) {
    cacheService.set(cacheKey, newUsername, CACHE_TTL);
  }
}
