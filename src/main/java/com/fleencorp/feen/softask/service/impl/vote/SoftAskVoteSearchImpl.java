package com.fleencorp.feen.softask.service.impl.vote;

import com.fleencorp.base.model.view.search.SearchResult;
import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.softask.constant.core.vote.SoftAskVoteType;
import com.fleencorp.feen.softask.contract.SoftAskCommonResponse;
import com.fleencorp.feen.softask.mapper.SoftAskInfoMapper;
import com.fleencorp.feen.softask.mapper.SoftAskMapper;
import com.fleencorp.feen.softask.model.domain.SoftAskVote;
import com.fleencorp.feen.softask.model.holder.SoftAskUserVoteHolder;
import com.fleencorp.feen.softask.model.info.vote.SoftAskUserVoteInfo;
import com.fleencorp.feen.softask.model.request.SoftAskSearchRequest;
import com.fleencorp.feen.softask.model.response.vote.core.SoftAskVoteResponse;
import com.fleencorp.feen.softask.model.search.SoftAskVoteSearchResult;
import com.fleencorp.feen.softask.repository.vote.SoftAskVoteSearchRepository;
import com.fleencorp.feen.softask.service.vote.SoftAskVoteSearchService;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static com.fleencorp.base.util.FleenUtil.toSearchResult;
import static java.util.Objects.nonNull;

@Service
public class SoftAskVoteSearchImpl implements SoftAskVoteSearchService {

  private final SoftAskVoteSearchRepository softAskVoteSearchRepository;
  private final SoftAskInfoMapper softAskInfoMapper;
  private final SoftAskMapper softAskMapper;
  private final Localizer localizer;

  public SoftAskVoteSearchImpl(
      final SoftAskVoteSearchRepository softAskVoteSearchRepository,
      final SoftAskInfoMapper softAskInfoMapper,
      final SoftAskMapper softAskMapper,
      final Localizer localizer) {
    this.softAskVoteSearchRepository = softAskVoteSearchRepository;
    this.softAskInfoMapper = softAskInfoMapper;
    this.softAskMapper = softAskMapper;
    this.localizer = localizer;
  }

  /**
   * Processes vote information for a collection of {@link SoftAskCommonResponse} objects
   * based on the given member's voting history.
   *
   * <p>Fetches the relevant votes by the member, maps them to their parent IDs,
   * and populates each response in the collection with its corresponding {@link SoftAskUserVoteInfo}.</p>
   *
   * @param softAskCommonResponses the collection of responses to enrich with vote info; must not be {@code null}.
   * @param member the {@link Member} whose votes are being evaluated.
   * @param <T> the type of response, extending {@link SoftAskCommonResponse}.
   */
  @Override
  public <T extends SoftAskCommonResponse> void processVotesForResponses(final Collection<T> softAskCommonResponses, final IsAMember member) {
    if (nonNull(softAskCommonResponses) && !softAskCommonResponses.isEmpty() && nonNull(member)) {

      final Collection<Long> parentIds = SoftAskUserVoteHolder.getParentIdsToScanForVotes(softAskCommonResponses);
      final Collection<SoftAskVote> userVotes = softAskVoteSearchRepository.findByParentsAndMember(parentIds, member.getMemberId());

      final SoftAskUserVoteHolder softAskUserVoteHolder = SoftAskUserVoteHolder.of(userVotes);
      final Map<Long, SoftAskVote> voteMap = softAskUserVoteHolder.groupVotes();

      softAskCommonResponses.forEach(response -> {
        final SoftAskVote userVote = voteMap.get(response.getParentId());
        Optional.ofNullable(userVote)
          .ifPresent(vote -> {
            final SoftAskUserVoteInfo softAskUserVoteInfo = softAskInfoMapper.toUserVoteInfo(vote.isVoted());
            response.setSoftAskUserVoteInfo(softAskUserVoteInfo);
        });
      });
    }
  }

  /**
   * Retrieves paginated vote data of type {@code VOTED} based on the provided {@link SoftAskSearchRequest}
   * and the current {@link RegisteredUser}.
   *
   * <p>If the search request is filtered by author, it fetches votes of type {@code VOTED}
   * authored by the given user. The results are mapped to response DTOs and wrapped in a localized
   * {@link SoftAskVoteSearchResult}.</p>
   *
   * @param searchRequest the search criteria containing filters and pagination.
   * @param user the currently logged-in user performing the search.
   * @return a localized {@link SoftAskVoteSearchResult} containing the paginated vote responses.
   */
  @Override
  @Transactional(readOnly = true)
  public SoftAskVoteSearchResult findUserVotes(final SoftAskSearchRequest searchRequest, final RegisteredUser user) {
    final Long authorId = searchRequest.getAuthorId();
    final Pageable pageable = searchRequest.getPage();

    final Page<SoftAskVote> page = searchRequest.isByAuthor()
      ? softAskVoteSearchRepository.findByAuthor(authorId, SoftAskVoteType.VOTED, pageable)
      : Page.empty();

    final Collection<SoftAskVoteResponse> softAskVoteResponses = softAskMapper.toSoftAskVoteResponses(page.getContent());
    final SearchResult searchResult = toSearchResult(softAskVoteResponses, page);
    final SoftAskVoteSearchResult softAskVoteSearchResult = SoftAskVoteSearchResult.of(searchResult);

    return localizer.of(softAskVoteSearchResult);
  }
}
