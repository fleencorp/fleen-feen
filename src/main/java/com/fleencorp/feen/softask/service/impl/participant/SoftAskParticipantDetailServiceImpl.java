package com.fleencorp.feen.softask.service.impl.participant;

import com.fleencorp.feen.common.service.impl.cache.CacheService;
import com.fleencorp.feen.common.service.misc.ObjectService;
import com.fleencorp.feen.shared.common.model.GeneratedParticipantDetail;
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
  private final SoftAskParticipantDetailRepository participantDetailRepository;

  private static final String USERNAME_CACHE_PREFIX = "sa::username:";
  private static final Duration CACHE_TTL = Duration.ofHours(1);

  public SoftAskParticipantDetailServiceImpl(
      final CacheService cacheService,
      final ObjectService objectService,
      final SoftAskParticipantDetailRepository participantDetailRepository,
      final UsernameService usernameService) {
    this.cacheService = cacheService;
    this.objectService = objectService;
    this.participantDetailRepository = participantDetailRepository;
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
    final GeneratedParticipantDetail generatedParticipantDetail = usernameService.generateRandomUsername();
    final String username = generatedParticipantDetail.username();
    final String displayName = generatedParticipantDetail.displayName();
    final SoftAskParticipantDetail softAskParticipantDetail = SoftAskParticipantDetail.of(softAskId, userId, username, displayName);

    final Map<String, String> avatarUrls = objectService.getAvatarBaseName(generateRandomNumberForAvatar());
    softAskParticipantDetail.setAvatarUrl(avatarUrls.get("default"));

    participantDetailRepository.save(softAskParticipantDetail);
    return softAskParticipantDetail;
  }

  /**
   * Retrieves a participant detail for the given soft ask and user, resolving it in three stages.
   *
   * <p>It first attempts to load the detail from the cache.
   * If not present, it falls back to retrieving it from the database.
   * If the detail is not found in the database either, a new one is generated, persisted, cached, and returned.</p>
   *
   * @param softAskId the identifier of the soft ask
   * @param userId the identifier of the user
   * @return an existing participant detail from cache or database, or a newly generated one if none exist
   */
  @Override
  @Transactional
  public SoftAskParticipantDetail getOrAssignParticipantDetail(final Long softAskId, final Long userId) {
    return findParticipantDetailInCache(softAskId, userId)
      .map(this::toSoftAskParticipantDetail)
      .orElseGet(() -> getFromDbOrGenerate(softAskId, userId));
  }

  /**
   * Retrieves a participant detail from the database if it exists, otherwise generates a new one.
   *
   * <p>If a record is found in the repository for the given soft ask and user, it is cached before being returned.
   * If no record exists, a new participant detail is generated, persisted, cached, and then returned.</p>
   *
   * @param softAskId the identifier of the soft ask
   * @param userId the identifier of the user
   * @return an existing or newly generated participant detail
   */
  private SoftAskParticipantDetail getFromDbOrGenerate(final Long softAskId, final Long userId) {
    return participantDetailRepository.findBySoftAskIdAndUserId(softAskId, userId)
      .map(detail -> cacheAndReturnDetail(softAskId, userId, detail))
      .orElseGet(() -> generateAndCacheAndReturnNewDetail(softAskId, userId));
  }

  /**
   * Generates a new participant detail, assigns an avatar, persists it, and caches the result.
   *
   * <p>A random username and display name are first generated via {@link #generateNewDetail()}.
   * An avatar URL is then selected from {@code objectService}. A new
   * {@link SoftAskParticipantDetail} is created and saved to the repository, and the same
   * detail is stored in the cache for faster subsequent lookups.</p>
   *
   * @param softAskId the identifier of the SoftAsk session
   * @param userId    the identifier of the user
   * @return the newly created and cached {@code SoftAskParticipantDetail}
   */
  private SoftAskParticipantDetail generateAndCacheAndReturnNewDetail(final Long softAskId, final Long userId) {
    GeneratedParticipantDetail newDetail = generateNewDetail();
    final String username = newDetail.username();
    final String displayName = newDetail.displayName();

    final Map<String, String> avatarUrls = objectService.getAvatarBaseName(generateRandomNumberForAvatar());
    final String avatarUrl = avatarUrls.get("default");

    SoftAskParticipantDetail participantDetail = SoftAskParticipantDetail.of(softAskId, userId, username, displayName);
    participantDetail.setAvatarUrl(avatarUrl);

    participantDetailRepository.save(participantDetail);
    cacheNewDetail(softAskId, userId, username, displayName, avatarUrl);
    return participantDetail;
  }

  /**
   * Retrieves a participant detail from the cache for the given SoftAsk and user.
   *
   * <p>The method builds a cache key using {@code softAskId} and {@code userId},
   * fetches the raw cached value, and converts it into a
   * {@link GeneratedParticipantDetail} if present.</p>
   *
   * @param softAskId the identifier of the SoftAsk session
   * @param userId    the identifier of the user
   * @return an {@link Optional} containing the cached participant detail if found,
   *         otherwise {@link Optional#empty()}
   */
  private Optional<GeneratedParticipantDetail> findParticipantDetailInCache(final Long softAskId, final Long userId) {
    final String cacheKey = generateCacheKey(softAskId, userId);
    final Object cachedDetail = cacheService.get(cacheKey);

    if (nonNull(cachedDetail)) {
      final String cachedValue = String.valueOf(cachedDetail);
      final GeneratedParticipantDetail participantDetail = GeneratedParticipantDetail.getFromCachedValue(cachedValue);

      return Optional.of(participantDetail);
    }

    return Optional.empty();
  }

  /**
   * Caches the given participant detail for the specified SoftAsk and user,
   * then returns the same detail.
   *
   * <p>The participant's username, display name, and avatar URL are extracted and
   * stored in the cache under a generated key, ensuring faster lookups on
   * subsequent requests.</p>
   *
   * @param softAskId         the identifier of the SoftAsk session
   * @param userId            the identifier of the user
   * @param participantDetail the participant detail to cache and return
   * @return the same {@code SoftAskParticipantDetail} that was provided
   */
  private SoftAskParticipantDetail cacheAndReturnDetail(final Long softAskId, final Long userId, SoftAskParticipantDetail participantDetail) {
    final String username = participantDetail.getUsername();
    final String displayName = participantDetail.getDisplayName();
    final String avatarUrl = participantDetail.getAvatarUrl();

    cacheNewDetail(softAskId, userId, username, displayName, avatarUrl);
    return participantDetail;
  }

  /**
   * Generates a new {@link GeneratedParticipantDetail} with a unique random username.
   *
   * <p>This method delegates to {@code usernameService.generateRandomUsername()} and retries
   * in case of a {@link DataIntegrityViolationException}, which can occur if the username
   * is already in use due to race conditions. The loop continues until a valid, unique
   * username is successfully generated.</p>
   *
   * @return a newly generated participant detail with a unique username
   */
  private GeneratedParticipantDetail generateNewDetail() {
    while (true) {
      try {
        return usernameService.generateRandomUsername();
      } catch (final DataIntegrityViolationException ex) {
        logIfEnabled(log::isErrorEnabled, () -> log.debug("""
      Username is already in use and may be because of race conditions. Continue and try to generate a new username.
      Message: {}
      """, ex.getMessage()));
      }
    }
  }

  /**
   * Converts a {@link GeneratedParticipantDetail} into a {@link SoftAskParticipantDetail}.
   *
   * <p>This creates a new participant detail entity using the username, display name,
   * and avatar extracted from the generated detail.</p>
   *
   * @param generated the generated participant detail containing username, display name, and avatar
   * @return a new {@code SoftAskParticipantDetail} created from the given generated detail
   */
  private SoftAskParticipantDetail toSoftAskParticipantDetail(GeneratedParticipantDetail generated) {
    return SoftAskParticipantDetail.of(
      generated.username(),
      generated.displayName(),
      generated.avatar()
    );
  }

  /**
   * Stores a participant detail in the cache for the given SoftAsk and user.
   *
   * <p>The detail is serialized into a cache value using the provided username,
   * display name, and avatar, then stored with a TTL under a generated cache key.</p>
   *
   * @param softAskId   the identifier of the SoftAsk session
   * @param userId      the identifier of the user
   * @param username    the participant's username
   * @param displayName the participant's display name
   * @param avatar      the participant's avatar URL
   */
  private void cacheNewDetail(final Long softAskId, final Long userId, final String username, final String displayName, final String avatar) {
    final String cacheKey = generateCacheKey(softAskId, userId);
    final String detailToCache = GeneratedParticipantDetail.createCacheValue(username, displayName, avatar);

    cacheService.set(cacheKey, detailToCache, CACHE_TTL);
  }

  /**
   * Generates a unique cache key for identifying a participant detail within a specific SoftAsk context.
   *
   * <p>The cache key is composed of the constant {@code USERNAME_CACHE_PREFIX}, followed by the
   * {@code softAskId} and {@code userId}, separated by a colon. This ensures that each participant
   * in a given SoftAsk session can be uniquely identified in the cache.</p>
   *
   * @param softAskId the identifier of the SoftAsk session
   * @param userId the identifier of the user participating in the SoftAsk
   * @return a cache key string in the format {@code PREFIX + softAskId + ":" + userId}
   */
  private String generateCacheKey(final Long softAskId, final Long userId) {
    return USERNAME_CACHE_PREFIX + softAskId + ":" + userId;
  }

}
