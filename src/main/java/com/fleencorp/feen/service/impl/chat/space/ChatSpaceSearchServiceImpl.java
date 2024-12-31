package com.fleencorp.feen.service.impl.chat.space;

import com.fleencorp.base.service.i18n.LocalizedResponse;
import com.fleencorp.feen.constant.stream.JoinStatus;
import com.fleencorp.feen.exception.chat.space.ChatSpaceNotFoundException;
import com.fleencorp.feen.mapper.chat.ChatSpaceMapper;
import com.fleencorp.feen.mapper.chat.member.ChatSpaceMemberMapper;
import com.fleencorp.feen.mapper.impl.chat.ChatSpaceMapperImpl;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.chat.ChatSpaceMember;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.projection.ChatSpaceMemberSelect;
import com.fleencorp.feen.model.projection.ChatSpaceRequestToJoinPendingSelect;
import com.fleencorp.feen.model.request.search.chat.space.ChatSpaceMemberSearchRequest;
import com.fleencorp.feen.model.request.search.chat.space.ChatSpaceSearchRequest;
import com.fleencorp.feen.model.response.chat.space.RetrieveChatSpaceResponse;
import com.fleencorp.feen.model.response.chat.space.base.ChatSpaceResponse;
import com.fleencorp.feen.model.response.chat.space.member.base.ChatSpaceMemberResponse;
import com.fleencorp.feen.model.search.broadcast.request.EmptyRequestToJoinSearchResult;
import com.fleencorp.feen.model.search.broadcast.request.RequestToJoinSearchResult;
import com.fleencorp.feen.model.search.chat.space.ChatSpaceSearchResult;
import com.fleencorp.feen.model.search.chat.space.EmptyChatSpaceSearchResult;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.chat.ChatSpaceMemberRepository;
import com.fleencorp.feen.repository.chat.ChatSpaceRepository;
import com.fleencorp.feen.repository.chat.UserChatSpaceRepository;
import com.fleencorp.feen.service.chat.space.ChatSpaceSearchService;
import com.fleencorp.feen.service.chat.space.ChatSpaceService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.fleencorp.base.util.FleenUtil.handleSearchResult;
import static com.fleencorp.base.util.FleenUtil.toSearchResult;
import static com.fleencorp.feen.constant.chat.space.ChatSpaceRequestToJoinStatus.PENDING;
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
@Service
public class ChatSpaceSearchServiceImpl implements ChatSpaceSearchService {

  private final ChatSpaceService chatSpaceService;
  private final ChatSpaceMemberRepository chatSpaceMemberRepository;
  private final ChatSpaceRepository chatSpaceRepository;
  private final UserChatSpaceRepository userChatSpaceRepository;
  private final LocalizedResponse localizedResponse;
  private final ChatSpaceMapper chatSpaceMapper;
  private final ChatSpaceMemberMapper chatSpaceMemberMapper;

  /**
   * Constructs a {@code ChatSpaceServiceImpl} with the specified dependencies.
   *
   * <p>This constructor initializes the service with all required components for managing
   * chat spaces, including repositories, mappers, and various utility services. It also injects
   * configuration values like the delegated authority email.</p>
   *
   * @param chatSpaceService for managing chat spaces
   * @param chatSpaceMemberRepository repository for managing chat space members.
   * @param chatSpaceRepository repository for chat space entities.
   * @param userChatSpaceRepository repository for user-chat space associations.
   * @param localizedResponse provides localized responses for API operations.
   * @param chatSpaceMapper maps chat space entities to response models.
   * @param chatSpaceMemberMapper maps chat space member entities to response models.
   */
  public ChatSpaceSearchServiceImpl(
      final ChatSpaceService chatSpaceService,
      final ChatSpaceMemberRepository chatSpaceMemberRepository,
      final ChatSpaceRepository chatSpaceRepository,
      final UserChatSpaceRepository userChatSpaceRepository,
      final LocalizedResponse localizedResponse,
      final ChatSpaceMapper chatSpaceMapper,
      final ChatSpaceMemberMapper chatSpaceMemberMapper) {
    this.chatSpaceService = chatSpaceService;
    this.chatSpaceRepository = chatSpaceRepository;
    this.chatSpaceMemberRepository = chatSpaceMemberRepository;
    this.userChatSpaceRepository = userChatSpaceRepository;
    this.localizedResponse = localizedResponse;
    this.chatSpaceMapper = chatSpaceMapper;
    this.chatSpaceMemberMapper = chatSpaceMemberMapper;
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
  public ChatSpaceSearchResult findSpaces(final ChatSpaceSearchRequest searchRequest, final FleenUser user) {
    final Page<ChatSpace> page;

    // Check if all required date parameters are set in the search request
    if (searchRequest.areAllDatesSet()) {
      // Retrieve chat spaces within the specified date range
      page = chatSpaceRepository.findByDateBetween(searchRequest.getStartDateTime(), searchRequest.getEndDateTime(), searchRequest.getDefaultActive(), searchRequest.getPage());
    } else if (nonNull(searchRequest.getTitle())) {
      // Retrieve chat spaces that match the specified title
      page = chatSpaceRepository.findByTitle(searchRequest.getTitle(), searchRequest.getDefaultActive(), searchRequest.getPage());
    } else {
      // Retrieve all chat spaces that match the default active status
      page = chatSpaceRepository.findMany(searchRequest.getDefaultActive(), searchRequest.getPage());
    }

    // Convert the retrieved chat spaces to response objects
    final List<ChatSpaceResponse> views = chatSpaceMapper.toChatSpaceResponses(page.getContent());
    // Determine user join status of spaces
    determineUserJoinStatusFoChatSpace(views, user);
    // Return a search result view with the chat space responses and pagination details
    return handleSearchResult(
      page,
      localizedResponse.of(ChatSpaceSearchResult.of(toSearchResult(views, page))),
      localizedResponse.of(EmptyChatSpaceSearchResult.of(toSearchResult(List.of(), page)))
    );
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
  public ChatSpaceSearchResult findSpacesCreated(final ChatSpaceSearchRequest searchRequest, final FleenUser user) {
    final Page<ChatSpace> page;

    // Check if all required date parameters are set in the search request
    if (searchRequest.areAllDatesSet()) {
      // Retrieve chat spaces created by the user within the specified date range
      page = userChatSpaceRepository.findByDateBetween(searchRequest.getStartDateTime(), searchRequest.getEndDateTime(), user.toMember(), searchRequest.getPage());
    } else if (nonNull(searchRequest.getTitle())) {
      // Retrieve chat spaces created by the user that match the specified title
      page = userChatSpaceRepository.findByTitle(searchRequest.getTitle(), user.toMember(), searchRequest.getPage());
    } else {
      // Retrieve all chat spaces created by the user
      page = userChatSpaceRepository.findMany(user.toMember(), searchRequest.getPage());
    }

    // Convert the retrieved chat spaces to response objects
    final List<ChatSpaceResponse> views = chatSpaceMapper.toChatSpaceResponses(page.getContent());
    // Update the total request to join for each chat space
    updateTotalRequestToJoinForChatSpaces(views);
    // Determine user join status of spaces
    determineUserJoinStatusFoChatSpace(views, user);
    // Return a search result view with the chat space responses and pagination details
    return handleSearchResult(
      page,
      localizedResponse.of(ChatSpaceSearchResult.of(toSearchResult(views, page))),
      localizedResponse.of(EmptyChatSpaceSearchResult.of(toSearchResult(List.of(), page)))
    );
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
  public ChatSpaceSearchResult findSpacesIBelongTo(final ChatSpaceSearchRequest searchRequest, final FleenUser user) {
    final Page<ChatSpaceMember> page;

    // Check if all required date parameters are set in the search request
    if (searchRequest.areAllDatesSet()) {
      // Retrieve chat spaces the user belongs to within the specified date range
      page = chatSpaceMemberRepository.findSpaceIBelongByDateBetween(searchRequest.getStartDateTime(), searchRequest.getEndDateTime(), user.toMember(), searchRequest.getPage());
    } else if (nonNull(searchRequest.getTitle())) {
      // Retrieve chat spaces the user belongs to that match the specified title
      page = chatSpaceMemberRepository.findSpaceIBelongByTitle(searchRequest.getTitle(), user.toMember(), searchRequest.getPage());
    } else {
      // Retrieve all chat spaces the user belongs to
      page = chatSpaceMemberRepository.findSpaceIBelongMany(user.toMember(), searchRequest.getPage());
    }

    // Convert the retrieved chat spaces from membership to response objects
    final List<ChatSpaceResponse> views = extractUserChatSpaceFromMembershipAndCreateChatResponse(page.getContent());
    // Determine user join status of spaces
    determineUserJoinStatusFoChatSpace(views, user);
    // Return a search result view with the chat space responses and pagination details
    return handleSearchResult(
      page,
      localizedResponse.of(ChatSpaceSearchResult.of(toSearchResult(views, page))),
      localizedResponse.of(EmptyChatSpaceSearchResult.of(toSearchResult(List.of(), page)))
    );
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
  public RetrieveChatSpaceResponse retrieveChatSpace(final Long chatSpaceId, final FleenUser user) {
    // Find the chat space by its ID or throw an exception if not found
    final ChatSpace chatSpace = chatSpaceService.findChatSpace(chatSpaceId);
    // Get the equivalent chat space response
    final ChatSpaceResponse chatSpaceResponse = chatSpaceMapper.toChatSpaceResponse(chatSpace);
    // Update join status of user in the chat space response
    updateUserJoinStatus(chatSpaceResponse, user);
    // Return a localized response containing the chat space details
    return localizedResponse.of(RetrieveChatSpaceResponse.of(chatSpaceResponse));
  }

  /**
   * Updates the join status of a user in a chat space based on the chat space response and user details.
   *
   * <p>This method retrieves the chat space member associated with the provided {@link FleenUser} and {@link ChatSpaceResponse}.
   * It then updates the user's join status based on the request-to-join status and the visibility information of the chat space.
   * If the user is found in the chat space, their join status is updated accordingly.</p>
   *
   * @param chatSpaceResponse the {@link ChatSpaceResponse} containing chat space details, including visibility information
   * @param user the {@link FleenUser} whose join status needs to be updated
   */
  public void updateUserJoinStatus(final ChatSpaceResponse chatSpaceResponse, final FleenUser user) {
    // Check if the provided chat space response and user are not null
    if (nonNull(chatSpaceResponse) && nonNull(user)) {
      // Retrieve the chat space member for the given user and chat space number ID
      chatSpaceMemberRepository
        .findByChatSpaceMemberAndChatSpace(ChatSpaceMember.of(user.getId()), ChatSpace.of(chatSpaceResponse.getNumberId()))
        .ifPresent(chatSpaceMember -> {
          // Get the join status based on the member's request-to-join status and chat space visibility
          final JoinStatus joinStatus = JoinStatus.getJoinStatus(chatSpaceMember.getRequestToJoinStatus(), chatSpaceResponse.getVisibility());
          // Update the chat space response with the new join status
          chatSpaceMapper.update(chatSpaceResponse, chatSpaceMember.getRequestToJoinStatus(), joinStatus);
        });
    }
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
        // Extract the chat space from each chat space member
        .map(ChatSpaceMember::getChatSpace)
        // Convert each chat space to a ChatSpaceResponse
        .map(chatSpaceMapper::toChatSpaceResponse)
        // Collect the responses into a list
        .collect(Collectors.toList());
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
  public RequestToJoinSearchResult findRequestToJoinSpace(final Long chatSpaceId, final ChatSpaceMemberSearchRequest searchRequest, final FleenUser user) {
    // Retrieve the chat space by its ID
    final ChatSpace chatSpace = chatSpaceService.findChatSpace(chatSpaceId);
    final Page<ChatSpaceMember> page;

    // Check if a member name is provided in the search request
    if (nonNull(searchRequest.getMemberName())) {
      // Fetch members with the specified name and pending join request status
      page = chatSpaceMemberRepository.findByChatSpaceAndMemberNameRequestToJoinStatus(chatSpace, searchRequest.getMemberName(), PENDING, searchRequest.getPage());
    } else {
      // Fetch all members with a pending join request status
      page = chatSpaceMemberRepository.findByChatSpaceAndRequestToJoinStatus(chatSpace, PENDING, searchRequest.getPage());
    }

    // Convert the chat space members to response objects
    final List<ChatSpaceMemberResponse> views = chatSpaceMemberMapper.toChatSpaceMemberResponses(page.getContent(), chatSpace);
    // Return a search result view with the request to join responses and pagination details
    return handleSearchResult(
      page,
      localizedResponse.of(RequestToJoinSearchResult.of(toSearchResult(views, page))),
      localizedResponse.of(EmptyRequestToJoinSearchResult.of(toSearchResult(List.of(), page)))
    );
  }

  /**
   * Determines the user's join status for each chat space in the provided list of {@link ChatSpaceResponse} objects.
   *
   * <p>If both the user and the responses are valid, the method retrieves the chat space IDs from the responses,
   * fetches the user's attendance or membership information from the {@link ChatSpaceMemberRepository},
   * and then updates the join status of each response based on the user's membership status.</p>
   *
   * <p>This ensures that each {@link ChatSpaceResponse} reflects the user's current membership or attendance status
   * for the corresponding chat spaces.</p>
   *
   * @param responses the list of {@link ChatSpaceResponse} objects to update with join status
   * @param user the {@link FleenUser} whose membership status will be checked and applied to the responses
   */
  protected void determineUserJoinStatusFoChatSpace(final List<ChatSpaceResponse> responses, final FleenUser user) {
    // Check if both user and responses are valid before proceeding
    if (isUserAndResponsesValid(responses, user)) {
      // Extract chat space IDs from the responses
      final List<Long> eventIds = extractAndGetChatSpaceIds(responses);
      // Retrieve the user's membership or attendance status for the chat spaces
      final List<ChatSpaceMemberSelect> userMemberships = chatSpaceMemberRepository.findByMemberAndEventOrStreamIds(user.toMember(), eventIds);
      // Group the user's membership statuses by chat space ID
      final Map<Long, ChatSpaceMemberSelect> membershipMap = groupMemberStatusByChatSpaceId(userMemberships);
      // Update the join status of the responses based on the membership status map
      updateJoinStatusInResponses(responses, membershipMap);
    }
  }

  /**
   * Checks if both the user and the collection of {@link ChatSpaceResponse} objects are valid.
   *
   * <p>This method ensures that the {@link FleenUser} object is not null, the user has been converted
   * to a valid {@link Member}, and the provided collection of responses is not null.</p>
   *
   * <p>If any of these checks fail, the method returns false, otherwise, it returns true.</p>
   *
   * @param responses the collection of {@link ChatSpaceResponse} objects to validate
   * @param user the {@link FleenUser} to validate
   * @return true if both the user and responses are valid; false otherwise
   */
  protected boolean isUserAndResponsesValid(final Collection<ChatSpaceResponse> responses, final FleenUser user) {
    // Ensure that the user, user's member, and responses are all non-null
    return nonNull(user) && nonNull(user.toMember()) && nonNull(responses);
  }

  /**
   * Extracts and returns the list of chat space IDs from the provided list of {@link ChatSpaceResponse}.
   *
   * <p>This method processes a list of {@link ChatSpaceResponse} objects, filtering out null entries
   * and mapping each response to its corresponding chat space ID, which is then collected into a list.</p>
   *
   * <p>If the input list is null, an empty list is returned.</p>
   *
   * @param responses the list of {@link ChatSpaceResponse} objects to extract chat space IDs from
   * @return a list of chat space IDs or an empty list if the input is null
   */
  protected static List<Long> extractAndGetChatSpaceIds(final List<ChatSpaceResponse> responses) {
    if (nonNull(responses)) {
      return responses.stream()
        // Filter out null responses
        .filter(Objects::nonNull)
        // Map each response to its chat space ID
        .map(ChatSpaceResponse::getNumberId)
        // Collect the IDs into a list
        .toList();
    }
    // Return an empty list if the input is null
    return List.of();
  }

  /**
   * Groups membership statuses by chat space ID from the provided list of {@link ChatSpaceMemberSelect}.
   *
   * <p>This method processes a list of {@link ChatSpaceMemberSelect} objects, filtering out null entries
   * and collecting the results into a map. The keys of the map are chat space IDs, and the values are
   * the corresponding membership statuses.</p>
   *
   * <p>If the input list is null or empty, an empty map is returned.</p>
   *
   * @param userMembership the list of user memberships to be processed
   * @return a map of chat space IDs to membership statuses
   */
  protected static Map<Long, ChatSpaceMemberSelect> groupMemberStatusByChatSpaceId(final List<ChatSpaceMemberSelect> userMembership) {
    if (nonNull(userMembership) && !userMembership.isEmpty()) {
      return userMembership.stream()
        // Filter out any null entries in the list
        .filter(Objects::nonNull)
        // Collect results into a map with chat space ID as key and membership status as value
        .collect(Collectors.toMap(ChatSpaceMemberSelect::getChatSpaceId, Function.identity()));
    }
    // Return an empty map if the input list is null or empty
    return Map.of();
  }

  /**
   * Updates the join status of each {@link ChatSpaceResponse} in the provided list based on the
   * corresponding membership status from the provided map.
   *
   * <p>If a response's ID exists in the membership status map, its join status is updated accordingly.</p>
   *
   * <p>This method iterates over the responses, checks the presence of each response's ID in the
   * membership status map, and sets the join status for responses with a matching entry.</p>
   *
   * @param responses the list of {@link ChatSpaceResponse} to be updated
   * @param membershipStatusMap a map of membership statuses keyed by the chat space ID
   */
  protected void updateJoinStatusInResponses(final List<ChatSpaceResponse> responses, final Map<Long, ChatSpaceMemberSelect> membershipStatusMap) {
    if (nonNull(responses) && nonNull(membershipStatusMap)) {
      responses.stream()
        .filter(Objects::nonNull)
        .forEach(chatSpace -> {
          // Retrieve the membership status for the current chat space response
          final Optional<ChatSpaceMemberSelect> existingMembership = Optional.ofNullable(membershipStatusMap.get(chatSpace.getNumberId()));
          // If a membership status exists, set the join status on the response
          existingMembership.ifPresent(membership -> chatSpaceMapper.update(chatSpace, membership.getRequestToJoinStatus(), membership.getJoinStatus()));
      });
    }
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
  private void updateTotalRequestToJoinForChatSpaces(final Collection<ChatSpaceResponse> views) {
    if (nonNull(views) && (!views.isEmpty())) {
      // Get a list of chatSpaceIds to retrieve join request counts
      final List<Long> chatSpaceIds = views.stream()
        .filter(Objects::nonNull)
        .map(ChatSpaceResponse::getNumberId)
        .toList();

      // Fetch the pending join request counts for the chat spaces
      final List<ChatSpaceRequestToJoinPendingSelect> pendingRequests = chatSpaceMemberRepository
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

}
