package com.fleencorp.feen.softask.service.impl.participant;

import com.fleencorp.feen.common.service.impl.cache.CacheService;
import com.fleencorp.feen.common.service.misc.ObjectService;
import com.fleencorp.feen.shared.common.model.GeneratedUsername;
import com.fleencorp.feen.softask.model.domain.SoftAskParticipantDetail;
import com.fleencorp.feen.softask.repository.participant.SoftAskParticipantDetailRepository;
import com.fleencorp.feen.softask.service.participant.SoftAskParticipantDetailService;
import com.fleencorp.feen.user.service.UsernameService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

import static com.fleencorp.feen.common.util.common.LoggingUtil.logIfEnabled;
import static com.fleencorp.feen.softask.mapper.impl.SoftAskCommonMapperImpl.generateRandomNumberForAvatar;
import static java.util.Objects.nonNull;

@Slf4j
@Service
public class SoftAskParticipantDetailServiceImpl implements SoftAskParticipantDetailService {

  private final CacheService cacheService;
  private final ObjectService objectService;
  private final UsernameService usernameService;
  private final SoftAskParticipantDetailRepository softAskParticipantDetailRepository;

  private static final String USERNAME_CACHE_PREFIX = "sa::username:";
  private static final Duration CACHE_TTL = Duration.ofHours(1);

  public SoftAskParticipantDetailServiceImpl(
      final CacheService cacheService,
      final ObjectService objectService,
      final SoftAskParticipantDetailRepository softAskParticipantDetailRepository,
      final UsernameService usernameService) {
    this.cacheService = cacheService;
    this.objectService = objectService;
    this.softAskParticipantDetailRepository = softAskParticipantDetailRepository;
    this.usernameService = usernameService;
  }

  /**
   * Generates and persists a new {@link SoftAskParticipantDetail} for the given user within
   * the context of a soft ask. A random username and display name are created, and an avatar
   * URL is assigned based on a randomly selected avatar base name. The participant detail is
   * saved to the repository before being returned.
   *
   * @param softAskId the identifier of the soft ask the participant is associated with
   * @param userId the identifier of the user for whom the participant detail is generated
   * @return the newly created and persisted {@code SoftAskParticipantDetail} with username,
   *         display name, and avatar information
   */
  @Override
  @Transactional
  public SoftAskParticipantDetail generateParticipantDetail(final Long softAskId, final Long userId) {
    final GeneratedUsername generatedUsername = usernameService.generateRandomUsername();
    final String username = generatedUsername.username();
    final String displayName = generatedUsername.displayName();
    final SoftAskParticipantDetail softAskParticipantDetail = SoftAskParticipantDetail.of(softAskId, userId, username, displayName);

    final Map<String, String> avatarUrls = objectService.getAvatarBaseName(generateRandomNumberForAvatar());
    softAskParticipantDetail.setAvatarUrl(avatarUrls.get("default"));

    softAskParticipantDetailRepository.save(softAskParticipantDetail);
    return softAskParticipantDetail;
  }

  /**
   * Retrieves or assigns a {@link SoftAskParticipantDetail} for the given user in the context of
   * a specific soft ask. A username and display name are resolved or generated, and a participant
   * detail object is created. An avatar URL is also assigned using a randomly selected avatar base
   * name before the participant detail is returned.
   *
   * @param softAskId the identifier of the soft ask the participant is associated with
   * @param userId the identifier of the user for whom the participant detail is retrieved or created
   * @return a fully initialized {@code SoftAskParticipantDetail} containing username, display name,
   *         and avatar information
   */
  @Override
  @Transactional
  public SoftAskParticipantDetail getOrAssignParticipantDetail(final Long softAskId, final Long userId) {
    final GeneratedUsername generatedUsername = getOrAssignUsername(softAskId, userId);
    final String username = generatedUsername.username();
    final String displayName = generatedUsername.displayName();

    SoftAskParticipantDetail participantDetail = SoftAskParticipantDetail.of(softAskId, userId, username, displayName);

    final Map<String, String> avatarUrls = objectService.getAvatarBaseName(generateRandomNumberForAvatar());
    participantDetail.setAvatarUrl(avatarUrls.get("default"));

    return participantDetail;
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
  @Transactional
  public GeneratedUsername getOrAssignUsername(final Long softAskId, final Long userId) {
    final String cacheKey = USERNAME_CACHE_PREFIX + softAskId + ":" + userId;
    final Object cachedUsername = cacheService.get(cacheKey);

    if (nonNull(cachedUsername)) {
      final String username = String.valueOf(cachedUsername);
      return GeneratedUsername.getFromCachedValue(username);
    }

    final Optional<SoftAskParticipantDetail> existingUsername = softAskParticipantDetailRepository.findUsernameBySoftAskIdAndUserId(softAskId, userId);
    if (existingUsername.isPresent()) {
      SoftAskParticipantDetail softAskParticipantDetail = existingUsername.get();
      final String username = softAskParticipantDetail.getUsername();
      final String displayName = softAskParticipantDetail.getDisplayName();

      final String usernameToCache = GeneratedUsername.createCacheValue(username, displayName);
      cacheUsername(cacheKey, usernameToCache);

      return GeneratedUsername.of(username, displayName);
    }

    while (true) {
      final GeneratedUsername generatedUsername = usernameService.generateRandomUsername();
      final String newUsername = generatedUsername.username();
      final String displayName = generatedUsername.displayName();

      try {
        final SoftAskParticipantDetail softAskParticipantDetail = SoftAskParticipantDetail.of(softAskId, userId, newUsername, displayName);
        final Map<String, String> avatarUrls = objectService.getAvatarBaseName(generateRandomNumberForAvatar());
        softAskParticipantDetail.setAvatarUrl(avatarUrls.get("default"));

        softAskParticipantDetailRepository.save(softAskParticipantDetail);

        cacheUsername(cacheKey, newUsername);
        return generatedUsername;
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
