package com.fleencorp.feen.softask.mapper.impl;

import com.fleencorp.feen.common.model.info.ParentInfo;
import com.fleencorp.feen.common.model.info.ShareCountInfo;
import com.fleencorp.feen.common.model.info.UserLocationInfo;
import com.fleencorp.feen.common.service.misc.ObjectService;
import com.fleencorp.feen.common.util.common.DateTimeUtil;
import com.fleencorp.feen.mapper.impl.BaseMapper;
import com.fleencorp.feen.mapper.info.ToInfoMapper;
import com.fleencorp.feen.softask.constant.other.MoodTag;
import com.fleencorp.feen.softask.contract.SoftAskCommonData;
import com.fleencorp.feen.softask.contract.SoftAskCommonResponse;
import com.fleencorp.feen.softask.mapper.SoftAskCommonMapper;
import com.fleencorp.feen.softask.mapper.SoftAskInfoMapper;
import com.fleencorp.feen.softask.model.domain.SoftAskVote;
import com.fleencorp.feen.softask.model.info.core.MoodTagInfo;
import com.fleencorp.feen.softask.model.info.vote.SoftAskUserVoteInfo;
import com.fleencorp.feen.softask.model.info.vote.SoftAskVoteCountInfo;
import com.fleencorp.feen.softask.model.response.participant.SoftAskParticipantResponse;
import com.fleencorp.feen.softask.model.response.vote.core.SoftAskVoteResponse;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.fleencorp.feen.common.service.impl.misc.MiscServiceImpl.setEntityUpdatableByUser;
import static java.util.Objects.nonNull;

@Component
public class SoftAskCommonMapperImpl extends BaseMapper implements SoftAskCommonMapper {

  private final ObjectService objectService;
  private final SoftAskInfoMapper softAskInfoMapper;
  private final ToInfoMapper toInfoMapper;

  public SoftAskCommonMapperImpl(
      final ObjectService objectService,
      final SoftAskInfoMapper softAskInfoMapper,
      final ToInfoMapper toInfoMapper,
      final MessageSource messageSource) {
    super(messageSource);
    this.objectService = objectService;
    this.softAskInfoMapper = softAskInfoMapper;
    this.toInfoMapper = toInfoMapper;
  }

  /**
   * Populates additional details of a soft ask response based on the provided entry data.
   *
   * <p>If both {@code entry} and {@code response} are not {@code null}, this method enriches
   * the response with various contextual details about the soft ask. These include vote
   * information, parent reference, participant details with avatar URLs, geographic data,
   * mood tags, and a display time label. It also sets whether the entity can be updated by
   * the user. The response is therefore augmented with metadata that supports rendering
   * and interaction in the client application.</p>
   *
   * @param entry the source object containing soft ask data
   * @param response the response object to be populated with additional details
   */
  @Override
  public void setOtherDetails(final SoftAskCommonData entry, final SoftAskCommonResponse response) {
    if (nonNull(entry) && nonNull(response)) {
      response.setIsUpdatable(false);

      toInfoMapper.setBookmarkInfo(response, false, entry.getBookmarkCount());

      final SoftAskVoteCountInfo voteCountInfo = softAskInfoMapper.toVoteCountInfo(entry.getVoteCount());
      response.setVoteCountInfo(voteCountInfo);

      final SoftAskUserVoteInfo userVoteInfo = softAskInfoMapper.toUserVoteInfo(false);
      response.setSoftAskUserVoteInfo(userVoteInfo);

      final ShareCountInfo shareCountInfo = toInfoMapper.toShareCountInfo(entry.getShareCount());
      response.setShareCountInfo(shareCountInfo);

      final Map<String, String> avatarUrls = ObjectService.getAvatarUrls(entry.getAvatarUrl());
      final SoftAskParticipantResponse participantResponse = SoftAskParticipantResponse.of(
        entry.getUserAliasOrUsername(),
        entry.getUserDisplayName(),
        avatarUrls
      );
      response.setSoftAskParticipantResponse(participantResponse);

      final UserLocationInfo userLocationInfo = UserLocationInfo.of(entry.getLatitude(), entry.getLongitude());
      response.setUserLocationInfo(userLocationInfo);

      final MoodTagInfo moodTagInfo = toMoodTagInfo(entry.getMoodTag());
      response.setMoodTagInfo(moodTagInfo);

      final String displayTimeLabel = DateTimeUtil.formatTime(entry.getCreatedOn());
      response.setDisplayTimeLabel(displayTimeLabel);

      setEntityUpdatableByUser(response, entry.getAuthorId());
    }
  }

  /**
   * Generates a random number within the range {@code 1} to {@code 2,000,000} (inclusive)
   * and returns it as a string.
   *
   * <p>This method uses {@link ThreadLocalRandom} to produce a uniformly distributed
   * random integer suitable for use as an avatar identifier. The generated value is
   * converted to its string representation before being returned.</p>
   *
   * @return a string representing the randomly generated number between 1 and 2,000,000
   */
  public static String generateRandomNumberForAvatar() {
    final int randoNumber = ThreadLocalRandom.current().nextInt(1, 2_000_001);
    return Integer.toString(randoNumber);
  }

  /**
   * Converts a {@link SoftAskVote} entity into a {@link SoftAskVoteResponse} DTO.
   *
   * <p>Maps fields such as vote ID and timestamps, constructs the parent info object,
   * and assigns a user vote info object with a fixed vote state of {@code true}.</p>
   *
   * @param entry the {@link SoftAskVote} entity to convert; can be {@code null}.
   * @return the corresponding {@link SoftAskVoteResponse} object, or {@code null} if the input is {@code null}.
   */
  @Override
  public SoftAskVoteResponse toSoftAskVoteResponse(final SoftAskVote entry) {
    if (nonNull(entry)) {
      final SoftAskVoteResponse response = new SoftAskVoteResponse();
      response.setId(entry.getVoteId());
      response.setCreatedOn(entry.getCreatedOn());
      response.setUpdatedOn(entry.getUpdatedOn());

      final ParentInfo parentInfo = ParentInfo.of(
        entry.getParentId(),
        entry.getParentTitle(),
        entry.getParentSummary()
      );
      response.setParentInfo(parentInfo);

      final SoftAskUserVoteInfo softAskUserVoteInfo = softAskInfoMapper.toUserVoteInfo(true);
      response.setUserVoteInfo(softAskUserVoteInfo);

      return response;
    }

    return null;
  }

  /**
   * Converts a {@link SoftAskVote} entity into a {@link SoftAskVoteResponse} DTO using the given vote state.
   *
   * <p>Maps vote ID, timestamps, and parent info. Sets the user vote info using the provided {@code voted} flag.</p>
   *
   * @param entry the {@link SoftAskVote} entity to convert; can be {@code null}.
   * @param voted {@code true} if the user has voted; {@code false} otherwise.
   * @return the corresponding {@link SoftAskVoteResponse} object, or {@code null} if the input is {@code null}.
   */
  @Override
  public SoftAskVoteResponse toSoftAskVoteResponse(final SoftAskVote entry, final boolean voted) {
    if (nonNull(entry)) {
      final SoftAskVoteResponse response = new SoftAskVoteResponse();
      response.setId(entry.getVoteId());
      response.setVoteType(entry.getVoteType());
      response.setCreatedOn(entry.getCreatedOn());
      response.setUpdatedOn(entry.getUpdatedOn());

      final ParentInfo parentInfo = ParentInfo.of(entry.getParentId());
      response.setParentInfo(parentInfo);

      final SoftAskUserVoteInfo softAskUserVoteInfo = softAskInfoMapper.toUserVoteInfo(voted);
      response.setUserVoteInfo(softAskUserVoteInfo);

      return response;
    }

    return null;
  }

  /**
   * Converts a collection of {@link SoftAskVote} entities into a collection of {@link SoftAskVoteResponse} DTOs.
   *
   * <p>If the input collection is {@code null}, an empty list is returned. Null elements within the collection
   * are filtered out before conversion using {@code toSoftAskVoteResponse}.</p>
   *
   * @param entries the collection of {@link SoftAskVote} entities to convert; can be {@code null}.
   * @return a list of corresponding {@link SoftAskVoteResponse} instances; never {@code null}.
   */
  @Override
  public Collection<SoftAskVoteResponse> toSoftAskVoteResponses(final Collection<SoftAskVote> entries) {
    return Optional.ofNullable(entries)
      .orElseGet(Collections::emptyList)
      .stream()
      .filter(Objects::nonNull)
      .map(this::toSoftAskVoteResponse)
      .toList();
  }

  /**
   * Converts a {@link MoodTag} into a {@link MoodTagInfo} representation.
   *
   * <p>If the provided {@code moodTag} is not {@code null}, this method creates a
   * {@link MoodTagInfo} containing the tag itself, its label, and a translated
   * text based on the tag’s message code. If the input is {@code null}, the method
   * returns {@code null}.</p>
   *
   * @param moodTag the mood tag to be converted
   * @return a {@link MoodTagInfo} object representing the given tag, or {@code null} if the input is {@code null}
   */
  private MoodTagInfo toMoodTagInfo(final MoodTag moodTag) {
    if (nonNull(moodTag)) {
      final String moodTagText = translate(moodTag.getMessageCode());
      return MoodTagInfo.of(moodTag, moodTag.getLabel(), moodTagText);
    }

    return null;
  }

}
