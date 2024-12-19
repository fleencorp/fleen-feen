package com.fleencorp.feen.service.impl.chat.space.join;

import com.fleencorp.base.service.i18n.LocalizedResponse;
import com.fleencorp.feen.constant.chat.space.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.constant.chat.space.member.ChatSpaceMemberRole;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.chat.space.*;
import com.fleencorp.feen.exception.chat.space.member.ChatSpaceMemberNotFoundException;
import com.fleencorp.feen.exception.member.MemberNotFoundException;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.chat.ChatSpaceMember;
import com.fleencorp.feen.model.domain.notification.Notification;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.dto.chat.member.JoinChatSpaceDto;
import com.fleencorp.feen.model.dto.chat.member.ProcessRequestToJoinChatSpaceDto;
import com.fleencorp.feen.model.dto.chat.member.RequestToJoinChatSpaceDto;
import com.fleencorp.feen.model.request.chat.space.membership.AddChatSpaceMemberRequest;
import com.fleencorp.feen.model.response.chat.space.JoinChatSpaceResponse;
import com.fleencorp.feen.model.response.chat.space.ProcessRequestToJoinChatSpaceResponse;
import com.fleencorp.feen.model.response.chat.space.RequestToJoinChatSpaceResponse;
import com.fleencorp.feen.model.response.chat.space.member.LeaveChatSpaceResponse;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.chat.ChatSpaceMemberRepository;
import com.fleencorp.feen.repository.chat.ChatSpaceRepository;
import com.fleencorp.feen.repository.user.MemberRepository;
import com.fleencorp.feen.service.chat.space.ChatSpaceService;
import com.fleencorp.feen.service.chat.space.join.ChatSpaceJoinService;
import com.fleencorp.feen.service.chat.space.member.ChatSpaceMemberService;
import com.fleencorp.feen.service.impl.chat.space.ChatSpaceUpdateService;
import com.fleencorp.feen.service.impl.notification.NotificationMessageService;
import com.fleencorp.feen.service.notification.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.fleencorp.base.util.ExceptionUtil.checkIsNullAny;
import static com.fleencorp.feen.service.impl.stream.base.StreamServiceImpl.verifyIfUserIsAuthorOrCreatorOrOwnerTryingToPerformAction;
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

  private final ChatSpaceMemberService chatSpaceMemberService;
  private final NotificationMessageService notificationMessageService;
  private final NotificationService notificationService;
  private final ChatSpaceUpdateService chatSpaceUpdateService;
  private final ChatSpaceMemberRepository chatSpaceMemberRepository;
  private final ChatSpaceRepository chatSpaceRepository;
  private final MemberRepository memberRepository;
  private final LocalizedResponse localizedResponse;

  /**
   * Constructs a {@code ChatSpaceServiceImpl} with the specified dependencies.
   *
   * <p>This constructor initializes the service with all required components for managing
   * chat spaces, including repositories, mappers, and various utility services. It also injects
   * configuration values like the delegated authority email.</p>
   *
   * @param notificationMessageService manages notifications sent as messages.
   * @param notificationService processes and sends general notifications.
   * @param chatSpaceUpdateService handles updates to chat spaces.
   * @param chatSpaceMemberRepository repository for managing chat space members.
   * @param chatSpaceRepository repository for chat space entities.
   * @param memberRepository repository for member-related data.
   * @param localizedResponse provides localized responses for API operations.
   */
  public ChatSpaceJoinServiceImpl(
      final ChatSpaceMemberService chatSpaceMemberService,
      final NotificationMessageService notificationMessageService,
      final NotificationService notificationService,
      final ChatSpaceUpdateService chatSpaceUpdateService,
      final ChatSpaceMemberRepository chatSpaceMemberRepository,
      final ChatSpaceRepository chatSpaceRepository,
      final MemberRepository memberRepository,
      final LocalizedResponse localizedResponse) {
    this.chatSpaceMemberService = chatSpaceMemberService;
    this.notificationMessageService = notificationMessageService;
    this.notificationService = notificationService;
    this.chatSpaceUpdateService = chatSpaceUpdateService;
    this.chatSpaceRepository = chatSpaceRepository;
    this.chatSpaceMemberRepository = chatSpaceMemberRepository;
    this.memberRepository = memberRepository;
    this.localizedResponse = localizedResponse;
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
   * @throws CannotJoinPrivateChatSpaceException if the chat space is not public.
   */
  @Override
  @Transactional
  public JoinChatSpaceResponse joinSpace(final Long chatSpaceId, final JoinChatSpaceDto joinChatSpaceDto, final FleenUser user) {
    // Find the chat space by its ID or throw an exception if not found
    final ChatSpace chatSpace = findChatSpace(chatSpaceId);
    // Verify if the user is the owner and fail the operation because the owner is automatically a member of the chat space
    verifyIfUserIsAuthorOrCreatorOrOwnerTryingToPerformAction(Member.of(chatSpace.getMemberId()), user);
    // Verify if the chat space is inactive and throw an exception if it is
    verifyIfChatSpaceInactive(chatSpace);
    // Verify that the chat space is public and throw an exception if it is not
    validatePublicSpace(chatSpace);
    // Find or create a membership entry for the user in the chat space
    findOrCreateChatSpaceMemberAndAddToChatSpace(chatSpace, joinChatSpaceDto, user);
    // Increase total members and save chat space
    increaseTotalMembersAndSave(chatSpace);
    // Return a localized response indicating successful joining
    return localizedResponse.of(JoinChatSpaceResponse.of());
  }

  /**
   * Finds an existing chat space member or creates a new one, adding the user to the chat space.
   *
   * <p>This method checks if the specified user is already a member of the given chat space. If the user
   * exists, it handles the existing member; otherwise, it creates and approves a new chat space member
   * based on the provided details.</p>
   *
   * @param chatSpace The chat space where the member is to be added or found.
   * @param joinChatSpaceDto The data transfer object containing information necessary for joining the chat space.
   * @param user The user attempting to join the chat space.
   */
  protected void findOrCreateChatSpaceMemberAndAddToChatSpace(final ChatSpace chatSpace, final JoinChatSpaceDto joinChatSpaceDto, final FleenUser user) {
    // Find the chat space member or create a new one if none exists
    chatSpaceMemberRepository.findByChatSpaceAndMember(chatSpace, user.toMember())
      .ifPresentOrElse(
        chatSpaceMember -> handleExistingChatSpaceMember(chatSpace, chatSpaceMember, user),
        () -> createAndApproveNewChatSpaceMember(chatSpace, joinChatSpaceDto, user)
      );
  }

  /**
   * Handles the actions required for an existing chat space member based on their membership status.
   *
   * <p>This method checks if the specified chat space member's request to join the chat space has been
   * approved or disapproved. If the request is already approved, an exception is thrown. If the request
   * is disapproved, the method approves the request and sends an invitation to the user.</p>
   *
   * @param chatSpace The chat space that the member is trying to join.
   * @param chatSpaceMember The existing member of the chat space.
   * @param user The user attempting to join the chat space.
   * @throws AlreadyJoinedChatSpaceException if the membership request has already been approved.
   */
  protected void handleExistingChatSpaceMember(final ChatSpace chatSpace, final ChatSpaceMember chatSpaceMember, final FleenUser user) {
    // Check the membership status of the existing chat space member
    if (chatSpaceMember.isRequestToJoinApproved()) {
      throw new AlreadyJoinedChatSpaceException();
    } else if (chatSpaceMember.isRequestToJoinDisapprovedOrPending()) {
      approveChatSpaceMemberJoinRequestAndSendInvitation(user, chatSpaceMember, chatSpace);
    }
  }

  /**
   * Creates and approves a new chat space member and sends an invitation.
   *
   * <p>This method instantiates a new chat space member using the provided chat space and user details,
   * then immediately approves the join request and sends an invitation to the user.</p>
   *
   * @param chatSpace The chat space to which the new member is being added.
   * @param joinChatSpaceDto The DTO containing the user's comment for the join request.
   * @param user The user attempting to join the chat space.
   */
  protected void createAndApproveNewChatSpaceMember(final ChatSpace chatSpace, final JoinChatSpaceDto joinChatSpaceDto, final FleenUser user) {
    // Create a new chat space member with the provided details
    final ChatSpaceMember newChatSpaceMember = ChatSpaceMember.of(chatSpace, user.toMember(), joinChatSpaceDto.getComment());
    // Approve the join request and send an invitation to the user
    approveChatSpaceMemberJoinRequestAndSendInvitation(user, newChatSpaceMember, chatSpace);
  }

  /**
   * Approves a chat space member's join request and sends an invitation to the member.
   *
   * <p>This method updates the join status of the specified chat space member to approved,
   * saves the updated member information to the repository, and then sends an invitation
   * to the user associated with the chat space member.</p>
   *
   * @param user The user whose join request is being approved.
   * @param chatSpaceMember The chat space member whose status is being updated.
   * @param chatSpace The chat space to which the member is being added.
   */
  protected void approveChatSpaceMemberJoinRequestAndSendInvitation(final FleenUser user, final ChatSpaceMember chatSpaceMember, final ChatSpace chatSpace) {
    // Approve the join status for the chat space member
    chatSpaceMember.approveJoinStatus();
    // Save the updated chat space member information to the repository
    chatSpaceMemberRepository.save(chatSpaceMember);
    // Send an invitation to the user to join the chat space
    chatSpaceUpdateService.addMember(chatSpaceMember, AddChatSpaceMemberRequest.of(chatSpace.getExternalIdOrName(), user.getEmailAddress()));
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
   */
  @Override
  @Transactional
  public RequestToJoinChatSpaceResponse requestToJoinSpace(final Long chatSpaceId, final RequestToJoinChatSpaceDto requestToJoinChatSpaceDto, final FleenUser user) {
    // Find the chat space by its ID or throw an exception if not found
    final ChatSpace chatSpace = findChatSpace(chatSpaceId);
    // Create a chat space member to update later
    ChatSpaceMember chatSpaceMember = new ChatSpaceMember();
    // Verify if the user is the owner and fail the operation because the owner is automatically a member of the chat space
    verifyIfUserIsAuthorOrCreatorOrOwnerTryingToPerformAction(Member.of(chatSpace.getMemberId()), user);
    // Verify if the chat space is inactive
    verifyIfChatSpaceInactive(chatSpace);
    // If the chat space is private, handle the join request
    if (isSpacePrivate(chatSpace)) {
      chatSpaceMember = handleJoinRequest(chatSpace, requestToJoinChatSpaceDto, user);
    }

    // Create and save notification
    final Notification notification = notificationMessageService.ofReceived(chatSpace, chatSpaceMember, chatSpace.getMember(), user.toMember());
    notificationService.save(notification);
    // Return a localized response confirming the request to join the chat space
    return localizedResponse.of(RequestToJoinChatSpaceResponse.of());
  }

  /**
   * Handles a user's request to join a chat space.
   *
   * <p>This method checks if the user is already a member of the chat space. If the user has an existing
   * join request, it updates the request status based on the provided comment. If the user is not a member,
   * a new chat space member is created with a pending join status.</p>
   *
   * @param chatSpace The chat space for which the join request is being made.
   * @param requestToJoinChatSpaceDto The DTO containing the request details for joining the chat space.
   * @param user The user requesting to join the chat space.
   */
  protected ChatSpaceMember handleJoinRequest(final ChatSpace chatSpace, final RequestToJoinChatSpaceDto requestToJoinChatSpaceDto, final FleenUser user) {
    // Check if the user is already a member of the chat space
    final AtomicReference<ChatSpaceMember> chatSpaceMemberAtomicReference = new AtomicReference<>();
    chatSpaceMemberRepository.findByChatSpaceAndMember(chatSpace, user.toMember())
      .ifPresentOrElse(chatSpaceMember -> {
        // Update the join status based on the existing request
        updateJoinStatusBasedOnExistingRequest(chatSpaceMember, requestToJoinChatSpaceDto.getComment());
        // Save the updated chat space member
        chatSpaceMemberRepository.save(chatSpaceMember);
        chatSpaceMemberAtomicReference.set(chatSpaceMember);
      }, () -> {
        // Create a new chat space member with a pending join status
        final ChatSpaceMember newChatSpaceMember = ChatSpaceMember.of(chatSpace, user.toMember(), requestToJoinChatSpaceDto.getComment());
        newChatSpaceMember.pendingJoinStatus();
        // Save the new chat space member
        chatSpaceMemberRepository.save(newChatSpaceMember);
        chatSpaceMemberAtomicReference.set(newChatSpaceMember);
    });
    return chatSpaceMemberAtomicReference.get();
  }

  /**
   * Updates the join status of an existing chat space member based on their previous join request status.
   *
   * <p>This method checks the current join request status of a chat space member. If the request is approved,
   * an exception is thrown indicating the user is already a member. If the request is disapproved, the member's
   * status is updated to pending with a comment. If the request is still pending, an exception is thrown.</p>
   *
   * @param chatSpaceMember The chat space member whose join request status is being updated.
   * @param comment The comment associated with the join request.
   */
  protected void updateJoinStatusBasedOnExistingRequest(final ChatSpaceMember chatSpaceMember, final String comment) {
    // Check if the join request has been approved
    if (chatSpaceMember.isRequestToJoinApproved()) {
      throw new AlreadyJoinedChatSpaceException();
    }
    // Check if the join request has been disapproved
    else if (chatSpaceMember.isRequestToJoinDisapproved()) {
      chatSpaceMember.pendingJoinStatusWithComment(comment);
    }
    // Check if the join request is still pending
    else if (chatSpaceMember.isRequestToJoinPending()) {
      throw new RequestToJoinChatSpacePendingException();
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
   * @throws MemberNotFoundException if the member does not exist.
   * @throws ChatSpaceMemberNotFoundException if the chat space member does not exist.
   * @throws AlreadyJoinedChatSpaceException if the member is already a part of the chat space.
   */
  @Override
  @Transactional
  public ProcessRequestToJoinChatSpaceResponse processRequestToJoinSpace(final Long chatSpaceId, final ProcessRequestToJoinChatSpaceDto processRequestToJoinChatSpaceDto, final FleenUser user) {
    // Find the chat space by its ID
    final ChatSpace chatSpace = findChatSpace(chatSpaceId);
    // Verify if the chat space has already been deleted and that the user is the creator or an admin of the chat space
    verifyIfChatSpaceAlreadyDeletedAndCreatorOrAdminOfSpace(chatSpace, user);
    // Find the member using the provided member ID from the DTO
    final Member member = findMember(processRequestToJoinChatSpaceDto.getActualMemberId());
    // Find the chat space member related to the chat space and member
    final ChatSpaceMember chatSpaceMember = findChatSpaceMember(chatSpace, member);
    // Set the admin comment for the space member
    chatSpaceMember.setSpaceAdminComment(processRequestToJoinChatSpaceDto.getComment());

    // Store the old request status for comparison
    final ChatSpaceRequestToJoinStatus oldRequestToJoinStatus = chatSpaceMember.getRequestToJoinStatus();
    // Process the join request status based on the DTO
    processJoinRequestStatus(chatSpaceMember, processRequestToJoinChatSpaceDto);
    // Notify the update service if the request status changes to approved
    if (ChatSpaceRequestToJoinStatus.isDisapprovedOrPending(oldRequestToJoinStatus) && processRequestToJoinChatSpaceDto.isApproved()) {
      notifyChatSpaceUpdateService(chatSpaceMember, chatSpace, member);
    }

    // Create and save notification
    final Notification notification = notificationMessageService.ofApprovedOrDisapproved(chatSpace, chatSpaceMember, chatSpace.getMember());
    notificationService.save(notification);
    // Return the localized response indicating the result of the processing
    return localizedResponse.of(ProcessRequestToJoinChatSpaceResponse.of(chatSpaceId, processRequestToJoinChatSpaceDto.getActualMemberId()));
  }

  /**
   * Processes the join request status for a chat space member based on the provided join status.
   *
   * <p>This method evaluates the current status of a chat space member's join request. If the request is
   * pending or disapproved, it updates the status based on the actual join status provided in the
   * {@code processRequestToJoinChatSpaceDto}. If approved, the join status is approved with an optional comment;
   * if disapproved, the request status is updated accordingly. The updated member information is then saved to the repository.</p>
   *
   * @param chatSpaceMember The chat space member whose join request status is being processed.
   * @param processRequestToJoinChatSpaceDto The DTO containing the actual join status and optional comment.
   */
  protected void processJoinRequestStatus(final ChatSpaceMember chatSpaceMember, final ProcessRequestToJoinChatSpaceDto processRequestToJoinChatSpaceDto) {
    // Check if the join request is disapproved or pending
    if (chatSpaceMember.isRequestToJoinDisapprovedOrPending()) {
      // Approve the join request if the actual status is approved
      if (ChatSpaceRequestToJoinStatus.isApproved(processRequestToJoinChatSpaceDto.getActualJoinStatus())) {
        // Disapprove the join request if the actual status is disapproved
        chatSpaceMember.approveJoinStatusWithComment(processRequestToJoinChatSpaceDto.getComment());
      } else if (ChatSpaceRequestToJoinStatus.isDisapproved(processRequestToJoinChatSpaceDto.getActualJoinStatus())) {
        chatSpaceMember.disapprovedRequestToJoin();
      }
      // Save the updated chat space member to the repository
      chatSpaceMemberRepository.save(chatSpaceMember);
    }
  }

  /**
   * Allows a user to leave a chat space by removing them from the specified {@link ChatSpace},
   * and returns a localized response indicating the successful removal.
   *
   * <p>This method retrieves the chat space based on the provided {@code chatSpaceId}, removes the user from the chat space,
   * and returns a {@link LeaveChatSpaceResponse} to confirm the member has left the chat space.</p>
   *
   * @param chatSpaceId the ID of the {@link ChatSpace} the user wishes to leave
   * @param user        the {@link FleenUser} who is leaving the chat space
   * @return a localized {@link LeaveChatSpaceResponse} indicating successful removal from the chat space
   */
  @Override
  @Transactional
  public LeaveChatSpaceResponse leaveChatSpace(final Long chatSpaceId, final FleenUser user) {
    // Retrieve the chat space using the provided chatSpaceId
    final ChatSpace chatSpace = findChatSpace(chatSpaceId);
    // Remove the user from the chat space
    chatSpaceMemberService.leaveChatSpaceOrRemoveChatSpaceMember(chatSpace, user.getId());
    // Return a localized response indicating the member removal was successful
    return localizedResponse.of(LeaveChatSpaceResponse.of());
  }

  /**
   * Finds a chat space by its ID.
   *
   * <p>This method retrieves a chat space from the repository using the provided ID.
   * If the chat space with the specified ID does not exist, a `ChatSpaceNotFoundException`
   * is thrown.</p>
   *
   * @param chatSpaceId The ID of the chat space to retrieve.
   * @return The chat space associated with the provided ID.
   * @throws ChatSpaceNotFoundException if no chat space with the specified ID is found.
   */
  protected ChatSpace findChatSpace(final Long chatSpaceId) {
    // Attempt to find the chat space by its ID in the repository
    return chatSpaceRepository.findById(chatSpaceId)
      // If not found, throw an exception with the chat space ID
      .orElseThrow(ChatSpaceNotFoundException.of(chatSpaceId));
  }

  /**
   * Finds a member by their unique identifier.
   *
   * <p>This method retrieves a member from the repository using the provided member ID. If no member is found
   * with the specified ID, a {@link MemberNotFoundException} is thrown.</p>
   *
   * @param memberId The unique identifier of the member to be retrieved.
   * @return The {@link Member} associated with the given member ID.
   * @throws MemberNotFoundException if no member is found with the specified ID.
   */
  protected Member findMember(final Long memberId) {
    // Retrieve the member by ID and throw an exception if not found
    return memberRepository.findById(memberId)
      .orElseThrow(MemberNotFoundException.of(memberId));
  }

  /**
   * Finds a chat space member based on the provided chat space and member.
   *
   * <p>This method retrieves a {@link ChatSpaceMember} from the repository using the specified chat space and member.
   * If no chat space member is found for the given chat space and member, a {@link ChatSpaceMemberNotFoundException}
   * is thrown.</p>
   *
   * @param chatSpace The chat space in which to find the member.
   * @param member The member whose association with the chat space is to be retrieved.
   * @return The {@link ChatSpaceMember} associated with the specified chat space and member.
   * @throws ChatSpaceMemberNotFoundException if no chat space member is found for the specified chat space and member.
   */
  protected ChatSpaceMember findChatSpaceMember(final ChatSpace chatSpace, final Member member) {
    // Retrieve the chat space member and throw an exception if not found
    return chatSpaceMemberRepository.findByChatSpaceAndMember(chatSpace, member)
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
  protected void notifyChatSpaceUpdateService(final ChatSpaceMember chatSpaceMember, final ChatSpace chatSpace, final Member member) {
    // Create a request to add the member to the chat space
    final AddChatSpaceMemberRequest addChatSpaceMemberRequest = AddChatSpaceMemberRequest.of(chatSpace.getExternalIdOrName(), member.getEmailAddress());
    // Notify the chat space update service of the new member addition
    chatSpaceUpdateService.addMember(chatSpaceMember, addChatSpaceMemberRequest);
  }

  /**
   * Validates that the provided user is either the creator or an admin of the specified chat space.
   *
   * <p>This method checks if the user is the creator of the chat space by comparing their IDs.
   * If the user is not the creator, it further checks if the user is an admin of the chat space.
   * If the user is neither the creator nor an admin, a {@link NotAnAdminOfChatSpaceException} is thrown.</p>
   *
   * @param chatSpace The chat space to validate against.
   * @param user The user whose permissions are being validated.
   * @throws FailedOperationException if any of the provided values is null.
   * @throws NotAnAdminOfChatSpaceException if the user is neither the creator nor an admin of the chat space.
   */
  protected void verifyCreatorOrAdminOfSpace(final ChatSpace chatSpace, final FleenUser user) {
    // Throw an exception if the any of the provided values is null
    checkIsNullAny(Set.of(chatSpace, user), FailedOperationException::new);

    // Check if the user is the creator of the space
    if (Objects.equals(chatSpace.getMemberId(), user.getId())) {
      return;
    }
    // Check if the user is an admin in the space
    if (checkIfUserIsAnAdminInSpace(chatSpace, user)) {
      return;
    }
    // If neither, throw exception
    throw new NotAnAdminOfChatSpaceException();
  }

  /**
   * Verifies if the specified chat space has already been marked as deleted.
   *
   * <p>This method checks if the provided chat space is not null and if it has been marked
   * as deleted. If the chat space is found to be deleted, an exception is thrown to indicate
   * that the chat space cannot be operated on.</p>
   *
   * @param chatSpace The chat space to verify.
   * @throws ChatSpaceAlreadyDeletedException if the chat space has already been deleted.
   */
  protected void verifyIfChatSpaceAlreadyDeleted(final ChatSpace chatSpace) {
    // Check if the chat space is not null and if it is marked as deleted
    if (nonNull(chatSpace) && chatSpace.isDeleted()) {
      // Throw an exception if the chat space is already deleted
      throw new ChatSpaceAlreadyDeletedException();
    }
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
  protected void verifyIfChatSpaceAlreadyDeletedAndCreatorOrAdminOfSpace(final ChatSpace chatSpace, final FleenUser user) {
    // Verify if the chat space has already been deleted
    verifyIfChatSpaceAlreadyDeleted(chatSpace);
    // Verify that the user is the creator or an admin of the chat space
    verifyCreatorOrAdminOfSpace(chatSpace, user);
  }

  /**
   * Verifies if the specified chat space is inactive.
   *
   * <p>This method checks if the provided chat space is not null and if it is marked
   * as inactive. If the chat space is found to be inactive, an exception is thrown to indicate
   * that the chat space cannot be operated on.</p>
   *
   * @param chatSpace The chat space to verify.
   * @throws ChatSpaceNotActiveException if the chat space is inactive.
   */
  protected void verifyIfChatSpaceInactive(final ChatSpace chatSpace) {
    // Check if the chat space is not null and if it is marked as inactive
    if (nonNull(chatSpace) && chatSpace.isInactive()) {
      // Throw an exception if the chat space is disabled or inactive
      throw new ChatSpaceNotActiveException();
    }
  }

  /**
   * Checks if the specified user is an admin in the provided chat space.
   *
   * <p>This method retrieves all members of the chat space with the admin role and extracts their IDs.
   * It then checks if the given user is among those members. If the user is found in the list of admin
   * member IDs, the method returns true; otherwise, it returns false.</p>
   *
   * @param chatSpace The chat space to check the user's admin status against.
   * @param user The user whose admin status is being checked.
   * @return {@code true} if the user is an admin in the chat space; {@code false} otherwise.
   */
  protected boolean checkIfUserIsAnAdminInSpace(final ChatSpace chatSpace, final FleenUser user) {
    // Retrieve all members of the chat space with the admin role
    final Set<ChatSpaceMember> chatSpaceMembers = chatSpaceMemberRepository.findByChatSpaceAndRole(chatSpace, ChatSpaceMemberRole.ADMIN);
    // Extract the IDs of the admin members
    final Set<Long> spaceMemberIds = extractMemberIds(chatSpaceMembers);
    // Check if the user is among the admin members
    return isSpaceMemberAnAdmin(spaceMemberIds, user);
  }

  /**
   * Extracts the member IDs from a set of chat space members.
   *
   * <p>This method checks if the provided set of chat space members is not null and not empty.
   * If valid, it streams the members and maps them to their corresponding member IDs, collecting
   * the results into a set. If the input set is null or empty, an empty set is returned.</p>
   *
   * @param chatSpaceMembers The set of chat space members from which to extract member IDs.
   * @return A set of member IDs extracted from the provided chat space members, or an empty set if the input is null or empty.
   */
  protected Set<Long> extractMemberIds(final Set<ChatSpaceMember> chatSpaceMembers) {
    // Check if the chat space members set is not null and not empty
    if (nonNull(chatSpaceMembers) && !chatSpaceMembers.isEmpty()) {
      // Stream the members and collect their IDs into a set
      return chatSpaceMembers.stream()
        .map(ChatSpaceMember::getMemberId)
        .collect(Collectors.toSet());
    }
    // Return an empty set if the input is null or empty
    return Set.of();
  }

  /**
   * Checks if the specified user is an admin among the provided space member IDs.
   *
   * <p>This method verifies that the set of space member IDs and the user are not null or empty.
   * If valid, it checks if the user’s ID is present in the set of space member IDs, indicating
   * that the user is an admin. If the set is null, empty, or the user is null, it returns false.</p>
   *
   * @param spaceMemberIds The set of space member IDs to check against.
   * @param user The user whose admin status is being checked.
   * @return True if the user is an admin (their ID is in the set of member IDs); false otherwise.
   */
  protected boolean isSpaceMemberAnAdmin(final Set<Long> spaceMemberIds, final FleenUser user) {
    // Check if the space member IDs set and user are not null or empty
    if (nonNull(spaceMemberIds) && !spaceMemberIds.isEmpty() && nonNull(user)) {
      // Check if the user's ID is present in the set of space member IDs
      return spaceMemberIds.contains(user.getId());
    }
    // Return false if the input set is null, empty, or the user is null
    return false;
  }

  /**
   * Checks if the specified chat space is private.
   *
   * <p>This method verifies that the chat space object is not null and checks its
   * privacy status. It returns true if the chat space is marked as private; otherwise,
   * it returns false.</p>
   *
   * @param chatSpace The chat space to check for privacy.
   * @return True if the chat space is private; false if it is public or the chat space is null.
   */
  public boolean isSpacePrivate(final ChatSpace chatSpace) {
    // Check if the chat space is not null and if it is marked as private
    return nonNull(chatSpace) && chatSpace.isPrivate();
  }

  /**
   * Validates whether the specified chat space is public.
   *
   * <p>This method checks if the provided chat space is private. If the chat space is private,
   * it throws an exception, preventing users from joining.</p>
   *
   * @param chatSpace The chat space to validate for public access.
   * @throws CannotJoinPrivateChatSpaceException if the chat space is private and cannot be joined.
   */
  protected void validatePublicSpace(final ChatSpace chatSpace) {
    // Check if the chat space is private and throw an exception if it is
    if (isSpacePrivate(chatSpace)) {
      throw new CannotJoinPrivateChatSpaceException();
    }
  }

  /**
   * Increases the total number of members in the specified chat space and saves the updated chat space entity.
   *
   * @param chatSpace the chat space where the total number of members should be increased
   */
  protected void increaseTotalMembersAndSave(final ChatSpace chatSpace) {
    // Increase total members in chat space
    chatSpace.increaseTotalMembers();
    // Save chat space to repository
    chatSpaceRepository.save(chatSpace);
  }

}
