package com.fleencorp.feen.softask.service.impl.reply;

import com.fleencorp.base.model.view.search.SearchResult;
import com.fleencorp.feen.softask.exception.core.SoftAskReplyNotFoundException;
import com.fleencorp.feen.softask.mapper.SoftAskMapper;
import com.fleencorp.feen.softask.model.domain.SoftAskReply;
import com.fleencorp.feen.softask.model.request.SoftAskSearchRequest;
import com.fleencorp.feen.softask.model.response.reply.core.SoftAskReplyResponse;
import com.fleencorp.feen.softask.model.search.SoftAskReplySearchResult;
import com.fleencorp.feen.softask.repository.reply.SoftAskReplySearchRepository;
import com.fleencorp.feen.softask.service.common.SoftAskCommonService;
import com.fleencorp.feen.softask.service.reply.SoftAskReplySearchService;
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
   * Finds a {@link SoftAskReply} by its ID.
   *
   * <p>Throws {@link SoftAskReplyNotFoundException} if no reply is found with the given ID.</p>
   *
   * @param softAskReplyId the ID of the SoftAsk reply to find.
   * @return the found {@link SoftAskReply} entity.
   * @throws SoftAskReplyNotFoundException if the reply does not exist.
   */
  @Override
  public SoftAskReply findSoftAskReply(final Long softAskReplyId) throws SoftAskReplyNotFoundException {
    return softAskReplySearchRepository.findById(softAskReplyId)
      .orElseThrow(SoftAskReplyNotFoundException.of(softAskReplyId));
  }

  /**
   * Delegates to {@link #findSoftAskReplies(SoftAskSearchRequest, RegisteredUser)} using the given {@link Member}.
   *
   * <p>Wraps the member in a {@link RegisteredUser} and forwards the request.</p>
   *
   * @param searchRequest the request containing filter and pagination parameters.
   * @param member the {@link Member} performing the request.
   * @return a localized {@link SoftAskReplySearchResult} containing the paginated and enriched replies.
   */
  @Override
  public SoftAskReplySearchResult findSoftAskReplies(final SoftAskSearchRequest searchRequest, final Member member) {
    final Long memberId = member.getMemberId();
    final RegisteredUser user = RegisteredUser.of(memberId);
    return findSoftAskReplies(searchRequest, user);
  }

  /**
   * Finds replies to a SoftAsk answer based on the provided {@link SoftAskSearchRequest}
   * and returns a localized {@link SoftAskReplySearchResult}.
   *
   * <p>If the request is filtered by author, it fetches replies created by that author.
   * Otherwise, it fetches replies associated with the specified SoftAsk answer ID.</p>
   *
   * @param searchRequest the request containing filter and pagination parameters.
   * @param user the {@link RegisteredUser} performing the request.
   * @return a localized {@link SoftAskReplySearchResult} containing the paginated and enriched replies.
   */
  @Override
  public SoftAskReplySearchResult findSoftAskReplies(final SoftAskSearchRequest searchRequest, final RegisteredUser user) {
    final Long parentId = searchRequest.getParentId();
    final Long authorId = searchRequest.getAuthorId();
    final Pageable pageable = searchRequest.getPage();

    final Page<SoftAskReply> page = searchRequest.isByAuthor()
      ? softAskReplySearchRepository.findByAuthor(authorId, pageable)
      : softAskReplySearchRepository.findBySoftAskAnswer(parentId, pageable);

    return processAndReturnSoftAskReplies(parentId, page, user.toMember());
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
   * @param member the {@link Member} whose context is used for vote enrichment.
   * @return a localized {@link SoftAskReplySearchResult} containing the processed replies,
   *         or an empty result if the page is {@code null}.
   */
  protected SoftAskReplySearchResult processAndReturnSoftAskReplies(final Long parentId, final Page<SoftAskReply> page, final Member member) {
    if (nonNull(page)) {
      final Collection<SoftAskReplyResponse> softAskReplyResponses = softAskMapper.toSoftAskReplyResponses(page.getContent());
      softAskCommonService.processSoftAskResponses(softAskReplyResponses, member);

      final SearchResult searchResult = toSearchResult(softAskReplyResponses, page);
      final SoftAskReplySearchResult softAskReplySearchResult = SoftAskReplySearchResult.of(parentId, searchResult);
      return localizer.of(softAskReplySearchResult);
    }

    return SoftAskReplySearchResult.empty(parentId);
  }
}
