package com.fleencorp.feen.softask.service.impl.softask;

import com.fleencorp.base.model.view.search.SearchResult;
import com.fleencorp.feen.model.contract.UserHaveOtherDetail;
import com.fleencorp.feen.softask.exception.core.SoftAskNotFoundException;
import com.fleencorp.feen.softask.mapper.SoftAskMapper;
import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.holder.UserOtherDetailHolder;
import com.fleencorp.feen.softask.model.projection.SoftAskWithDetail;
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
   * Retrieves a soft ask by its ID along with its replies and returns the corresponding response.
   *
   * <p>This method finds the soft ask using the given identifier, maps it into a
   * {@link SoftAskResponse}, and retrieves a limited number of its replies using the
   * provided {@link SoftAskSearchRequest}. The replies are attached to the response,
   * and the soft ask response is further enriched with bookmarks, votes, location, and
   * update permissions for the given user. The result is then wrapped into a
   * {@link SoftAskRetrieveResponse} and localized before being returned.</p>
   *
   * @param softAskId     the ID of the soft ask to retrieve
   * @param searchRequest the search request used to configure reply retrieval and user details
   * @param user          the registered user performing the retrieval
   * @return a localized {@link SoftAskRetrieveResponse} containing the soft ask and its replies
   */
  @Override
  public SoftAskRetrieveResponse retrieveSoftAsk(final Long softAskId, final SoftAskSearchRequest searchRequest, final RegisteredUser user) {
    final Member member = user.toMember();
    final SoftAsk softAsk = findSoftAsk(softAskId);
    final SoftAskResponse softAskResponse = softAskMapper.toSoftAskResponse(softAsk);
    final Collection<SoftAskResponse> softAskResponses = List.of(softAskResponse);
    final UserOtherDetailHolder userOtherDetailHolder = searchRequest.getUserOtherDetail();

    final SoftAskReplySearchResult softAskReplySearchResult = softAskCommonService.findSomeSoftAskRepliesForSoftAsk(searchRequest, softAskResponse, member);
    softAskResponse.setSoftAskReplySearchResult(softAskReplySearchResult);

    softAskCommonService.processSoftAskResponses(softAskResponses, member, userOtherDetailHolder);
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

    final Page<SoftAskWithDetail> page = searchRequest.isByAuthor()
      ? softAskSearchRepository.findByAuthor(member.getMemberId(), pageable)
      : softAskSearchRepository.findMany(searchRequest.getPage());

    return processAndReturnSoftAsks(page, user.toMember(), searchRequest.getUserOtherDetail());
  }

  /**
   * Processes a page of soft asks and returns the corresponding search result.
   *
   * <p>This method maps the content of the given {@link Page} of
   * {@link SoftAskWithDetail} entities to {@link SoftAskResponse} objects,
   * enriches them with bookmarks, votes, location, and update permissions,
   * and converts them into a {@link SearchResult}. The result is then wrapped
   * into a {@link SoftAskSearchResult} and localized before being returned.
   * If the provided page is {@code null}, an empty search result is returned.</p>
   *
   * @param page                the page of soft asks with details, may be {@code null}
   * @param member              the member for whom the responses are processed
   * @param userHaveOtherDetail the user detail object used to set location information
   * @return a localized {@link SoftAskSearchResult} containing the processed soft asks,
   *         or an empty result if the input page is {@code null}
   */
  private SoftAskSearchResult processAndReturnSoftAsks(final Page<SoftAskWithDetail> page, final Member member, final UserHaveOtherDetail userHaveOtherDetail) {
    if (nonNull(page)) {
      final Collection<SoftAskResponse> softAskResponses = softAskMapper.toSoftAskResponses(page.getContent());
      softAskCommonService.processSoftAskResponses(softAskResponses, member, userHaveOtherDetail);

      final SearchResult searchResult = toSearchResult(softAskResponses, page);
      final SoftAskSearchResult softAskSearchResult = SoftAskSearchResult.of(searchResult);

      return localizer.of(softAskSearchResult);
    }

    return SoftAskSearchResult.empty();
  }
}
