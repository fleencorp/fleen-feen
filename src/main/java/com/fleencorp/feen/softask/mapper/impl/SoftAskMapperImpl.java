package com.fleencorp.feen.softask.mapper.impl;

import com.fleencorp.feen.common.model.info.IsDeletedInfo;
import com.fleencorp.feen.common.model.info.ParentInfo;
import com.fleencorp.feen.mapper.impl.BaseMapper;
import com.fleencorp.feen.mapper.info.ToInfoMapper;
import com.fleencorp.feen.softask.contract.SoftAskCommonData;
import com.fleencorp.feen.softask.contract.SoftAskCommonResponse;
import com.fleencorp.feen.softask.mapper.SoftAskInfoMapper;
import com.fleencorp.feen.softask.mapper.SoftAskMapper;
import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.domain.SoftAskReply;
import com.fleencorp.feen.softask.model.domain.SoftAskVote;
import com.fleencorp.feen.softask.model.info.SoftAskReplyCountInfo;
import com.fleencorp.feen.softask.model.info.vote.SoftAskUserVoteInfo;
import com.fleencorp.feen.softask.model.info.vote.SoftAskVoteCountInfo;
import com.fleencorp.feen.softask.model.response.participant.SoftAskParticipantResponse;
import com.fleencorp.feen.softask.model.response.reply.core.SoftAskReplyResponse;
import com.fleencorp.feen.softask.model.response.softask.core.SoftAskResponse;
import com.fleencorp.feen.softask.model.response.vote.core.SoftAskVoteResponse;
import com.fleencorp.feen.softask.model.search.SoftAskReplySearchResult;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.nonNull;

@Component
public final class SoftAskMapperImpl extends BaseMapper implements SoftAskMapper {

  private final SoftAskInfoMapper softAskInfoMapper;
  private final ToInfoMapper toInfoMapper;

  public SoftAskMapperImpl(
    final SoftAskInfoMapper softAskInfoMapper,
    final ToInfoMapper toInfoMapper,
    final MessageSource messageSource) {
    super(messageSource);
    this.softAskInfoMapper = softAskInfoMapper;
    this.toInfoMapper = toInfoMapper;
  }

  /**
   * Converts a {@link SoftAsk} entity into a {@link SoftAskResponse} DTO.
   *
   * <p>Maps fields such as ID, title, content, timestamps, author ID, and reply count information.
   * Also delegates additional detail population to {@code setOtherDetails}.</p>
   *
   * @param entry the {@link SoftAsk} entity to convert; can be {@code null}.
   * @return the corresponding {@link SoftAskResponse} object, or {@code null} if the input is {@code null}.
   */
  @Override
  public SoftAskResponse toSoftAskResponse(final SoftAsk entry) {
    if (nonNull(entry)) {
      final SoftAskResponse response = new SoftAskResponse();
      response.setId(entry.getSoftAskId());
      response.setMemberId(entry.getAuthorId());
      response.setTitle(entry.getTitle());
      response.setContent(entry.getDescription());
      response.setShareCount(entry.getShareCount());

      response.setAuthorId(entry.getAuthorId());
      response.setOrganizerId(entry.getAuthorId());
      response.setIsUpdatable(false);

      response.setCreatedOn(entry.getCreatedOn());
      response.setUpdatedOn(entry.getUpdatedOn());

      toInfoMapper.setBookmarkInfo(response, false, entry.getBookmarkCount());

      final SoftAskReplyCountInfo softAskReplyCountInfo = softAskInfoMapper.toReplyCountInfo(entry.getReplyCount());
      response.setReplyCountInfo(softAskReplyCountInfo);

      final SoftAskReplySearchResult softAskReplySearchResult = SoftAskReplySearchResult.empty(entry.getSoftAskId());
      response.setSoftAskReplySearchResult(softAskReplySearchResult);

      setOtherDetails(entry, response);

      return response;
    }

    return null;
  }

  /**
   * Converts a collection of {@link SoftAsk} entities into a collection of {@link SoftAskResponse} DTOs.
   *
   * <p>If the input collection is {@code null}, an empty list is returned. Null elements in the collection
   * are filtered out before mapping.</p>
   *
   * @param entries the collection of {@link SoftAsk} entities to convert; can be {@code null}.
   * @return a list of corresponding {@link SoftAskResponse} instances; never {@code null}.
   */
  @Override
  public Collection<SoftAskResponse> toSoftAskResponses(final Collection<SoftAsk> entries) {
    return Optional.ofNullable(entries)
      .orElseGet(Collections::emptyList)
      .stream()
      .filter(Objects::nonNull)
      .map(this::toSoftAskResponse)
      .toList();
  }

  /**
   * Converts a {@link SoftAskReply} entity into a {@link SoftAskReplyResponse} DTO.
   *
   * <p>Maps fields such as ID, author ID, content, timestamps, and member ID,
   * and delegates additional detail population to {@code setOtherDetails}.</p>
   *
   * @param entry the {@link SoftAskReply} entity to convert; can be {@code null}.
   * @return the corresponding {@link SoftAskReplyResponse} object, or {@code null} if the input is {@code null}.
   */
  @Override
  public SoftAskReplyResponse toSoftAskReplyResponse(final SoftAskReply entry) {
    if (nonNull(entry)) {
      final SoftAskReplyResponse response = new SoftAskReplyResponse();
      response.setId(entry.getSoftAskReplyId());
      response.setMemberId(entry.getAuthorId());
      response.setContent(entry.getContent());

      response.setAuthorId(entry.getAuthorId());
      response.setOrganizerId(entry.getAuthorId());
      response.setShareCount(entry.getShareCount());
      response.setIsUpdatable(false);

      response.setCreatedOn(entry.getCreatedOn());
      response.setUpdatedOn(entry.getUpdatedOn());

      toInfoMapper.setBookmarkInfo(response, false, entry.getBookmarkCount());

      final SoftAskReplyCountInfo replyCountInfo = softAskInfoMapper.toReplyCountInfo(entry.getChildReplyCount());
      response.setReplyCountInfo(replyCountInfo);

      final SoftAskReplySearchResult searchResult = SoftAskReplySearchResult.empty(entry.getSoftAskReplyId());
      response.setChildRepliesSearchResult(searchResult);

      setOtherDetails(entry, response);

      return response;
    }

    return null;
  }

  /**
   * Converts a collection of {@link SoftAskReply} entities into a collection of {@link SoftAskReplyResponse} DTOs.
   *
   * <p>If the input collection is {@code null}, an empty list is returned. Null elements within the collection
   * are filtered out before conversion using {@code toSoftAskReplyResponse}.</p>
   *
   * @param entries the collection of {@link SoftAskReply} entities to convert; can be {@code null}.
   * @return a list of corresponding {@link SoftAskReplyResponse} instances; never {@code null}.
   */
  @Override
  public Collection<SoftAskReplyResponse> toSoftAskReplyResponses(final Collection<SoftAskReply> entries) {
    return Optional.ofNullable(entries)
      .orElseGet(Collections::emptyList)
      .stream()
      .filter(Objects::nonNull)
      .map(this::toSoftAskReplyResponse)
      .toList();
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

  /**
   * Populates additional fields on a {@link SoftAskCommonResponse} object using data from a {@link SoftAskCommonData} entry.
   *
   * <p>Sets vote info, vote count, parent information, and participant details on the response
   * using helper mappers and static factory methods.</p>
   *
   * @param entry the {@link SoftAskCommonData} source entity; must not be {@code null}.
   * @param response the {@link SoftAskCommonResponse} target DTO; must not be {@code null}.
   */
  private void setOtherDetails(final SoftAskCommonData entry, final SoftAskCommonResponse response) {
    if (nonNull(entry) && nonNull(response)) {
      final SoftAskVoteCountInfo voteCountInfo = softAskInfoMapper.toVoteCountInfo(entry.getVoteCount());
      response.setVoteCountInfo(voteCountInfo);

      final SoftAskUserVoteInfo softAskUserVoteInfo = softAskInfoMapper.toUserVoteInfo(false);
      response.setSoftAskUserVoteInfo(softAskUserVoteInfo);

      final ParentInfo parentInfo = ParentInfo.of(entry.getParentId(), entry.getParentTitle());
      response.setParentInfo(parentInfo);

      final SoftAskParticipantResponse softAskParticipantResponse = SoftAskParticipantResponse.of(entry.getUserOtherName());
      response.setSoftAskParticipantResponse(softAskParticipantResponse);
    }
  }

  @Override
  public IsDeletedInfo toIsDeletedInfo(final boolean isDeleted) {
    return toInfoMapper.toIsDeletedInfo(isDeleted);
  }
}
