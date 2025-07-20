package com.fleencorp.feen.softask.service.impl.answer;

import com.fleencorp.base.model.view.search.SearchResult;
import com.fleencorp.feen.softask.exception.core.SoftAskAnswerNotFoundException;
import com.fleencorp.feen.softask.mapper.SoftAskMapper;
import com.fleencorp.feen.softask.model.domain.SoftAskAnswer;
import com.fleencorp.feen.softask.model.request.SoftAskSearchRequest;
import com.fleencorp.feen.softask.model.response.answer.core.SoftAskAnswerResponse;
import com.fleencorp.feen.softask.model.search.SoftAskAnswerSearchResult;
import com.fleencorp.feen.softask.repository.answer.SoftAskAnswerSearchRepository;
import com.fleencorp.feen.softask.service.answer.SoftAskAnswerSearchService;
import com.fleencorp.feen.softask.service.common.SoftAskCommonService;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;

import static com.fleencorp.base.util.FleenUtil.toSearchResult;
import static java.util.Objects.nonNull;

@Service
public class SoftAskAnswerSearchServiceImpl implements SoftAskAnswerSearchService {

  private final SoftAskCommonService softAskCommonService;
  private final SoftAskAnswerSearchRepository softAskAnswerSearchRepository;
  private final SoftAskMapper softAskMapper;
  private final Localizer localizer;

  public SoftAskAnswerSearchServiceImpl(
    @Lazy final SoftAskCommonService softAskCommonService,
    final SoftAskAnswerSearchRepository softAskAnswerSearchRepository,
    final SoftAskMapper softAskMapper,
    final Localizer localizer) {
    this.softAskCommonService = softAskCommonService;
    this.softAskAnswerSearchRepository = softAskAnswerSearchRepository;
    this.softAskMapper = softAskMapper;
    this.localizer = localizer;
  }

  /**
   * Finds a {@link SoftAskAnswer} by its ID.
   *
   * <p>Throws {@link SoftAskAnswerNotFoundException} if no answer is found with the given ID.</p>
   *
   * @param softAskAnswerId the ID of the SoftAsk answer to find.
   * @return the found {@link SoftAskAnswer} entity.
   * @throws SoftAskAnswerNotFoundException if the answer does not exist.
   */
  @Override
  public SoftAskAnswer findSoftAskAnswer(final Long softAskAnswerId) throws SoftAskAnswerNotFoundException {
    return softAskAnswerSearchRepository.findById(softAskAnswerId)
      .orElseThrow(SoftAskAnswerNotFoundException.of(softAskAnswerId));
  }

  /**
   * Delegates to {@link #findSoftAskAnswers(SoftAskSearchRequest, RegisteredUser)} using the given {@link Member}.
   *
   * <p>Wraps the member in a {@link RegisteredUser} and forwards the request.</p>
   *
   * @param searchRequest the request containing filter and pagination parameters.
   * @param member the {@link Member} performing the request.
   * @return a localized {@link SoftAskAnswerSearchResult} containing the paginated and enriched answers.
   */
  @Override
  public SoftAskAnswerSearchResult findSoftAskAnswers(final SoftAskSearchRequest searchRequest, final Member member) {
    final Long memberId = member.getMemberId();
    final RegisteredUser user = RegisteredUser.of(memberId);
    return findSoftAskAnswers(searchRequest, user);
  }

  /**
   * Finds answers to a SoftAsk question based on the provided {@link SoftAskSearchRequest}
   * and returns a localized {@link SoftAskAnswerSearchResult}.
   *
   * <p>If the search request is filtered by author, it fetches answers created by that author.
   * Otherwise, it fetches answers associated with the specified SoftAsk question ID.</p>
   *
   * @param searchRequest the request containing filter and pagination parameters.
   * @param user the {@link RegisteredUser} performing the request.
   * @return a localized {@link SoftAskAnswerSearchResult} containing the paginated and enriched answers.
   */
  @Override
  public SoftAskAnswerSearchResult findSoftAskAnswers(final SoftAskSearchRequest searchRequest, final RegisteredUser user) {
    final Long parentId = searchRequest.getParentId();
    final Long authorId = searchRequest.getAuthorId();
    final Member member = user.toMember();
    final Pageable pageable = searchRequest.getPage();

    final Page<SoftAskAnswer> page = searchRequest.isByAuthor()
      ? softAskAnswerSearchRepository.findByAuthor(authorId, pageable)
      : softAskAnswerSearchRepository.findBySoftAsk(parentId, pageable);

    return processAndReturnSoftAskAnswers(parentId, page, member);
  }

  /**
   * Processes a page of {@link SoftAskAnswer} entities and returns a localized {@link SoftAskAnswerSearchResult}
   * tied to the specified parent ID.
   *
   * <p>Converts the answer entities to DTOs, enriches them with voting and metadata using {@code processSoftAskResponses},
   * and wraps the result in a localized search response.</p>
   *
   * @param parentId the ID of the SoftAsk parent entry associated with the answers.
   * @param page the paginated answers to process; can be {@code null}.
   * @param member the {@link Member} whose context is used for vote enrichment.
   * @return a localized {@link SoftAskAnswerSearchResult} containing the processed answers,
   *         or an empty result if the page is {@code null}.
   */
  private SoftAskAnswerSearchResult processAndReturnSoftAskAnswers(final Long parentId, final Page<SoftAskAnswer> page, final Member member) {
    if (nonNull(page)) {
      final Collection<SoftAskAnswerResponse> softAskAnswerResponses = softAskMapper.toSoftAskAnswerResponses(page.getContent());
      softAskCommonService.processSoftAskResponses(softAskAnswerResponses, member);

      final SearchResult searchResult = toSearchResult(softAskAnswerResponses, page);
      final SoftAskAnswerSearchResult softAskAnswerSearchResult = SoftAskAnswerSearchResult.of(parentId, searchResult);
      return localizer.of(softAskAnswerSearchResult);
    }

    return SoftAskAnswerSearchResult.empty(parentId);
  }
}
