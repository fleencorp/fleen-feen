package com.fleencorp.feen.softask.service.impl.reply;

import com.fleencorp.feen.common.model.info.IsDeletedInfo;
import com.fleencorp.feen.mapper.common.UnifiedMapper;
import com.fleencorp.feen.softask.contract.SoftAskCommonResponse;
import com.fleencorp.feen.softask.exception.core.SoftAskNotFoundException;
import com.fleencorp.feen.softask.exception.core.SoftAskReplyNotFoundException;
import com.fleencorp.feen.softask.exception.core.SoftAskUpdateDeniedException;
import com.fleencorp.feen.softask.mapper.SoftAskMapper;
import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.domain.SoftAskReply;
import com.fleencorp.feen.softask.model.dto.reply.AddSoftAskReplyDto;
import com.fleencorp.feen.softask.model.dto.reply.DeleteSoftAskReplyDto;
import com.fleencorp.feen.softask.model.holder.SoftAskReplyParentDetailHolder;
import com.fleencorp.feen.softask.model.response.reply.SoftAskReplyAddResponse;
import com.fleencorp.feen.softask.model.response.reply.SoftAskReplyDeleteResponse;
import com.fleencorp.feen.softask.model.response.reply.core.SoftAskReplyResponse;
import com.fleencorp.feen.softask.repository.reply.SoftAskReplyRepository;
import com.fleencorp.feen.softask.service.common.SoftAskCommonService;
import com.fleencorp.feen.softask.service.common.SoftAskOperationService;
import com.fleencorp.feen.softask.service.reply.SoftAskReplyService;
import com.fleencorp.feen.softask.service.softask.SoftAskSearchService;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import com.fleencorp.feen.user.service.member.MemberService;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
public class SoftAskReplyServiceImpl implements SoftAskReplyService {

  private final MemberService memberService;
  private final SoftAskCommonService softAskCommonService;
  private final SoftAskOperationService softAskOperationService;
  private final SoftAskReplyRepository softAskReplyRepository;
  private final SoftAskSearchService softAskSearchService;
  private final SoftAskMapper softAskMapper;
  private final UnifiedMapper unifiedMapper;
  private final Localizer localizer;

  public SoftAskReplyServiceImpl(
      final MemberService memberService,
      @Lazy final SoftAskCommonService softAskCommonService,
      final SoftAskOperationService softAskOperationService,
      final SoftAskReplyRepository softAskReplyRepository,
      final SoftAskSearchService softAskSearchService,
      final SoftAskMapper softAskMapper,
      final UnifiedMapper unifiedMapper,
      final Localizer localizer) {
    this.memberService = memberService;
    this.softAskCommonService = softAskCommonService;
    this.softAskOperationService = softAskOperationService;
    this.softAskReplyRepository = softAskReplyRepository;
    this.softAskSearchService = softAskSearchService;
    this.softAskMapper = softAskMapper;
    this.unifiedMapper = unifiedMapper;
    this.localizer = localizer;
  }

  /**
   * Adds a new {@code SoftAskReply} to a {@code SoftAsk} or to an existing {@code SoftAskReply}.
   *
   * <p>This method creates a reply from the given DTO and user information, associates it either with
   * the main {@code SoftAsk} or with a parent reply (if provided), and persists it in the repository.
   * After saving, it updates the appropriate reply count (either for the main SoftAsk or for a
   * parent reply) and processes the reply for additional soft-ask-related operations.</p>
   *
   * <p>The response includes the updated reply count and details of the newly added reply, localized
   * for the requesting user.</p>
   *
   * @param addSoftAskReplyDto the DTO containing reply creation details, including target {@code SoftAsk} and optional parent reply
   * @param user               the registered user creating the reply
   * @return a localized {@link SoftAskReplyAddResponse} containing the new reply’s details and the updated reply count
   * @throws SoftAskReplyNotFoundException if a specified parent reply does not exist
   * @throws SoftAskNotFoundException      if the target {@code SoftAsk} does not exist
   */
  @Override
  @Transactional
  public SoftAskReplyAddResponse addSoftAskReply(final AddSoftAskReplyDto addSoftAskReplyDto, final RegisteredUser user) {
    final Member author = memberService.findMember(user.getId());
    final Long softAskId = addSoftAskReplyDto.getSoftAskId();
    final Long softAskParentReplyId = addSoftAskReplyDto.getSoftAskParentReplyId();

    final SoftAskReplyParentDetailHolder softAskReplyParentDetailHolder = findSoftAskReplyParentDetailHolder(addSoftAskReplyDto, softAskId, softAskParentReplyId);
    final SoftAsk softAsk = softAskReplyParentDetailHolder.softAsk();
    final SoftAskReply softAskParentReply = softAskReplyParentDetailHolder.softAskReply();

    final SoftAskReply reply = addSoftAskReplyDto.toSoftAskReply(author, softAsk, softAskParentReply);
    softAskReplyRepository.save(reply);
    final Integer replyCount = updateReplyCountOfSoftAskOrSoftAskReply(addSoftAskReplyDto, softAskId, softAskParentReplyId);

    final SoftAskReplyResponse softAskReplyResponse = softAskMapper.toSoftAskReplyResponse(reply);
    final Collection<SoftAskCommonResponse> softAskCommonResponses = List.of(softAskReplyResponse);
    softAskCommonService.processSoftAskResponses(softAskCommonResponses, author);

    final SoftAskReplyAddResponse softAskReplyAddResponse = SoftAskReplyAddResponse.of(replyCount, softAskReplyResponse);
    return localizer.of(softAskReplyAddResponse);
  }

  /**
   * Deletes a {@code SoftAskReply} by marking it as deleted and persisting the change.
   *
   * <p>This method locates the target reply using {@link #findSoftAskReplyToDelete(Long, DeleteSoftAskReplyDto, Long, Long)},
   * verifies that the requesting user is the author, marks the reply as deleted, and saves the updated entity.</p>
   *
   * <p>The deletion is logical rather than physical, allowing the reply to remain in storage while
   * being flagged as deleted. A localized {@link SoftAskReplyDeleteResponse} is returned with the
   * updated deletion status.</p>
   *
   * @param softAskReplyId        the ID of the reply to delete
   * @param deleteSoftAskReplyDto the DTO containing deletion parameters
   * @param user                  the user requesting deletion
   * @return a localized response containing the reply ID and its deletion status
   * @throws SoftAskUpdateDeniedException if the user is not authorized to delete the reply
   * @throws SoftAskReplyNotFoundException if the reply cannot be found
   */
  @Override
  @Transactional
  public SoftAskReplyDeleteResponse deleteSoftAskReply(final Long softAskReplyId, final DeleteSoftAskReplyDto deleteSoftAskReplyDto, final RegisteredUser user)
      throws SoftAskUpdateDeniedException {
    final Long softAskId = deleteSoftAskReplyDto.getSoftAskId();
    final Long softAskParentReplyId = deleteSoftAskReplyDto.getSoftAskParentReplyId();

    final SoftAskReply softAskReply = findSoftAskReplyToDelete(softAskReplyId, deleteSoftAskReplyDto, softAskId, softAskParentReplyId);
    softAskReply.checkIsAuthor(user.getId());
    softAskReply.delete();
    softAskReplyRepository.save(softAskReply);

    final IsDeletedInfo isDeletedInfo = unifiedMapper.toIsDeletedInfo(softAskReply.isDeleted());
    final SoftAskReplyDeleteResponse softAskReplyDeleteResponse = SoftAskReplyDeleteResponse.of(softAskReplyId, isDeletedInfo);

    return localizer.of(softAskReplyDeleteResponse);
  }

  /**
   * Retrieves the details of the parent entity for a {@code SoftAskReply}.
   *
   * <p>The parent can be either another {@code SoftAskReply} when replying to an existing reply, or
   * the {@code SoftAsk} itself when replying directly to the main SoftAsk. If the DTO indicates that
   * there is a parent reply, this method fetches that reply along with its associated
   * {@code SoftAsk}. Otherwise, it retrieves the {@code SoftAsk} directly.</p>
   *
   * <p>If the specified parent reply cannot be found, a {@link SoftAskReplyNotFoundException} is
   * thrown.</p>
   *
   * @param addSoftAskReplyDto   the DTO containing reply creation details, including whether this reply has a parent reply
   * @param softAskId            the ID of the {@code SoftAsk} to which the reply belongs
   * @param softAskParentReplyId the ID of the parent reply if this is a nested reply, or {@code null} if replying directly to the {@code SoftAsk}
   * @return a {@link SoftAskReplyParentDetailHolder} containing both the {@code SoftAsk} and the optional parent {@code SoftAskReply}
   * @throws SoftAskReplyNotFoundException if a specified parent reply cannot be found
   */
  private SoftAskReplyParentDetailHolder findSoftAskReplyParentDetailHolder(final AddSoftAskReplyDto addSoftAskReplyDto, final Long softAskId, final Long softAskParentReplyId) {
    final SoftAskReply softAskParentReply;
    final SoftAsk softAsk;

    if (addSoftAskReplyDto.hasSoftAskParentReply()) {
      softAskParentReply = softAskReplyRepository.findBySoftAskAndParentReply(softAskId, softAskParentReplyId)
        .orElseThrow(() -> new SoftAskReplyNotFoundException(softAskParentReplyId));
      softAsk = softAskParentReply.getSoftAsk();
    } else {
      softAskParentReply = null;
      softAsk = softAskSearchService.findSoftAsk(softAskId);
    }

    return SoftAskReplyParentDetailHolder.of(softAsk, softAskParentReply);
  }

  /**
   * Updates the reply count for either a {@code SoftAsk} or a child {@code SoftAskReply}
   * depending on whether the provided {@link AddSoftAskReplyDto} represents a reply
   * to another reply or a direct reply to the main {@code SoftAsk}.
   *
   * <p>If the DTO indicates that it is replying to a parent reply, the child reply count
   * of that parent reply is incremented. Otherwise, the main reply count of the {@code SoftAsk}
   * itself is incremented.</p>
   *
   * @param addSoftAskReplyDto     the DTO containing information about the reply being added,
   *                                including whether it has a parent reply
   * @param softAskId              the ID of the {@code SoftAsk} to which the reply belongs
   * @param softAskParentReplyId   the ID of the parent reply if this is a nested reply,
   *                                or {@code null} if replying directly to the {@code SoftAsk}
   * @return the updated reply count after incrementing
   */
  private Integer updateReplyCountOfSoftAskOrSoftAskReply(final AddSoftAskReplyDto addSoftAskReplyDto, final Long softAskId, final Long softAskParentReplyId) {
    return addSoftAskReplyDto.hasSoftAskParentReply()
      ? softAskOperationService.incrementSoftAskReplyChildReplyCountAndGetReplyCount(softAskId, softAskParentReplyId)
      : softAskOperationService.incrementSoftAskReplyCountAndGetReplyCount(softAskId);
  }

  /**
   * Finds a {@code SoftAskReply} targeted for deletion.
   *
   * <p>This method retrieves a reply by its ID, associated {@code SoftAsk}, and optionally its
   * parent reply if the deletion request specifies one. It ensures that the reply exists before
   * deletion and throws an exception if it cannot be found.</p>
   *
   * <p>If {@link DeleteSoftAskReplyDto#hasSoftAskParentReply()} returns {@code true}, the method
   * searches for a reply under the given parent reply. Otherwise, it searches for a direct reply
   * under the specified {@code SoftAsk}.</p>
   *
   * @param softAskReplyId        the ID of the reply to delete
   * @param deleteSoftAskReplyDto the DTO containing deletion parameters
   * @param softAskId             the ID of the {@code SoftAsk} the reply belongs to
   * @param softAskParentReplyId  the ID of the parent reply, if applicable
   * @return the {@link SoftAskReply} entity to be deleted
   * @throws SoftAskReplyNotFoundException if the reply cannot be found
   */
  private SoftAskReply findSoftAskReplyToDelete(final Long softAskReplyId, final DeleteSoftAskReplyDto deleteSoftAskReplyDto, final Long softAskId, final Long softAskParentReplyId) {
    final SoftAskReply softAskReply;
    if (deleteSoftAskReplyDto.hasSoftAskParentReply()) {
      softAskReply = softAskReplyRepository.findBySoftAskAndReplyParentAndReply(softAskId, softAskParentReplyId, softAskReplyId)
        .orElseThrow(() -> new SoftAskReplyNotFoundException(softAskReplyId));
    } else {
      softAskReply = softAskReplyRepository.findBySoftAskAndReply(softAskId, softAskReplyId)
        .orElseThrow(() -> new SoftAskReplyNotFoundException(softAskReplyId));
    }

    return softAskReply;
  }
}
