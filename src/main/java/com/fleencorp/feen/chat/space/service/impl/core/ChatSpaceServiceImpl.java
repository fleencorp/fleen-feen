package com.fleencorp.feen.chat.space.service.impl.core;

import com.fleencorp.feen.chat.space.constant.member.ChatSpaceMemberRole;
import com.fleencorp.feen.chat.space.exception.core.ChatSpaceAlreadyDeletedException;
import com.fleencorp.feen.chat.space.exception.core.ChatSpaceNotAnAdminException;
import com.fleencorp.feen.chat.space.exception.core.ChatSpaceNotFoundException;
import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.chat.space.model.domain.ChatSpaceMember;
import com.fleencorp.feen.chat.space.model.dto.core.CreateChatSpaceDto;
import com.fleencorp.feen.chat.space.model.dto.core.UpdateChatSpaceDto;
import com.fleencorp.feen.chat.space.model.dto.core.UpdateChatSpaceStatusDto;
import com.fleencorp.feen.chat.space.model.info.core.ChatSpaceStatusInfo;
import com.fleencorp.feen.chat.space.model.request.external.core.CreateChatSpaceRequest;
import com.fleencorp.feen.chat.space.model.request.external.core.DeleteChatSpaceRequest;
import com.fleencorp.feen.chat.space.model.request.external.core.UpdateChatSpaceRequest;
import com.fleencorp.feen.chat.space.model.response.CreateChatSpaceResponse;
import com.fleencorp.feen.chat.space.model.response.DeleteChatSpaceResponse;
import com.fleencorp.feen.chat.space.model.response.core.ChatSpaceResponse;
import com.fleencorp.feen.chat.space.model.response.update.UpdateChatSpaceResponse;
import com.fleencorp.feen.chat.space.model.response.update.UpdateChatSpaceStatusResponse;
import com.fleencorp.feen.chat.space.repository.ChatSpaceRepository;
import com.fleencorp.feen.chat.space.service.core.ChatSpaceService;
import com.fleencorp.feen.chat.space.service.member.ChatSpaceMemberOperationsService;
import com.fleencorp.feen.chat.space.service.update.ChatSpaceUpdateService;
import com.fleencorp.feen.chat.space.util.ChatSpaceServiceUtil;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.common.model.info.IsDeletedInfo;
import com.fleencorp.feen.mapper.common.UnifiedMapper;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static com.fleencorp.base.util.ExceptionUtil.checkIsNullAny;
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
public class ChatSpaceServiceImpl implements ChatSpaceService {

  private final ChatSpaceUpdateService chatSpaceUpdateService;
  private final ChatSpaceMemberOperationsService chatSpaceMemberOperationsService;
  private final ChatSpaceRepository chatSpaceRepository;
  private final UnifiedMapper unifiedMapper;
  private final Localizer localizer;

  /**
   * Constructs a new {@code ChatSpaceServiceImpl}, the core service for managing chat space entities and their lifecycle.
   *
   * @param chatSpaceUpdateService the service responsible for updating chat space information
   * @param chatSpaceMemberOperationsService the service for handling operations related to chat space members
   * @param chatSpaceRepository the repository for accessing chat space data from the database
   * @param unifiedMapper the utility for converting between entities and data transfer objects
   * @param localizer the component used for providing localized messages
   */
  public ChatSpaceServiceImpl(
      final ChatSpaceUpdateService chatSpaceUpdateService,
      final ChatSpaceMemberOperationsService chatSpaceMemberOperationsService,
      final ChatSpaceRepository chatSpaceRepository,
      final UnifiedMapper unifiedMapper,
      final Localizer localizer) {
    this.chatSpaceUpdateService = chatSpaceUpdateService;
    this.chatSpaceMemberOperationsService = chatSpaceMemberOperationsService;
    this.chatSpaceRepository = chatSpaceRepository;
    this.localizer = localizer;
    this.unifiedMapper = unifiedMapper;
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
  @Override
  public ChatSpace findChatSpace(final Long chatSpaceId) throws ChatSpaceNotFoundException {
    // Attempt to find the chat space by its ID in the repository
    return chatSpaceRepository.findById(chatSpaceId)
      // If not found, throw an exception with the chat space ID
      .orElseThrow(ChatSpaceNotFoundException.of(chatSpaceId));
  }

  /**
   * Creates a chat space using the provided DTO and user information.
   *
   * <p>This method saves a chat space entity, sends a request to create the chat space,
   * and returns a localized response.</p>
   *
   * @param createChatSpaceDto DTO containing the chat space details.
   * @param user The user creating the chat space.
   * @return A response with details of the created chat space.
   */
  @Override
  @Transactional
  public CreateChatSpaceResponse createChatSpace(final CreateChatSpaceDto createChatSpaceDto, final RegisteredUser user) {
    ChatSpace chatSpace = createChatSpaceDto.toChatSpace(user.toMember());
    chatSpace = chatSpaceRepository.save(chatSpace);
    final CreateChatSpaceRequest createChatSpaceRequest = getCreateChatSpaceRequest(createChatSpaceDto, user);

    final ChatSpaceMember chatSpaceMember = ChatSpaceMember.ofOrganizer(chatSpace, user.toMember());
    chatSpaceMemberOperationsService.save(chatSpaceMember);
    increaseTotalMembersAndSave(chatSpace);

    chatSpaceUpdateService.createChatSpace(chatSpace, createChatSpaceRequest);

    final ChatSpaceResponse chatSpaceResponse = unifiedMapper.toChatSpaceResponseByAdminUpdate(chatSpace);
    final CreateChatSpaceResponse createChatSpaceResponse = CreateChatSpaceResponse.of(chatSpaceResponse);

    return localizer.of(createChatSpaceResponse);
  }

  /**
   * Constructs a request to create a new chat space using the provided DTO and user information.
   *
   * @param createChatSpaceDto The DTO containing the necessary details to create a new chat space (title, description, and guidelines).
   * @param user The user who is initiating the chat space creation, used to retrieve the email address.
   * @return A {@link CreateChatSpaceRequest} object containing the chat space creation details.
   * @throws FailedOperationException If any of the provided parameters are null.
   */
  protected static CreateChatSpaceRequest getCreateChatSpaceRequest(final CreateChatSpaceDto createChatSpaceDto, final RegisteredUser user) {
    // Validate that neither the DTO nor the user is null
    checkIsNullAny(List.of(createChatSpaceDto, user), FailedOperationException::new);

    // Create and return a create chat space request object using the DTO details and user email address
    return CreateChatSpaceRequest.of(
      createChatSpaceDto.getTitle(),
      createChatSpaceDto.getDescription(),
      createChatSpaceDto.getGuidelinesOrRules(),
      user.getEmailAddress()
    );
  }

  /**
   * Updates an existing chat space with new details.
   *
   * <p>This method fetches a chat space by its ID, updates its details based on the provided DTO,
   * and persists the changes to the repository. It also sends an update request to an external
   * chat service to synchronize the changes.</p>
   *
   * @param chatSpaceId The ID of the chat space to be updated.
   * @param updateChatSpaceDto DTO containing the updated chat space details.
   * @param user The user performing the update operation.
   * @return A response containing the updated chat space details.
   * @throws ChatSpaceNotFoundException if the chat space with the specified ID is not found.
   * @throws ChatSpaceAlreadyDeletedException if the chat space has already been deleted.
   * @throws ChatSpaceNotAnAdminException if the user is not authorized to disable the chat space.
   * @throws FailedOperationException if there is an invalid input
   */
  @Override
  @Transactional
  public UpdateChatSpaceResponse updateChatSpace(final Long chatSpaceId, final UpdateChatSpaceDto updateChatSpaceDto, final RegisteredUser user)
    throws ChatSpaceNotFoundException, ChatSpaceAlreadyDeletedException, ChatSpaceNotAnAdminException,
      FailedOperationException {
    // Find the chat space by ID or throw an exception if it doesn't exist
    final ChatSpace chatSpace = findChatSpace(chatSpaceId);
    // Verify if the chat space has already been deleted and that the user is the creator or an admin of the chat space
    verifyIfChatSpaceAlreadyDeletedAndCreatorOrAdminOfSpace(chatSpace, user);
    // Update the chat space with the new details or info
    chatSpace.updateDetails(
      updateChatSpaceDto.getTitle(),
      updateChatSpaceDto.getDescription(),
      updateChatSpaceDto.getTags(),
      updateChatSpaceDto.getGuidelinesOrRules(),
      updateChatSpaceDto.getVisibility()
    );

    // Save the updated chat space entity to the repository
    chatSpaceRepository.save(chatSpace);
    // Create update chat space request and send to external service
    createAndUpdateChatSpaceInExternalService(updateChatSpaceDto, chatSpace);
    // Convert the chat space to the response
    final ChatSpaceResponse chatSpaceResponse = unifiedMapper.toChatSpaceResponseByAdminUpdate(chatSpace);
    // Create the response
    final UpdateChatSpaceResponse updateChatSpaceResponse = UpdateChatSpaceResponse.of(chatSpaceResponse);
    // Return a localized response with the updated chat space details
    return localizer.of(updateChatSpaceResponse);
  }

  /**
   * Updates an existing chat space in an external service based on the provided DTO.
   *
   * @param updateChatSpaceDto The DTO containing the updated details of the chat space, such as title, description, and guidelines.
   * @param chatSpace The internal representation of the chat space being updated.
   * @throws FailedOperationException If any of the provided parameters are null.
   */
  protected void createAndUpdateChatSpaceInExternalService(final UpdateChatSpaceDto updateChatSpaceDto, final ChatSpace chatSpace) {
    checkIsNullAny(List.of(updateChatSpaceDto, chatSpace), FailedOperationException::new);

    // Prepare the request to update the external chat space service
    final UpdateChatSpaceRequest updateChatSpaceRequest = UpdateChatSpaceRequest.of(
      chatSpace.getExternalIdOrName(),
      updateChatSpaceDto.getTitle(),
      updateChatSpaceDto.getDescription(),
      updateChatSpaceDto.getGuidelinesOrRules());
    // Call the service to update the external chat space
    chatSpaceUpdateService.updateChatSpace(updateChatSpaceRequest);
  }

  /**
   * Deletes a chat space by its ID.
   *
   * <p>This method retrieves the chat space by its ID, marks it for deletion, and
   * then saves the changes. It returns a localized response confirming the deletion.</p>
   *
   * @param chatSpaceId The ID of the chat space to be deleted.
   * @param user The user requesting the deletion operation.
   * @return A response confirming the deletion of the chat space, localized based on the user's locale.
   * @throws ChatSpaceNotFoundException if the chat space with the specified ID is not found.
   * @throws ChatSpaceAlreadyDeletedException if the chat space is already deleted
   * @throws ChatSpaceNotAnAdminException if the user is not authorized to disable the chat space.
   * @throws FailedOperationException if there is an invalid input
   */
  @Override
  @Transactional
  public DeleteChatSpaceResponse deleteChatSpace(final Long chatSpaceId, final RegisteredUser user)
      throws ChatSpaceNotFoundException, ChatSpaceAlreadyDeletedException, ChatSpaceNotAnAdminException,
        FailedOperationException {
    final ChatSpace chatSpace = findChatSpace(chatSpaceId);

    verifyIfChatSpaceAlreadyDeletedAndCreatorOrAdminOfSpace(chatSpace, user);
    chatSpace.delete();
    chatSpaceRepository.save(chatSpace);

    final IsDeletedInfo isDeletedInfo = unifiedMapper.toIsDeletedInfo(chatSpace.isDeleted());
    final DeleteChatSpaceResponse deleteChatSpaceResponse = DeleteChatSpaceResponse.of(chatSpaceId, isDeletedInfo);

    return localizer.of(deleteChatSpaceResponse);
  }

  /**
   * Deletes a chat space by an admin using its ID.
   *
   * <p>This method is intended for administrative use. It retrieves the chat space by its ID,
   * marks it for deletion, and saves the changes. Additionally, it sends a request to update
   * the external system by deleting the chat space through the `chatSpaceUpdateService`.</p>
   *
   * @param chatSpaceId The ID of the chat space to be deleted by the admin.
   * @param user The admin user performing the deletion operation.
   * @return A response confirming the deletion of the chat space, localized based on the admin locale.
   * @throws ChatSpaceNotFoundException if the chat space with the specified ID is not found.
   */
  @Override
  @Transactional
  public DeleteChatSpaceResponse deleteChatSpaceByAdmin(final Long chatSpaceId, final RegisteredUser user)
      throws ChatSpaceNotFoundException {
    final ChatSpace chatSpace = findChatSpace(chatSpaceId);

    chatSpace.delete();
    chatSpaceRepository.save(chatSpace);
    deleteChatSpaceExternally(chatSpace);

    final IsDeletedInfo isDeletedInfo = unifiedMapper.toIsDeletedInfo(chatSpace.isDeleted());
    final DeleteChatSpaceResponse deleteChatSpaceResponse = DeleteChatSpaceResponse.of(chatSpaceId, isDeletedInfo);

    return localizer.of(deleteChatSpaceResponse);
  }

  private void deleteChatSpaceExternally(final ChatSpace chatSpace) {
    final DeleteChatSpaceRequest deleteChatSpaceRequest = DeleteChatSpaceRequest.of(chatSpace.getExternalIdOrName());
    chatSpaceUpdateService.deleteChatSpace(deleteChatSpaceRequest);
  }

  /**
   * Updates the status of a chat space based on the provided information.
   *
   * <p>This method calls the internal {@code updateChatSpaceStatus} method to update the status of the chat space
   * identified by {@code chatSpaceId}. It throws several exceptions if the chat space is not found, already deleted,
   * or if the user is not an admin of the chat space.
   *
   * @param chatSpaceId the ID of the chat space whose status is being updated
   * @param updateChatSpaceStatusDto the data transfer object containing the new status for the chat space
   * @param user the user attempting to update the chat space status, must be an admin of the chat space
   * @return an {@code UpdateChatSpaceStatusResponse} containing the result of the status update
   * @throws ChatSpaceNotFoundException if the chat space with the given ID does not exist
   * @throws ChatSpaceAlreadyDeletedException if the chat space has already been deleted
   * @throws ChatSpaceNotAnAdminException if the user is not an admin of the chat space
   * @throws FailedOperationException if the operation fails for any other reason
   */
  @Override
  @Transactional
  public UpdateChatSpaceStatusResponse updateChatSpaceStatus(final Long chatSpaceId, final UpdateChatSpaceStatusDto updateChatSpaceStatusDto, final RegisteredUser user)
    throws ChatSpaceNotFoundException, ChatSpaceAlreadyDeletedException, ChatSpaceNotAnAdminException,
      FailedOperationException {
    return updateChatSpaceStatus(chatSpaceId, updateChatSpaceStatusDto.getStatus(), user);
  }

  /**
   * Toggles the status of a chat space between enabled and disabled.
   *
   * <p>If the chat space is not found, has already been deleted, or the user is not the creator
   * or an admin of the chat space, appropriate exceptions are thrown. The status is toggled
   * based on the provided {@code enable} flag.</p>
   *
   * @param chatSpaceId the ID of the chat space to be toggled
   * @param enable a boolean flag indicating whether to enable (true) or disable (false) the chat space
   * @param user the user performing the operation, who must be the creator or an admin of the chat space
   * @return an {@link UpdateChatSpaceStatusResponse} containing the updated status of the chat space
   * @throws ChatSpaceNotFoundException if the chat space with the provided ID is not found
   * @throws ChatSpaceAlreadyDeletedException if the chat space has already been deleted
   * @throws ChatSpaceNotAnAdminException if the user is neither the creator nor an admin of the chat space
   * @throws FailedOperationException if the operation fails for any other reason
   */
  protected UpdateChatSpaceStatusResponse updateChatSpaceStatus(final Long chatSpaceId, final boolean enable, final RegisteredUser user)
    throws ChatSpaceNotFoundException, ChatSpaceAlreadyDeletedException, ChatSpaceNotAnAdminException,
      FailedOperationException {
    final ChatSpace chatSpace = findChatSpace(chatSpaceId);
    verifyIfChatSpaceAlreadyDeletedAndCreatorOrAdminOfSpace(chatSpace, user);

    if (enable) {
      chatSpace.enable();
    } else {
      chatSpace.disable();
    }

    chatSpaceRepository.save(chatSpace);

    final ChatSpaceStatusInfo chatSpaceStatusInfo = unifiedMapper.toChatSpaceStatusInfo(chatSpace.getStatus());
    final UpdateChatSpaceStatusResponse updateChatSpaceStatusResponse = UpdateChatSpaceStatusResponse.of(chatSpaceId, chatSpaceStatusInfo);

    return localizer.of(updateChatSpaceStatusResponse);
  }

  /**
   * Validates that the provided user is either the creator or an admin of the specified chat space.
   *
   * <p>This method checks if the user is the creator of the chat space by comparing their IDs.
   * If the user is not the creator, it further checks if the user is an admin of the chat space.
   * If the user is neither the creator nor an admin, a {@link ChatSpaceNotAnAdminException} is thrown.</p>
   *
   * @param chatSpace The chat space to validate against.
   * @param member The member whose permissions are being validated.
   * @throws FailedOperationException if any of the provided values is null.
   * @throws ChatSpaceNotAnAdminException if the user is neither the creator nor an admin of the chat space.
   */
  @Override
  public boolean verifyCreatorOrAdminOfChatSpace(final ChatSpace chatSpace, final Member member)
    throws FailedOperationException, ChatSpaceNotAnAdminException {

    if (nonNull(chatSpace) && nonNull(member)) {
      return verifyCreatorOrAdminOfChatSpace(chatSpace, member.getMemberId());
    }

    throw new ChatSpaceNotAnAdminException();
  }

  /**
   * Validates that the provided user is either the creator or an admin of the specified chat space.
   *
   * <p>This method checks if the user is the creator of the chat space by comparing their IDs.
   * If the user is not the creator, it further checks if the user is an admin of the chat space.
   * If the user is neither the creator nor an admin, a {@link ChatSpaceNotAnAdminException} is thrown.</p>
   *
   * @param chatSpace The chat space to validate against.
   * @param memberId  The member id whose permissions are being validated.
   * @throws FailedOperationException       if any of the provided values is null.
   * @throws ChatSpaceNotAnAdminException if the user is neither the creator nor an admin of the chat space.
   */
  protected boolean verifyCreatorOrAdminOfChatSpace(final ChatSpace chatSpace, final Long memberId)
    throws FailedOperationException, ChatSpaceNotAnAdminException {
    checkIsNullAny(List.of(chatSpace, memberId), FailedOperationException::new);

    if (chatSpace.isOrganizer(memberId) || checkIfUserIsAnAdminInSpace(chatSpace, memberId)) {
      return true;
    }

    throw new ChatSpaceNotAnAdminException();
  }

  /**
   * Verifies whether the given member is either the creator (organizer) or an admin of the specified chat space.
   *
   * <p>The method checks for {@code null} arguments and throws a {@link FailedOperationException} if any are missing.
   * If the member is the organizer of the chat space or an admin within it, the method returns {@code true}.
   * Otherwise, a {@link ChatSpaceNotAnAdminException} may be thrown during admin validation.</p>
   *
   * @param chatSpace the chat space in which the member's role is being verified
   * @param memberId the identifier of the member whose role is being checked
   * @return {@code true} if the member is either the organizer or an admin of the chat space, {@code false} otherwise
   * @throws FailedOperationException if {@code chatSpace} or {@code memberId} is {@code null}
   * @throws ChatSpaceNotAnAdminException if the member is not an admin when admin validation is performed
   */
  @Override
  public boolean verifyCreatorOrAdminOfChatSpaceNoThrow(final ChatSpace chatSpace, final Long memberId)
      throws FailedOperationException, ChatSpaceNotAnAdminException {
    checkIsNullAny(List.of(chatSpace, memberId), FailedOperationException::new);
    return chatSpace.isOrganizer(memberId) || checkIfUserIsAnAdminInSpace(chatSpace, memberId);
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
   * @throws ChatSpaceNotAnAdminException If the user is not the creator or an admin of the chat space.
   */
  protected void verifyIfChatSpaceAlreadyDeletedAndCreatorOrAdminOfSpace(final ChatSpace chatSpace, final RegisteredUser user)
    throws ChatSpaceAlreadyDeletedException, ChatSpaceNotAnAdminException, FailedOperationException {
    // Verify if the chat space has already been deleted
    chatSpace.checkNotDeleted();
    // Verify that the user is the creator or an admin of the chat space
    verifyCreatorOrAdminOfChatSpace(chatSpace, user.toMember());
  }

  /**
   * Checks if the specified user is an admin in the provided chat space.
   *
   * <p>This method retrieves all members of the chat space with the admin role and extracts their IDs.
   * It then checks if the given user is among those members. If the user is found in the list of admin
   * member IDs, the method returns true; otherwise, it returns false.</p>
   *
   * @param chatSpace The chat space to check the user's admin status against.
   * @param memberId The member id whose admin status is being checked.
   * @return {@code true} if the user is an admin in the chat space; {@code false} otherwise.
   */
  protected boolean checkIfUserIsAnAdminInSpace(final ChatSpace chatSpace, final Long memberId) {
    // Retrieve all members of the chat space with the admin role
    final Set<ChatSpaceMember> chatSpaceMembers = chatSpaceMemberOperationsService.findByChatSpaceAndRole(chatSpace, ChatSpaceMemberRole.ADMIN);
    // Extract the IDs of the admin members
    final Set<Long> spaceMemberIds = ChatSpaceServiceUtil.extractMemberIds(chatSpaceMembers);
    // Check if the user is among the admin members
    return ChatSpaceServiceUtil.isSpaceMemberAnAdmin(spaceMemberIds, memberId);
  }

  @Override
  public void increaseTotalMembersAndSave(final ChatSpace chatSpace) {
    chatSpaceRepository.incrementTotalMembers(chatSpace.getChatSpaceId());
  }

  @Override
  public Boolean existsByMembers(final Member viewer, final Member target) {
    return chatSpaceMemberOperationsService.existsByMembers(viewer.getMemberId(), target.getMemberId());
  }

  public Integer incrementLikeCount(final Long chatSpaceId) {
    chatSpaceRepository.incrementAndGetLikeCount(chatSpaceId);
    return chatSpaceRepository.getLikeCount(chatSpaceId);
  }

  private Integer decrementLikeCount(final Long chatSpaceId) {
    chatSpaceRepository.decrementAndGetLikeCount(chatSpaceId);
    return chatSpaceRepository.getLikeCount(chatSpaceId);
  }

  @Override
  @Transactional
  public Integer updateLikeCount(final Long chatSpaceId, final boolean isLiked) {
    return isLiked ? incrementLikeCount(chatSpaceId) : decrementLikeCount(chatSpaceId);
  }

  private Integer incrementBookmarkCount(final Long chatSpaceId) {
    chatSpaceRepository.incrementAndBookmarkCount(chatSpaceId);
    return chatSpaceRepository.getBookmarkCount(chatSpaceId);
  }

  private Integer decrementBookmarkCount(final Long chatSpaceId) {
    chatSpaceRepository.decrementAndGetBookmarkCount(chatSpaceId);
    return chatSpaceRepository.getBookmarkCount(chatSpaceId);
  }

  @Override
  @Transactional
  public Integer updateBookmarkCount(final Long chatSpaceId, final boolean increment) {
    return increment
      ? incrementBookmarkCount(chatSpaceId)
      : decrementBookmarkCount(chatSpaceId);
  }
}
