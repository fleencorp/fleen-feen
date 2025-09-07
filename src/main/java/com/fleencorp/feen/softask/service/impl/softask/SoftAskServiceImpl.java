package com.fleencorp.feen.softask.service.impl.softask;

import com.fleencorp.feen.common.model.info.IsDeletedInfo;
import com.fleencorp.feen.shared.chat.space.contract.IsAChatSpace;
import com.fleencorp.feen.shared.member.MemberNotFoundException;
import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.shared.stream.contract.IsAStream;
import com.fleencorp.feen.softask.constant.core.SoftAskParentType;
import com.fleencorp.feen.softask.contract.SoftAskCommonResponse;
import com.fleencorp.feen.softask.exception.core.SoftAskNotFoundException;
import com.fleencorp.feen.softask.exception.core.SoftAskParentNotFoundException;
import com.fleencorp.feen.softask.exception.core.SoftAskUpdateDeniedException;
import com.fleencorp.feen.softask.mapper.SoftAskMapper;
import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.domain.SoftAskParticipantDetail;
import com.fleencorp.feen.softask.model.dto.softask.AddSoftAskDto;
import com.fleencorp.feen.softask.model.dto.softask.DeleteSoftAskDto;
import com.fleencorp.feen.softask.model.factory.SoftAskFactory;
import com.fleencorp.feen.softask.model.holder.SoftAskParentDetailHolder;
import com.fleencorp.feen.softask.model.response.softask.SoftAskAddResponse;
import com.fleencorp.feen.softask.model.response.softask.SoftAskDeleteResponse;
import com.fleencorp.feen.softask.model.response.softask.core.SoftAskResponse;
import com.fleencorp.feen.softask.service.common.SoftAskCommonService;
import com.fleencorp.feen.softask.service.common.SoftAskOperationService;
import com.fleencorp.feen.softask.service.other.SoftAskQueryService;
import com.fleencorp.feen.softask.service.softask.SoftAskSearchService;
import com.fleencorp.feen.softask.service.softask.SoftAskService;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
public class SoftAskServiceImpl implements SoftAskService {

  private final SoftAskCommonService softAskCommonService;
  private final SoftAskSearchService softAskSearchService;
  private final SoftAskOperationService softAskOperationService;
  private final SoftAskQueryService softAskQueryService;
  private final SoftAskMapper softAskMapper;
  private final Localizer localizer;
  
  public SoftAskServiceImpl(
      final SoftAskCommonService softAskCommonService,
      final SoftAskOperationService softAskOperationService,
      final SoftAskSearchService softAskSearchService,
      final SoftAskQueryService softAskQueryService,
      final SoftAskMapper softAskMapper,
      final Localizer localizer) {
    this.softAskCommonService = softAskCommonService;
    this.softAskOperationService = softAskOperationService;
    this.softAskSearchService = softAskSearchService;
    this.softAskQueryService = softAskQueryService;
    this.softAskMapper = softAskMapper;
    this.localizer = localizer;
  }

  /**
   * Adds a new {@link SoftAsk} authored by the given {@link RegisteredUser}.
   *
   * <p>The method resolves the requesting member, validates and retrieves the parent context
   * (such as stream or chat space), constructs a new soft ask, persists it, and maps it into
   * a localized response object.</p>
   *
   * @param addSoftAskDto the DTO containing the details required to create a soft ask
   * @param user the user authoring the soft ask
   * @return a {@link SoftAskAddResponse} containing the created soft ask's identifier and details
   * @throws MemberNotFoundException if the member corresponding to the given user does not exist
   * @throws SoftAskParentNotFoundException if the specified chat space or stream parent cannot be found
   */
  @Override
  @Transactional
  public SoftAskAddResponse addSoftAsk(final AddSoftAskDto addSoftAskDto, final RegisteredUser user)
      throws MemberNotFoundException, SoftAskParentNotFoundException {
    final IsAMember member = softAskQueryService.findMemberOrThrow(user.getId());

    final SoftAskParentDetailHolder softAskParentDetailHolder = findAndValidateParent(addSoftAskDto);
    final SoftAskParentType parentType = softAskParentDetailHolder.parentType();
    final String parentTitle = softAskParentDetailHolder.parentTitle();

    final SoftAsk softAsk = SoftAskFactory.toSoftAsk(addSoftAskDto, parentTitle, parentType, member);
    softAskOperationService.setGeoHashAndGeoPrefix(softAsk);
    softAskOperationService.save(softAsk);

    final SoftAskParticipantDetail softAskParticipantDetail = softAskOperationService.generateParticipantDetail(softAsk.getSoftAskId(), user.getId());
    softAsk.setSoftAskParticipantDetail(softAskParticipantDetail);

    final SoftAskResponse softAskResponse = softAskMapper.toSoftAskResponse(softAsk);
    final Collection<SoftAskCommonResponse> softAskCommonResponses = List.of(softAskResponse);
    softAskCommonService.processSoftAskResponses(softAskCommonResponses, member, addSoftAskDto.getUserOtherDetail());

    final SoftAskAddResponse softAskAddResponse = SoftAskAddResponse.of(softAsk.getSoftAskId(), softAskResponse);
    return localizer.of(softAskAddResponse);
  }

  /**
   * Finds and validates the parent entity for a new {@link SoftAsk}.
   *
   * <p>The method checks whether the given {@link AddSoftAskDto} specifies a parent. If no parent is provided,
   * an empty {@link SoftAskParentDetailHolder} is returned. If a parent is specified, the method retrieves the
   * parent {@link IsAChatSpace} or {@link IsAStream} based on the parent type, and returns a detail holder
   * containing the resolved entities and type information.</p>
   *
   * @param addSoftAskDto the DTO containing details of the soft ask and its potential parent
   * @return a {@link SoftAskParentDetailHolder} encapsulating the resolved parent details
   * @throws MemberNotFoundException if the parent member does not exist
   * @throws SoftAskParentNotFoundException if the parent chat space or stream does not exist
   */
  private SoftAskParentDetailHolder findAndValidateParent(final AddSoftAskDto addSoftAskDto)
      throws MemberNotFoundException, SoftAskParentNotFoundException {

    if (addSoftAskDto.hasNoParent()) {
      return SoftAskParentDetailHolder.empty();
    }

    final Long parentId = addSoftAskDto.getParentId();

    IsAChatSpace chatSpace = addSoftAskDto.isChatSpaceParent() ? softAskQueryService.findChatSpaceOrThrow(parentId) : null;
    IsAStream stream = addSoftAskDto.isStreamParent() ? softAskQueryService.findStreamOrThrow(parentId) : null;

    return SoftAskParentDetailHolder.of(chatSpace, stream, addSoftAskDto.getParentType());
  }

  /**
   * Deletes the specified {@link SoftAsk} if the given {@link RegisteredUser} is the author.
   *
   * <p>The method retrieves the soft ask by ID, verifies that the requesting user is the author,
   * marks the soft ask as deleted, and returns a localized response containing the deletion status.</p>
   *
   * @param deleteSoftAskDto the DTO containing the ID of the soft ask to delete
   * @param user the user requesting the deletion
   * @return a {@link SoftAskDeleteResponse} indicating whether the soft ask was successfully deleted
   * @throws SoftAskNotFoundException if the soft ask with the given ID does not exist
   * @throws SoftAskUpdateDeniedException if the user is not authorized to delete the soft ask
   */
  @Override
  @Transactional
  public SoftAskDeleteResponse deleteSoftAsk(final DeleteSoftAskDto deleteSoftAskDto, final RegisteredUser user)
      throws SoftAskNotFoundException, SoftAskUpdateDeniedException {
    final Long softAskId = deleteSoftAskDto.getSoftAskId();
    final SoftAsk softAsk = softAskSearchService.findSoftAsk(softAskId);

    softAsk.checkIsAuthor(user.getId());
    softAsk.delete();

    final IsDeletedInfo isDeletedInfo = softAskMapper.toIsDeletedInfo(softAsk.isDeleted());
    final SoftAskDeleteResponse softAskDeleteResponse = SoftAskDeleteResponse.of(softAskId, isDeletedInfo);

    return localizer.of(softAskDeleteResponse);
  }
}
