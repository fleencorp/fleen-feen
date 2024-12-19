package com.fleencorp.feen.service.impl.chat.space.member;

import com.fleencorp.base.service.i18n.LocalizedResponse;
import com.fleencorp.feen.constant.chat.space.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.constant.chat.space.member.ChatSpaceMemberRole;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.chat.space.ChatSpaceNotFoundException;
import com.fleencorp.feen.exception.chat.space.NotAnAdminOfChatSpaceException;
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
import com.fleencorp.feen.model.info.chat.space.ChatSpaceMemberRoleInfo;
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
import com.fleencorp.feen.repository.user.MemberRepository;
import com.fleencorp.feen.service.chat.space.ChatSpaceService;
import com.fleencorp.feen.service.chat.space.member.ChatSpaceMemberService;
import com.fleencorp.feen.service.impl.chat.space.ChatSpaceUpdateService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.fleencorp.base.util.ExceptionUtil.checkIsNullAny;
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

  private final ChatSpaceUpdateService chatSpaceUpdateService;
  private final ChatSpaceMemberRepository chatSpaceMemberRepository;
  private final ChatSpaceRepository chatSpaceRepository;
  private final MemberRepository memberRepository;
  private final LocalizedResponse localizedResponse;
  private final ChatSpaceMemberMapper chatSpaceMemberMapper;

  /**
   * Constructs a {@code ChatSpaceServiceImpl} with the specified dependencies.
   *
   * <p>This constructor initializes the service with all required components for managing
   * chat spaces, including repositories, mappers, and various utility services. It also injects
   * configuration values like the delegated authority email.</p>
   *
   * @param chatSpaceUpdateService handles updates to chat spaces.
   * @param chatSpaceMemberRepository repository for managing chat space members.
   * @param chatSpaceRepository repository for chat space entities.
   * @param memberRepository repository for member-related data.
   * @param localizedResponse provides localized responses for API operations.
   * @param chatSpaceMemberMapper maps chat space member entities to response models.
   */
  public ChatSpaceMemberServiceImpl(

      final ChatSpaceUpdateService chatSpaceUpdateService,
      final ChatSpaceMemberRepository chatSpaceMemberRepository,
      final ChatSpaceRepository chatSpaceRepository,
      final MemberRepository memberRepository,
      final LocalizedResponse localizedResponse,
      final ChatSpaceMemberMapper chatSpaceMemberMapper) {
    this.chatSpaceUpdateService = chatSpaceUpdateService;
    this.chatSpaceRepository = chatSpaceRepository;
    this.chatSpaceMemberRepository = chatSpaceMemberRepository;
    this.memberRepository = memberRepository;
    this.localizedResponse = localizedResponse;
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
    final ChatSpace chatSpace = findChatSpace(chatSpaceId);
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
      localizedResponse.of(ChatSpaceMemberSearchResult.of(toSearchResult(views, page))),
      localizedResponse.of(EmptyChatSpaceMemberSearchResult.of(toSearchResult(List.of(), page)))
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
   */
  @Override
  @Transactional
  public UpgradeChatSpaceMemberToAdminResponse upgradeChatSpaceMemberToAdmin(final Long chatSpaceId, final UpgradeChatSpaceMemberToAdminDto upgradeChatSpaceMemberToAdminDto, final FleenUser user) {
    // Find the chat space by its ID or throw an exception if not found
    final ChatSpace chatSpace = findChatSpace(chatSpaceId);
    // Verify that the user is the creator or an admin of the chat space
    verifyCreatorOrAdminOfSpace(chatSpace, user);

    // Find the chat space member to be upgraded or throw an exception if not found
    final ChatSpaceMember chatSpaceMember = chatSpaceMemberRepository
      .findByChatSpaceMemberAndChatSpace(ChatSpaceMember.of(upgradeChatSpaceMemberToAdminDto.getActualChatSpaceMemberId()), chatSpace)
      .orElseThrow(ChatSpaceMemberNotFoundException.of(upgradeChatSpaceMemberToAdminDto.getActualChatSpaceMemberId()));

    // Upgrade the member's role to admin
    chatSpaceMember.upgradeRole();
    // Save the updated chat space member information to the repository
    chatSpaceMemberRepository.save(chatSpaceMember);
    // Get the c
    final ChatSpaceMemberRoleInfo roleInfo = chatSpaceMemberMapper.toRole(chatSpaceMember);
    // Return a localized response confirming the upgrade
    return localizedResponse.of(UpgradeChatSpaceMemberToAdminResponse.of(chatSpaceId, upgradeChatSpaceMemberToAdminDto.getActualChatSpaceMemberId(), roleInfo));
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
   */
  @Override
  @Transactional
  public DowngradeChatSpaceAdminToMemberResponse downgradeChatSpaceAdminToMember(final Long chatSpaceId, final DowngradeChatSpaceAdminToMemberDto downgradeChatSpaceAdminToMemberDto, final FleenUser user) {
    // Find the chat space by its ID or throw an exception if not found
    final ChatSpace chatSpace = findChatSpace(chatSpaceId);
    // Verify that the user is the creator or an admin of the chat space
    verifyCreatorOrAdminOfSpace(chatSpace, user);

    // Find the chat space admin to be downgraded or throw an exception if not found
    final ChatSpaceMember chatSpaceMember = chatSpaceMemberRepository
      .findByChatSpaceMemberAndChatSpace(ChatSpaceMember.of(downgradeChatSpaceAdminToMemberDto.getActualChatSpaceMemberId()), chatSpace)
      .orElseThrow(ChatSpaceMemberNotFoundException.of(downgradeChatSpaceAdminToMemberDto.getActualChatSpaceMemberId()));
    // Downgrade the admin role to a member
    chatSpaceMember.downgradeRole();
    // Save the updated chat space member information to the repository
    chatSpaceMemberRepository.save(chatSpaceMember);
    // Get chat space member role
    final ChatSpaceMemberRoleInfo roleInfo = chatSpaceMemberMapper.toRole(chatSpaceMember);
    // Return a localized response confirming the downgrade
    return localizedResponse.of(DowngradeChatSpaceAdminToMemberResponse.of(chatSpaceId, downgradeChatSpaceAdminToMemberDto.getActualChatSpaceMemberId(), roleInfo));
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
   */
  @Override
  @Transactional
  public AddChatSpaceMemberResponse addMember(final Long chatSpaceId, final AddChatSpaceMemberDto addChatSpaceMemberDto, final FleenUser user) {
    // Find the chat space by its ID
    final ChatSpace chatSpace = findChatSpace(chatSpaceId);
    // Validate if the user is the creator or an admin of the chat space
    verifyCreatorOrAdminOfSpace(chatSpace, user);
    // Find the member to be added using the provided member ID
    final Member member = findMember(addChatSpaceMemberDto.getActualMemberId());
    // Find or create the chat space member object
    final ChatSpaceMember chatSpaceMember = findOrCreateChatMember(chatSpace, member);
    // Approve chat space member join status since the request is been made by the admin
    approveChatMemberJoinStatusAndSaveIfNew(chatSpaceMember);
    // Set an admin comment for the chat space member
    chatSpaceMember.setSpaceAdminComment(addChatSpaceMemberDto.getComment());
    // Notify the chat space update service about the change
    notifyChatSpaceUpdateService(chatSpaceMember, chatSpace, member);
    // Return a localized response indicating success
    return localizedResponse.of(AddChatSpaceMemberResponse.of(chatSpaceId, chatSpaceMember.getChatSpaceMemberId()));
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
   */
  @Override
  @Transactional
  public RemoveChatSpaceMemberResponse removeMember(final Long chatSpaceId, final RemoveChatSpaceMemberDto removeChatSpaceMemberDto, final FleenUser user) {
    // Retrieve the chat space using the provided chatSpaceId
    final ChatSpace chatSpace = findChatSpace(chatSpaceId);
    // Verify that the user is the creator or an admin of the chat space
    verifyCreatorOrAdminOfSpace(chatSpace, user);
    // Remove the user from the chat space
    final ChatSpaceMember chatSpaceMember = leaveChatSpaceOrRemoveChatSpaceMember(chatSpace, removeChatSpaceMemberDto.getActualMemberId());
    // Return a localized response indicating the member removal was successful
    return localizedResponse.of(RemoveChatSpaceMemberResponse.of(chatSpaceId, chatSpaceMember.getChatSpaceMemberId()));
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
   * @param memberId the ID of the member to be removed
   * @return the deleted {@link ChatSpaceMember}
   */
  @Override
  public ChatSpaceMember leaveChatSpaceOrRemoveChatSpaceMember(final ChatSpace chatSpace, final Long memberId) {
    // Locate the chat space member to be removed using the member ID from the DTO
    final ChatSpaceMember chatSpaceMember = findChatSpaceMember(chatSpace, Member.of(memberId));
    // Remove the member from the chat space repository
    chatSpaceMemberRepository.delete(chatSpaceMember);
    // Decrease total members and save chat space
    decreaseTotalMembersAndSave(chatSpace);

    // Notify the chat space update service about the removal
    chatSpaceUpdateService.removeMember(RemoveChatSpaceMemberRequest.of(chatSpace.getExternalIdOrName(), chatSpaceMember.getExternalIdOrName()));
    // Return deleted chat space member details
    return chatSpaceMember;
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
    chatSpace.decreaseTotalMembers();
    // Save chat space to repository
    chatSpaceRepository.save(chatSpace);
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
    if (nonNull(stream.getChatSpace()) && nonNull(stream.getChatSpaceId())) {
      // Find if the attendee is a member of the chat space
      final Optional<ChatSpaceMember> existingChatSpaceMember = chatSpaceMemberRepository.findByChatSpaceAndMemberAndStatus(stream.getChatSpace(), streamAttendee.getMember(), ChatSpaceRequestToJoinStatus.approved());
      // Return true if a member exists, otherwise false
      return existingChatSpaceMember.isPresent();
    }
    // Return false if there's no chat space or no valid chat space ID
    return false;
  }

}
