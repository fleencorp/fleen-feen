package com.fleencorp.feen.softask.service.impl.reply;

import com.fleencorp.feen.mapper.common.UnifiedMapper;
import com.fleencorp.feen.common.model.info.IsDeletedInfo;
import com.fleencorp.feen.softask.contract.SoftAskCommonResponse;
import com.fleencorp.feen.softask.exception.core.SoftAskReplyNotFoundException;
import com.fleencorp.feen.softask.exception.core.SoftAskUpdateDeniedException;
import com.fleencorp.feen.softask.mapper.SoftAskMapper;
import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.domain.SoftAskAnswer;
import com.fleencorp.feen.softask.model.domain.SoftAskReply;
import com.fleencorp.feen.softask.model.dto.reply.AddSoftAskReplyDto;
import com.fleencorp.feen.softask.model.dto.reply.DeleteSoftAskReplyDto;
import com.fleencorp.feen.softask.model.response.reply.SoftAskReplyAddResponse;
import com.fleencorp.feen.softask.model.response.reply.SoftAskReplyDeleteResponse;
import com.fleencorp.feen.softask.model.response.reply.core.SoftAskReplyResponse;
import com.fleencorp.feen.softask.repository.reply.SoftAskReplyRepository;
import com.fleencorp.feen.softask.service.answer.SoftAskAnswerSearchService;
import com.fleencorp.feen.softask.service.common.SoftAskCommonService;
import com.fleencorp.feen.softask.service.common.SoftAskOperationService;
import com.fleencorp.feen.softask.service.reply.SoftAskReplyService;
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
  private final SoftAskAnswerSearchService softAskAnswerSearchService;
  private final SoftAskReplyRepository softAskReplyRepository;
  private final SoftAskMapper softAskMapper;
  private final UnifiedMapper unifiedMapper;
  private final Localizer localizer;

  public SoftAskReplyServiceImpl(
    final MemberService memberService,
    @Lazy final SoftAskCommonService softAskCommonService,
    final SoftAskOperationService softAskOperationService,
    final SoftAskAnswerSearchService softAskAnswerSearchService,
    final SoftAskReplyRepository softAskReplyRepository,
    final SoftAskMapper softAskMapper,
    final UnifiedMapper unifiedMapper,
    final Localizer localizer) {
    this.memberService = memberService;
    this.softAskCommonService = softAskCommonService;
    this.softAskOperationService = softAskOperationService;
    this.softAskAnswerSearchService = softAskAnswerSearchService;
    this.softAskReplyRepository = softAskReplyRepository;
    this.softAskMapper = softAskMapper;
    this.unifiedMapper = unifiedMapper;
    this.localizer = localizer;
  }

  /**
   * Adds a reply to an existing {@link SoftAskAnswer}.
   *
   * <p>Finds the author {@link Member} and the target {@link SoftAskAnswer}, constructs the {@link SoftAskReply} entity
   * from the DTO, saves it, increments the reply count, and returns a localized response containing the
   * updated reply count and reply details.</p>
   *
   * @param dto the data transfer object containing reply content and the answer ID to reply to.
   * @param user the {@link RegisteredUser} adding the reply.
   * @return a localized {@link SoftAskReplyAddResponse} with the updated reply count and reply data.
   */
  @Override
  @Transactional
  public SoftAskReplyAddResponse addSoftAskReply(final AddSoftAskReplyDto dto, final RegisteredUser user) {
    final Member author = memberService.findMember(user.getId());
    final Long softAskAnswerId = dto.getSoftAskAnswerId();

    final SoftAskAnswer softAskAnswer = softAskAnswerSearchService.findSoftAskAnswer(softAskAnswerId);
    final SoftAsk softAsk = SoftAsk.of(softAskAnswer.getSoftAskId());
    final SoftAskReply reply = dto.toSoftAskReply(author, softAsk, softAskAnswer);

    softAskReplyRepository.save(reply);
    final Integer answerReplyCount = softAskOperationService.incrementSoftAskAnswerReplyCountAndGetReplyCount(softAskAnswerId);

    final SoftAskReplyResponse softAskReplyResponse = softAskMapper.toSoftAskReplyResponse(reply);
    final Collection<SoftAskCommonResponse> softAskCommonResponses = List.of(softAskReplyResponse);
    softAskCommonService.processSoftAskResponses(softAskCommonResponses, author);

    final SoftAskReplyAddResponse softAskReplyAddResponse = SoftAskReplyAddResponse.of(answerReplyCount, softAskReplyResponse);
    return localizer.of(softAskReplyAddResponse);
  }

  /**
   * Deletes a {@link SoftAskReply} if the requesting user is the author.
   *
   * <p>Fetches the reply by its ID, validates that the user is the author, marks it as deleted,
   * and returns a localized response indicating the deletion status.</p>
   *
   * @param deleteSoftAskReplyDto the DTO containing the ID of the reply to delete.
   * @param user the {@link RegisteredUser} requesting the deletion.
   * @return a localized {@link SoftAskReplyDeleteResponse} with the deletion status.
   * @throws SoftAskUpdateDeniedException if the user is not authorized to delete the reply.
   */
  @Override
  @Transactional
  public SoftAskReplyDeleteResponse deleteSoftAskReply(final DeleteSoftAskReplyDto deleteSoftAskReplyDto, final RegisteredUser user) throws SoftAskUpdateDeniedException {
    final Long softAskReplyId = deleteSoftAskReplyDto.getSoftAskReplyId();
    final SoftAskReply softAskReply = softAskReplyRepository.findById(softAskReplyId)
      .orElseThrow(SoftAskReplyNotFoundException.of(softAskReplyId));

    softAskReply.checkIsAuthor(user.getId());
    softAskReply.delete();

    final IsDeletedInfo isDeletedInfo = unifiedMapper.toIsDeletedInfo(softAskReply.isDeleted());
    final SoftAskReplyDeleteResponse softAskReplyDeleteResponse = SoftAskReplyDeleteResponse.of(softAskReplyId, isDeletedInfo);

    return localizer.of(softAskReplyDeleteResponse);
  }
}
