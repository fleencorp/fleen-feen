package com.fleencorp.feen.service.impl.chat.space.join;

import com.fleencorp.feen.constant.chat.space.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.chat.space.ChatSpaceNotFoundException;
import com.fleencorp.feen.exception.chat.space.core.ChatSpaceAlreadyDeletedException;
import com.fleencorp.feen.exception.chat.space.core.ChatSpaceNotActiveException;
import com.fleencorp.feen.exception.chat.space.core.NotAnAdminOfChatSpaceException;
import com.fleencorp.feen.exception.chat.space.join.request.AlreadyJoinedChatSpaceException;
import com.fleencorp.feen.exception.chat.space.join.request.CannotJoinPrivateChatSpaceWithoutApprovalException;
import com.fleencorp.feen.exception.chat.space.member.ChatSpaceMemberNotFoundException;
import com.fleencorp.feen.exception.chat.space.member.ChatSpaceMemberRemovedException;
import com.fleencorp.feen.mapper.common.UnifiedMapper;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.chat.ChatSpaceMember;
import com.fleencorp.feen.model.domain.notification.Notification;
import com.fleencorp.feen.model.dto.chat.member.JoinChatSpaceDto;
import com.fleencorp.feen.model.dto.chat.member.ProcessRequestToJoinChatSpaceDto;
import com.fleencorp.feen.model.dto.chat.member.RequestToJoinChatSpaceDto;
import com.fleencorp.feen.model.info.chat.space.membership.ChatSpaceMembershipInfo;
import com.fleencorp.feen.model.response.chat.space.member.LeaveChatSpaceResponse;
import com.fleencorp.feen.model.response.chat.space.membership.JoinChatSpaceResponse;
import com.fleencorp.feen.model.response.chat.space.membership.ProcessRequestToJoinChatSpaceResponse;
import com.fleencorp.feen.model.response.chat.space.membership.RequestToJoinChatSpaceResponse;
import com.fleencorp.feen.service.chat.space.ChatSpaceSearchService;
import com.fleencorp.feen.service.chat.space.ChatSpaceService;
import com.fleencorp.feen.service.chat.space.join.ChatSpaceJoinService;
import com.fleencorp.feen.service.chat.space.member.ChatSpaceMemberOperationsService;
import com.fleencorp.feen.service.chat.space.member.ChatSpaceMemberService;
import com.fleencorp.feen.service.impl.notification.NotificationMessageService;
import com.fleencorp.feen.service.notification.NotificationService;
import com.fleencorp.feen.user.exception.member.MemberNotFoundException;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class ChatSpaceJoinServiceImpl implements ChatSpaceJoinService {

  private final ChatSpaceService chatSpaceService;
  private final ChatSpaceMemberOperationsService chatSpaceMemberOperationsService;
  private final ChatSpaceMemberService chatSpaceMemberService;
  private final ChatSpaceSearchService chatSpaceSearchService;
  private final NotificationMessageService notificationMessageService;
  private final NotificationService notificationService;
  private final UnifiedMapper unifiedMapper;
  private final Localizer localizer;

  /**
   * Constructs a new {@code ChatSpaceJoinServiceImpl} with all required services and utilities.
   *
   * @param chatSpaceService the service responsible for core chat space operations
   * @param chatSpaceMemberOperationsService the service handling chat space member actions such as joining or leaving
   * @param chatSpaceMemberService the service for managing chat space member information
   * @param chatSpaceSearchService the service for searching and discovering chat spaces
   * @param notificationMessageService the service for creating message content for notifications
   * @param notificationService the service responsible for sending notifications
   * @param unifiedMapper the mapper utility for transforming objects across layers
   * @param localizer the utility for retrieving localized text based on locale
   */
  public ChatSpaceJoinServiceImpl(
      final ChatSpaceService chatSpaceService,
      final ChatSpaceMemberOperationsService chatSpaceMemberOperationsService,
      final ChatSpaceMemberService chatSpaceMemberService,
      final ChatSpaceSearchService chatSpaceSearchService,
      final NotificationMessageService notificationMessageService,
      final NotificationService notificationService,
      final UnifiedMapper unifiedMapper,
      final Localizer localizer) {
    this.chatSpaceService = chatSpaceService;
    this.chatSpaceMemberService = chatSpaceMemberService;
    this.chatSpaceMemberOperationsService = chatSpaceMemberOperationsService;
    this.chatSpaceSearchService = chatSpaceSearchService;
    this.notificationMessageService = notificationMessageService;
    this.notificationService = notificationService;
    this.unifiedMapper = unifiedMapper;
    this.localizer = localizer;
  }

  /**
   * Allows a user to join a chat space identified by its ID.
   *
   * <p>This method retrieves the chat space using the provided ID and performs validation to
   * ensure that the chat space is active and public. If valid, it either finds an existing
   * member entry for the user in the chat space or creates a new membership record.</p>
   *
   * @param chatSpaceId The ID of the chat space the user wants to join.
   * @param joinChatSpaceDto The DTO containing any additional information required for joining the space.
   * @param user The user attempting to join the chat space.
   * @return A response confirming the user’s successful joining of the chat space, localized based on the user's locale.
   * @throws ChatSpaceNotFoundException if the chat space with the specified ID does not exist.
   * @throws ChatSpaceNotActiveException if the chat space is inactive.
   * @throws CannotJoinPrivateChatSpaceWithoutApprovalException if the chat space is not public.
   * @throws FailedOperationException if there is an invalid input
   */
  @Override
  @Transactional
  public JoinChatSpaceResponse joinSpace(final Long chatSpaceId, final JoinChatSpaceDto joinChatSpaceDto, final RegisteredUser user)
    throws ChatSpaceNotFoundException, ChatSpaceNotActiveException, CannotJoinPrivateChatSpaceWithoutApprovalException,
      FailedOperationException {
    // Find the chat space by its ID or throw an exception if not found
    final ChatSpace chatSpace = chatSpaceService.findChatSpace(chatSpaceId);
    // Verify if the chat space is inactive and throw an exception if otherwise
    chatSpace.checkIsInactive();
    // Verify if the user is the owner and fail the operation because the owner is automatically a member of the chat space
    chatSpace.checkIsNotOrganizer(user.getId());
    // Verify that the chat space is public and throw an exception if otherwise
    chatSpace.checkIsNotPrivate();
    // Find an existing chat space member associated with the user or create a new one
    final ChatSpaceMember chatSpaceMember = chatSpaceMemberService.getExistingOrCreateNewChatSpaceMember(chatSpace, user);
    // Handle user request to join the chat space
    handleJoinRequestForPublicChatSpace(chatSpaceMember, joinChatSpaceDto.getComment());
    // Handle user request to join the chat space externally
    addMemberToChatSpaceExternally(chatSpace, chatSpaceMember, user.toMember());
    // Increase total members and save chat space
    chatSpaceService.increaseTotalMembersAndSave(chatSpace);
    // Get the membership info
    final ChatSpaceMembershipInfo chatSpaceMembershipInfo = unifiedMapper.getMembershipInfo(chatSpaceMember, chatSpace);
    // Create the response
    final JoinChatSpaceResponse joinChatSpaceResponse = JoinChatSpaceResponse.of(chatSpaceId, chatSpaceMembershipInfo, chatSpace.getSpaceLink(), chatSpace.getTotalMembers());
    // Return a localized response indicating successful joining
    return localizer.of(joinChatSpaceResponse);
  }

  /**
   * Handles a join request for a public chat space.
   *
   * <p>If the provided {@code chatSpaceMember} is not {@code null}, this method performs several checks and updates:
   * it ensures the member has not been removed, verifies that the join request is neither approved nor pending,
   * updates the join status to approved, assigns the provided comment, and saves the updated member to the repository.
   *
   * @param chatSpaceMember the chat space member attempting to join
   * @param comment an optional comment from the member included with the join request
   */
  protected void handleJoinRequestForPublicChatSpace(final ChatSpaceMember chatSpaceMember, final String comment) {
    if (nonNull(chatSpaceMember)) {
      // Verify the user is not removed from the chat space
      chatSpaceMember.checkNotRemoved();
      // Check if the user request to join is not approved or pending
      chatSpaceMember.checkIsNotApprovedOrPending();
      // Approve the join status for the chat space member
      chatSpaceMember.approveJoinStatus();
      // Set the chat space member comment
      chatSpaceMember.setMemberComment(comment);
      // Save the updated chat space member information to the repository
      chatSpaceMemberOperationsService.save(chatSpaceMember);
    }
  }

  /**
   * Handles a user's request to join a chat space.
   *
   * <p>This method checks if the specified chat space is active. If the chat space is private,
   * it processes the join request. A localized response is returned upon successful request.</p>
   *
   * @param chatSpaceId The ID of the chat space the user wants to join.
   * @param requestToJoinChatSpaceDto The DTO containing the request details for joining the chat space.
   * @param user The user requesting to join the chat space.
   * @return A localized response confirming the request to join the chat space.
   * @throws ChatSpaceNotFoundException if the chat space with the specified ID does not exist.
   * @throws ChatSpaceNotActiveException if the chat space is inactive.
   * @throws ChatSpaceMemberRemovedException if the user has been removed from the chat space
   * @throws FailedOperationException if there is an invalid input
   */
  @Override
  @Transactional
  public RequestToJoinChatSpaceResponse requestToJoinSpace(final Long chatSpaceId, final RequestToJoinChatSpaceDto requestToJoinChatSpaceDto, final RegisteredUser user)
    throws ChatSpaceNotFoundException, ChatSpaceNotActiveException, ChatSpaceMemberRemovedException,
      FailedOperationException  {
    // Find the chat space by its ID or throw an exception if not found
    final ChatSpace chatSpace = chatSpaceService.findChatSpace(chatSpaceId);
    // Verify if the chat space is inactive
    chatSpace.checkIsInactive();
    // Verify if the user is the owner and fail the operation because the owner is automatically a member of the chat space
    chatSpace.checkIsNotOrganizer(user.getId());
    // Verify that the chat space is not public and throw an exception if otherwise
    chatSpace.checkIsNotPublic();
    // Find an existing chat space member associated with the user or create a new one
    final ChatSpaceMember chatSpaceMember = chatSpaceMemberService.getExistingOrCreateNewChatSpaceMember(chatSpace, user);
    // Handle the user request to join
    handleJoinRequestForPrivateChatSpace(chatSpaceMember, requestToJoinChatSpaceDto.getComment());
    // Create a notification
    final Notification notification = notificationMessageService.ofReceivedChatSpaceJoinRequest(chatSpace, chatSpaceMember, chatSpace.getMember(), user.toMember());
    // Save the notification
    notificationService.save(notification);
    // Get the membership info
    final ChatSpaceMembershipInfo chatSpaceMembershipInfo = unifiedMapper.getMembershipInfo(chatSpaceMember, chatSpace);
    // Create the response
    final RequestToJoinChatSpaceResponse requestToJoinChatSpaceResponse = RequestToJoinChatSpaceResponse.of(chatSpaceId, chatSpaceMembershipInfo, chatSpace.getTotalMembers());
    // Return a localized response confirming the request to join the chat space
    return localizer.of(requestToJoinChatSpaceResponse);
  }

  /**
   * Handles a join request for a private chat space.
   *
   * <p>If the provided {@code chatSpaceMember} is not {@code null}, this method ensures the member has not been removed,
   * verifies that the join request is neither approved nor already pending, marks the join request as pending with
   * the given comment, and saves the updated member to the repository.
   *
   * @param chatSpaceMember the chat space member attempting to join
   * @param comment an optional comment from the member included with the join request
   */
  protected void handleJoinRequestForPrivateChatSpace(final ChatSpaceMember chatSpaceMember, final String comment) {
    if (nonNull(chatSpaceMember)) {
      // Verify the user is not removed from the chat space
      chatSpaceMember.checkNotRemoved();
      // Check if the user request to join is not approved or pending
      chatSpaceMember.checkIsNotApprovedOrPending();
      // Mark request as pending
      chatSpaceMember.markJoinRequestAsPendingWithComment(comment);
      // Save the chat space member
      chatSpaceMemberOperationsService.save(chatSpaceMember);
    }
  }

  /**
   * Processes a request to join a chat space, either approving or disapproving the request.
   *
   * <p>This method first validates whether the user is the creator or an admin of the chat space.
   * It then retrieves the member associated with the request and updates the chat space member's
   * status based on the provided DTO. If the request status changes from disapproved or pending
   * to approved, the chat space update service is notified.</p>
   *
   * @param chatSpaceId The ID of the chat space for which the join request is being processed.
   * @param processRequestToJoinChatSpaceDto The DTO containing the details of the join request and status.
   * @param user The user processing the request, which must be the creator or an admin of the chat space.
   * @return A response indicating the result of processing the join request.
   * @throws ChatSpaceNotFoundException if the chat space does not exist.
   * @throws ChatSpaceAlreadyDeletedException if the chat space has been deleted.
   * @throws MemberNotFoundException if the member does not exist.
   * @throws ChatSpaceMemberNotFoundException if the chat space member does not exist.
   * @throws AlreadyJoinedChatSpaceException if the member is already a part of the chat space.
   * @throws FailedOperationException if there is an invalid input
   */
  @Override
  @Transactional
  public ProcessRequestToJoinChatSpaceResponse processRequestToJoinSpace(final Long chatSpaceId, final ProcessRequestToJoinChatSpaceDto processRequestToJoinChatSpaceDto, final RegisteredUser user)
      throws ChatSpaceNotFoundException, ChatSpaceAlreadyDeletedException, MemberNotFoundException,
        ChatSpaceMemberNotFoundException, AlreadyJoinedChatSpaceException, FailedOperationException {
    // Find the chat space by its ID
    final ChatSpace chatSpace = chatSpaceService.findChatSpace(chatSpaceId);
    // Retrieve the chat space member id
    final Long chatSpaceMemberId = processRequestToJoinChatSpaceDto.getChatSpaceMemberId();
    // Verify if the chat space has already been deleted
    chatSpace.checkNotDeleted();
    // Check that the user is an admin of the chat space
    checkIsAnAdminOfChatSpace(chatSpace, user);
    // Find the chat space member related to the chat space and member
    final ChatSpaceMember chatSpaceMember = chatSpaceMemberService.findByChatSpaceAndChatSpaceMemberId(chatSpace, chatSpaceMemberId);
    // Find the member using the provided member ID from the DTO
    final Member member = chatSpaceMember.getMember();
    // Store the previous request status for comparison
    final ChatSpaceRequestToJoinStatus previousRequestToJoinStatus = chatSpaceMember.getRequestToJoinStatus();
    // Handle the user request to join the chat space
    handleProcessingOfJoinRequest(chatSpaceMember, processRequestToJoinChatSpaceDto);
    // Store the current request status for comparison
    final ChatSpaceRequestToJoinStatus currentRequestToJoinStatus = chatSpaceMember.getRequestToJoinStatus();
    // Add the member to the chat space externally
    addMemberToChatSpaceExternally(chatSpace, chatSpaceMember, previousRequestToJoinStatus, currentRequestToJoinStatus, member);

    // Create a notification
    final Notification notification = notificationMessageService.ofApprovedOrDisapprovedChatSpaceJoinRequest(chatSpace, chatSpaceMember, chatSpace.getMember());
    // Save the notification
    notificationService.save(notification);
    // Get the membership info
    final ChatSpaceMembershipInfo chatSpaceMembershipInfo = unifiedMapper.getMembershipInfo(chatSpaceMember, chatSpace);
    // Get the total request to join
    final Long totalRequestToJoin = chatSpaceSearchService.getTotalRequestToJoinForChatSpace(chatSpaceId);
    // Create the response
    final ProcessRequestToJoinChatSpaceResponse processRequestToJoinChatSpaceResponse = ProcessRequestToJoinChatSpaceResponse.of(
      chatSpaceId,
      chatSpaceMemberId,
      chatSpaceMembershipInfo,
      chatSpace.getTotalMembers(),
      totalRequestToJoin
    );
    // Return the localized response indicating the result of the processing
    return localizer.of(processRequestToJoinChatSpaceResponse);
  }

  /**
   * Handles the processing of a join request for a chat space.
   *
   * <p>This method updates the join request status for a {@code chatSpaceMember} based on the provided admin decision.
   * It sets the admin comment, checks if the join request is disapproved or pending, and updates the status accordingly.
   * Finally, the updated member information is saved to the repository.
   *
   * @param chatSpaceMember the chat space member whose join request is being processed
   * @param processRequestToJoinChatSpaceDto the data transfer object containing the new join status and admin comment
   */
  protected void handleProcessingOfJoinRequest(final ChatSpaceMember chatSpaceMember, final ProcessRequestToJoinChatSpaceDto processRequestToJoinChatSpaceDto) {
    final String adminComment = processRequestToJoinChatSpaceDto.getComment();
    // Set the admin comment for the space member
    chatSpaceMember.setSpaceAdminComment(adminComment);
    // Get the updated request to join status decision by the admin
    final ChatSpaceRequestToJoinStatus newRequestToJoinStatus = processRequestToJoinChatSpaceDto.getJoinStatus();
    // Check if the join request is disapproved or pending
    if (chatSpaceMember.isRequestToJoinDisapprovedOrPending()) {
      // Update the join request status of the member
      chatSpaceMember.approveOrDisapproveJoinRequest(newRequestToJoinStatus);
      // Save the updated chat space member to the repository
      chatSpaceMemberOperationsService.save(chatSpaceMember);
    }
  }

  /**
   * Adds a member to a chat space externally based on a change in the join request status.
   *
   * <p>If the {@code currentRequestToJoinStatus} is approved and the {@code previousRequestToJoinStatus} was either
   * disapproved or pending, this method calls the {@code addMemberToChatSpaceExternally} method to add the member
   * to the chat space.
   *
   * @param chatSpace the chat space to which the member is being added
   * @param chatSpaceMember the chat space member object to be added
   * @param previousRequestToJoinStatus the previous join request status of the member
   * @param currentRequestToJoinStatus the current join request status of the member
   * @param member the member being added to the chat space
   */
  protected void addMemberToChatSpaceExternally(final ChatSpace chatSpace, final ChatSpaceMember chatSpaceMember, final ChatSpaceRequestToJoinStatus previousRequestToJoinStatus, final ChatSpaceRequestToJoinStatus currentRequestToJoinStatus, final Member member) {
    // Notify the update service if the request status changes to approved
    if (ChatSpaceRequestToJoinStatus.isApproved(currentRequestToJoinStatus) &&
        ChatSpaceRequestToJoinStatus.isDisapprovedOrPending(previousRequestToJoinStatus)) {
      addMemberToChatSpaceExternally(chatSpace, chatSpaceMember, member);
    }
  }

  /**
   * Adds a member to a chat space using an external service.
   *
   * <p>This method delegates the task of adding the member to the chat space to the
   * {@code chatSpaceMemberService}, which handles the process of adding the {@code chatSpaceMember} to the
   * provided {@code chatSpace} for the given {@code member}.
   *
   * @param chatSpace the chat space to which the member is being added
   * @param chatSpaceMember the chat space member object to be added
   * @param member the member being added to the chat space
   */
  protected void addMemberToChatSpaceExternally(final ChatSpace chatSpace, final ChatSpaceMember chatSpaceMember, final Member member) {
    chatSpaceMemberService.addMemberToChatSpaceExternally(chatSpaceMember, chatSpace, member);
  }

  /**
   * Allows a user to leave a chat space by removing them from the specified {@link ChatSpace},
   * and returns a localized response indicating the successful removal.
   *
   * <p>This method retrieves the chat space based on the provided {@code chatSpaceId}, removes the user from the chat space,
   * and returns a {@link LeaveChatSpaceResponse} to confirm the member has left the chat space.</p>
   *
   * @param chatSpaceId the ID of the {@link ChatSpace} the user wishes to leave
   * @param user        the {@link RegisteredUser} who is leaving the chat space
   * @return a localized {@link LeaveChatSpaceResponse} indicating successful removal from the chat space
   * @throws FailedOperationException if there is an invalid input
   */
  @Override
  @Transactional
  public LeaveChatSpaceResponse leaveChatSpace(final Long chatSpaceId, final RegisteredUser user) throws FailedOperationException {
    // Retrieve the chat space using the provided chatSpaceId
    final ChatSpace chatSpace = chatSpaceService.findChatSpace(chatSpaceId);
    // Locate the chat space member to be removed using the member ID from the DTO
    final ChatSpaceMember chatSpaceMember = chatSpaceMemberService.findByChatSpaceAndMember(chatSpace, user.toMember());
    // Remove the user from the chat space
    chatSpaceMemberService.leaveChatSpace(chatSpace, chatSpaceMember);
    // Get the membership info
    final ChatSpaceMembershipInfo chatSpaceMembershipInfo = unifiedMapper.getMembershipInfo(chatSpaceMember, chatSpace);
    // Create the response
    final LeaveChatSpaceResponse leaveChatSpaceResponse = LeaveChatSpaceResponse.of(chatSpaceId, chatSpaceMembershipInfo, chatSpace.getTotalMembers());
    // Return a localized response indicating the member left successfully
    return localizer.of(leaveChatSpaceResponse);
  }

  /**
   * Validates that the chat space has not been deleted and that the user is either the creator or an admin of the chat space.
   *
   * <p>This method first checks if the provided {@code chatSpace} has been deleted. If the chat space is not
   * deleted, it then verifies whether the provided {@code user} is the creator or an admin of the chat space.</p>
   *
   * @param chatSpace The chat space to validate.
   * @param user The user whose permissions are being checked.
   * @throws ChatSpaceAlreadyDeletedException If the chat space has been deleted.
   * @throws NotAnAdminOfChatSpaceException If the user is not the creator or an admin of the chat space.
   */
  protected void checkIsAnAdminOfChatSpace(final ChatSpace chatSpace, final RegisteredUser user) {
    // Verify that the user is the creator or an admin of the chat space
    chatSpaceService.verifyCreatorOrAdminOfChatSpace(chatSpace, user.toMember());
  }

}
