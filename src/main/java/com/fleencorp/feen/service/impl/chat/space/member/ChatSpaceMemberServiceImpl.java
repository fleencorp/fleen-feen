package com.fleencorp.feen.service.impl.chat.space.member;

import com.fleencorp.feen.constant.chat.space.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.chat.space.ChatSpaceNotFoundException;
import com.fleencorp.feen.exception.chat.space.core.NotAnAdminOfChatSpaceException;
import com.fleencorp.feen.exception.chat.space.member.ChatSpaceMemberNotFoundException;
import com.fleencorp.feen.user.exception.MemberNotFoundException;
import com.fleencorp.feen.mapper.common.UnifiedMapper;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.chat.ChatSpaceMember;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.model.dto.chat.member.AddChatSpaceMemberDto;
import com.fleencorp.feen.model.dto.chat.member.RemoveChatSpaceMemberDto;
import com.fleencorp.feen.model.dto.chat.member.RestoreChatSpaceMemberDto;
import com.fleencorp.feen.model.dto.chat.role.DowngradeChatSpaceAdminToMemberDto;
import com.fleencorp.feen.model.dto.chat.role.UpgradeChatSpaceMemberToAdminDto;
import com.fleencorp.feen.model.holder.ChatSpaceAndMemberDetailsHolder;
import com.fleencorp.feen.model.info.chat.space.membership.ChatSpaceMembershipInfo;
import com.fleencorp.feen.model.request.chat.space.membership.AddChatSpaceMemberRequest;
import com.fleencorp.feen.model.request.chat.space.membership.RemoveChatSpaceMemberRequest;
import com.fleencorp.feen.model.request.search.chat.space.ChatSpaceMemberSearchRequest;
import com.fleencorp.feen.model.response.chat.space.member.*;
import com.fleencorp.feen.model.response.chat.space.member.base.ChatSpaceMemberResponse;
import com.fleencorp.feen.model.search.chat.space.member.ChatSpaceMemberSearchResult;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import com.fleencorp.feen.repository.chat.space.ChatSpaceRepository;
import com.fleencorp.feen.service.chat.space.ChatSpaceService;
import com.fleencorp.feen.service.chat.space.member.ChatSpaceMemberOperationsService;
import com.fleencorp.feen.service.chat.space.member.ChatSpaceMemberService;
import com.fleencorp.feen.service.chat.space.update.ChatSpaceUpdateService;
import com.fleencorp.feen.user.service.MemberService;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.fleencorp.base.util.ExceptionUtil.checkIsNullAny;
import static com.fleencorp.base.util.FleenUtil.toSearchResult;
import static com.fleencorp.feen.service.impl.common.MiscServiceImpl.determineIfUserIsTheOrganizerOfEntity;
import static java.util.Objects.isNull;
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
public class ChatSpaceMemberServiceImpl implements ChatSpaceMemberService {

  private final ChatSpaceMemberOperationsService chatSpaceMemberOperationsService;
  private final ChatSpaceService chatSpaceService;
  private final ChatSpaceUpdateService chatSpaceUpdateService;
  private final MemberService memberService;
  private final ChatSpaceRepository chatSpaceRepository;
  private final UnifiedMapper unifiedMapper;
  private final Localizer localizer;

  /**
   * Constructs a new {@code ChatSpaceMemberServiceImpl}, which handles operations related to members within a chat space.
   *
   * @param chatSpaceService the service for managing chat space lifecycles and operations
   * @param chatSpaceUpdateService the service responsible for updating chat space details
   * @param chatSpaceMemberOperationsService the service for member-specific operations within a chat space
   * @param memberService the service for general member-related functionality
   * @param chatSpaceRepository the repository for accessing and persisting chat space data
   * @param unifiedMapper the utility for mapping between entities and DTOs
   * @param localizer the component for retrieving localized messages
   */
  public ChatSpaceMemberServiceImpl(
      final ChatSpaceService chatSpaceService,
      final ChatSpaceUpdateService chatSpaceUpdateService,
      final ChatSpaceMemberOperationsService chatSpaceMemberOperationsService,
      final MemberService memberService,
      final ChatSpaceRepository chatSpaceRepository,
      final UnifiedMapper unifiedMapper,
      final Localizer localizer) {
    this.chatSpaceMemberOperationsService = chatSpaceMemberOperationsService;
    this.chatSpaceService = chatSpaceService;
    this.chatSpaceUpdateService = chatSpaceUpdateService;
    this.memberService = memberService;
    this.chatSpaceRepository = chatSpaceRepository;
    this.unifiedMapper = unifiedMapper;
    this.localizer = localizer;
  }

  /**
   * Finds and returns a paginated list of chat space members based on the provided search request.
   *
   * <p>If a member's name is included in the search request, it filters the results by that name;
   * otherwise, it returns all members of the specified chat space.</p>
   *
   * @param chatSpaceId the ID of the chat space
   * @param searchRequest the search criteria for filtering members (includes pagination details)
   * @param user the current user performing the search
   * @return a ChatSpaceMemberSearchResult containing the list of chat space members and pagination info
   */
  @Override
  public ChatSpaceMemberSearchResult findChatSpaceMembers(final Long chatSpaceId, final ChatSpaceMemberSearchRequest searchRequest, final RegisteredUser user) {
    // Retrieve the chat space
    final ChatSpace chatSpace = chatSpaceService.findChatSpace(chatSpaceId);
    final Page<ChatSpaceMember> page;
    final String memberName = searchRequest.getMemberName();
    final Pageable pageable = searchRequest.getPage();

    // Check if the search request includes a member's name
    if (nonNull(memberName)) {
      // Find members by chat space and member name with pagination
      page = chatSpaceMemberOperationsService.findByChatSpaceAndMemberName(chatSpace, memberName, pageable);
    } else {
      // Find all members by chat space with pagination
      page = chatSpaceMemberOperationsService.findByChatSpace(chatSpace, pageable);
    }

    // Convert the chat space members to response views
    final List<ChatSpaceMemberResponse> chatSpaceMemberResponses = unifiedMapper.toChatSpaceMemberResponses(page.getContent(), chatSpace);
    // Determine if the possible authenticated user is the organizer of the entity
    determineIfUserIsTheOrganizerOfEntity(chatSpaceMemberResponses, user.toMember());
    // Set chat space members that are updatable by the user
    setChatSpaceMembersThatAreUpdatableByUser(chatSpace, chatSpaceMemberResponses, user.toMember());
    // Create the search result
    final ChatSpaceMemberSearchResult chatSpaceMemberSearchResult = ChatSpaceMemberSearchResult.of(toSearchResult(chatSpaceMemberResponses, page));
    // Return a search result with the responses and pagination details
    return localizer.of(chatSpaceMemberSearchResult);
  }

  /**
   * Finds administrator members of a chat space, optionally filtered by member name.
   *
   * <p>If a member name is provided in the search request, this method filters the results by name.
   * Otherwise, it returns all admins of the chat space with pagination.</p>
   *
   * @param chatSpaceId the ID of the chat space to search in
   * @param searchRequest the search request containing filters and pagination
   * @param user the currently authenticated user
   * @return a localized search result containing the chat space member responses
   */
  @Override
  public ChatSpaceMemberSearchResult findChatSpaceAdmins(final Long chatSpaceId, final ChatSpaceMemberSearchRequest searchRequest, final RegisteredUser user) {
    // Retrieve the chat space
    final ChatSpace chatSpace = chatSpaceService.findChatSpace(chatSpaceId);
    final Page<ChatSpaceMember> page;
    final Pageable pageable = searchRequest.getPage();
    final String memberName = searchRequest.getMemberName();

    // Check if the search request includes a member's name
    if (nonNull(memberName)) {
      // Find members by chat space and member name with pagination
      page = chatSpaceMemberOperationsService.findAdminByChatSpaceAndMemberName(chatSpace, memberName, pageable);
    } else {
      // Find all members by chat space with pagination
      page = chatSpaceMemberOperationsService.findAdminByChatSpace(chatSpace, pageable);
    }

    // Convert the chat space members to response views
    final List<ChatSpaceMemberResponse> chatSpaceMemberResponses = unifiedMapper.toChatSpaceMemberResponses(page.getContent(), chatSpace);
    // Determine if the possible authenticated user is the organizer of the entity
    determineIfUserIsTheOrganizerOfEntity(chatSpaceMemberResponses, user.toMember());
    // Create the search result
    final ChatSpaceMemberSearchResult chatSpaceMemberSearchResult = ChatSpaceMemberSearchResult.of(toSearchResult(chatSpaceMemberResponses, page));
    // Return a search result with the responses and pagination details
    return localizer.of(chatSpaceMemberSearchResult);
  }

  /**
   * Upgrades a chat space member to an admin role within the specified chat space.
   *
   * <p>This method retrieves the chat space by its ID and locates the member to be upgraded
   * based on the provided member ID. If the chat space or member is not found, it throws a
   * {@link ChatSpaceNotFoundException} or {@link ChatSpaceMemberNotFoundException}. If found,
   * the member's role is upgraded to admin, and the updated member information is saved to the
   * repository. The response contains details of the upgrade operation.</p>
   *
   * @param chatSpaceId The ID of the chat space where the member is to be upgraded.
   * @param upgradeChatSpaceMemberToAdminDto The DTO containing the member ID to be upgraded.
   * @param user The user requesting the upgrade operation.
   * @return A response confirming the upgrade of the chat space member to admin, localized based on the user's locale.
   * @throws ChatSpaceNotFoundException if the chat space with the specified ID is not found.
   * @throws ChatSpaceMemberNotFoundException if the chat space member with the specified ID is not found in the chat space.
   * @throws NotAnAdminOfChatSpaceException if the operation is not performed by an admin
   * @throws FailedOperationException if there is an invalid input
   */
  @Override
  @Transactional
  public UpgradeChatSpaceMemberToAdminResponse upgradeChatSpaceMemberToAdmin(final Long chatSpaceId, final UpgradeChatSpaceMemberToAdminDto upgradeChatSpaceMemberToAdminDto, final RegisteredUser user)
    throws ChatSpaceNotFoundException, ChatSpaceMemberNotFoundException, NotAnAdminOfChatSpaceException,
      FailedOperationException {
    // Get the chat space member id
    final Long chatSpaceMemberId = upgradeChatSpaceMemberToAdminDto.getChatSpaceMemberId();
    // Get the chat space and chat space member details
    final ChatSpaceAndMemberDetailsHolder chatSpaceAndMemberDetailsHolder = checkIfRoleCanBeUpdated(chatSpaceId, chatSpaceMemberId, user);
    // Get the chat space
    final ChatSpace chatSpace = chatSpaceAndMemberDetailsHolder.chatSpace();
    // Get the chat space member
    final ChatSpaceMember chatSpaceMember = chatSpaceAndMemberDetailsHolder.member();
    // Upgrade the member's role to admin
    chatSpaceMember.upgradeRole();
    // Save the updated chat space member information to the repository
    chatSpaceMemberOperationsService.save(chatSpaceMember);
    // Get the membership info
    final ChatSpaceMembershipInfo chatSpaceMembershipInfo = unifiedMapper.getMembershipInfo(chatSpaceMember, chatSpace);
    // Create the response
    final UpgradeChatSpaceMemberToAdminResponse upgradeChatSpaceMemberToAdminResponse = UpgradeChatSpaceMemberToAdminResponse.of(chatSpaceId, chatSpaceMemberId, chatSpaceMembershipInfo);
    // Return a localized response confirming the upgrade
    return localizer.of(upgradeChatSpaceMemberToAdminResponse);
  }

  /**
   * Downgrades a chat space admin to a member role within the specified chat space.
   *
   * <p>This method retrieves the chat space by its ID and locates the admin to be downgraded
   * based on the provided member ID. If the chat space or admin is not found, it throws a
   * {@link ChatSpaceNotFoundException} or {@link ChatSpaceMemberNotFoundException}. If found,
   * the admin role is downgraded to a member, and the updated member information is saved to the
   * repository. The response contains details of the downgrade operation.</p>
   *
   * @param chatSpaceId The ID of the chat space where the admin is to be downgraded.
   * @param downgradeChatSpaceAdminToMemberDto The DTO containing the admin ID to be downgraded.
   * @param user The user requesting the downgrade operation.
   * @return A response confirming the downgrade of the chat space admin to a member, localized based on the user's locale.
   * @throws ChatSpaceNotFoundException if the chat space with the specified ID is not found.
   * @throws ChatSpaceMemberNotFoundException if the chat space member with the specified ID is not found in the chat space.
   * @throws NotAnAdminOfChatSpaceException if the operation is not performed by an admin
   * @throws FailedOperationException if there is an invalid input
   */
  @Override
  @Transactional
  public DowngradeChatSpaceAdminToMemberResponse downgradeChatSpaceAdminToMember(final Long chatSpaceId, final DowngradeChatSpaceAdminToMemberDto downgradeChatSpaceAdminToMemberDto, final RegisteredUser user)
    throws ChatSpaceNotFoundException, ChatSpaceMemberNotFoundException, NotAnAdminOfChatSpaceException,
      FailedOperationException  {
    // Get the chat space member id
    final Long chatSpaceMemberId = downgradeChatSpaceAdminToMemberDto.getChatSpaceMemberId();
    // Get the chat space and chat space member details
    final ChatSpaceAndMemberDetailsHolder chatSpaceAndMemberDetailsHolder = checkIfRoleCanBeUpdated(chatSpaceId, chatSpaceMemberId, user);
    // Get the chat space
    final ChatSpace chatSpace = chatSpaceAndMemberDetailsHolder.chatSpace();
    // Get the chat space member
    final ChatSpaceMember chatSpaceMember = chatSpaceAndMemberDetailsHolder.member();
    // Downgrade the admin role to a member
    chatSpaceMember.downgradeRole();
    // Save the updated chat space member information to the repository
    chatSpaceMemberOperationsService.save(chatSpaceMember);
    // Get the membership info
    final ChatSpaceMembershipInfo chatSpaceMembershipInfo = unifiedMapper.getMembershipInfo(chatSpaceMember, chatSpace);
    // Create the response
    final DowngradeChatSpaceAdminToMemberResponse downgradeChatSpaceAdminToMemberResponse = DowngradeChatSpaceAdminToMemberResponse.of(chatSpaceId, chatSpaceMemberId, chatSpaceMembershipInfo);
    // Return a localized response confirming the downgrade
    return localizer.of(downgradeChatSpaceAdminToMemberResponse);
  }

  /**
   * Checks if a user's role in a chat space can be updated, ensuring certain restrictions are met.
   *
   * <p>This method performs checks to ensure that the admin role cannot be upgraded or downgraded, and
   * a user cannot upgrade or downgrade their own role. It retrieves the chat space and member details and
   * returns the details if the role update is allowed.
   *
   * @param chatSpaceId the ID of the chat space
   * @param chatSpaceMemberId the ID of the chat space member
   * @param user the user attempting to update the role
   * @return a {@code ChatSpaceAndMemberDetailsHolder} containing the chat space and member details
   * @throws ChatSpaceNotFoundException if the chat space cannot be found
   * @throws ChatSpaceMemberNotFoundException if the chat space member cannot be found
   */
  protected ChatSpaceAndMemberDetailsHolder checkIfRoleCanBeUpdated(final Long chatSpaceId, final Long chatSpaceMemberId, final RegisteredUser user) {
    // Get the chat space and chat space member details
    final ChatSpaceAndMemberDetailsHolder chatSpaceAndMemberDetailsHolder = getChatSpaceAndMemberDetails(chatSpaceId, chatSpaceMemberId, user);
    // Get the chat space
    final ChatSpace chatSpace = chatSpaceAndMemberDetailsHolder.chatSpace();
    // Get the chat space member
    final ChatSpaceMember chatSpaceMember = chatSpaceAndMemberDetailsHolder.member();
    // Admin role cannot be upgraded or downgraded
    checkOrganizerCannotBeUpgradedOrDowngraded(chatSpace, chatSpaceMemberId);
    // Cannot upgrade or downgrade self
    checkAdminCannotUpgradeOrDowngradeSelf(chatSpaceMember, user.getId());

    return chatSpaceAndMemberDetailsHolder;
  }

  /**
   * Retrieves the chat space and member details for the specified chat space ID and member ID.
   *
   * <p>This method first locates the chat space by its ID. It ensures that an admin cannot
   * add or update their own membership in the chat space. Additionally, it verifies whether
   * the provided user is either the creator or an admin of the chat space.
   * After that, the method attempts to find the chat space member by their ID, associated
   * with the given chat space. If the member is not found, an exception is thrown.</p>
   *
   * @param chatSpaceId the ID of the chat space to retrieve
   * @param chatSpaceMemberId the ID of the chat space member to retrieve
   * @param user the user attempting the operation
   * @return a {@link ChatSpaceAndMemberDetailsHolder} containing both the chat space and member details
   * @throws ChatSpaceNotFoundException if the chat space is not found
   * @throws NotAnAdminOfChatSpaceException if the operation is not performed by an admin
   * @throws ChatSpaceMemberNotFoundException if the chat space member with the specified ID is not found in the chat space.
   */
  private ChatSpaceAndMemberDetailsHolder getChatSpaceAndMemberDetails(final Long chatSpaceId, final Long chatSpaceMemberId, final RegisteredUser user) {
    // Find the chat space by its ID or throw an exception if not found
    final ChatSpace chatSpace = chatSpaceService.findChatSpace(chatSpaceId);
    // Verify that the user is the creator or an admin of the chat space
    chatSpaceService.verifyCreatorOrAdminOfChatSpace(chatSpace, user.toMember());

    // Find the chat space admin to be downgraded or throw an exception if not found
    final ChatSpaceMember chatSpaceMember = chatSpaceMemberOperationsService.findByChatSpaceMemberAndChatSpace(ChatSpaceMember.of(chatSpaceMemberId), chatSpace)
      .orElseThrow(ChatSpaceMemberNotFoundException.of(chatSpaceMemberId));

    return ChatSpaceAndMemberDetailsHolder.of(chatSpace, chatSpaceMember);
  }

  /**
   * Adds a member to a chat space.
   *
   * <p>This method retrieves the chat space based on the provided {@code chatSpaceId}, validates the user's
   * permissions to add members, and finds or creates a chat space member object. The member is then
   * updated with an optional admin comment, and the chat space update service is notified of the change.</p>
   *
   * @param chatSpaceId The ID of the chat space.
   * @param addChatSpaceMemberDto The data transfer object containing member details.
   * @param user The user performing the action.
   * @return A response indicating the result of the operation.
   * @throws ChatSpaceNotFoundException if the chat space with the specified ID is not found.
   * @throws MemberNotFoundException if the member can't be found
   * @throws NotAnAdminOfChatSpaceException if the operation is not performed by an admin
   * @throws FailedOperationException if there is an invalid input
   */
  @Override
  @Transactional
  public AddChatSpaceMemberResponse addMember(final Long chatSpaceId, final AddChatSpaceMemberDto addChatSpaceMemberDto, final RegisteredUser user)
    throws ChatSpaceNotFoundException, MemberNotFoundException, NotAnAdminOfChatSpaceException,
      FailedOperationException  {
    // Get the member id
    final Long memberId = addChatSpaceMemberDto.getMemberId();
    // Find the chat space by its ID
    final ChatSpace chatSpace = chatSpaceService.findChatSpace(chatSpaceId);
    // Organizer cannot add self
    chatSpace.checkIsNotOrganizer(user.getId());
    // Validate if the user is the creator or an admin of the chat space
    chatSpaceService.verifyCreatorOrAdminOfChatSpace(chatSpace, user.toMember());
    // Find the member to be added using the provided member ID
    final Member member = memberService.findMember(memberId);
    // Find or create the chat space member object
    final ChatSpaceMember chatSpaceMember = findOrCreateChatMember(chatSpace, member);
    // Approve chat space member join status since the request is been made by the admin
    approveChatMemberJoinStatusAndSaveIfNew(chatSpaceMember);
    // Set an admin comment for the chat space member
    chatSpaceMember.setSpaceAdminComment(addChatSpaceMemberDto.getComment());
    // Notify the chat space update service about the change
    addMemberToChatSpaceExternally(chatSpaceMember, chatSpace, member);
    // Create the response
    final AddChatSpaceMemberResponse addChatSpaceMemberResponse = AddChatSpaceMemberResponse.of(chatSpaceId, memberId);
    // Return a localized response indicating success
    return localizer.of(addChatSpaceMemberResponse);
  }

  /**
   * Restores a previously removed member from a chat space.
   *
   * <p>This method performs several validation steps:
   * it ensures the chat space exists, verifies that the requesting user
   * is either the creator or an admin of the chat space, confirms the target member
   * exists and is not the organizer, and then proceeds to restore the member.</p>
   *
   * <p>If successful, it returns a localized response indicating that the member
   * was restored. Otherwise, appropriate exceptions are thrown.</p>
   *
   * @param chatSpaceId the ID of the chat space
   * @param restoreChatSpaceMemberDto the DTO containing the ID of the chat space member to restore
   * @param user the user initiating the restoration
   * @return a localized {@link RestoreChatSpaceMemberResponse} indicating success
   *
   * @throws ChatSpaceNotFoundException if the chat space with the given ID does not exist
   * @throws ChatSpaceMemberNotFoundException if the chat space member is not found
   * @throws NotAnAdminOfChatSpaceException if the user is not authorized to perform the action
   * @throws FailedOperationException if the restoration operation fails
   */
  @Override
  @Transactional
  public RestoreChatSpaceMemberResponse restoreRemovedMember(final Long chatSpaceId, final RestoreChatSpaceMemberDto restoreChatSpaceMemberDto, final RegisteredUser user)
    throws ChatSpaceNotFoundException, ChatSpaceMemberNotFoundException, NotAnAdminOfChatSpaceException,
      FailedOperationException  {
    // Find the chat space by its ID
    final ChatSpace chatSpace = chatSpaceService.findChatSpace(chatSpaceId);
    // Validate if the user is the creator or an admin of the chat space
    chatSpaceService.verifyCreatorOrAdminOfChatSpace(chatSpace, user.toMember());
    // Get the member id
    final Long chatSpaceMemberId = restoreChatSpaceMemberDto.getChatSpaceMemberId();
    // Find the chat space member
    final ChatSpaceMember chatSpaceMember = findByChatSpaceAndChatSpaceMemberId(chatSpace, chatSpaceMemberId);
    // Organizer cannot restore self
    chatSpace.checkIsNotOrganizer(chatSpaceMember.getMemberId());
    // Handle the restore of the chat space member
    handleRestoreMember(chatSpaceMember);
    // Create the response
    final RestoreChatSpaceMemberResponse restoreChatSpaceMemberResponse = RestoreChatSpaceMemberResponse.of(chatSpaceId, chatSpaceMemberId);
    // Return a localized response indicating success
    return localizer.of(restoreChatSpaceMemberResponse);
  }

  /**
   * Handles the restoration of a previously removed {@link ChatSpaceMember}.
   *
   * <p>If the member is not {@code null} and is not marked as removed,
   * their join status is approved and the member is saved to the repository.</p>
   *
   * <p>If the member is {@code null} or marked as removed, the method throws
   * a {@link FailedOperationException}.</p>
   *
   * @param chatSpaceMember the chat space member to restore
   * @throws FailedOperationException if the operation cannot be completed
   */
  protected void handleRestoreMember(final ChatSpaceMember chatSpaceMember) {
    if (nonNull(chatSpaceMember) && chatSpaceMember.isRemoved()) {
      // Approve the request
      chatSpaceMember.approveJoinStatus();
      // Save the member
      chatSpaceMemberOperationsService.save(chatSpaceMember);
      return;
    }

    throw FailedOperationException.of();
  }

  /**
   * Removes a member from a chat space based on the provided member ID.
   *
   * <p>This method first retrieves the chat space using the provided {@code chatSpaceId}. It then verifies that
   * the user invoking the removal is either the creator or an admin of the chat space. After confirming the user's 
   * permissions, it locates the chat space member to be removed using the member ID from the 
   * {@code removeChatSpaceMemberDto}. Finally, it deletes the member from the repository and updates the chat space 
   * to reflect the change.</p>
   *
   * @param chatSpaceId The ID of the chat space from which the member is to be removed.
   * @param removeChatSpaceMemberDto The DTO containing the member ID of the member to be removed.
   * @param user The user attempting to remove the member.
   * @return A response object indicating the result of the removal operation.
   * @throws NotAnAdminOfChatSpaceException If the user is not the creator or an admin of the chat space.
   * @throws ChatSpaceMemberNotFoundException If the specified chat space member does not exist.
   * @throws FailedOperationException if there is an invalid input
   */
  @Override
  @Transactional
  public RemoveChatSpaceMemberResponse removeMember(final Long chatSpaceId, final RemoveChatSpaceMemberDto removeChatSpaceMemberDto, final RegisteredUser user)
    throws ChatSpaceNotFoundException, NotAnAdminOfChatSpaceException, ChatSpaceMemberNotFoundException,
      FailedOperationException {
    // Retrieve the chat space using the provided chatSpaceId
    final ChatSpace chatSpace = chatSpaceService.findChatSpace(chatSpaceId);
    // Get the chat space member id
    final Long chatSpaceMemberId = removeChatSpaceMemberDto.getChatSpaceMemberId();
    // Verify that the user is the creator or an admin of the chat space
    chatSpaceService.verifyCreatorOrAdminOfChatSpace(chatSpace, user.toMember());
    // Locate the chat space member to be removed using the member ID from the DTO
    final ChatSpaceMember chatSpaceMember = findByChatSpaceAndChatSpaceMemberId(chatSpace, chatSpaceMemberId);
    // User cannot remove self from chat space
    memberCannotRemoveSelfFromChatSpace(chatSpaceMember, user.getId());
    // Cannot remove organizer of chat space
    cannotRemoveOrganizerOfChatSpace(chatSpace, chatSpaceMember);
    // Remove the user from the chat space
    removeChatSpaceMember(chatSpace, chatSpaceMember);
    // Remove member from chat space externally
    removeChatSpaceMemberExternally(chatSpace, chatSpaceMember);

    final ChatSpaceMembershipInfo membershipInfo = unifiedMapper.getMembershipInfo(chatSpaceMember, chatSpace);
    // Create the response
    final RemoveChatSpaceMemberResponse removeChatSpaceMemberResponse = RemoveChatSpaceMemberResponse.of(chatSpaceId, chatSpaceMemberId, membershipInfo);
    // Return a localized response indicating the member removal was successful
    return localizer.of(removeChatSpaceMemberResponse);
  }

  /**
   * Removes a member from the specified chat space or allows the member to leave.
   *
   * <p>This method handles the removal of a member from a chat space based on their member ID.
   * It first finds the member within the chat space, deletes the member entry from the repository,
   * updates the total number of members in the chat space, and notifies the chat space update service
   * of the member's removal.</p>
   *
   * @param chatSpace the chat space from which the member wants to leave
   * @param chatSpaceMember the chat space member that wants to leave
   * @throws ChatSpaceMemberNotFoundException if the chat space member cannot be found
   * @throws FailedOperationException if there is an invalid input
   */
  @Override
  @Transactional
  public void leaveChatSpace(final ChatSpace chatSpace, final ChatSpaceMember chatSpaceMember)
      throws ChatSpaceMemberNotFoundException, FailedOperationException {
    // Admin is not allow to leave chat space
    chatSpaceMember.checkIsEligibleToLeave(chatSpace.getOrganizerId());
    // Allow the member to leave chat space
    chatSpaceMember.leave();
    // Save the member
    chatSpaceMemberOperationsService.save(chatSpaceMember);
    // Decrease total members and save chat space
    decreaseTotalMembersAndSave(chatSpace);
    // Create external request
    final RemoveChatSpaceMemberRequest removeChatSpaceMemberRequest = RemoveChatSpaceMemberRequest.of(chatSpace.getExternalIdOrName(), chatSpaceMember.getExternalIdOrName());
    // Notify the chat space update service about the removal
    chatSpaceUpdateService.removeMember(removeChatSpaceMemberRequest);
  }

  /**
   * Ensures that a member is not attempting to remove themselves from a ChatSpace.
   *
   * <p>This method should be called before performing a member removal operation.
   * If the {@code userId} corresponds to the same member ID as the {@code chatSpaceMember}
   * (i.e. the user is trying to remove themselves), a {@link FailedOperationException} is thrown.</p>
   *
   * @param chatSpaceMember the ChatSpaceMember entity representing the member to be removed
   * @param userId the ID of the currently authenticated user attempting the removal
   * @throws FailedOperationException if either parameter is null or if the user is trying to remove themselves
   */
  protected void memberCannotRemoveSelfFromChatSpace(final ChatSpaceMember chatSpaceMember, final Long userId) {
    // Check none of the item in the collection is null or fail
    checkIsNullAny(List.of(chatSpaceMember, userId), FailedOperationException::new);

    // Extract the user member id of the chat space member to be removed
    final Long memberIdOfChatSpaceMemberToBeRemoved = chatSpaceMember.getMemberId();
    // Throw an error if user to be removed is the same as user performing action
    if (Objects.equals(memberIdOfChatSpaceMemberToBeRemoved, userId)) {
      throw FailedOperationException.of();
    }
  }

  /**
   * Validates that the organizer of a ChatSpace is not being removed.
   *
   * <p>This method ensures that the member represented by the {@link ChatSpaceMember} is not
   * the organizer of the given {@link ChatSpace}. If the member is the organizer, a
   * {@link FailedOperationException} is thrown to prevent the operation.</p>
   *
   * @param chatSpace the ChatSpace from which a member is being removed
   * @param chatSpaceMember the member being considered for removal
   * @throws FailedOperationException if any input is null or if the member is the organizer
   */
  protected void cannotRemoveOrganizerOfChatSpace(final ChatSpace chatSpace, final ChatSpaceMember chatSpaceMember) {
    // Check none of the item in the collection is null or fail
    checkIsNullAny(List.of(chatSpace, chatSpaceMember), FailedOperationException::new);

    final Long organizerId = chatSpace.getOrganizerId();
    // Extract the user member id of the chat space member to be removed
    final Long memberIdOfChatSpaceMemberToBeRemoved = chatSpaceMember.getMemberId();
    // Throw an error if user to be removed is the same as user performing action
    if (Objects.equals(organizerId, memberIdOfChatSpaceMemberToBeRemoved)) {
      throw FailedOperationException.of();
    }
  }

  /**
   * Removes a member from the specified chat space.
   *
   * <p>This method locates the chat space member to be removed by their member ID. It then marks
   * the member as removed and updates the chat space by saving the changes. Additionally, the
   * total number of members in the chat space is decreased and the chat space update service is notified
   * about the removal. The removed chat space member details are then returned.</p>
   *
   * @param chatSpace the chat space from which the member is to be removed
   * @param chatSpaceMember the chat space member to be removed
   * @throws ChatSpaceMemberNotFoundException if the member is not found in the chat space
   */
  protected void removeChatSpaceMember(final ChatSpace chatSpace, final ChatSpaceMember chatSpaceMember) {
    // Check is not null
    checkIsNullAny(List.of(chatSpace, chatSpaceMember), FailedOperationException::new);

    // Mark the member as removed from the chat space
    chatSpaceMember.markAsRemoved();
    // Save the chat space member
    chatSpaceMemberOperationsService.save(chatSpaceMember);
    // Decrease total members and save chat space
    decreaseTotalMembersAndSave(chatSpace);
  }

  /**
   * Notifies the external chat space update service to remove a member from a ChatSpace.
   *
   * <p>This method builds a {@link RemoveChatSpaceMemberRequest} using the external identifiers
   * of the ChatSpace and the member to be removed, and delegates the removal action to
   * {@code chatSpaceUpdateService}.</p>
   *
   * @param chatSpace the ChatSpace from which the member should be removed
   * @param chatSpaceMember the member to be removed from the ChatSpace
   */
  protected void removeChatSpaceMemberExternally(final ChatSpace chatSpace, final ChatSpaceMember chatSpaceMember) {
    // Check none of the item in the collection is null or fail
    checkIsNullAny(List.of(chatSpace, chatSpaceMember), FailedOperationException::new);

    // Create external removal request
    final RemoveChatSpaceMemberRequest removeChatSpaceMemberRequest = RemoveChatSpaceMemberRequest.of(
      chatSpace.getExternalIdOrName(),
      chatSpaceMember.getExternalIdOrName()
    );
    // Notify the chat space update service about the removal
    chatSpaceUpdateService.removeMember(removeChatSpaceMemberRequest);
  }

  /**
   * Finds a chat space member based on the provided chat space and chat space member id.
   *
   * <p>This method retrieves a {@link ChatSpaceMember} from the repository using the specified chat space and chat space member ID.
   * If no chat space member is found for the given chat space and chat space member id, a {@link ChatSpaceMemberNotFoundException}
   * is thrown.</p>
   *
   * @param chatSpace The chat space in which to find the member.
   * @param chatSpaceMemberId The id associated with a chat space member in a chat space.
   * @return The {@link ChatSpaceMember} associated with the specified chat space and member.
   * @throws ChatSpaceMemberNotFoundException if no chat space member is found for the specified chat space and member.
   */
  @Override
  public ChatSpaceMember findByChatSpaceAndChatSpaceMemberId(final ChatSpace chatSpace, final Long chatSpaceMemberId) throws ChatSpaceMemberNotFoundException {
    // Retrieve the chat space member and throw an exception if not found
    return chatSpaceMemberOperationsService.findByChatSpaceAndMember(chatSpace, chatSpaceMemberId)
      .orElseThrow(ChatSpaceMemberNotFoundException.of(chatSpaceMemberId));
  }

  /**
   * Retrieves an existing {@link ChatSpaceMember} for the given {@link ChatSpace} and {@link RegisteredUser},
   * or creates a new one if none exists.
   *
   * <p>This method first validates that neither the chat space nor the user is {@code null}.
   * If either is {@code null}, a {@link FailedOperationException} is thrown.
   * Then it searches for an existing membership; if found, returns it.
   * Otherwise, it creates a new {@link ChatSpaceMember} instance</p>
   *
   * @param chatSpace the chat space in which the membership is checked or created
   * @param user the user for whom the membership is checked or created
   * @return an existing or newly created {@link ChatSpaceMember} instance
   * @throws FailedOperationException if {@code chatSpace} or {@code user} is {@code null}
   */
  @Override
  public ChatSpaceMember getExistingOrCreateNewChatSpaceMember(final ChatSpace chatSpace, final RegisteredUser user) throws FailedOperationException {
    // Throw an exception if the any of the provided values is null
    checkIsNullAny(Set.of(chatSpace, user), FailedOperationException::new);

    // Search for the user as a member of the chat space and if it doesn't exist, create a new one
    return chatSpaceMemberOperationsService.findByChatSpaceAndMember(chatSpace, user.toMember())
      .orElseGet(() -> ChatSpaceMember.of(chatSpace, user.toMember()));
  }

  /**
   * Finds a chat space member based on the provided chat space and member.
   *
   * <p>This method retrieves a {@link ChatSpaceMember} from the repository using the specified chat space and member.
   * If no chat space member is found for the given chat space and member, a {@link ChatSpaceMemberNotFoundException}
   * is thrown.</p>
   *
   * @param chatSpace The chat space in which to find the member.
   * @param member The member associated with a chat space member in a chat space.
   * @return The {@link ChatSpaceMember} associated with the specified chat space and member.
   * @throws ChatSpaceMemberNotFoundException if no chat space member is found for the specified chat space and member.
   */
  @Override
  public ChatSpaceMember findByChatSpaceAndMember(final ChatSpace chatSpace, final Member member) throws ChatSpaceMemberNotFoundException {
    // Retrieve the chat space member and throw an exception if not found
    return chatSpaceMemberOperationsService.findByChatSpaceAndMember(chatSpace, member)
      .orElseThrow(ChatSpaceMemberNotFoundException.of(member.getMemberId()));
  }

  /**
   * Notifies the chat space update service about the addition of a member to a chat space.
   *
   * <p>This method creates an {@link AddChatSpaceMemberRequest} using the external ID or name of the chat space
   * and the email address of the member being added. It then calls the chat space update service to process
   * the addition of the member.</p>
   *
   * @param chatSpaceMember The {@link ChatSpaceMember} being added to the chat space.
   * @param chatSpace The {@link ChatSpace} to which the member is being added.
   * @param member The {@link Member} that is being added to the chat space.
   */
  @Override
  @Async
  public void addMemberToChatSpaceExternally(final ChatSpaceMember chatSpaceMember, final ChatSpace chatSpace, final Member member) {
    // Create a request to add the member to the chat space
    final AddChatSpaceMemberRequest addChatSpaceMemberRequest = AddChatSpaceMemberRequest.of(chatSpace.getExternalIdOrName(), member.getEmailAddress());
    // Notify the chat space update service of the new member addition
    chatSpaceUpdateService.addMember(chatSpaceMember, addChatSpaceMemberRequest);
  }

  /**
   * Finds an existing chat space member or creates a new one if it doesn't exist.
   *
   * <p>This method attempts to find a member within a given chat space. If the member does
   * not already exist in the space, a new {@code ChatSpaceMember} is created and saved
   * to the repository.</p>
   *
   * @param chatSpace the chat space in which to find or create the member.
   * @param member the member to find or create in the chat space.
   * @return the existing or newly created {@code ChatSpaceMember}.
   */
  protected ChatSpaceMember findOrCreateChatMember(final ChatSpace chatSpace, final Member member) {
    // Attempt to find the chat space member by chat space and member or create one if none can be found
    return chatSpaceMemberOperationsService.findByChatSpaceAndMember(chatSpace, member)
      .orElse(ChatSpaceMember.of(chatSpace, member));
  }

  /**
   * Approves the join status of the given {@link ChatSpaceMember} and saves it to the repository if it is newly created.
   *
   * <p>This method first checks if the {@code chatSpaceMember} is non-null, then approves the member's join status by
   * invoking {@link ChatSpaceMember#approveJoinStatus()}. If the member does not yet have a {@code chatSpaceMemberId},
   * indicating it is a new entity, it will be saved to the repository.</p>
   *
   * @param chatSpaceMember the {@link ChatSpaceMember} whose join status is to be approved
   *                        and saved if it is newly created
   */
  protected void approveChatMemberJoinStatusAndSaveIfNew(final ChatSpaceMember chatSpaceMember) {
    // Verify the chat space member is not empty
    if (nonNull(chatSpaceMember)) {
      chatSpaceMember.approveJoinStatus();
      // If the chat space member is newly created, save it to the repository
      if (isNull(chatSpaceMember.getChatSpaceMemberId())) {
        chatSpaceMemberOperationsService.save(chatSpaceMember);
      }
    }
  }

  /**
   * Decreases the total number of members in the specified chat space and saves the updated chat space entity.
   *
   * @param chatSpace the chat space where the total number of members should be decreased
   */
  protected void decreaseTotalMembersAndSave(final ChatSpace chatSpace) {
    // Decrease total members in chat space
    chatSpaceRepository.decrementTotalMembers(chatSpace.getChatSpaceId());
  }

  /**
   * Approves the attendee's request to join a stream if the stream is linked to a chat space and the attendee is a member of the chat space.
   *
   * <p>This method checks whether the provided stream has an associated chat space. If so, it verifies if the attendee is a member
   * of the chat space by searching for an existing chat space member. If the attendee is found to be a member, the method returns {@code true};
   * otherwise, it returns {@code false}.</p>
   *
   * @param stream The stream entity which may be associated with a chat space.
   * @param streamAttendee The attendee whose membership in the chat space is being evaluated.
   * @return {@code true} if the stream has a chat space and the attendee is a member of that chat space; {@code false} otherwise.
   */
  @Override
  public boolean checkIfStreamHasChatSpaceAndAttendeeIsAMemberOfChatSpace(final FleenStream stream, final StreamAttendee streamAttendee) {
    // Check if the stream has an associated chat space with a valid ID
    if (stream.hasChatSpaceId()) {
      // Find if the attendee is a member of the chat space
      final Optional<ChatSpaceMember> existingChatSpaceMember = chatSpaceMemberOperationsService.findByChatSpaceAndMemberAndStatus(stream.getChatSpace(), streamAttendee.getMember(), ChatSpaceRequestToJoinStatus.approved());
      // Return true if a member exists, otherwise false
      return existingChatSpaceMember.isPresent();
    }
    // Return false if there's no chat space or no valid chat space ID
    return false;
  }

  /**
   * Verifies that an admin cannot add or update their own membership in the chat space.
   *
   * <p>This method checks whether the provided member ID corresponds to the owner of the chat space.
   * If the member is the owner, an exception is thrown to prevent the admin from adding or updating
   * their own membership. This ensures that the owner of the chat space cannot manipulate their own
   * role or membership through this operation.</p>
   *
   * <p>If the member is not the owner, the method does nothing and the operation can proceed normally.</p>
   *
   * @param chatSpaceMember The {@link ChatSpaceMember} in which the verification is being performed.
   * @param memberId The ID of the member being checked.
   * @throws FailedOperationException if the member is the owner of the chat space.
   */
  protected void checkAdminCannotUpgradeOrDowngradeSelf(final ChatSpaceMember chatSpaceMember, final Long memberId) {
    final Long memberIdOfChatSpaceMember = chatSpaceMember.getMemberId();
    if (Objects.equals(memberIdOfChatSpaceMember, memberId)) {
      throw FailedOperationException.of();
    }
  }

  /**
   * Validates that the organizer of the chat space cannot be upgraded or downgraded.
   *
   * <p>If the provided {@code memberIdOfChatSpaceMember} matches the organizer ID of the chat space,
   * a {@link FailedOperationException} is thrown to prevent any role change on the organizer.</p>
   *
   * @param chatSpace The chat space containing the organizer information.
   * @param memberIdOfChatSpaceMember The ID of the chat space member being checked.
   * @throws FailedOperationException if any of the parameters are {@code null}, or if the member is the organizer.
   */
  protected void checkOrganizerCannotBeUpgradedOrDowngraded(final ChatSpace chatSpace, final Long memberIdOfChatSpaceMember) {
    checkIsNullAny(List.of(chatSpace, memberIdOfChatSpaceMember), FailedOperationException::new);

    if (Objects.equals(chatSpace.getOrganizerId(), memberIdOfChatSpaceMember)) {
      throw FailedOperationException.of();
    }
  }

  /**
   * Determines whether each chat space member response in the collection can be updated by the current user,
   * based on their administrative privileges in the chat space.
   *
   * <p>If the user is identified as an admin or the creator of the chat space, all member responses
   * in the collection are marked as updatable.</p>
   *
   * @param chatSpace The chat space for which the membership update permission is being evaluated.
   * @param chatSpaceMemberResponses A collection of chat space member responses to be potentially marked as updatable.
   * @param member The current user whose permissions are used to determine updatability.
   */
  protected void setChatSpaceMembersThatAreUpdatableByUser(final ChatSpace chatSpace, final Collection<ChatSpaceMemberResponse> chatSpaceMemberResponses, final Member member) {
    // Check if chat space members and user are not null, and if the list of chat space members is not empty
    if (nonNull(chatSpaceMemberResponses) && !chatSpaceMemberResponses.isEmpty() && nonNull(member)) {
      // Verify if the user is an admin or the creator of the chat space
      final boolean isAdmin = chatSpaceService.verifyCreatorOrAdminOfChatSpace(chatSpace, member);
      // Set whether the chat spaces members are updatable by the user based on their admin status
      setChatSpaceMembersThatAreUpdatableByUser(chatSpaceMemberResponses, isAdmin);
    }
  }

  /**
   * Marks a collection of chat space member responses as updatable if the current user is an admin.
   *
   * <p>This method checks if the provided collection is non-null and non-empty,
   * and if the user has administrative privileges. If so, each member response
   * in the collection is marked as updatable.</p>
   *
   * @param chatSpaceMemberResponses A collection of chat space member responses to be potentially marked as updatable.
   * @param isAdmin {@code true} if the user is an admin and can update chat space members; {@code false} otherwise.
   */
  protected static void setChatSpaceMembersThatAreUpdatableByUser(final Collection<ChatSpaceMemberResponse> chatSpaceMemberResponses, final boolean isAdmin) {
    // Check if the chat space members are not null or empty and if the user is an admin
    if (nonNull(chatSpaceMemberResponses) && !chatSpaceMemberResponses.isEmpty() && isAdmin) {
      // Iterate through the chat space members and mark each as updatable
      chatSpaceMemberResponses.stream()
        .filter(Objects::nonNull)
        .forEach(ChatSpaceMemberResponse::markAsUpdatable);
    }
  }

}
