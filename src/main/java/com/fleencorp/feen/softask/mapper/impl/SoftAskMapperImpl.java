package com.fleencorp.feen.softask.mapper.impl;

import com.fleencorp.feen.common.model.info.IsDeletedInfo;
import com.fleencorp.feen.common.model.info.ParentInfo;
import com.fleencorp.feen.mapper.impl.BaseMapper;
import com.fleencorp.feen.mapper.info.ToInfoMapper;
import com.fleencorp.feen.shared.common.contract.IsAuthor;
import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.softask.contract.SoftAskCommonData;
import com.fleencorp.feen.softask.contract.SoftAskCommonResponse;
import com.fleencorp.feen.softask.mapper.SoftAskCommonMapper;
import com.fleencorp.feen.softask.mapper.SoftAskInfoMapper;
import com.fleencorp.feen.softask.mapper.SoftAskMapper;
import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.domain.SoftAskParticipantDetail;
import com.fleencorp.feen.softask.model.domain.SoftAskReply;
import com.fleencorp.feen.softask.model.domain.SoftAskVote;
import com.fleencorp.feen.softask.model.info.reply.SoftAskReplyCountInfo;
import com.fleencorp.feen.softask.model.projection.SoftAskReplyWithDetail;
import com.fleencorp.feen.softask.model.projection.SoftAskWithDetail;
import com.fleencorp.feen.softask.model.response.reply.core.SoftAskReplyResponse;
import com.fleencorp.feen.softask.model.response.softask.core.SoftAskResponse;
import com.fleencorp.feen.softask.model.response.vote.core.SoftAskVoteResponse;
import com.fleencorp.feen.softask.model.search.SoftAskReplySearchResult;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;

@Component
public final class SoftAskMapperImpl extends BaseMapper implements SoftAskMapper {

  private final SoftAskCommonMapper softAskCommonMapper;
  private final SoftAskInfoMapper softAskInfoMapper;
  private final ToInfoMapper toInfoMapper;

  public SoftAskMapperImpl(
      final SoftAskCommonMapper softAskCommonMapper,
      final SoftAskInfoMapper softAskInfoMapper,
      final ToInfoMapper toInfoMapper,
      final MessageSource messageSource) {
    super(messageSource);
    this.softAskCommonMapper = softAskCommonMapper;
    this.softAskInfoMapper = softAskInfoMapper;
    this.toInfoMapper = toInfoMapper;
  }

  @Override
  public SoftAskResponse toSoftAskResponse(final SoftAsk entry) {
    return toSoftAskResponse(entry, null);
  }

  /**
   * Converts a {@link SoftAsk} entity into a {@link SoftAskResponse} DTO.
   *
   * <p>The method maps core fields such as identifiers, title, description,
   * timestamps, and slug. It also includes reply count information,
   * initializes an empty reply search result, and attaches parent details.
   * Additional metadata such as author-related details are set using
   * helper methods. If the provided entry is {@code null}, the method
   * returns {@code null}.</p>
   *
   * @param entry the SoftAsk entity to convert
   * @param member the member viewing or interacting with the SoftAsk
   * @return a populated SoftAskResponse, or null if the entry is null
   */
  @Override
  public SoftAskResponse toSoftAskResponse(final SoftAsk entry, final IsAMember member) {
    if (nonNull(entry)) {
      final SoftAskResponse response = new SoftAskResponse();
      response.setId(entry.getSoftAskId());
      response.setMemberId(entry.getAuthorId());
      response.setTitle(entry.getTitle());
      response.setQuestion(entry.getDescription());

      response.setAuthorId(entry.getAuthorId());
      response.setOrganizerId(entry.getAuthorId());

      response.setCreatedOn(entry.getCreatedOn());
      response.setUpdatedOn(entry.getUpdatedOn());
      response.setSlug(entry.getSlug());

      final SoftAskReplyCountInfo softAskReplyCountInfo = softAskInfoMapper.toReplyCountInfo(entry.getReplyCount());
      response.setReplyCountInfo(softAskReplyCountInfo);

      final SoftAskReplySearchResult softAskReplySearchResult = SoftAskReplySearchResult.empty(entry.getSoftAskId());
      response.setSoftAskReplySearchResult(softAskReplySearchResult);

      final ParentInfo parentInfo = ParentInfo.of(entry.getParentId(), entry.getParentTitle());
      response.setParentInfo(parentInfo);

      setOtherDetails(entry, response);
      setIsAuthorDetails(entry, member, response);

      return response;
    }

    return null;
  }

  /**
   * Converts a collection of {@link SoftAskWithDetail} entities into a collection of {@link SoftAskResponse} DTOs.
   *
   * <p>If the input collection is {@code null}, an empty list is returned. Null elements in the collection
   * are filtered out before mapping.</p>
   *
   * @param entries the collection of {@link SoftAskWithDetail} entities to convert; can be {@code null}.
   * @return a list of corresponding {@link SoftAskResponse} instances; never {@code null}.
   */
  @Override
  public Collection<SoftAskResponse> toSoftAskResponses(final Collection<SoftAskWithDetail> entries) {
    if (nonNull(entries)) {
      return entries.stream()
        .filter(Objects::nonNull)
        .map(entry -> {
          final SoftAsk softAsk = entry.softAsk();
          final SoftAskParticipantDetail softAskParticipantDetail = entry.username();
          softAsk.setParticipant(softAskParticipantDetail);

          return toSoftAskResponse(softAsk);
      }).toList();
    }

    return List.of();
  }

  @Override
  public SoftAskReplyResponse toSoftAskReplyResponse(final SoftAskReply entry) {
    return toSoftAskReplyResponse(entry, null);
  }

  /**
   * Converts a {@link SoftAskReply} entity into a {@link SoftAskReplyResponse} DTO.
   *
   * <p>The method maps key fields such as identifiers, content, author information,
   * timestamps, and slug. It attaches parent details, reply count information,
   * and initializes an empty search result for child replies. Additional metadata
   * is set using helper methods. If the provided entry is {@code null}, the method
   * returns {@code null}.</p>
   *
   * @param entry the SoftAskReply entity to convert
   * @param member the member viewing or interacting with the reply
   * @return a populated SoftAskReplyResponse, or null if the entry is null
   */
  @Override
  public SoftAskReplyResponse toSoftAskReplyResponse(final SoftAskReply entry, final IsAMember member) {
    if (nonNull(entry)) {
      final SoftAskReplyResponse response = new SoftAskReplyResponse();
      response.setId(entry.getSoftAskReplyId());
      response.setMemberId(entry.getAuthorId());
      response.setContent(entry.getContent());

      response.setOrganizerId(entry.getAuthorId());
      response.setAuthorId(entry.getAuthorId());

      response.setSlug(entry.getSlug());
      response.setCreatedOn(entry.getCreatedOn());
      response.setUpdatedOn(entry.getUpdatedOn());

      final ParentInfo parentInfo = ParentInfo.of(entry.getParentId(), entry.getSoftAskId(), entry.getParentTitle());
      response.setParentInfo(parentInfo);

      final SoftAskReplyCountInfo replyCountInfo = softAskInfoMapper.toReplyCountInfo(entry.getChildReplyCount());
      response.setReplyCountInfo(replyCountInfo);

      final SoftAskReplySearchResult searchResult = SoftAskReplySearchResult.empty(entry.getSoftAskReplyId());
      response.setChildRepliesSearchResult(searchResult);

      setOtherDetails(entry, response);
      setIsAuthorDetails(entry, member, response);

      return response;
    }

    return null;
  }

  /**
   * Converts a collection of {@link SoftAskReplyWithDetail} entities into a collection of {@link SoftAskReplyResponse} DTOs.
   *
   * <p>If the input collection is {@code null}, an empty list is returned. Null elements within the collection
   * are filtered out before conversion using {@code toSoftAskReplyResponse}.</p>
   *
   * @param entries the collection of {@link SoftAskReplyWithDetail} entities to convert; can be {@code null}.
   * @return a list of corresponding {@link SoftAskReplyResponse} instances; never {@code null}.
   */
  @Override
  public Collection<SoftAskReplyResponse> toSoftAskReplyResponses(final Collection<SoftAskReplyWithDetail> entries) {
    if (nonNull(entries)) {
      return entries.stream()
        .filter(Objects::nonNull)
        .map(entry -> {
          final SoftAskReply reply = entry.reply();
          final SoftAskParticipantDetail participantDetail = entry.username();
          reply.setSoftAskParticipantDetail(participantDetail);

          return toSoftAskReplyResponse(reply);
        }).toList();
    }

    return List.of();
  }

  @Override
  public SoftAskVoteResponse toSoftAskVoteResponse(final SoftAskVote entry, final boolean voted) {
    return softAskCommonMapper.toSoftAskVoteResponse(entry, voted);
  }

  @Override
  public Collection<SoftAskVoteResponse> toSoftAskVoteResponses(final Collection<SoftAskVote> entries) {
    return softAskCommonMapper.toSoftAskVoteResponses(entries);
  }

  @Override
  public IsDeletedInfo toIsDeletedInfo(final boolean isDeleted) {
    return toInfoMapper.toIsDeletedInfo(isDeleted);
  }

  private static void setIsAuthorDetails(final SoftAskCommonData entry, final IsAMember member, SoftAskCommonResponse response) {
    if (IsAuthor.isAuthor(member, entry.getAuthorId())) {
      response.markAsAuthor();
    }
  }

  private void setOtherDetails(final SoftAskCommonData entry, final SoftAskCommonResponse response) {
    softAskCommonMapper.setOtherDetails(entry, response);
  }

}
