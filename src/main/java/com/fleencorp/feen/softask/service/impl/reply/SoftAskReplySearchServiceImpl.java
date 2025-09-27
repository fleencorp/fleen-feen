package com.fleencorp.feen.softask.service.impl.reply;

import com.fleencorp.base.model.view.search.SearchResult;
import com.fleencorp.feen.model.contract.UserHaveOtherDetail;
import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.softask.exception.core.SoftAskReplyNotFoundException;
import com.fleencorp.feen.softask.mapper.SoftAskMapper;
import com.fleencorp.feen.softask.model.domain.SoftAskReply;
import com.fleencorp.feen.softask.model.holder.UserOtherDetailHolder;
import com.fleencorp.feen.softask.model.projection.SoftAskReplyWithDetail;
import com.fleencorp.feen.softask.model.request.SoftAskSearchRequest;
import com.fleencorp.feen.softask.model.response.reply.SoftAskReplyRetrieveResponse;
import com.fleencorp.feen.softask.model.response.reply.core.SoftAskReplyResponse;
import com.fleencorp.feen.softask.model.search.SoftAskReplySearchResult;
import com.fleencorp.feen.softask.repository.reply.SoftAskReplySearchRepository;
import com.fleencorp.feen.softask.service.common.SoftAskCommonService;
import com.fleencorp.feen.softask.service.reply.SoftAskReplySearchService;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;

import static com.fleencorp.base.util.FleenUtil.toSearchResult;
import static java.util.Objects.nonNull;

@Service
public class SoftAskReplySearchServiceImpl implements SoftAskReplySearchService {

  private final SoftAskCommonService softAskCommonService;
  private final SoftAskReplySearchRepository softAskReplySearchRepository;
  private final SoftAskMapper softAskMapper;
  private final Localizer localizer;

  public SoftAskReplySearchServiceImpl(
      @Lazy final SoftAskCommonService softAskCommonService,
      final SoftAskReplySearchRepository softAskReplySearchRepository,
      final SoftAskMapper softAskMapper,
      final Localizer localizer) {
    this.softAskCommonService = softAskCommonService;
    this.softAskReplySearchRepository = softAskReplySearchRepository;
    this.softAskMapper = softAskMapper;
    this.localizer = localizer;
  }

  /**
   * Retrieves a specific reply for a given Soft Ask and prepares a localized response.
   *
   * <p>This method locates the {@link SoftAskReply} identified by the provided
   * {@code softAskReplyId} within the context of the parent Soft Ask specified in
   * the {@link SoftAskSearchRequest}. It then maps the entity to a
   * {@link SoftAskReplyResponse}, attaches its child replies, wraps it into a
   * {@link SoftAskReplyRetrieveResponse}, and localizes the result for the caller.</p>
   *
   * @param searchRequest the search request containing the parent Soft Ask identifier and additional filtering or paging options
   * @param softAskReplyId the identifier of the Soft Ask reply to retrieve
   * @param user the {@link RegisteredUser} performing the request, used to derive membership context
   * @return a localized {@link SoftAskReplyRetrieveResponse} containing the requested reply and its child replies; never {@code null}
   * @throws SoftAskReplyNotFoundException if no reply exists for the specified {@code softAskReplyId under the given parent Soft Ask
   */
  @Override
  public SoftAskReplyRetrieveResponse retrieveSoftAskReply(final SoftAskSearchRequest searchRequest, final Long softAskReplyId, RegisteredUser user)
      throws SoftAskReplyNotFoundException {
    final Long softAskId = searchRequest.getParentId();
    final SoftAskReply softAskReply = findSoftAskReply(softAskId, softAskReplyId);
    final IsAMember member = user.toMember();

    searchRequest.setPageSize(10);
    searchRequest.updateParentReplyId(softAskReplyId);

    final SoftAskReplyResponse softAskReplyResponse = softAskMapper.toSoftAskReplyResponse(softAskReply, member);
    final SoftAskReplySearchResult softAskReplySearchResult = findSoftAskReplies(searchRequest, member);
    softAskReplyResponse.setChildRepliesSearchResult(softAskReplySearchResult);

    final SoftAskReplyRetrieveResponse softAskReplyRetrieveResponse = SoftAskReplyRetrieveResponse.of(softAskReplyId, softAskReplyResponse);
    return localizer.of(softAskReplyRetrieveResponse);
  }

  /**
   * Finds and returns a {@code SoftAskReply} entity by its SoftAsk ID and reply ID.
   *
   * <p>This method queries the repository for a reply matching the provided IDs.
   * If no matching reply is found, it throws a {@link SoftAskReplyNotFoundException}.</p>
   *
   * @param softAskId      the ID of the SoftAsk associated with the reply
   * @param softAskReplyId the ID of the SoftAskReply to find
   * @return the found {@code SoftAskReply} entity
   * @throws SoftAskReplyNotFoundException if no reply exists for the given IDs
   */
  @Override
  public SoftAskReply findSoftAskReply(final Long softAskId, final Long softAskReplyId) throws SoftAskReplyNotFoundException {
    return softAskReplySearchRepository.findBySoftAskAndSoftAskReply(softAskId, softAskReplyId)
      .orElseThrow(SoftAskReplyNotFoundException.of(softAskReplyId));
  }

  @Override
  public SoftAskReplySearchResult findSoftAskReplies(final SoftAskSearchRequest searchRequest, final RegisteredUser user) {
    return findSoftAskReplies(searchRequest, user.toMember());
  }

  /**
   * Finds replies to a SoftAsk based on the provided {@link SoftAskSearchRequest}
   * and returns a localized {@link SoftAskReplySearchResult}.
   *
   * <p>If the request is filtered by author, it fetches replies created by that author.
   * Otherwise, it fetches replies associated with the specified SoftAsk reply ID.</p>
   *
   * @param searchRequest the request containing filter and pagination parameters.
   * @param member the {@link IsAMember} performing the request.
   * @return a localized {@link SoftAskReplySearchResult} containing the paginated and enriched replies.
   */
  @Override
  public SoftAskReplySearchResult findSoftAskReplies(final SoftAskSearchRequest searchRequest, final IsAMember member) {
    final Long softAskId = searchRequest.getParentId();
    final Long parentReplyId = searchRequest.getParentReplyId();
    final Long authorId = searchRequest.getAuthorId();
    final Pageable pageable = searchRequest.getPage();
    final UserOtherDetailHolder userOtherDetailHolder = searchRequest.getUserOtherDetail();
    final Page<SoftAskReplyWithDetail> page;

    if (searchRequest.hasParentId() && searchRequest.hasParentReplyId()) {
      page = softAskReplySearchRepository.findBySoftAskAndParentReply(softAskId, parentReplyId, pageable);
    } else if (searchRequest.hasParentId()) {
      page = softAskReplySearchRepository.findBySoftAsk(softAskId, pageable);
    } else if (searchRequest.isByAuthor()) {
      page = softAskReplySearchRepository.findByAuthor(authorId, pageable);
    } else {
      page = Page.empty();
    }

    return processAndReturnSoftAskReplies(softAskId, page, member, userOtherDetailHolder);
  }

  /**
   * Processes a page of {@link SoftAskReply} entities and returns a localized {@link SoftAskReplySearchResult}
   * tied to the specified parent ID.
   *
   * <p>Converts the reply entities to DTOs, enriches them with voting and metadata using {@code processSoftAskResponses},
   * and wraps the result in a localized search response.</p>
   *
   * @param parentId the ID of the SoftAsk parent entry associated with the replies.
   * @param page the paginated replies to process; can be {@code null}.
   * @param member the {@link IsAMember} whose context is used for vote enrichment.
   * @return a localized {@link SoftAskReplySearchResult} containing the processed replies,
   *         or an empty result if the page is {@code null}.
   */
  protected SoftAskReplySearchResult processAndReturnSoftAskReplies(final Long parentId, final Page<SoftAskReplyWithDetail> page, final IsAMember member, final UserHaveOtherDetail userHaveOtherDetail) {
    SoftAskReplySearchResult softAskReplySearchResult = SoftAskReplySearchResult.empty(parentId);

    if (nonNull(page)) {
      final Collection<SoftAskReplyResponse> softAskReplyResponses = softAskMapper.toSoftAskReplyResponses(page.getContent(), member);
      softAskCommonService.processSoftAskResponses(softAskReplyResponses, member, userHaveOtherDetail);

      final SearchResult<SoftAskReplyResponse> searchResult = toSearchResult(softAskReplyResponses, page);
      softAskReplySearchResult = SoftAskReplySearchResult.of(parentId, searchResult);
    }

    return localizer.of(softAskReplySearchResult);
  }
}
