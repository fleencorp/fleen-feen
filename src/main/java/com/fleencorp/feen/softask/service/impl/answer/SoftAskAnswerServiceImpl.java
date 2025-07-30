package com.fleencorp.feen.softask.service.impl.answer;

import com.fleencorp.feen.mapper.common.UnifiedMapper;
import com.fleencorp.feen.common.model.info.IsDeletedInfo;
import com.fleencorp.feen.softask.contract.SoftAskCommonResponse;
import com.fleencorp.feen.softask.exception.core.SoftAskAnswerNotFoundException;
import com.fleencorp.feen.softask.exception.core.SoftAskNotFoundException;
import com.fleencorp.feen.softask.exception.core.SoftAskUpdateDeniedException;
import com.fleencorp.feen.softask.mapper.SoftAskMapper;
import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.domain.SoftAskAnswer;
import com.fleencorp.feen.softask.model.dto.answer.AddSoftAskAnswerDto;
import com.fleencorp.feen.softask.model.dto.answer.DeleteSoftAskAnswerDto;
import com.fleencorp.feen.softask.model.response.answer.SoftAskAnswerAddResponse;
import com.fleencorp.feen.softask.model.response.answer.SoftAskAnswerDeleteResponse;
import com.fleencorp.feen.softask.model.response.answer.core.SoftAskAnswerResponse;
import com.fleencorp.feen.softask.repository.answer.SoftAskAnswerRepository;
import com.fleencorp.feen.softask.service.answer.SoftAskAnswerService;
import com.fleencorp.feen.softask.service.common.SoftAskCommonService;
import com.fleencorp.feen.softask.service.common.SoftAskOperationService;
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
public class SoftAskAnswerServiceImpl implements SoftAskAnswerService {

  private final MemberService memberService;
  private final SoftAskCommonService softAskCommonService;
  private final SoftAskOperationService softAskOperationService;
  private final SoftAskSearchService softAskSearchService;
  private final SoftAskAnswerRepository softAskAnswerRepository;
  private final SoftAskMapper softAskMapper;
  private final UnifiedMapper unifiedMapper;
  private final Localizer localizer;

  public SoftAskAnswerServiceImpl(
    final MemberService memberService,
    @Lazy SoftAskCommonService softAskCommonService,
    final SoftAskSearchService softAskSearchService,
    final SoftAskOperationService softAskOperationService,
    final SoftAskAnswerRepository softAskAnswerRepository,
    final SoftAskMapper softAskMapper,
    final UnifiedMapper unifiedMapper,
    final Localizer localizer) {
    this.memberService = memberService;
    this.softAskCommonService = softAskCommonService;
    this.softAskSearchService = softAskSearchService;
    this.softAskOperationService = softAskOperationService;
    this.softAskAnswerRepository = softAskAnswerRepository;
    this.softAskMapper = softAskMapper;
    this.unifiedMapper = unifiedMapper;
    this.localizer = localizer;
  }

  /**
   * Adds a new answer to a SoftAsk question.
   *
   * <p>Finds the author {@link Member} from the given user, fetches the target {@link SoftAsk} question,
   * converts the DTO to an entity, saves the answer, increments the answer count on the question,
   * and returns a localized response containing the updated answer count and the saved answer's details.</p>
   *
   * @param dto the data transfer object containing the new answer data.
   * @param user the {@link RegisteredUser} adding the answer.
   * @return a localized {@link SoftAskAnswerAddResponse} containing the updated answer count and answer details.
   * @throws SoftAskNotFoundException if the target SoftAsk question cannot be found.
   */
  @Override
  @Transactional
  public SoftAskAnswerAddResponse addSoftAskAnswer(final AddSoftAskAnswerDto dto, final RegisteredUser user) throws SoftAskNotFoundException {
    final Member author = memberService.findMember(user.getId());

    final Long softAskId = dto.getSoftAskId();
    final SoftAsk softAsk = softAskSearchService.findSoftAsk(softAskId);
    final SoftAskAnswer answer = dto.toSoftAskAnswer(author, softAsk);
    softAskAnswerRepository.save(answer);

    final Integer answerCount = softAskOperationService.incrementSoftAskAnswerCountAndGetAnswerCount(softAskId);

    final SoftAskAnswerResponse softAskAnswerResponse = softAskMapper.toSoftAskAnswerResponse(answer);
    final Collection<SoftAskCommonResponse> softAskCommonResponses = List.of(softAskAnswerResponse);

    softAskCommonService.processSoftAskResponses(softAskCommonResponses, author);

    final SoftAskAnswerAddResponse softAskAnswerAddResponse = SoftAskAnswerAddResponse.of(answerCount, softAskAnswerResponse);
    return localizer.of(softAskAnswerAddResponse);
  }

  /**
   * Deletes a SoftAsk answer if the requesting user is the author.
   *
   * <p>Finds the answer by ID, verifies the user's authorization to delete,
   * marks the answer as deleted, and returns a localized response indicating
   * the deletion status.</p>
   *
   * @param deleteSoftAskAnswerDto the DTO containing the ID of the answer to delete.
   * @param user the {@link RegisteredUser} requesting the deletion.
   * @return a localized {@link SoftAskAnswerDeleteResponse} with the deletion status.
   * @throws SoftAskAnswerNotFoundException if the answer with the specified ID does not exist.
   * @throws SoftAskUpdateDeniedException if the user is not authorized to delete the answer.
   */
  @Override
  @Transactional
  public SoftAskAnswerDeleteResponse deleteSoftAskAnswer(final DeleteSoftAskAnswerDto deleteSoftAskAnswerDto, final RegisteredUser user)
      throws SoftAskAnswerNotFoundException, SoftAskUpdateDeniedException {
    final Long softAskAnswerId = deleteSoftAskAnswerDto.getSoftAskAnswerId();
    final SoftAskAnswer softAskAnswer = softAskAnswerRepository.findById(softAskAnswerId)
      .orElseThrow(SoftAskAnswerNotFoundException.of(softAskAnswerId));

    softAskAnswer.checkIsAuthor(user.getId());
    softAskAnswer.delete();

    final IsDeletedInfo isDeletedInfo = unifiedMapper.toIsDeletedInfo(softAskAnswer.isDeleted());
    final SoftAskAnswerDeleteResponse softAskAnswerDeleteResponse = SoftAskAnswerDeleteResponse.of(softAskAnswerId, isDeletedInfo);

    return localizer.of(softAskAnswerDeleteResponse);
  }
}
