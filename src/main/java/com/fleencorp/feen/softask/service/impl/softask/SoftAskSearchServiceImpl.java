package com.fleencorp.feen.softask.service.impl.softask;

import com.fleencorp.base.model.view.search.SearchResult;
import com.fleencorp.feen.softask.exception.core.SoftAskNotFoundException;
import com.fleencorp.feen.softask.mapper.SoftAskMapper;
import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.request.SoftAskSearchRequest;
import com.fleencorp.feen.softask.model.response.softask.SoftAskRetrieveResponse;
import com.fleencorp.feen.softask.model.response.softask.core.SoftAskResponse;
import com.fleencorp.feen.softask.model.search.SoftAskReplySearchResult;
import com.fleencorp.feen.softask.model.search.SoftAskSearchResult;
import com.fleencorp.feen.softask.repository.softask.SoftAskRepository;
import com.fleencorp.feen.softask.repository.softask.SoftAskSearchRepository;
import com.fleencorp.feen.softask.service.common.SoftAskCommonService;
import com.fleencorp.feen.softask.service.softask.SoftAskSearchService;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

import static com.fleencorp.base.util.FleenUtil.toSearchResult;
import static java.util.Objects.nonNull;

@Service
public class SoftAskSearchServiceImpl implements SoftAskSearchService {

  private final SoftAskCommonService softAskCommonService;
  private final SoftAskRepository softAskRepository;
  private final SoftAskSearchRepository softAskSearchRepository;
  private final SoftAskMapper softAskMapper;
  private final Localizer localizer;

  public SoftAskSearchServiceImpl(
    @Lazy final SoftAskCommonService softAskCommonService,
    final SoftAskRepository softAskRepository,
    final SoftAskSearchRepository softAskSearchRepository,
    final SoftAskMapper softAskMapper,
    final Localizer localizer) {
    this.softAskCommonService = softAskCommonService;
    this.softAskRepository = softAskRepository;
    this.softAskSearchRepository = softAskSearchRepository;
    this.softAskMapper = softAskMapper;
    this.localizer = localizer;
  }

  /**
   * Finds a {@link SoftAsk} by its ID.
   *
   * <p>Throws {@link SoftAskNotFoundException} if no soft ask is found with the given ID.</p>
   *
   * @param softAskId the ID of the SoftAsk to find.
   * @return the found {@link SoftAsk} entity.
   * @throws SoftAskNotFoundException if the SoftAsk does not exist.
   */
  @Override
  public SoftAsk findSoftAsk(final Long softAskId) {
    return softAskRepository.findById(softAskId)
      .orElseThrow(SoftAskNotFoundException.of(softAskId));
  }

  /**
   * Retrieves a {@link SoftAsk} by its ID and returns a localized {@link SoftAskRetrieveResponse}.
   *
   * <p>Fetches the entity, maps it to a response, attaches related replies, processes voting and editability metadata,
   * and wraps the result in a localized response.</p>
   *
   * @param softAskId the ID of the {@link SoftAsk} to retrieve.
   * @param user the {@link RegisteredUser} making the request.
   * @return a localized {@link SoftAskRetrieveResponse} containing the full soft ask and related data.
   */
  @Override
  public SoftAskRetrieveResponse retrieveSoftAsk(final Long softAskId, final RegisteredUser user) {
    final Member member = user.toMember();
    final SoftAsk softAsk = findSoftAsk(softAskId);
    final SoftAskResponse softAskResponse = softAskMapper.toSoftAskResponse(softAsk);
    final Collection<SoftAskResponse> softAskResponses = List.of(softAskResponse);

    final SoftAskReplySearchResult softAskReplySearchResult = softAskCommonService.findSomeSoftAskRepliesForSoftAsk(softAskResponse, member);
    softAskResponse.setSoftAskReplySearchResult(softAskReplySearchResult);

    softAskCommonService.processSoftAskResponses(softAskResponses, member);
    final SoftAskRetrieveResponse softAskRetrieveResponse = SoftAskRetrieveResponse.of(softAskId, softAskResponse);

    return localizer.of(softAskRetrieveResponse);
  }

  /**
   * Finds a paginated list of {@link SoftAsk} entries based on the search request and user context.
   *
   * <p>If the search is by author, retrieves only the entries created by the user;
   * otherwise, retrieves general entries using the pagination settings in the request.
   * The result is then processed and localized before being returned.</p>
   *
   * @param searchRequest the request containing filters and pagination info.
   * @param user the {@link RegisteredUser} initiating the search.
   * @return a localized {@link SoftAskSearchResult} containing the result list.
   */
  @Override
  public SoftAskSearchResult findSoftAsks(final SoftAskSearchRequest searchRequest, final RegisteredUser user) {
    final Member member = user.toMember();
    final Pageable pageable = searchRequest.getPage();

    final Page<SoftAsk> page = searchRequest.isByAuthor()
      ? softAskSearchRepository.findByAuthor(member.getMemberId(), pageable)
      : softAskSearchRepository.findMany(searchRequest.getPage());

    return processAndReturnSoftAsks(page, user.toMember());
  }

  /**
   * Processes a page of {@link SoftAsk} entities and returns a localized {@link SoftAskSearchResult}.
   *
   * <p>Maps each entity to a {@link SoftAskResponse}, enriches them with vote and ownership metadata,
   * wraps the results in a {@link SearchResult}, and returns a localized search result.</p>
   *
   * @param page the {@link Page} of {@link SoftAsk} entities to process.
   * @param member the {@link Member} requesting the data, used for personalization.
   * @return a localized {@link SoftAskSearchResult} containing the processed results,
   *         or an empty result if the page is {@code null}.
   */
  private SoftAskSearchResult processAndReturnSoftAsks(final Page<SoftAsk> page, final Member member) {
    if (nonNull(page)) {
      final Collection<SoftAskResponse> softAskResponses = softAskMapper.toSoftAskResponses(page.getContent());
      softAskCommonService.processSoftAskResponses(softAskResponses, member);

      final SearchResult searchResult = toSearchResult(softAskResponses, page);
      final SoftAskSearchResult softAskSearchResult = SoftAskSearchResult.of(searchResult);

      return localizer.of(softAskSearchResult);
    }

    return SoftAskSearchResult.empty();
  }
}
