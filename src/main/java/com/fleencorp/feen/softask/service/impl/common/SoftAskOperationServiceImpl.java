package com.fleencorp.feen.softask.service.impl.common;

import com.fleencorp.feen.common.service.location.GeoService;
import com.fleencorp.feen.shared.common.model.GeneratedUsername;
import com.fleencorp.feen.softask.contract.SoftAskCommonData;
import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.domain.SoftAskReply;
import com.fleencorp.feen.softask.model.domain.SoftAskParticipantDetail;
import com.fleencorp.feen.softask.repository.reply.SoftAskReplyRepository;
import com.fleencorp.feen.softask.repository.softask.SoftAskRepository;
import com.fleencorp.feen.softask.service.common.SoftAskOperationService;
import com.fleencorp.feen.softask.service.participant.SoftAskParticipantDetailService;
import com.fleencorp.feen.softask.service.reply.SoftAskReplySearchService;
import com.fleencorp.feen.softask.service.softask.SoftAskSearchService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Objects.nonNull;

@Service
public class SoftAskOperationServiceImpl implements SoftAskOperationService {

  private final GeoService geoService;
  private final SoftAskReplySearchService softAskReplySearchService;
  private final SoftAskSearchService softAskSearchService;
  private final SoftAskParticipantDetailService softAskParticipantDetailService;
  private final SoftAskReplyRepository softAskReplyRepository;
  private final SoftAskRepository softAskRepository;

  public SoftAskOperationServiceImpl(
      final GeoService geoService,
      final SoftAskReplySearchService softAskReplySearchService,
      final SoftAskSearchService softAskSearchService,
      final SoftAskParticipantDetailService softAskParticipantDetailService,
      final SoftAskReplyRepository softAskReplyRepository,
      final SoftAskRepository softAskRepository) {
    this.geoService = geoService;
    this.softAskReplySearchService = softAskReplySearchService;
    this.softAskSearchService = softAskSearchService;
    this.softAskParticipantDetailService = softAskParticipantDetailService;
    this.softAskReplyRepository = softAskReplyRepository;
    this.softAskRepository = softAskRepository;
  }

  @Override
  @Transactional
  public SoftAskReply save(final SoftAskReply softAskReply) {
    return softAskReplyRepository.save(softAskReply);
  }

  @Override
  @Transactional
  public SoftAsk save(final SoftAsk softAsk) {
    return softAskRepository.save(softAsk);
  }

  @Override
  public SoftAsk findSoftAsk(final Long softAskId) {
    return softAskSearchService.findSoftAsk(softAskId);
  }

  @Override
  public SoftAskReply findSoftAskReply(final Long softAskId, final Long softAskReplyId) {
    return softAskReplySearchService.findSoftAskReply(softAskId, softAskReplyId);
  }

  private Integer incrementSoftAskVoteAndGetVoteCount(final Long softAskId) {
    softAskRepository.incrementVoteCount(softAskId);
    return softAskRepository.getVoteCount(softAskId);
  }

  private Integer decrementSoftAskVoteAndGetVoteCount(final Long softAskId) {
    softAskRepository.decrementVoteCount(softAskId);
    return softAskRepository.getVoteCount(softAskId);
  }

  @Override
  @Transactional
  public Integer updateVoteCount(final Long softAskId, final boolean isVoted) {
    return isVoted
      ? incrementSoftAskVoteAndGetVoteCount(softAskId)
      : decrementSoftAskVoteAndGetVoteCount(softAskId);
  }

  private Integer incrementSoftAskReplyVoteAndGetVoteCount(final Long softAskId, final Long softAskReplyId) {
    softAskReplyRepository.incrementVoteCount(softAskId, softAskReplyId);
    return softAskReplyRepository.getVoteCount(softAskId, softAskReplyId);
  }

  private Integer decrementSoftAskReplyVoteAndGetVoteCount(final Long softAskId, final Long softAskReplyId) {
    softAskReplyRepository.decrementVoteCount(softAskId, softAskReplyId);
    return softAskReplyRepository.getVoteCount(softAskId, softAskReplyId);
  }

  @Override
  @Transactional
  public Integer updateVoteCount(final Long softAskId, final Long softAskReplyId, final boolean isVoted) {
    return isVoted
      ? incrementSoftAskReplyVoteAndGetVoteCount(softAskId, softAskReplyId)
      : decrementSoftAskReplyVoteAndGetVoteCount(softAskId, softAskReplyId);
  }

  @Override
  @Transactional
  public Integer incrementSoftAskReplyCountAndGetReplyCount(final Long softAskId) {
    softAskRepository.incrementReplyCount(softAskId);
    return softAskRepository.getReplyCount(softAskId);
  }

  @Override
  @Transactional
  public Integer incrementSoftAskReplyChildReplyCountAndGetReplyCount(final Long softAskId, final Long softAskReplyParentId) {
    softAskReplyRepository.incrementReplyChildReplyCount(softAskId, softAskReplyParentId);
    return softAskReplyRepository.getReplyChildReplyCount(softAskId, softAskReplyParentId);
  }

  private Integer incrementBookmarkCount(final Long softAskId) {
    softAskRepository.incrementAndBookmarkCount(softAskId);
    return softAskRepository.getBookmarkCount(softAskId);
  }

  private Integer decrementBookmarkCount(final Long softAskId) {
    softAskRepository.decrementAndGetBookmarkCount(softAskId);
    return softAskRepository.getBookmarkCount(softAskId);
  }

  @Override
  @Transactional
  public Integer updateBookmarkCount(final Long softAskId, final boolean isBookmarked) {
    return isBookmarked
      ? incrementBookmarkCount(softAskId)
      : decrementBookmarkCount(softAskId);
  }

  private Integer incrementSoftAskReplyBookmarkCount(final Long softAskId, final Long softAskReplyId) {
    softAskReplyRepository.incrementAndBookmarkCount(softAskId, softAskReplyId);
    return softAskReplyRepository.getBookmarkCount(softAskId, softAskReplyId);
  }

  private Integer decrementSoftAskReplyBookmarkCount(final Long softAskId, final Long softAskReplyId) {
    softAskReplyRepository.decrementAndGetBookmarkCount(softAskId, softAskReplyId);
    return softAskReplyRepository.getBookmarkCount(softAskId, softAskReplyId);
  }

  @Override
  @Transactional
  public Integer updateBookmarkCount(final Long softAskId, final Long softAskReplyId, final boolean isBookmarked) {
    return isBookmarked
      ? incrementSoftAskReplyBookmarkCount(softAskId, softAskReplyId)
      : decrementSoftAskReplyBookmarkCount(softAskId, softAskReplyId);
  }

  @Override
  public SoftAskParticipantDetail generateParticipantDetail(final Long softAskId, final Long userId) {
    return softAskParticipantDetailService.generateParticipantDetail(softAskId, userId);
  }

  @Override
  public SoftAskParticipantDetail getOrAssignUsername(final Long softAskId, final Long userId) {
    final GeneratedUsername generatedUsername = softAskParticipantDetailService.getOrAssignUsername(softAskId, userId);
    final String username = generatedUsername.username();
    final String displayName = generatedUsername.displayName();

    return SoftAskParticipantDetail.of(softAskId, userId, username, displayName);
  }

  /**
   * Sets the geohash and geohash prefix for the given soft ask data.
   *
   * <p>This method encodes the latitude and longitude of the provided
   * {@link SoftAskCommonData} into a geohash with precision 9 and derives
   * a geohash prefix of length 5. Both values are then stored in the
   * corresponding fields of the provided object. If the input is null,
   * the method performs no action.</p>
   *
   * @param softAskCommonData the soft ask data containing latitude and longitude,
   *                          may be {@code null}
   */
  @Override
  public void setGeoHashAndGeoPrefix(final SoftAskCommonData softAskCommonData) {
    if (nonNull(softAskCommonData)) {
      final String geoHash = geoService.encodeAndGetGeohash(softAskCommonData.getLatitude(), softAskCommonData.getLongitude(), 9);
      final String geohashPrefix = geoService.getGeohashPrefix(geoHash, 5);

      softAskCommonData.setGeoHash(geoHash);
      softAskCommonData.setGeoHashPrefix(geohashPrefix);
    }
  }

}
