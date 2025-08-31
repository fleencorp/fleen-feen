package com.fleencorp.feen.softask.mapper.impl;

import com.fleencorp.feen.common.model.info.ParentInfo;
import com.fleencorp.feen.common.model.info.UserLocationInfo;
import com.fleencorp.feen.common.service.misc.ObjectService;
import com.fleencorp.feen.common.util.common.DateTimeUtil;
import com.fleencorp.feen.mapper.impl.BaseMapper;
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

  public SoftAskCommonMapperImpl(
      final ObjectService objectService,
      final SoftAskInfoMapper softAskInfoMapper,
      final MessageSource messageSource) {
    super(messageSource);
    this.objectService = objectService;
    this.softAskInfoMapper = softAskInfoMapper;
  }

  @Override
  public void setOtherDetails(final SoftAskCommonData entry, final SoftAskCommonResponse response) {
    if (nonNull(entry) && nonNull(response)) {
      response.setIsUpdatable(false);

      final SoftAskVoteCountInfo voteCountInfo = softAskInfoMapper.toVoteCountInfo(entry.getVoteCount());
      response.setVoteCountInfo(voteCountInfo);

      final SoftAskUserVoteInfo softAskUserVoteInfo = softAskInfoMapper.toUserVoteInfo(false);
      response.setSoftAskUserVoteInfo(softAskUserVoteInfo);

      final ParentInfo parentInfo = ParentInfo.of(entry.getParentId(), entry.getParentTitle());
      response.setParentInfo(parentInfo);

      final Map<String, String> avatarUrls = objectService.getAvatarUrls(generateRandomNumberForAvatar());
      final SoftAskParticipantResponse softAskParticipantResponse = SoftAskParticipantResponse.of(
        entry.getUserAliasOrUsername(),
        avatarUrls
      );
      response.setSoftAskParticipantResponse(softAskParticipantResponse);

      final UserLocationInfo userLocationInfo = UserLocationInfo.of(entry.getLatitude(), entry.getLongitude());
      response.setUserLocationInfo(userLocationInfo);

      final MoodTagInfo moodTagInfo = toMoodTagInfo(entry.getMoodTag());
      response.setMoodTagInfo(moodTagInfo);

      final String displayTimeLabel = DateTimeUtil.formatTime(entry.getCreatedOn());
      response.setDisplayTimeLabel(displayTimeLabel);

      setEntityUpdatableByUser(response, entry.getAuthorId());
    }
  }

  private static String generateRandomNumberForAvatar() {
    return String.valueOf(ThreadLocalRandom.current().nextInt(1, 2_000_001));
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
        entry.getParentContent()
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

  private MoodTagInfo toMoodTagInfo(final MoodTag moodTag) {
    if (nonNull(moodTag)) {
      final String moodTagText = translate(moodTag.getMessageCode());
      return MoodTagInfo.of(moodTag, moodTag.getLabel(), moodTagText);
    }
    return null;
  }

}
