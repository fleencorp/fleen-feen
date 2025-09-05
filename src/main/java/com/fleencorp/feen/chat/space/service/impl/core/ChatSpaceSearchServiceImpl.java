package com.fleencorp.feen.chat.space.service.impl.core;

import com.fleencorp.base.model.view.search.SearchResult;
import com.fleencorp.feen.chat.space.constant.core.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.chat.space.constant.core.ChatSpaceStatus;
import com.fleencorp.feen.chat.space.exception.core.ChatSpaceNotFoundException;
import com.fleencorp.feen.chat.space.mapper.ChatSpaceMapper;
import com.fleencorp.feen.chat.space.mapper.impl.ChatSpaceMapperImpl;
import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.chat.space.model.domain.ChatSpaceMember;
import com.fleencorp.feen.chat.space.model.info.core.ChatSpaceTotalMemberRequestToJoinInfo;
import com.fleencorp.feen.chat.space.model.projection.ChatSpaceRequestToJoinPendingSelect;
import com.fleencorp.feen.chat.space.model.request.core.ChatSpaceMemberSearchRequest;
import com.fleencorp.feen.chat.space.model.request.core.ChatSpaceSearchRequest;
import com.fleencorp.feen.chat.space.model.response.RetrieveChatSpaceResponse;
import com.fleencorp.feen.chat.space.model.response.core.ChatSpaceResponse;
import com.fleencorp.feen.chat.space.model.response.member.base.ChatSpaceMemberResponse;
import com.fleencorp.feen.chat.space.model.search.core.ChatSpaceSearchResult;
import com.fleencorp.feen.chat.space.model.search.core.RemovedMemberSearchResult;
import com.fleencorp.feen.chat.space.model.search.core.RequestToJoinSearchResult;
import com.fleencorp.feen.chat.space.model.search.mutual.MutualChatSpaceMembershipSearchResult;
import com.fleencorp.feen.chat.space.service.core.ChatSpaceOperationsService;
import com.fleencorp.feen.chat.space.service.core.ChatSpaceOtherService;
import com.fleencorp.feen.chat.space.service.core.ChatSpaceSearchService;
import com.fleencorp.feen.chat.space.service.core.ChatSpaceService;
import com.fleencorp.feen.chat.space.service.member.ChatSpaceMemberOperationsService;
import com.fleencorp.feen.mapper.common.UnifiedMapper;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.service.member.MemberService;
import com.fleencorp.localizer.service.Localizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.fleencorp.base.util.FleenUtil.toSearchResult;
import static com.fleencorp.feen.chat.space.constant.core.ChatSpaceRequestToJoinStatus.PENDING;
import static java.util.Objects.nonNull;

/**
 * Implementation of the {@link ChatSpaceService} interface, providing methods
 * for managing chat spaces and their associated members.
 *
 * <p>This class handles the core logic for creating, updating, and retrieving
 * chat spaces, as well as processing membership requests and notifications.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Slf4j
@Service
public class ChatSpaceSearchServiceImpl implements ChatSpaceSearchService {

  private final ChatSpaceMemberOperationsService chatSpaceMemberOperationsService;
  private final ChatSpaceOperationsService chatSpaceOperationsService;
  private final ChatSpaceService chatSpaceService;
  private final ChatSpaceOtherService chatSpaceOtherService;
  private final MemberService memberService;
  private final ChatSpaceMapper chatSpaceMapper;
  private final UnifiedMapper unifiedMapper;
  private final Localizer localizer;

  public ChatSpaceSearchServiceImpl(
      final ChatSpaceMemberOperationsService chatSpaceMemberOperationsService,
      final ChatSpaceOperationsService chatSpaceOperationsService,
      final ChatSpaceService chatSpaceService,
      final ChatSpaceOtherService chatSpaceOtherService,
      final MemberService memberService,
      final ChatSpaceMapper chatSpaceMapper,
      final UnifiedMapper unifiedMapper,
      final Localizer localizer) {
    this.chatSpaceMemberOperationsService = chatSpaceMemberOperationsService;
    this.chatSpaceOperationsService = chatSpaceOperationsService;
    this.chatSpaceService = chatSpaceService;
    this.chatSpaceOtherService = chatSpaceOtherService;
    this.memberService = memberService;
    this.chatSpaceMapper = chatSpaceMapper;
    this.unifiedMapper = unifiedMapper;
    this.localizer = localizer;
  }

  /**
   * Finds chat spaces based on the provided search request criteria, which may include
   * date range, title, or default active status.
   *
   * <p>This method checks if all dates are set in the search request to filter chat spaces
   * by the specified date range. If the dates are not set, it checks if a title is provided
   * to filter chat spaces by that title. If neither condition is met, it retrieves all
   * chat spaces that match the default active status.</p>
   *
   * @param searchRequest The search request containing criteria for filtering chat spaces.
   * @return A ChatSpaceSearchResult containing the search results of chat spaces matching the criteria.
   */
  @Override
  public ChatSpaceSearchResult findSpaces(final ChatSpaceSearchRequest searchRequest, final RegisteredUser user) {
    final Page<ChatSpace> page;
    final Pageable pageable = searchRequest.getPage();
    final ChatSpaceStatus chatSpaceStatus = searchRequest.getDefaultActive();
    final String title = searchRequest.getTitle();
    final LocalDateTime startDateTime = searchRequest.getStartDateTime();
    final LocalDateTime endDateTime = searchRequest.getEndDateTime();

    if (searchRequest.areAllDatesSet()) {
      page = chatSpaceOperationsService.findByDateBetween(startDateTime, endDateTime, chatSpaceStatus, pageable);
    } else if (nonNull(title)) {
      page = chatSpaceOperationsService.findByTitle(title, chatSpaceStatus, pageable);
    } else {
      page = chatSpaceOperationsService.findMany(chatSpaceStatus, pageable);
    }

    final List<ChatSpaceResponse> chatSpaceResponses = unifiedMapper.toChatSpaceResponses(page.getContent());
    chatSpaceOtherService.processOtherChatSpaceDetails(chatSpaceResponses, user);

    final SearchResult searchResult = toSearchResult(chatSpaceResponses, page);
    final ChatSpaceSearchResult chatSpaceSearchResult = ChatSpaceSearchResult.of(searchResult);

    return localizer.of(chatSpaceSearchResult);
  }

  /**
   * Finds chat spaces created by the specified user based on the provided search request criteria,
   * which may include date range, title, or a default active status.
   *
   * <p>This method checks if all required date parameters are set in the search request to filter
   * chat spaces by the specified date range. If the dates are not set, it checks if a title is
   * provided to filter chat spaces by that title. If neither condition is met, it retrieves all
   * chat spaces created by the user.</p>
   *
   * @param searchRequest The search request containing criteria for filtering chat spaces.
   * @param user The user whose created chat spaces are being searched.
   * @return A ChatSpaceSearchResult containing the search results of chat spaces created by the user matching the criteria.
   */
  @Override
  public ChatSpaceSearchResult findMySpaces(final ChatSpaceSearchRequest searchRequest, final RegisteredUser user) {
    final Page<ChatSpace> page;
    final Pageable pageable = searchRequest.getPage();
    final Member member = user.toMember();
    final String title = searchRequest.getTitle();
    final LocalDateTime startDateTime = searchRequest.getStartDateTime();
    final LocalDateTime endDateTime = searchRequest.getEndDateTime();

    if (searchRequest.areAllDatesSet()) {
      page = chatSpaceOperationsService.findByDateBetweenForUser(startDateTime, endDateTime, member, pageable);
    } else if (nonNull(title)) {
      page = chatSpaceOperationsService.findByTitleForUser(title, member, pageable);
    } else {
      page = chatSpaceOperationsService.findManyForUser(member, pageable);
    }

    final List<ChatSpaceResponse> chatSpaceResponses = unifiedMapper.toChatSpaceResponses(page.getContent());

    chatSpaceOtherService.processOtherChatSpaceDetails(chatSpaceResponses, user);
    updateTotalRequestToJoinForChatSpaces(chatSpaceResponses);

    final SearchResult searchResult = toSearchResult(chatSpaceResponses, page);
    final ChatSpaceSearchResult chatSpaceSearchResult = ChatSpaceSearchResult.of(searchResult);

    return localizer.of(chatSpaceSearchResult);
  }

  /**
   * Finds chat spaces that the specified user belongs to based on the provided search request criteria,
   * which may include a date range, title, or a default active status.
   *
   * <p>This method first checks if all required date parameters are set in the search request to
   * filter chat spaces by the specified date range. If not, it checks if a title is provided to
   * filter chat spaces by that title. If neither condition is met, it retrieves all chat spaces
   * the user belongs to.</p>
   *
   * @param searchRequest The search request containing criteria for filtering chat spaces.
   * @param user The user whose membership in chat spaces is being queried.
   * @return A ChatSpaceSearchResult containing the search results of chat spaces the user belongs to, matching the criteria.
   */
  @Override
  public ChatSpaceSearchResult findSpacesIBelongTo(final ChatSpaceSearchRequest searchRequest, final RegisteredUser user) {
    final Page<ChatSpaceMember> page;
    final Pageable pageable = searchRequest.getPage();
    final Member member = user.toMember();
    final String title = searchRequest.getTitle();
    final LocalDateTime startDateTime = searchRequest.getStartDateTime();
    final LocalDateTime endDateTime = searchRequest.getEndDateTime();

    if (searchRequest.areAllDatesSet()) {
      page = chatSpaceMemberOperationsService.findSpaceIBelongByDateBetween(startDateTime, endDateTime, member, pageable);
    } else if (nonNull(title)) {
      page = chatSpaceMemberOperationsService.findSpaceIBelongByTitle(title, user.toMember(), pageable);
    } else {
      page = chatSpaceMemberOperationsService.findSpaceIBelongMany(user.toMember(), pageable);
    }

    final List<ChatSpaceResponse> chatSpaceResponses = extractUserChatSpaceFromMembershipAndCreateChatResponse(page.getContent());
    chatSpaceOtherService.processOtherChatSpaceDetails(chatSpaceResponses, user);

    final SearchResult searchResult = toSearchResult(chatSpaceResponses, page);
    final ChatSpaceSearchResult chatSpaceSearchResult = ChatSpaceSearchResult.of(searchResult);

    return localizer.of(chatSpaceSearchResult);
  }

  /**
   * Finds chat spaces where the given user and another specified user are both members.
   *
   * <p>The method retrieves the target member from the database and, if specified in the search request,
   * fetches chat spaces attended by both users. If no target user is provided, an empty page is returned.
   * The results are mapped to {@link ChatSpaceResponse} objects, enriched with additional details, and
   * then wrapped in a {@link MutualChatSpaceMembershipSearchResult} that is localized before being returned.</p>
   *
   * @param searchRequest the search request containing pagination and target user details
   * @param user the currently logged-in user whose memberships will be compared
   * @return a localized {@link MutualChatSpaceMembershipSearchResult} containing the common chat spaces and the target user's name
   */
  @Override
  public MutualChatSpaceMembershipSearchResult findChatSpacesMembershipWithAnotherUser(final ChatSpaceSearchRequest searchRequest, final RegisteredUser user) {
    Page<ChatSpace> page = new PageImpl<>(List.of());
    final Pageable pageable = searchRequest.getPage();
    final Member member = user.toMember();
    Member targetMember = searchRequest.getAnotherUser();

    targetMember = memberService.findMember(targetMember.getMemberId());

    if (searchRequest.hasAnotherUser()) {
      // Retrieve streams attended together by the current user and another user
      page = chatSpaceOperationsService.findCommonChatSpaces(member, targetMember, pageable);
    }

    final List<ChatSpaceResponse> chatSpaceResponses = unifiedMapper.toChatSpaceResponses(page.getContent());
    chatSpaceOtherService.processOtherChatSpaceDetails(chatSpaceResponses, user);

    final SearchResult searchResult = toSearchResult(chatSpaceResponses, page);
    final MutualChatSpaceMembershipSearchResult mutualChatSpaceMembershipSearchResult = MutualChatSpaceMembershipSearchResult.of(searchResult, targetMember.getFullName());
    return localizer.of(mutualChatSpaceMembershipSearchResult);
  }

  /**
   * Retrieves the details of a chat space by its ID.
   *
   * <p>This method looks up a chat space by its ID and returns a response containing
   * the chat space details. It ensures that the result is localized based on the user's preferences.</p>
   *
   * @param chatSpaceId The ID of the chat space to be retrieved.
   * @param user The user requesting the retrieval operation.
   * @return A response containing the chat space details, localized based on the user's locale.
   * @throws ChatSpaceNotFoundException if the chat space with the specified ID is not found.
   */
  @Override
  public RetrieveChatSpaceResponse retrieveChatSpace(final Long chatSpaceId, final RegisteredUser user) throws ChatSpaceNotFoundException {
    final ChatSpace chatSpace = chatSpaceService.findChatSpace(chatSpaceId);

    final ChatSpaceResponse chatSpaceResponse = unifiedMapper.toChatSpaceResponse(chatSpace);
    final List<ChatSpaceResponse> chatSpaceResponses = List.of(chatSpaceResponse);

    chatSpaceOtherService.processOtherChatSpaceDetails(chatSpaceResponses, user);

    final RetrieveChatSpaceResponse retrieveChatSpaceResponse = RetrieveChatSpaceResponse.of(chatSpaceResponse);
    return localizer.of(retrieveChatSpaceResponse);
  }

  /**
   * Extracts chat spaces from the provided list of chat space members and creates a response
   * representation for each chat space.
   *
   * <p>This method checks if the provided list of chat space members is not null or empty.
   * If valid, it maps each member to its corresponding chat space, then converts each chat space
   * to a {@link ChatSpaceResponse} using the {@link ChatSpaceMapperImpl}. If the list is empty or null,
   * an empty list is returned.</p>
   *
   * @param chatSpaceMembers The list of chat space members from which to extract chat spaces.
   * @return A list of {@link ChatSpaceResponse} objects representing the user's chat spaces.
   */
  protected List<ChatSpaceResponse> extractUserChatSpaceFromMembershipAndCreateChatResponse(final List<ChatSpaceMember> chatSpaceMembers) {
    // Check if the list of chat space members is not null and not empty
    if (nonNull(chatSpaceMembers) && !chatSpaceMembers.isEmpty()) {
      return chatSpaceMembers.stream()
        .filter(Objects::nonNull)
        // Extract the chat space from each chat space member
        .map(ChatSpaceMember::getChatSpace)
        // Convert each chat space to a ChatSpaceResponse
        .map(unifiedMapper::toChatSpaceResponse)
        // Collect the responses into a list
        .toList();
    }
    // Return an empty list if the input is null or empty
    return List.of();
  }

  /**
   * Finds chat space members with pending requests to join a specific chat space,
   * optionally filtering by member name.
   *
   * <p>This method retrieves the chat space using the provided chat space ID.
   * If a member name is specified in the search request, it fetches members
   * with that name who have a pending join request status. If no member name
   * is provided, it retrieves all members with a pending status for the chat space.</p>
   *
   * @param chatSpaceId The ID of the chat space for which to find members with pending join requests.
   * @param searchRequest The search request containing pagination details and optional member name.
   * @param user The user performing the search, which must have appropriate permissions.
   * @return A RequestToJoinSearchResult containing the search results of chat space members with pending join requests.
   * @throws ChatSpaceNotFoundException if the chat space does not exist.
   */
  @Override
  public RequestToJoinSearchResult findRequestToJoinSpace(final Long chatSpaceId, final ChatSpaceMemberSearchRequest searchRequest, final RegisteredUser user) {
    final Page<ChatSpaceMember> page;
    final Pageable pageable = searchRequest.getPage();
    final String memberName = searchRequest.getMemberName();
    final Set<ChatSpaceRequestToJoinStatus> joinStatusesForSearch = searchRequest.forPendingOrDisapprovedRequestToJoinStatus();

    final ChatSpace chatSpace = chatSpaceService.findChatSpace(chatSpaceId);
    chatSpaceService.verifyCreatorOrAdminOfChatSpace(chatSpace, user.toMember());

    if (nonNull(memberName)) {
      page = chatSpaceMemberOperationsService.findByChatSpaceAndMemberNameAndRequestToJoinStatus(chatSpace, memberName, joinStatusesForSearch, pageable);
    } else {
      page = chatSpaceMemberOperationsService.findByChatSpaceAndRequestToJoinStatus(chatSpace, joinStatusesForSearch, pageable);
    }

    final List<ChatSpaceMemberResponse> chatSpaceMemberResponses = unifiedMapper.toChatSpaceMemberResponses(page.getContent(), chatSpace);
    final SearchResult searchResult = toSearchResult(chatSpaceMemberResponses, page);
    final RequestToJoinSearchResult requestToJoinSearchResult = RequestToJoinSearchResult.of(searchResult);

    return localizer.of(requestToJoinSearchResult);
  }

  /**
   * Finds members who have been removed from a specific chat space,
   * optionally filtering by member name.
   *
   * <p>This method retrieves the chat space using the provided chat space ID.
   * If a member name is specified in the search request, it fetches removed
   * members with that name. If no name is provided, it retrieves all removed
   * members for the given chat space.</p>
   *
   * @param chatSpaceId The ID of the chat space for which to find removed members.
   * @param searchRequest The search request containing pagination details and optional member name.
   * @param user The user performing the search, which must have appropriate permissions.
   * @return A RemovedMemberSearchResult containing the search results of removed chat space members.
   * @throws ChatSpaceNotFoundException if the chat space does not exist.
   */
  @Override
  public RemovedMemberSearchResult findRemovedMembers(final Long chatSpaceId, final ChatSpaceMemberSearchRequest searchRequest, final RegisteredUser user) {
    final Page<ChatSpaceMember> page;
    final Pageable pageable = searchRequest.getPage();
    final String memberName = searchRequest.getMemberName();

    final ChatSpace chatSpace = chatSpaceService.findChatSpace(chatSpaceId);
    chatSpaceService.verifyCreatorOrAdminOfChatSpace(chatSpace, user.toMember());

    // Check if a member name is provided in the search request
    if (nonNull(memberName)) {
      page = chatSpaceMemberOperationsService.findByChatSpaceAndMemberNameAndRemoved(chatSpace, memberName, pageable);
    } else {
      page = chatSpaceMemberOperationsService.findByChatSpaceAndRemoved(chatSpace, pageable);
    }

    final List<ChatSpaceMemberResponse> chatSpaceMembers = unifiedMapper.toChatSpaceMemberResponses(page.getContent(), chatSpace);
    final RemovedMemberSearchResult removedMemberSearchResult = RemovedMemberSearchResult.of(toSearchResult(chatSpaceMembers, page));

    return localizer.of(removedMemberSearchResult);
  }

  /**
   * Updates the total number of pending requests to join for each chat space in the given collection of ChatSpaceResponse objects.
   *
   * <p>This method retrieves the pending join request counts for each chat space by querying the repository,
   * maps the counts to the respective chat spaces, and then updates the {@code totalRequestToJoin} field in each {@link ChatSpaceResponse}.
   * If no pending requests are found for a chat space, the total is set to 0.</p>
   *
   * @param responses a collection of {@link ChatSpaceResponse} objects representing the chat spaces
   */
  protected void updateTotalRequestToJoinForChatSpaces(final Collection<ChatSpaceResponse> responses) {
    if (nonNull(responses) && (!responses.isEmpty())) {
      // Get a list of chatSpaceIds to retrieve join request counts
      final List<Long> chatSpaceIds = responses.stream()
        .filter(Objects::nonNull)
        .map(ChatSpaceResponse::getNumberId)
        .toList();

      // Fetch the pending join request counts for the chat spaces
      final List<ChatSpaceRequestToJoinPendingSelect> pendingRequests = chatSpaceMemberOperationsService
        .countPendingJoinRequestsForChatSpaces(chatSpaceIds, PENDING);

      // Map the counts back to the ChatSpaceResponse objects
      final Map<Long, Integer> pendingRequestsMap = pendingRequests.stream()
        .filter(Objects::nonNull)
        .collect(Collectors.toMap(ChatSpaceRequestToJoinPendingSelect::getChatSpaceId, ChatSpaceRequestToJoinPendingSelect::getTotalRequestToJoinTotal));

      responses.stream()
        .filter(Objects::nonNull)
        .forEach(response -> {
          final Integer totalMembers = pendingRequestsMap.getOrDefault(response.getNumberId(), 0);
          final ChatSpaceTotalMemberRequestToJoinInfo chatSpaceTotalMemberRequestToJoinInfo = chatSpaceMapper.toChatSpaceTotalMemberRequestToJoinInfo(totalMembers);
          response.setChatSpaceTotalMemberRequestToJoinInfo(chatSpaceTotalMemberRequestToJoinInfo);
      });
    }
  }

  /**
   * Retrieves the total number of pending requests to join a specific chat space.
   *
   * <p>This method checks for pending join requests for the given {@code chatSpaceId}.
   * If the {@code chatSpaceId} is not null, it queries the repository to count the
   * pending join requests. The result is returned as a long value representing the
   * total pending join requests for that chat space.</p>
   *
   * @param chatSpaceId the ID of the chat space to get the pending join request count for
   * @return the total number of pending requests to join the specified chat space, or {@code 0L} if the ID is null or no pending requests exist
   */
  @Override
  public Integer getTotalRequestToJoinForChatSpace(final Long chatSpaceId) {
    if (nonNull(chatSpaceId)) {
      // Create a list based on the chat space id
      final List<Long> chatSpaceIds = List.of(chatSpaceId);

      // Fetch the pending join request counts for the chat spaces
      final List<ChatSpaceRequestToJoinPendingSelect> pendingRequests = chatSpaceMemberOperationsService
        .countPendingJoinRequestsForChatSpaces(chatSpaceIds, PENDING);

      // Map the counts back to the ChatSpaceResponse objects
      final Map<Long, Integer> pendingRequestsMap = pendingRequests.stream()
        .filter(Objects::nonNull)
        .collect(Collectors.toMap(ChatSpaceRequestToJoinPendingSelect::getChatSpaceId, ChatSpaceRequestToJoinPendingSelect::getTotalRequestToJoinTotal));

      return pendingRequestsMap.getOrDefault(chatSpaceId, 0);
    }
    return 0;
  }


}
