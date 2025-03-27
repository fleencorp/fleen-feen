package com.fleencorp.feen.service.impl.chat.space.member;

import com.fleencorp.feen.constant.chat.space.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.chat.space.ChatSpaceNotFoundException;
import com.fleencorp.feen.exception.chat.space.core.NotAnAdminOfChatSpaceException;
import com.fleencorp.feen.exception.chat.space.member.ChatSpaceMemberNotFoundException;
import com.fleencorp.feen.exception.member.MemberNotFoundException;
import com.fleencorp.feen.mapper.chat.member.ChatSpaceMemberMapper;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.chat.ChatSpaceMember;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.dto.chat.DowngradeChatSpaceAdminToMemberDto;
import com.fleencorp.feen.model.dto.chat.UpgradeChatSpaceMemberToAdminDto;
import com.fleencorp.feen.model.dto.chat.member.AddChatSpaceMemberDto;
import com.fleencorp.feen.model.dto.chat.member.RemoveChatSpaceMemberDto;
import com.fleencorp.feen.model.holder.ChatSpaceAndMemberDetailsHolder;
import com.fleencorp.feen.model.info.chat.space.member.ChatSpaceMemberRoleInfo;
import com.fleencorp.feen.model.request.chat.space.membership.AddChatSpaceMemberRequest;
import com.fleencorp.feen.model.request.chat.space.membership.RemoveChatSpaceMemberRequest;
import com.fleencorp.feen.model.request.search.chat.space.ChatSpaceMemberSearchRequest;
import com.fleencorp.feen.model.response.chat.space.member.AddChatSpaceMemberResponse;
import com.fleencorp.feen.model.response.chat.space.member.DowngradeChatSpaceAdminToMemberResponse;
import com.fleencorp.feen.model.response.chat.space.member.RemoveChatSpaceMemberResponse;
import com.fleencorp.feen.model.response.chat.space.member.UpgradeChatSpaceMemberToAdminResponse;
import com.fleencorp.feen.model.response.chat.space.member.base.ChatSpaceMemberResponse;
import com.fleencorp.feen.model.search.chat.space.member.ChatSpaceMemberSearchResult;
import com.fleencorp.feen.model.search.chat.space.member.EmptyChatSpaceMemberSearchResult;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.chat.ChatSpaceMemberRepository;
import com.fleencorp.feen.repository.chat.ChatSpaceRepository;
import com.fleencorp.feen.service.chat.space.ChatSpaceService;
import com.fleencorp.feen.service.chat.space.member.ChatSpaceMemberService;
import com.fleencorp.feen.service.impl.chat.space.ChatSpaceUpdateService;
import com.fleencorp.feen.service.user.MemberService;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.fleencorp.base.util.ExceptionUtil.checkIsTrue;
import static com.fleencorp.base.util.FleenUtil.handleSearchResult;
import static com.fleencorp.base.util.FleenUtil.toSearchResult;
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

  private final ChatSpaceService chatSpaceService;
  private final ChatSpaceUpdateService chatSpaceUpdateService;
  private final MemberService memberService;
  private final ChatSpaceMemberRepository chatSpaceMemberRepository;
  private final ChatSpaceRepository chatSpaceRepository;
  private final Localizer localizer;
  private final ChatSpaceMemberMapper chatSpaceMemberMapper;

  /**
   * Constructs a {@code ChatSpaceServiceImpl} with the specified dependencies.
   *
   * <p>This constructor initializes the service with all required components for managing
   * chat spaces, including repositories, mappers, and various utility services. It also injects
   * configuration values like the delegated authority email.</p>
   *
   * @param chatSpaceService handles chat spaces
   * @param chatSpaceUpdateService handles updates to chat spaces
   * @param memberService the service for managing members and profile information
   * @param chatSpaceMemberRepository repository for managing chat space members
   * @param chatSpaceRepository repository for chat space entities
   * @param localizer provides localized responses for API operations
   * @param chatSpaceMemberMapper maps chat space member entities to response models
   */
  public ChatSpaceMemberServiceImpl(
      final ChatSpaceService chatSpaceService,
      final ChatSpaceUpdateService chatSpaceUpdateService,
      final MemberService memberService,
      final ChatSpaceMemberRepository chatSpaceMemberRepository,
      final ChatSpaceRepository chatSpaceRepository,
      final Localizer localizer,
      final ChatSpaceMemberMapper chatSpaceMemberMapper) {
    this.chatSpaceService = chatSpaceService;
    this.chatSpaceUpdateService = chatSpaceUpdateService;
    this.memberService = memberService;
    this.chatSpaceRepository = chatSpaceRepository;
    this.chatSpaceMemberRepository = chatSpaceMemberRepository;
    this.localizer = localizer;
    this.chatSpaceMemberMapper = chatSpaceMemberMapper;
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
  public ChatSpaceMemberSearchResult findChatSpaceMembers(final Long chatSpaceId, final ChatSpaceMemberSearchRequest searchRequest, final FleenUser user) {
    // Retrieve the chat space
    final ChatSpace chatSpace = chatSpaceService.findChatSpace(chatSpaceId);
    final Page<ChatSpaceMember> page;

    // Check if the search request includes a member's name
    if (nonNull(searchRequest.getMemberName())) {
      // Find members by chat space and member name with pagination
      page = chatSpaceMemberRepository.findByChatSpaceAndMemberName(ChatSpace.of(chatSpaceId), searchRequest.getMemberName(), searchRequest.getPage());
    } else {
      // Find all members by chat space with pagination
      page = chatSpaceMemberRepository.findByChatSpace(ChatSpace.of(chatSpaceId), searchRequest.getPage());
    }

    // Convert the chat space members to response views
    final List<ChatSpaceMemberResponse> views = chatSpaceMemberMapper.toChatSpaceMemberResponses(page.getContent(), chatSpace);
    // Return a search result view with the chat space member responses and pagination details
    return handleSearchResult(
      page,
      localizer.of(ChatSpaceMemberSearchResult.of(toSearchResult(views, page))),
      localizer.of(EmptyChatSpaceMemberSearchResult.of(toSearchResult(List.of(), page)))
    );
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
  public UpgradeChatSpaceMemberToAdminResponse upgradeChatSpaceMemberToAdmin(final Long chatSpaceId, final UpgradeChatSpaceMemberToAdminDto upgradeChatSpaceMemberToAdminDto, final FleenUser user)
    throws ChatSpaceNotFoundException, ChatSpaceMemberNotFoundException, NotAnAdminOfChatSpaceException,
      FailedOperationException {
    // Get the chat space member id
    final Long chatSpaceMemberId = upgradeChatSpaceMemberToAdminDto.getChatSpaceMemberId();
    // Get the chat space and chat space member details
    final ChatSpaceAndMemberDetailsHolder chatSpaceAndMemberDetailsHolder = getChatSpaceAndMemberDetails(chatSpaceId, chatSpaceMemberId, user);
    // Get the chat space member
    final ChatSpaceMember chatSpaceMember = chatSpaceAndMemberDetailsHolder.member();

    // Upgrade the member's role to admin
    chatSpaceMember.upgradeRole();
    // Save the updated chat space member information to the repository
    chatSpaceMemberRepository.save(chatSpaceMember);
    // Get the role info
    final ChatSpaceMemberRoleInfo roleInfo = chatSpaceMemberMapper.toMemberRoleInfo(chatSpaceMember.getRole());
    // Create the response
    final UpgradeChatSpaceMemberToAdminResponse upgradeChatSpaceMemberToAdminResponse = UpgradeChatSpaceMemberToAdminResponse.of(chatSpaceId, chatSpaceMemberId, roleInfo);
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
  public DowngradeChatSpaceAdminToMemberResponse downgradeChatSpaceAdminToMember(final Long chatSpaceId, final DowngradeChatSpaceAdminToMemberDto downgradeChatSpaceAdminToMemberDto, final FleenUser user)
    throws ChatSpaceNotFoundException, ChatSpaceMemberNotFoundException, NotAnAdminOfChatSpaceException,
      FailedOperationException  {
    // Get the chat space member id
    final Long chatSpaceMemberId = downgradeChatSpaceAdminToMemberDto.getChatSpaceMemberId();
    // Get the chat space and chat space member details
    final ChatSpaceAndMemberDetailsHolder chatSpaceAndMemberDetailsHolder = getChatSpaceAndMemberDetails(chatSpaceId, chatSpaceMemberId, user);
    // Get the chat space member
    final ChatSpaceMember chatSpaceMember = chatSpaceAndMemberDetailsHolder.member();

    // Downgrade the admin role to a member
    chatSpaceMember.downgradeRole();
    // Save the updated chat space member information to the repository
    chatSpaceMemberRepository.save(chatSpaceMember);
    // Get chat space member role
    final ChatSpaceMemberRoleInfo roleInfo = chatSpaceMemberMapper.toMemberRoleInfo(chatSpaceMember.getRole());
    // Return a localized response confirming the downgrade
    return localizer.of(DowngradeChatSpaceAdminToMemberResponse.of(chatSpaceId, chatSpaceMemberId, roleInfo));
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
  private ChatSpaceAndMemberDetailsHolder getChatSpaceAndMemberDetails(final Long chatSpaceId, final Long chatSpaceMemberId, final FleenUser user) {
    // Find the chat space by its ID or throw an exception if not found
    final ChatSpace chatSpace = chatSpaceService.findChatSpace(chatSpaceId);
    // Admin cannot add self
    verifyAdminCannotAddOrUpdateSelf(chatSpace, user.getId());
    // Verify that the user is the creator or an admin of the chat space
    chatSpaceService.verifyCreatorOrAdminOfSpace(chatSpace, user);

    // Find the chat space admin to be downgraded or throw an exception if not found
    final ChatSpaceMember chatSpaceMember = chatSpaceMemberRepository.findByChatSpaceMemberAndChatSpace(ChatSpaceMember.of(chatSpaceMemberId), chatSpace)
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
  public AddChatSpaceMemberResponse addMember(final Long chatSpaceId, final AddChatSpaceMemberDto addChatSpaceMemberDto, final FleenUser user)
    throws ChatSpaceNotFoundException, MemberNotFoundException, NotAnAdminOfChatSpaceException,
      FailedOperationException  {
    // Get the member id
    final Long memberId = addChatSpaceMemberDto.getMemberId();
    // Find the chat space by its ID
    final ChatSpace chatSpace = chatSpaceService.findChatSpace(chatSpaceId);
    // Admin cannot add self
    verifyAdminCannotAddOrUpdateSelf(chatSpace, user.getId());
    // Validate if the user is the creator or an admin of the chat space
    chatSpaceService.verifyCreatorOrAdminOfSpace(chatSpace, user);
    // Find the member to be added using the provided member ID
    final Member member = memberService.findMember(memberId);
    // Find or create the chat space member object
    final ChatSpaceMember chatSpaceMember = findOrCreateChatMember(chatSpace, member);
    // Approve chat space member join status since the request is been made by the admin
    approveChatMemberJoinStatusAndSaveIfNew(chatSpaceMember);
    // Set an admin comment for the chat space member
    chatSpaceMember.setSpaceAdminComment(addChatSpaceMemberDto.getComment());
    // Notify the chat space update service about the change
    notifyChatSpaceUpdateService(chatSpaceMember, chatSpace, member);
    // Create the response
    final AddChatSpaceMemberResponse addChatSpaceMemberResponse = AddChatSpaceMemberResponse.of(chatSpaceId, memberId);
    // Return a localized response indicating success
    return localizer.of(addChatSpaceMemberResponse);
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
  public RemoveChatSpaceMemberResponse removeMember(final Long chatSpaceId, final RemoveChatSpaceMemberDto removeChatSpaceMemberDto, final FleenUser user)
    throws ChatSpaceNotFoundException, NotAnAdminOfChatSpaceException, ChatSpaceMemberNotFoundException,
      FailedOperationException {
    // Retrieve the chat space using the provided chatSpaceId
    final ChatSpace chatSpace = chatSpaceService.findChatSpace(chatSpaceId);
    // Admin cannot add self
    verifyAdminCannotAddOrUpdateSelf(chatSpace, user.getId());
    // Verify that the user is the creator or an admin of the chat space
    chatSpaceService.verifyCreatorOrAdminOfSpace(chatSpace, user);
    // Remove the user from the chat space
    final ChatSpaceMember chatSpaceMember = removeChatSpaceMember(chatSpace, removeChatSpaceMemberDto.getMemberId());
    // Return a localized response indicating the member removal was successful
    return localizer.of(RemoveChatSpaceMemberResponse.of(chatSpaceId, chatSpaceMember.getChatSpaceMemberId()));
  }

  /**
   * Removes a member from the specified chat space or allows the member to leave.
   *
   * <p>This method handles the removal of a member from a chat space based on their member ID.
   * It first finds the member within the chat space, deletes the member entry from the repository,
   * updates the total number of members in the chat space, and notifies the chat space update service
   * of the member's removal.</p>
   *
   * @param chatSpace the chat space from which the member is being removed
   * @param memberId  the ID of the member to be removed
   * @throws ChatSpaceMemberNotFoundException if the chat space member cannot be found
   * @throws FailedOperationException if there is an invalid input
   */
  @Override
  @Transactional
  public void leaveChatSpace(final ChatSpace chatSpace, final Long memberId)
      throws ChatSpaceMemberNotFoundException, FailedOperationException {
    // Locate the chat space member to be removed using the member ID from the DTO
    final ChatSpaceMember chatSpaceMember = findByChatSpaceAndMember(chatSpace, Member.of(memberId));
    // Admin is not allow to leave chat space
    checkIsTrue(chatSpaceMember.isNotTheOwner(chatSpace.getMemberId()), FailedOperationException::new);
    // Allow the member to leave chat space
    chatSpaceMember.leave();
    // Save the member
    chatSpaceMemberRepository.save(chatSpaceMember);
    // Decrease total members and save chat space
    decreaseTotalMembersAndSave(chatSpace);
    // Create external request
    final RemoveChatSpaceMemberRequest removeChatSpaceMemberRequest = RemoveChatSpaceMemberRequest.of(chatSpace.getExternalIdOrName(), chatSpaceMember.getExternalIdOrName());
    // Notify the chat space update service about the removal
    chatSpaceUpdateService.removeMember(removeChatSpaceMemberRequest);
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
   * @param memberId the ID of the member to be removed
   * @return the {@link ChatSpaceMember} that was removed
   * @throws ChatSpaceMemberNotFoundException if the member is not found in the chat space
   */
  protected ChatSpaceMember removeChatSpaceMember(final ChatSpace chatSpace, final Long memberId) {
    // Locate the chat space member to be removed using the member ID from the DTO
    final ChatSpaceMember chatSpaceMember = findByChatSpaceAndMember(chatSpace, Member.of(memberId));
    // Mark the member as removed from the chat space
    chatSpaceMember.markAsRemoved();
    // Save the chat space member
    chatSpaceMemberRepository.save(chatSpaceMember);
    // Decrease total members and save chat space
    decreaseTotalMembersAndSave(chatSpace);
    // Create external removal request
    final RemoveChatSpaceMemberRequest removeChatSpaceMemberRequest = RemoveChatSpaceMemberRequest.of(
      chatSpace.getExternalIdOrName(),
      chatSpaceMember.getExternalIdOrName()
    );
    // Notify the chat space update service about the removal
    chatSpaceUpdateService.removeMember(removeChatSpaceMemberRequest);
    // Return deleted chat space member details
    return chatSpaceMember;
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
    return chatSpaceMemberRepository.findByChatSpaceAndMember(chatSpace, chatSpaceMemberId)
      .orElseThrow(ChatSpaceMemberNotFoundException.of(chatSpaceMemberId));
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
  public ChatSpaceMember findByChatSpaceAndMember(final ChatSpace chatSpace, final Member member) throws ChatSpaceMemberNotFoundException {
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
  @Override
  public void notifyChatSpaceUpdateService(final ChatSpaceMember chatSpaceMember, final ChatSpace chatSpace, final Member member) {
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
    return chatSpaceMemberRepository.findByChatSpaceAndMember(chatSpace, member)
      .orElse(ChatSpaceMember.of(chatSpace, member, null));
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
        chatSpaceMemberRepository.save(chatSpaceMember);
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
      final Optional<ChatSpaceMember> existingChatSpaceMember = chatSpaceMemberRepository.findByChatSpaceAndMemberAndStatus(stream.getChatSpace(), streamAttendee.getMember(), ChatSpaceRequestToJoinStatus.approved());
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
   * @param chatSpace The {@link ChatSpace} in which the verification is being performed.
   * @param memberId The ID of the member being checked.
   * @throws FailedOperationException if the member is the owner of the chat space.
   */
  protected void verifyAdminCannotAddOrUpdateSelf(final ChatSpace chatSpace, final Long memberId) {
    if (chatSpace.isOrganizer(memberId)) {
      throw new FailedOperationException();
    }
  }


}
