package com.fleencorp.feen.service.impl.chat.space;

import com.fleencorp.base.model.view.search.SearchResult;
import com.fleencorp.feen.constant.chat.space.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.constant.chat.space.ChatSpaceStatus;
import com.fleencorp.feen.exception.chat.space.ChatSpaceNotFoundException;
import com.fleencorp.feen.link.model.response.base.LinkResponse;
import com.fleencorp.feen.link.service.LinkService;
import com.fleencorp.feen.mapper.common.UnifiedMapper;
import com.fleencorp.feen.mapper.impl.chat.ChatSpaceMapperImpl;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.chat.ChatSpaceMember;
import com.fleencorp.feen.model.projection.chat.space.ChatSpaceMemberSelect;
import com.fleencorp.feen.model.projection.chat.space.ChatSpaceRequestToJoinPendingSelect;
import com.fleencorp.feen.model.request.search.chat.space.ChatSpaceMemberSearchRequest;
import com.fleencorp.feen.model.request.search.chat.space.ChatSpaceSearchRequest;
import com.fleencorp.feen.model.response.chat.space.RetrieveChatSpaceResponse;
import com.fleencorp.feen.model.response.chat.space.base.ChatSpaceResponse;
import com.fleencorp.feen.model.response.chat.space.member.base.ChatSpaceMemberResponse;
import com.fleencorp.feen.model.search.chat.space.ChatSpaceSearchResult;
import com.fleencorp.feen.model.search.chat.space.mutual.MutualChatSpaceMembershipSearchResult;
import com.fleencorp.feen.model.search.join.RemovedMemberSearchResult;
import com.fleencorp.feen.model.search.join.RequestToJoinSearchResult;
import com.fleencorp.feen.service.chat.space.ChatSpaceOperationsService;
import com.fleencorp.feen.service.chat.space.ChatSpaceSearchService;
import com.fleencorp.feen.service.chat.space.ChatSpaceService;
import com.fleencorp.feen.service.chat.space.member.ChatSpaceMemberOperationsService;
import com.fleencorp.feen.like.service.LikeService;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import com.fleencorp.feen.user.service.MemberService;
import com.fleencorp.localizer.service.Localizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.fleencorp.base.util.FleenUtil.toSearchResult;
import static com.fleencorp.feen.constant.chat.space.ChatSpaceRequestToJoinStatus.PENDING;
import static com.fleencorp.feen.service.impl.common.MiscServiceImpl.*;
import static com.fleencorp.feen.util.CommonUtil.allNonNull;
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
  private final LikeService likeService;
  private final LinkService linkService;
  private final MemberService memberService;
  private final UnifiedMapper unifiedMapper;
  private final Localizer localizer;

  private static final int DEFAULT_NUMBER_OF_MEMBERS_TO_GET_FOR_CHAT_SPACE = 10;

  /**
   * Constructs a new {@code ChatSpaceSearchServiceImpl}, which provides functionality for searching and retrieving chat spaces.
   *
   * @param chatSpaceMemberOperationsService the service for managing member interactions and permissions within chat spaces
   * @param chatSpaceOperationsService the service for handling operational logic related to chat spaces
   * @param chatSpaceService the core service for managing chat space entities
   * @param likeService the service for handling like interactions on chat spaces
   * @param linkService the service for managing links associated with chat spaces
   * @param memberService the service for managing members
   * @param unifiedMapper the utility for mapping between domain models and DTOs
   * @param localizer the component used for resolving localized messages
   */
  public ChatSpaceSearchServiceImpl(
      final ChatSpaceMemberOperationsService chatSpaceMemberOperationsService,
      final ChatSpaceOperationsService chatSpaceOperationsService,
      final ChatSpaceService chatSpaceService,
      final LikeService likeService,
      final LinkService linkService,
      final MemberService memberService,
      final UnifiedMapper unifiedMapper,
      final Localizer localizer) {
    this.chatSpaceMemberOperationsService = chatSpaceMemberOperationsService;
    this.chatSpaceOperationsService = chatSpaceOperationsService;
    this.chatSpaceService = chatSpaceService;
    this.likeService = likeService;
    this.linkService = linkService;
    this.memberService = memberService;
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

    // Check if all required date parameters are set in the search request
    if (searchRequest.areAllDatesSet()) {
      // Retrieve chat spaces within the specified date range
      page = chatSpaceOperationsService.findByDateBetween(startDateTime, endDateTime, chatSpaceStatus, pageable);
    } else if (nonNull(title)) {
      // Retrieve chat spaces that match the specified title
      page = chatSpaceOperationsService.findByTitle(title, chatSpaceStatus, pageable);
    } else {
      // Retrieve all chat spaces that match the default active status
      page = chatSpaceOperationsService.findMany(chatSpaceStatus, pageable);
    }

    // Convert the retrieved chat spaces to response objects
    final List<ChatSpaceResponse> chatSpaceResponses = unifiedMapper.toChatSpaceResponses(page.getContent());
    // Process other details of the chat space responses
    processOtherChatSpaceDetails(chatSpaceResponses, user);
    // Create the search result
    final SearchResult searchResult = toSearchResult(chatSpaceResponses, page);
    // Create the search result
    final ChatSpaceSearchResult chatSpaceSearchResult = ChatSpaceSearchResult.of(searchResult);
    // Return a search result with the responses and pagination details
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

    // Check if all required date parameters are set in the search request
    if (searchRequest.areAllDatesSet()) {
      // Retrieve chat spaces created by the user within the specified date range
      page = chatSpaceOperationsService.findByDateBetweenForUser(startDateTime, endDateTime, member, pageable);
    } else if (nonNull(title)) {
      // Retrieve chat spaces created by the user that match the specified title
      page = chatSpaceOperationsService.findByTitleForUser(title, member, pageable);
    } else {
      // Retrieve all chat spaces created by the user
      page = chatSpaceOperationsService.findManyForUser(member, pageable);
    }

    // Convert the retrieved chat spaces to response objects
    final List<ChatSpaceResponse> chatSpaceResponses = unifiedMapper.toChatSpaceResponses(page.getContent());
    // Process other details of the chat space responses
    processOtherChatSpaceDetails(chatSpaceResponses, user);
    // Update the total request to join for each chat space
    updateTotalRequestToJoinForChatSpaces(chatSpaceResponses);
    // Create the search result
    final SearchResult searchResult = toSearchResult(chatSpaceResponses, page);
    // Create the search result
    final ChatSpaceSearchResult chatSpaceSearchResult = ChatSpaceSearchResult.of(searchResult);
    // Return a search result with the responses and pagination details
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

    // Check if all required date parameters are set in the search request
    if (searchRequest.areAllDatesSet()) {
      // Retrieve chat spaces the user belongs to within the specified date range
      page = chatSpaceMemberOperationsService.findSpaceIBelongByDateBetween(startDateTime, endDateTime, member, pageable);
    } else if (nonNull(title)) {
      // Retrieve chat spaces the user belongs to that match the specified title
      page = chatSpaceMemberOperationsService.findSpaceIBelongByTitle(title, user.toMember(), pageable);
    } else {
      // Retrieve all chat spaces the user belongs to
      page = chatSpaceMemberOperationsService.findSpaceIBelongMany(user.toMember(), pageable);
    }

    // Convert the retrieved chat spaces from membership to response objects
    final List<ChatSpaceResponse> chatSpaceResponses = extractUserChatSpaceFromMembershipAndCreateChatResponse(page.getContent());
    // Process other details of the chat space responses
    processOtherChatSpaceDetails(chatSpaceResponses, user);
    // Create the search result
    final SearchResult searchResult = toSearchResult(chatSpaceResponses, page);
    // Create the search result
    final ChatSpaceSearchResult chatSpaceSearchResult = ChatSpaceSearchResult.of(searchResult);
    // Return a search result with the responses and pagination details
    return localizer.of(chatSpaceSearchResult);
  }

  @Override
  public MutualChatSpaceMembershipSearchResult findChatSpacesMembershipWithAnotherUser(final ChatSpaceSearchRequest searchRequest, final RegisteredUser user) {
    Page<ChatSpace> page = new PageImpl<>(List.of());
    final Pageable pageable = searchRequest.getPage();
    final Member member = user.toMember();
    Member targetMember = searchRequest.getAnotherUser();
    // Find the target member
    targetMember = memberService.findMember(targetMember.getMemberId());

    if (searchRequest.hasAnotherUser()) {
      // Retrieve streams attended together by the current user and another user
      page = chatSpaceOperationsService.findCommonChatSpaces(member, targetMember, pageable);
    }

    final List<ChatSpaceResponse> chatSpaceResponses = unifiedMapper.toChatSpaceResponses(page.getContent());
    // Process other details of the chat space responses
    processOtherChatSpaceDetails(chatSpaceResponses, user);
    // Create the search result
    final SearchResult searchResult = toSearchResult(chatSpaceResponses, page);
    // Create the search result
    final MutualChatSpaceMembershipSearchResult mutualChatSpaceMembershipSearchResult = MutualChatSpaceMembershipSearchResult.of(searchResult, targetMember.getFullName());
    // Return a search result with the responses and pagination details
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
  public RetrieveChatSpaceResponse retrieveChatSpace(final Long chatSpaceId, final RegisteredUser user) {
    // Find the chat space by its ID or throw an exception if not found
    final ChatSpace chatSpace = chatSpaceService.findChatSpace(chatSpaceId);
    // Get the equivalent chat space response
    final ChatSpaceResponse chatSpaceResponse = unifiedMapper.toChatSpaceResponse(chatSpace);
    // Create a list
    final List<ChatSpaceResponse> chatSpaceResponses = List.of(chatSpaceResponse);
    // Process other details of the chat space responses
    processOtherChatSpaceDetails(chatSpaceResponses, user);
    // Create the response
    final RetrieveChatSpaceResponse retrieveChatSpaceResponse = RetrieveChatSpaceResponse.of(chatSpaceResponse);
    // Return a localized response containing the chat space details
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

    // Retrieve the chat space by its ID
    final ChatSpace chatSpace = chatSpaceService.findChatSpace(chatSpaceId);
    // Verify that the user is the creator or an admin of the chat space
    chatSpaceService.verifyCreatorOrAdminOfChatSpace(chatSpace, user.toMember());

    // Check if a member name is provided in the search request
    if (nonNull(memberName)) {
      // Fetch members with the specified name and pending join request status
      page = chatSpaceMemberOperationsService.findByChatSpaceAndMemberNameAndRequestToJoinStatus(chatSpace, memberName, joinStatusesForSearch, pageable);
    } else {
      // Fetch all members with a pending join request status
      page = chatSpaceMemberOperationsService.findByChatSpaceAndRequestToJoinStatus(chatSpace, joinStatusesForSearch, pageable);
    }

    // Convert the chat space members to response objects
    final List<ChatSpaceMemberResponse> chatSpaceMemberResponses = unifiedMapper.toChatSpaceMemberResponses(page.getContent(), chatSpace);
    // Create the search result
    final SearchResult searchResult = toSearchResult(chatSpaceMemberResponses, page);
    // Create the search result
    final RequestToJoinSearchResult requestToJoinSearchResult = RequestToJoinSearchResult.of(searchResult);
    // Return a search result with the responses and pagination details
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

    // Retrieve the chat space by its ID
    final ChatSpace chatSpace = chatSpaceService.findChatSpace(chatSpaceId);
    // Verify that the user is the creator or an admin of the chat space
    chatSpaceService.verifyCreatorOrAdminOfChatSpace(chatSpace, user.toMember());

    // Check if a member name is provided in the search request
    if (nonNull(memberName)) {
      // Fetch members with the specified name
      page = chatSpaceMemberOperationsService.findByChatSpaceAndMemberNameAndRemoved(chatSpace, memberName, pageable);
    } else {
      // Fetch all members
      page = chatSpaceMemberOperationsService.findByChatSpaceAndRemoved(chatSpace, pageable);
    }

    // Convert the chat space members to response objects
    final List<ChatSpaceMemberResponse> chatSpaceMembers = unifiedMapper.toChatSpaceMemberResponses(page.getContent(), chatSpace);
    // Create the search result
    final RemovedMemberSearchResult removedMemberSearchResult = RemovedMemberSearchResult.of(toSearchResult(chatSpaceMembers, page));
    // Return a search result with the responses and pagination details
    return localizer.of(removedMemberSearchResult);
  }

  /**
   * Updates the total number of pending requests to join for each chat space in the given collection of ChatSpaceResponse objects.
   *
   * <p>This method retrieves the pending join request counts for each chat space by querying the repository,
   * maps the counts to the respective chat spaces, and then updates the {@code totalRequestToJoin} field in each {@link ChatSpaceResponse}.
   * If no pending requests are found for a chat space, the total is set to 0.</p>
   *
   * @param views a collection of {@link ChatSpaceResponse} objects representing the chat spaces
   */
  protected void updateTotalRequestToJoinForChatSpaces(final Collection<ChatSpaceResponse> views) {
    if (nonNull(views) && (!views.isEmpty())) {
      // Get a list of chatSpaceIds to retrieve join request counts
      final List<Long> chatSpaceIds = views.stream()
        .filter(Objects::nonNull)
        .map(ChatSpaceResponse::getNumberId)
        .toList();

      // Fetch the pending join request counts for the chat spaces
      final List<ChatSpaceRequestToJoinPendingSelect> pendingRequests = chatSpaceMemberOperationsService
        .countPendingJoinRequestsForChatSpaces(chatSpaceIds, PENDING);

      // Map the counts back to the ChatSpaceResponse objects
      final Map<Long, Long> pendingRequestsMap = pendingRequests.stream()
        .filter(Objects::nonNull)
        .collect(Collectors.toMap(ChatSpaceRequestToJoinPendingSelect::getChatSpaceId, ChatSpaceRequestToJoinPendingSelect::getRequestToJoinTotal));

      // Set the total requests to join in each ChatSpaceResponse
      views.stream()
        .filter(Objects::nonNull)
        .forEach(view -> view.setTotalRequestToJoin(pendingRequestsMap.getOrDefault(view.getNumberId(), 0L)));
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
  public Long getTotalRequestToJoinForChatSpace(final Long chatSpaceId) {
    if (nonNull(chatSpaceId)) {
      // Create a list based on the chat space id
      final List<Long> chatSpaceIds = List.of(chatSpaceId);

      // Fetch the pending join request counts for the chat spaces
      final List<ChatSpaceRequestToJoinPendingSelect> pendingRequests = chatSpaceMemberOperationsService
        .countPendingJoinRequestsForChatSpaces(chatSpaceIds, PENDING);

      // Map the counts back to the ChatSpaceResponse objects
      final Map<Long, Long> pendingRequestsMap = pendingRequests.stream()
        .filter(Objects::nonNull)
        .collect(Collectors.toMap(ChatSpaceRequestToJoinPendingSelect::getChatSpaceId, ChatSpaceRequestToJoinPendingSelect::getRequestToJoinTotal));

      return pendingRequestsMap.getOrDefault(chatSpaceId, 0L);
    }
    return 0L;
  }

  /**
   * Processes additional details for the given list of chat space responses, including membership status
   * and organizer determination.
   *
   * <p>This method first checks that the provided list of chat space responses and user are not null.
   * It then retrieves the user's membership status for the chat spaces and processes each response
   * by setting membership details, retrieving recent members, and determining if the user is the organizer.</p>
   *
   * @param chatSpacesResponses the list of chat space responses to process
   * @param user                the user whose membership and organizer status are to be determined
   */
  protected void processOtherChatSpaceDetails(final List<ChatSpaceResponse> chatSpacesResponses, final RegisteredUser user) {
    // Check if chat spaces and user are non-null and user has a member associated
    if (allNonNull(chatSpacesResponses, user, user.toMember()) && !chatSpacesResponses.isEmpty()) {
      // Get the user's membership status map for the chat spaces
      final Map<Long, ChatSpaceMemberSelect> membershipDetailsMap = getUserMembershipDetailsMap(chatSpacesResponses, user);
      // Set likes for chat space where user has no membership
      likeService.populateChatSpaceLikesForNonMembership(chatSpacesResponses, membershipDetailsMap, user.toMember());
      // Set likes for chat space where user has membership
      likeService.populateChatSpaceLikesForMembership(chatSpacesResponses, membershipDetailsMap, user.toMember());
      // Process each non-null chat space response
      chatSpacesResponses.stream()
        .filter(Objects::nonNull)
        .forEach(chatSpaceResponse -> processChatSpaceResponse(chatSpaceResponse, membershipDetailsMap, user));
    }
  }

  /**
   * Retrieves the user's membership status for the given list of chat spaces and returns it as a map.
   *
   * <p>This method extracts the chat space IDs from the provided {@code chatSpacesResponses},
   * fetches the user's membership details for those chat spaces from the repository,
   * and groups the membership status by chat space ID.</p>
   *
   * @param chatSpacesResponses the list of chat space responses to process
   * @param user                the user whose membership status is to be retrieved
   * @return a map where the keys are chat space IDs and the values are the corresponding membership status
   */
  protected Map<Long, ChatSpaceMemberSelect> getUserMembershipDetailsMap(final List<ChatSpaceResponse> chatSpacesResponses, final RegisteredUser user) {
    // Extract chat space IDs from the responses
    final List<Long> chatSpaceIds = extractAndGetEntriesIds(chatSpacesResponses);
    // Convert the user to a domain
    final Member member = user.toMember();
    // Retrieve the user's membership details for the given chat spaces
    final List<ChatSpaceMemberSelect> userMemberships = chatSpaceMemberOperationsService.findByMemberAndChatSpaceIds(member, chatSpaceIds);
    // Group membership statuses by chat space ID
    return groupMembershipByEntriesId(userMemberships);
  }

  /**
   * Processes the given {@code ChatSpaceResponse} by setting membership status, recent members,
   * and determining if the given user is the organizer.
   *
   * <p>This method sets the membership status of the chat space using the provided {@code membershipStatusMap}.
   * It retrieves and assigns some of the most recent approved members to the chat space response.
   * Additionally, it determines whether the given user is the organizer of the chat space.</p>
   *
   * @param chatSpaceResponse    the chat space response object to process
   * @param membershipDetailsMap  a map containing membership status information, keyed by chat space ID
   * @param user                 the user whose organizer status is to be determined
   */
  protected void processChatSpaceResponse(final ChatSpaceResponse chatSpaceResponse, final Map<Long, ChatSpaceMemberSelect> membershipDetailsMap, final RegisteredUser user) {
    // Set user's membership status
    setMembershipDetails(chatSpaceResponse, membershipDetailsMap);
    // Populate recent chat space members
    setSomeRecentChatSpaceMembers(chatSpaceResponse);
    // Set links that are updatable by the user
    setChatSpaceThatAreUpdatableByUser(chatSpaceResponse, membershipDetailsMap);
    // Set the links associated with the chat space
    setLinks(chatSpaceResponse, user);
    // Check if the user is the organizer
    determineIfUserIsTheOrganizerOfEntity(chatSpaceResponse, user.toMember());
  }

  /**
   * Sets the membership status for the given {@code ChatSpaceResponse} using the provided membership status map.
   *
   * <p>This method retrieves the membership details for the chat space from the given {@code membershipStatusMap}
   * using the {@code numberId} of the {@code chatSpaceResponse}. If a matching entry is found, it updates
   * the chat space response with membership-related information.</p>
   *
   * @param chatSpaceResponse    the chat space response object to update with membership details
   * @param membershipDetailsMap  a map containing membership status information, keyed by chat space ID
   */
  protected void setMembershipDetails(final ChatSpaceResponse chatSpaceResponse, final Map<Long, ChatSpaceMemberSelect> membershipDetailsMap) {
    // Retrieve the member details
    final ChatSpaceMemberSelect membershipDetail = membershipDetailsMap.get(chatSpaceResponse.getNumberId());
    // Check if is not null
    Optional.ofNullable(membershipDetail)
      .ifPresent(membership -> unifiedMapper.setMembershipInfo(
        chatSpaceResponse,
        membership.getRequestToJoinStatus(),
        membership.getJoinStatus(),
        membership.getRole(),
        membership.isAMember(),
        membership.isAdmin(),
        membership.hasLeft(),
        membership.isRemoved()
    ));
  }

  /**
   * Sets a subset of recently approved members for the given {@code ChatSpaceResponse}.
   *
   * <p>This method retrieves a limited number of active members from the chat space, converts them into
   * {@code ChatSpaceMemberResponse} objects, and assigns them as a set to the {@code someMembers} field of
   * the provided {@code ChatSpaceResponse}.</p>
   *
   * @param chatSpaceResponse the chat space response object to which the recent members will be assigned
   */
  protected void setSomeRecentChatSpaceMembers(final ChatSpaceResponse chatSpaceResponse) {
    // Parse string ID to Long
    final Long chatSpaceId = chatSpaceResponse.getNumberId();
    // Convert to chat space
    final ChatSpace chatSpace = ChatSpace.of(chatSpaceId);
    // Create a pageable request to fetch a limited number of members
    final Pageable pageable = PageRequest.of(0, DEFAULT_NUMBER_OF_MEMBERS_TO_GET_FOR_CHAT_SPACE);
    // Retrieve active chat space members with approved status
    final Page<ChatSpaceMember> page = chatSpaceMemberOperationsService.findActiveChatSpaceMembers(chatSpace, ChatSpaceRequestToJoinStatus.APPROVED, pageable);
    // Convert members to their response representation
    final List<ChatSpaceMemberResponse> chatSpaceMemberResponses = unifiedMapper.toChatSpaceMemberResponsesPublic(page.getContent());
    // Convert list to a set to ensure uniqueness
    final Set<ChatSpaceMemberResponse> chatSpaceMemberResponsesSet = new HashSet<>(chatSpaceMemberResponses);
    // Set members in response object
    chatSpaceResponse.setSomeMembers(chatSpaceMemberResponsesSet);
  }

  /**
   * Sets the links for the given chat space response based on the user's information.
   *
   * <p>If both the {@code chatSpaceResponse} and {@code user} are non-null, this method retrieves the associated
   * links for the chat space from the {@code linkService} and updates the {@code chatSpaceResponse} with the
   * retrieved links.
   *
   * @param chatSpaceResponse the chat space response object to which the links are being added
   * @param user the user whose context is used to fetch the chat space links
   */
  protected void setLinks(final ChatSpaceResponse chatSpaceResponse, final RegisteredUser user) {
    if (nonNull(chatSpaceResponse) && nonNull(user)) {
      final List<LinkResponse> links = linkService.findChatSpaceLinks(chatSpaceResponse.getNumberId());

      chatSpaceResponse.setLinks(new HashSet<>(links));
    }
  }

  /**
   * Determines if a given chat space response should be marked as updatable based on
   * the user's membership status and role.
   *
   * <p>This method checks whether the user associated with the provided membership status
   * map is an admin of the chat space. If so, it marks the {@code ChatSpaceResponse}
   * as updatable.</p>
   *
   * @param chatSpaceResponse The response object representing the chat space to potentially mark as updatable.
   * @param membershipDetailsMap A map of chat space IDs to corresponding membership status objects.
   */
  protected static void setChatSpaceThatAreUpdatableByUser(final ChatSpaceResponse chatSpaceResponse, final Map<Long, ChatSpaceMemberSelect> membershipDetailsMap) {
    // Retrieve the member details
    final ChatSpaceMemberSelect membershipStatus = membershipDetailsMap.get(chatSpaceResponse.getNumberId());
    // Check if is not null
    if (nonNull(membershipStatus) && membershipStatus.isAdmin()) {
      chatSpaceResponse.markAsUpdatable();
    }
  }

}
