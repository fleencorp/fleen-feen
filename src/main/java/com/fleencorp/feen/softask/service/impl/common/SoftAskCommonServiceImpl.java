package com.fleencorp.feen.softask.service.impl.common;

import com.fleencorp.base.model.view.search.SearchResult;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.model.contract.Updatable;
import com.fleencorp.feen.softask.constant.core.SoftAskType;
import com.fleencorp.feen.softask.contract.SoftAskCommonData;
import com.fleencorp.feen.softask.contract.SoftAskCommonResponse;
import com.fleencorp.feen.softask.exception.core.SoftAskAnswerNotFoundException;
import com.fleencorp.feen.softask.exception.core.SoftAskReplyNotFoundException;
import com.fleencorp.feen.softask.exception.core.SoftAskUpdateDeniedException;
import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.domain.SoftAskAnswer;
import com.fleencorp.feen.softask.model.domain.SoftAskReply;
import com.fleencorp.feen.softask.model.dto.common.UpdateSoftAskContentDto;
import com.fleencorp.feen.softask.model.request.SoftAskSearchRequest;
import com.fleencorp.feen.softask.model.response.answer.core.SoftAskAnswerResponse;
import com.fleencorp.feen.softask.model.response.common.SoftAskContentUpdateResponse;
import com.fleencorp.feen.softask.model.response.softask.core.SoftAskResponse;
import com.fleencorp.feen.softask.model.search.SoftAskAnswerSearchResult;
import com.fleencorp.feen.softask.model.search.SoftAskReplySearchResult;
import com.fleencorp.feen.softask.service.answer.SoftAskAnswerSearchService;
import com.fleencorp.feen.softask.service.common.SoftAskCommonService;
import com.fleencorp.feen.softask.service.common.SoftAskOperationService;
import com.fleencorp.feen.softask.service.reply.SoftAskReplySearchService;
import com.fleencorp.feen.softask.service.softask.SoftAskSearchService;
import com.fleencorp.feen.softask.service.vote.SoftAskVoteSearchService;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

import static com.fleencorp.feen.common.service.impl.misc.MiscServiceImpl.setEntityUpdatableByUser;
import static java.util.Objects.nonNull;

@Service
public class SoftAskCommonServiceImpl implements SoftAskCommonService {

  private final SoftAskAnswerSearchService softAskAnswerSearchService;
  private final SoftAskReplySearchService softAskReplySearchService;
  private final SoftAskOperationService softAskOperationService;
  private final SoftAskSearchService softAskSearchService;
  private final SoftAskVoteSearchService softAskVoteSearchService;
  private final Localizer localizer;

  public SoftAskCommonServiceImpl(
    final SoftAskAnswerSearchService softAskAnswerSearchService,
    final SoftAskReplySearchService softAskReplySearchService,
    final SoftAskOperationService softAskOperationService,
    final SoftAskSearchService softAskSearchService,
    final SoftAskVoteSearchService softAskVoteSearchService,
    final Localizer localizer) {
    this.softAskAnswerSearchService = softAskAnswerSearchService;
    this.softAskReplySearchService = softAskReplySearchService;
    this.softAskOperationService = softAskOperationService;
    this.softAskSearchService = softAskSearchService;
    this.softAskVoteSearchService = softAskVoteSearchService;
    this.localizer = localizer;
  }

  /**
   * Enriches a collection of {@link SoftAskCommonResponse} objects with user-specific metadata.
   *
   * <p>Processes votes for each response using {@link SoftAskVoteSearchService}, and sets whether the entity
   * is updatable by the current member.</p>
   *
   * @param softAskCommonResponses the collection of responses to process.
   * @param member the {@link Member} whose context is used for vote and update permissions.
   * @param <T> the type of response, extending {@link SoftAskCommonResponse}.
   */
  @Override
  public <T extends SoftAskCommonResponse> void processSoftAskResponses(final Collection<T> softAskCommonResponses, final Member member) {
    if (nonNull(softAskCommonResponses) && nonNull(member)) {
      softAskVoteSearchService.processVotesForResponses(softAskCommonResponses, member);
      softAskCommonResponses.forEach(softAskCommonResponse -> setEntityUpdatableByUser((Updatable) softAskCommonResponse, member.getMemberId()));
    }
  }

  /**
   * Retrieves a limited number of answers (page size of 10) for the given {@link SoftAskResponse},
   * and for each answer, fetches a small set of replies (page size of 2).
   *
   * <p>Each {@link SoftAskAnswerResponse} in the result is enriched with its own {@link SoftAskReplySearchResult}
   * via {@code findSomeSoftAskReplyForSoftAskAnswer}.</p>
   *
   * @param softAskResponse the {@link SoftAskResponse} representing the parent SoftAsk entry.
   * @param member the {@link Member} requesting the data.
   * @return a {@link SoftAskAnswerSearchResult} containing up to 10 answers, each possibly enriched with replies.
   */
  @Override
  public SoftAskAnswerSearchResult findSomeSoftAskAnswersForSoftAsk(final SoftAskResponse softAskResponse, final Member member) {
    final SoftAskSearchRequest searchRequest = SoftAskSearchRequest.of(softAskResponse.getNumberId());
    searchRequest.setPageSize(10);

    final SoftAskAnswerSearchResult softAskAnswerSearchResult = softAskAnswerSearchService.findSoftAskAnswers(searchRequest, member);
    final SearchResult searchResult = softAskAnswerSearchResult.getResult();

    searchResult.getValues().forEach(searchResultValue -> {
      final SoftAskAnswerResponse softAskAnswerResponse = (SoftAskAnswerResponse) searchResultValue;
      final SoftAskReplySearchResult softAskReplySearchResult = findSomeSoftAskReplyForSoftAskAnswer(softAskAnswerResponse, member);
      softAskAnswerResponse.setReplySearchResult(softAskReplySearchResult);
    });

    return softAskAnswerSearchResult;
  }

  /**
   * Fetches a limited number of replies (page size of 2) for the given {@link SoftAskAnswerResponse}
   * by constructing a {@link SoftAskSearchRequest} based on its ID.
   *
   * <p>Delegates the actual fetching to {@link SoftAskReplySearchService#findSoftAskReplies} using the given member context.</p>
   *
   * @param softAskAnswerResponse the response DTO representing the SoftAsk answer to find replies for.
   * @param member the {@link Member} requesting the replies.
   * @return a {@link SoftAskReplySearchResult} containing up to two replies for the specified SoftAsk answer.
   */
  private SoftAskReplySearchResult findSomeSoftAskReplyForSoftAskAnswer(final SoftAskAnswerResponse softAskAnswerResponse, final Member member) {
    final SoftAskSearchRequest searchRequest = SoftAskSearchRequest.of(softAskAnswerResponse.getNumberId());
    searchRequest.setPageSize(2);

    return softAskReplySearchService.findSoftAskReplies(searchRequest, member);
  }

  /**
   * Updates the content of a {@link SoftAskCommonData} entity such as a {@link SoftAsk}, {@link SoftAskAnswer}, or {@link SoftAskReply}.
   *
   * <p>Finds the target entity based on its ID and type, verifies that the requesting user is the author,
   * updates the content, saves the entity, and returns a localized response.</p>
   *
   * @param softAskTypeId the ID of the entity to update.
   * @param dto the DTO containing the new content and type of the entity.
   * @param user the {@link RegisteredUser} performing the update.
   * @return a localized {@link SoftAskContentUpdateResponse} confirming the update.
   * @throws SoftAskAnswerNotFoundException if the answer is not found.
   * @throws SoftAskReplyNotFoundException if the reply is not found.
   * @throws SoftAskUpdateDeniedException if the user is not authorized to update the content.
   */
  @Override
  @Transactional
  public SoftAskContentUpdateResponse updateSoftAskContent(final Long softAskTypeId, final UpdateSoftAskContentDto dto, final RegisteredUser user)
    throws SoftAskAnswerNotFoundException, SoftAskReplyNotFoundException, SoftAskUpdateDeniedException,
      FailedOperationException {

    final SoftAskCommonData softAskCommonData = findSoftAskTypeToUpdate(softAskTypeId, dto.getSoftAskType());
    softAskCommonData.checkIsAuthor(user.getId());
    softAskCommonData.setContent(dto.getContent());
    saveSoftAskCommonData(softAskCommonData);

    final SoftAskContentUpdateResponse softAskContentUpdateResponse = SoftAskContentUpdateResponse.of(softAskTypeId);
    return localizer.of(softAskContentUpdateResponse);
  }

  /**
   * Finds a {@link SoftAskCommonData} entity by its ID and {@link SoftAskType}, to be used for an update operation.
   *
   * <p>Delegates the lookup to the appropriate service based on the provided type:
   * {@code ANSWER}, {@code REPLY}, or {@code SOFT_ASK}. If the type is {@code SOFT_ASK},
   * it performs an additional check to ensure only one answer is associated.</p>
   *
   * @param softAskTypeId the ID of the entity to retrieve.
   * @param softAskType the type of entity to fetch.
   * @return the matching {@link SoftAskCommonData} instance.
   * @throws FailedOperationException if the type is {@code null} or unrecognized.
   */
  private SoftAskCommonData findSoftAskTypeToUpdate(final Long softAskTypeId, final SoftAskType softAskType) {
    if (nonNull(softAskType)) {

      return switch (softAskType) {
        case ANSWER -> softAskAnswerSearchService.findSoftAskAnswer(softAskTypeId);
        case REPLY -> softAskReplySearchService.findSoftAskReply(softAskTypeId);
        case SOFT_ASK -> {
          final SoftAsk softAsk = softAskSearchService.findSoftAsk(softAskTypeId);
          softAsk.checkIsAnswerIsNotMoreThanOne();
          yield softAsk;
        }
      };
    }

    throw FailedOperationException.of();
  }

  /**
   * Persists a {@link SoftAskCommonData} entity using the {@link SoftAskOperationService},
   * based on its concrete type.
   *
   * <p>If the entity is a {@link SoftAskAnswer}, {@link SoftAskReply}, or {@link SoftAsk},
   * it is saved via the corresponding overload of {@code softAskOperationService.save(...)}.</p>
   *
   * @param softAskCommonData the entity to persist; must be an instance of {@link SoftAsk}, {@link SoftAskAnswer}, or {@link SoftAskReply}.
   */
  private void saveSoftAskCommonData(final SoftAskCommonData softAskCommonData) {
    if (softAskCommonData instanceof SoftAskAnswer softAskAnswer) {
      softAskOperationService.save(softAskAnswer);
    } else if (softAskCommonData instanceof SoftAskReply softAskReply) {
      softAskOperationService.save(softAskReply);
    } else if (softAskCommonData instanceof SoftAsk softAsk) {
      softAskOperationService.save(softAsk);
    }
  }

}
