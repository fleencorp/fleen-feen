package com.fleencorp.feen.softask.service.impl.softask;

import com.fleencorp.feen.chat.space.exception.core.ChatSpaceNotFoundException;
import com.fleencorp.feen.stream.exception.core.StreamNotFoundException;
import com.fleencorp.feen.mapper.common.UnifiedMapper;
import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.common.model.info.IsDeletedInfo;
import com.fleencorp.feen.chat.space.service.core.ChatSpaceOperationsService;
import com.fleencorp.feen.stream.service.common.StreamOperationsService;
import com.fleencorp.feen.softask.constant.core.SoftAskParentType;
import com.fleencorp.feen.softask.exception.core.SoftAskUpdateDeniedException;
import com.fleencorp.feen.softask.mapper.SoftAskMapper;
import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.dto.softask.AddSoftAskDto;
import com.fleencorp.feen.softask.model.dto.softask.DeleteSoftAskDto;
import com.fleencorp.feen.softask.model.holder.SoftAskParentDetailHolder;
import com.fleencorp.feen.softask.model.response.softask.SoftAskAddResponse;
import com.fleencorp.feen.softask.model.response.softask.SoftAskDeleteResponse;
import com.fleencorp.feen.softask.model.response.softask.core.SoftAskResponse;
import com.fleencorp.feen.softask.repository.softask.SoftAskRepository;
import com.fleencorp.feen.softask.service.softask.SoftAskSearchService;
import com.fleencorp.feen.softask.service.softask.SoftAskService;
import com.fleencorp.feen.user.exception.member.MemberNotFoundException;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import com.fleencorp.feen.user.service.member.MemberService;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SoftAskServiceImpl implements SoftAskService {
  
  private final ChatSpaceOperationsService chatSpaceOperationsService;
  private final MemberService memberService;
  private final StreamOperationsService streamOperationsService;
  private final SoftAskSearchService softAskSearchService;
  private final SoftAskRepository softAskRepository;
  private final SoftAskMapper softAskMapper;
  private final UnifiedMapper unifiedMapper;
  private final Localizer localizer;
  
  public SoftAskServiceImpl(
      final ChatSpaceOperationsService chatSpaceOperationsService,
      final MemberService memberService,
      final StreamOperationsService streamOperationsService,
      final SoftAskSearchService softAskSearchService,
      final SoftAskRepository softAskRepository,
      final SoftAskMapper softAskMapper,
      final UnifiedMapper unifiedMapper,
      final Localizer localizer) {
    this.chatSpaceOperationsService = chatSpaceOperationsService;
    this.memberService = memberService;
    this.streamOperationsService = streamOperationsService;
    this.softAskSearchService = softAskSearchService;
    this.softAskRepository = softAskRepository;
    this.softAskMapper = softAskMapper;
    this.unifiedMapper = unifiedMapper;
    this.localizer = localizer;
  }

  /**
   * Adds a new {@link SoftAsk} using the data from the given DTO and the user.
   *
   * <p>Validates and resolves the parent details, constructs a new SoftAsk entity,
   * saves it, maps it to a response, and returns a localized add response.</p>
   *
   * @param addSoftAskDto the DTO containing data for the new SoftAsk.
   * @param user the {@link RegisteredUser} adding the SoftAsk.
   * @return a localized {@link SoftAskAddResponse} containing the saved SoftAsk’s ID and response data.
   * @throws MemberNotFoundException if the member is not found.
   * @throws StreamNotFoundException if the stream parent is not found.
   * @throws ChatSpaceNotFoundException if the chat space parent is not found.
   */
  @Override
  @Transactional
  public SoftAskAddResponse addSoftAsk(final AddSoftAskDto addSoftAskDto, final RegisteredUser user)
      throws MemberNotFoundException, StreamNotFoundException, ChatSpaceNotFoundException {
    final Member member = memberService.findMember(user.getId());

    final SoftAskParentDetailHolder softAskParentDetailHolder = findAndValidateParent(addSoftAskDto);
    final SoftAskParentType parentType = softAskParentDetailHolder.parentType();
    final String parentTitle = softAskParentDetailHolder.parentTitle();
    final ChatSpace chatSpace = softAskParentDetailHolder.chatSpace();
    final FleenStream stream = softAskParentDetailHolder.stream();

    final SoftAsk softAsk = addSoftAskDto.toSoftAsk(member, parentTitle, parentType, chatSpace, stream);
    softAskRepository.save(softAsk);

    final SoftAskResponse softAskResponse = softAskMapper.toSoftAskResponse(softAsk);
    final SoftAskAddResponse softAskAddResponse = SoftAskAddResponse.of(softAsk.getSoftAskId(), softAskResponse);
    return localizer.of(softAskAddResponse);
  }

  /**
   * Finds and validates the parent entity for a new SoftAsk based on the DTO.
   *
   * <p>If the DTO has no parent, returns an empty holder.
   * Otherwise, attempts to find the parent as a {@link ChatSpace} or a {@link FleenStream}
   * depending on the DTO’s flags, throwing exceptions if not found.</p>
   *
   * @param addSoftAskDto the DTO containing parent info.
   * @return a {@link SoftAskParentDetailHolder} encapsulating the resolved parent details.
   * @throws MemberNotFoundException if the member is not found.
   * @throws ChatSpaceNotFoundException if the chat space parent is not found.
   * @throws StreamNotFoundException if the stream parent is not found.
   */
  private SoftAskParentDetailHolder findAndValidateParent(final AddSoftAskDto addSoftAskDto)
    throws MemberNotFoundException, ChatSpaceNotFoundException, StreamNotFoundException {

    if (addSoftAskDto.hasNoParent()) {
      return SoftAskParentDetailHolder.of();
    }

    final Long parentId = addSoftAskDto.getParentId();
    final ChatSpace chatSpace = addSoftAskDto.isChatSpaceParent() ? chatSpaceOperationsService.findChatSpace(parentId) : null;
    final FleenStream stream = addSoftAskDto.isStreamParent() ? streamOperationsService.findStream(parentId) : null;

    return SoftAskParentDetailHolder.of(chatSpace, stream);
  }

  /**
   * Deletes a {@link SoftAsk} after verifying the requesting user is the author.
   *
   * <p>Fetches the SoftAsk by ID, checks author permissions, marks it as deleted,
   * and returns a localized response indicating deletion status.</p>
   *
   * @param deleteSoftAskDto the DTO containing the ID of the SoftAsk to delete.
   * @param user the {@link RegisteredUser} requesting the deletion.
   * @return a localized {@link SoftAskDeleteResponse} indicating the outcome.
   * @throws SoftAskUpdateDeniedException if the user is not authorized to delete the SoftAsk.
   */
  @Override
  @Transactional
  public SoftAskDeleteResponse deleteSoftAsk(final DeleteSoftAskDto deleteSoftAskDto, final RegisteredUser user) throws SoftAskUpdateDeniedException {
    final Long softAskId = deleteSoftAskDto.getSoftAskId();
    final SoftAsk softAsk = softAskSearchService.findSoftAsk(softAskId);

    softAsk.checkIsAuthor(user.getId());
    softAsk.delete();

    final IsDeletedInfo isDeletedInfo = unifiedMapper.toIsDeletedInfo(softAsk.isDeleted());
    final SoftAskDeleteResponse softAskDeleteResponse = SoftAskDeleteResponse.of(softAskId, isDeletedInfo);

    return localizer.of(softAskDeleteResponse);
  }
}
