package com.fleencorp.feen.softask.service.impl.participant;

import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.softask.mapper.SoftAskInfoMapper;
import com.fleencorp.feen.softask.model.info.vote.total.TotalSoftAskConversationVotedInfo;
import com.fleencorp.feen.softask.model.info.vote.total.TotalSoftAskReplyVotedInfo;
import com.fleencorp.feen.softask.model.info.vote.total.TotalSoftAskVotedInfo;
import com.fleencorp.feen.softask.model.request.SoftAskSearchRequest;
import com.fleencorp.feen.softask.model.response.user.SoftAskUserProfileRetrieveResponse;
import com.fleencorp.feen.softask.model.search.SoftAskReplySearchResult;
import com.fleencorp.feen.softask.model.search.SoftAskSearchResult;
import com.fleencorp.feen.softask.model.search.SoftAskVoteSearchResult;
import com.fleencorp.feen.softask.service.participant.SoftAskParticipantService;
import com.fleencorp.feen.softask.service.reply.SoftAskReplySearchService;
import com.fleencorp.feen.softask.service.softask.SoftAskSearchService;
import com.fleencorp.feen.softask.service.vote.SoftAskVoteSearchService;
import com.fleencorp.feen.softask.service.vote.SoftAskVoteService;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.stereotype.Service;

import static java.util.Objects.nonNull;

@Service
public class SoftAskParticipantServiceImpl implements SoftAskParticipantService {

  private final SoftAskReplySearchService softAskReplySearchService;
  private final SoftAskSearchService softAskSearchService;
  private final SoftAskVoteSearchService softAskVoteSearchService;
  private final SoftAskVoteService softAskVoteService;
  private final SoftAskInfoMapper softAskInfoMapper;
  private final Localizer localizer;

  public SoftAskParticipantServiceImpl(
      final SoftAskReplySearchService softAskReplySearchService,
      final SoftAskSearchService softAskSearchService,
      final SoftAskVoteSearchService softAskVoteSearchService,
      final SoftAskVoteService softAskVoteService,
      final SoftAskInfoMapper softAskInfoMapper,
      final Localizer localizer) {
    this.softAskReplySearchService = softAskReplySearchService;
    this.softAskSearchService = softAskSearchService;
    this.softAskVoteSearchService = softAskVoteSearchService;
    this.softAskVoteService = softAskVoteService;
    this.softAskInfoMapper = softAskInfoMapper;
    this.localizer = localizer;
  }

  /**
   * Retrieves the user profile response containing summary search results and total vote counts.
   *
   * <p>Creates a search request limited to the user’s authored SoftAsk entries,
   * sets search results and vote totals on the response,
   * and returns the localized response.</p>
   *
   * @param user the {@link RegisteredUser} whose profile is being retrieved.
   * @return a localized {@link SoftAskUserProfileRetrieveResponse} containing profile details.
   */
  @Override
  public SoftAskUserProfileRetrieveResponse findUserProfile(final RegisteredUser user) {
    final IsAMember member = user.toMember();
    final SoftAskSearchRequest searchRequest = new SoftAskSearchRequest();
    searchRequest.setPageSize(2);

    searchRequest.setAuthor(member);
    final SoftAskUserProfileRetrieveResponse softAskUserProfileResponse = SoftAskUserProfileRetrieveResponse.of();
    setSearchResults(softAskUserProfileResponse, searchRequest, user);
    setTotalVotes(softAskUserProfileResponse, member.getMemberId());

    return localizer.of(softAskUserProfileResponse);
  }

  /**
   * Sets various search result data on the user profile response based on the given search request and user.
   *
   * <p>Performs searches for replies, soft asks, and user votes,
   * then assigns the resulting search results to the response.</p>
   *
   * @param softAskUserProfileResponse the profile response to populate with search results.
   * @param searchRequest the search criteria used for querying.
   * @param user the {@link RegisteredUser} performing the searches.
   */
  private void setSearchResults(final SoftAskUserProfileRetrieveResponse softAskUserProfileResponse, final SoftAskSearchRequest searchRequest, final RegisteredUser user) {
    if (nonNull(softAskUserProfileResponse) && nonNull(searchRequest)) {
      final SoftAskReplySearchResult softAskReplySearchResult = softAskReplySearchService.findSoftAskReplies(searchRequest, user);
      final SoftAskSearchResult softAskSearchResult = softAskSearchService.findSoftAsks(searchRequest, user);
      final SoftAskVoteSearchResult softAskVoteSearchResult = softAskVoteSearchService.findUserVotes(searchRequest, user);

      softAskUserProfileResponse.setSoftAskReplySearchResult(softAskReplySearchResult);
      softAskUserProfileResponse.setSoftAskSearchResult(softAskSearchResult);
      softAskUserProfileResponse.setSoftAskVoteSearchResult(softAskVoteSearchResult);
    }
  }

  /**
   * Sets the total vote counts for various SoftAsk categories on the given user profile response.
   *
   * <p>Fetches the total number of votes the user has received for replies, and general SoftAsk,
   * then calculates the total votes across all categories.</p>
   *
   * <p>Maps these counts to their respective info objects and assigns them to the response.</p>
   *
   * @param softAskUserProfileResponse the response object to populate with vote totals.
   * @param memberId the ID of the member whose votes are being counted.
   */
  private void setTotalVotes(final SoftAskUserProfileRetrieveResponse softAskUserProfileResponse, final Long memberId) {
    if (nonNull(softAskUserProfileResponse)) {
      final Integer totalSoftAskReplyVotes = softAskVoteService.countUserSoftAskReplyVotes(memberId);
      final Integer totalSoftAskVotes = softAskVoteService.countUserSoftAskVotes(memberId);
      final Integer totalSoftAskConversationVotes = totalSoftAskReplyVotes + totalSoftAskVotes;

      final TotalSoftAskReplyVotedInfo totalSoftAskReplyVotedInfo = softAskInfoMapper.toTotalSoftAskReplyVotedInfo(totalSoftAskReplyVotes);
      final TotalSoftAskVotedInfo totalSoftAskVotedInfo = softAskInfoMapper.toTotalSoftAskVotedInfo(totalSoftAskVotes);
      final TotalSoftAskConversationVotedInfo totalSoftAskConversationVotedInfo = softAskInfoMapper.toTotalSoftAskConversationVotedInfo(totalSoftAskConversationVotes);

      softAskUserProfileResponse.setTotalSoftAskReplyVotedInfo(totalSoftAskReplyVotedInfo);
      softAskUserProfileResponse.setTotalSoftAskVotedInfo(totalSoftAskVotedInfo);
      softAskUserProfileResponse.setTotalSoftAskConversationVotedInfo(totalSoftAskConversationVotedInfo);
    }
  }
}
