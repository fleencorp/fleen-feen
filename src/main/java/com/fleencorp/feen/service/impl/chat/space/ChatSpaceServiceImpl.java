package com.fleencorp.feen.service.impl.chat.space;

import com.fleencorp.feen.constant.chat.space.member.ChatSpaceMemberRole;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.chat.space.ChatSpaceNotFoundException;
import com.fleencorp.feen.exception.chat.space.core.ChatSpaceAlreadyDeletedException;
import com.fleencorp.feen.exception.chat.space.core.NotAnAdminOfChatSpaceException;
import com.fleencorp.feen.mapper.chat.ChatSpaceMapper;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.chat.ChatSpaceMember;
import com.fleencorp.feen.model.dto.chat.CreateChatSpaceDto;
import com.fleencorp.feen.model.dto.chat.UpdateChatSpaceDto;
import com.fleencorp.feen.model.request.chat.space.CreateChatSpaceRequest;
import com.fleencorp.feen.model.request.chat.space.DeleteChatSpaceRequest;
import com.fleencorp.feen.model.request.chat.space.UpdateChatSpaceRequest;
import com.fleencorp.feen.model.response.chat.space.CreateChatSpaceResponse;
import com.fleencorp.feen.model.response.chat.space.DeleteChatSpaceResponse;
import com.fleencorp.feen.model.response.chat.space.base.ChatSpaceResponse;
import com.fleencorp.feen.model.response.chat.space.update.DisableChatSpaceResponse;
import com.fleencorp.feen.model.response.chat.space.update.EnableChatSpaceResponse;
import com.fleencorp.feen.model.response.chat.space.update.UpdateChatSpaceResponse;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.chat.ChatSpaceMemberRepository;
import com.fleencorp.feen.repository.chat.ChatSpaceRepository;
import com.fleencorp.feen.service.chat.space.ChatSpaceService;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
  private final ChatSpaceMemberRepository chatSpaceMemberRepository;
  private final ChatSpaceRepository chatSpaceRepository;
  private final Localizer localizer;
  private final ChatSpaceMapper chatSpaceMapper;

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
   * @param localizer provides localized responses for API operations.
   * @param chatSpaceMapper maps chat space entities to response models.
   */
  public ChatSpaceServiceImpl(
      final ChatSpaceUpdateService chatSpaceUpdateService,
      final ChatSpaceMemberRepository chatSpaceMemberRepository,
      final ChatSpaceRepository chatSpaceRepository,
      final Localizer localizer,
      final ChatSpaceMapper chatSpaceMapper) {
    this.chatSpaceUpdateService = chatSpaceUpdateService;
    this.chatSpaceRepository = chatSpaceRepository;
    this.chatSpaceMemberRepository = chatSpaceMemberRepository;
    this.localizer = localizer;
    this.chatSpaceMapper = chatSpaceMapper;
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
  public CreateChatSpaceResponse createChatSpace(final CreateChatSpaceDto createChatSpaceDto, final FleenUser user) {
    // Initialize a new chat space based on the dto details
    ChatSpace chatSpace = createChatSpaceDto.toChatSpace(user.toMember());
    // Save the new chat space to the repository
    chatSpace = chatSpaceRepository.save(chatSpace);
    // Create a request object for creating the chat space
    final CreateChatSpaceRequest createChatSpaceRequest = getCreateChatSpaceRequest(createChatSpaceDto, user);

    // Create and add admin or organizer of space as chat space member
    final ChatSpaceMember chatSpaceMember = ChatSpaceMember.of(chatSpace, user.toMember());
    // Save chat space member to repository
    chatSpaceMemberRepository.save(chatSpaceMember);
    // Increase total members and save chat space
    increaseTotalMembersAndSave(chatSpace);
    // Delegate the creation of the chat space to the update service
    chatSpaceUpdateService.createChatSpace(chatSpace, createChatSpaceRequest);
    // Convert the chat space to its response
    final ChatSpaceResponse chatSpaceResponse = chatSpaceMapper.toChatSpaceResponseByAdminUpdate(chatSpace);
    // Create the response
    final CreateChatSpaceResponse createChatSpaceResponse = CreateChatSpaceResponse.of(chatSpaceResponse);
    // Return a localized response with the chat space details
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
  protected static CreateChatSpaceRequest getCreateChatSpaceRequest(final CreateChatSpaceDto createChatSpaceDto, final FleenUser user) {
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
   * @throws NotAnAdminOfChatSpaceException if the user is not authorized to disable the chat space.
   * @throws FailedOperationException if there is an invalid input
   */
  @Override
  @Transactional
  public UpdateChatSpaceResponse updateChatSpace(final Long chatSpaceId, final UpdateChatSpaceDto updateChatSpaceDto, final FleenUser user)
    throws ChatSpaceNotFoundException, ChatSpaceAlreadyDeletedException, NotAnAdminOfChatSpaceException,
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
    final ChatSpaceResponse chatSpaceResponse = chatSpaceMapper.toChatSpaceResponseByAdminUpdate(chatSpace);
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
   * @throws NotAnAdminOfChatSpaceException if the user is not authorized to disable the chat space.
   * @throws FailedOperationException if there is an invalid input
   */
  @Override
  @Transactional
  public DeleteChatSpaceResponse deleteChatSpace(final Long chatSpaceId, final FleenUser user)
      throws ChatSpaceNotFoundException, ChatSpaceAlreadyDeletedException, NotAnAdminOfChatSpaceException,
        FailedOperationException {
    // Find the chat space by its ID or throw an exception if not found
    final ChatSpace chatSpace = findChatSpace(chatSpaceId);
    // Verify if the chat space has already been deleted and that the user is the creator or an admin of the chat space
    verifyIfChatSpaceAlreadyDeletedAndCreatorOrAdminOfSpace(chatSpace, user);
    // Mark the chat space as deleted
    chatSpace.delete();
    // Save the updated chat space status to the repository
    chatSpaceRepository.save(chatSpace);
    // Create the response
    final DeleteChatSpaceResponse deleteChatSpaceResponse = DeleteChatSpaceResponse.of(chatSpaceId);
    // Return a localized response confirming the deletion
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
  public DeleteChatSpaceResponse deleteChatSpaceByAdmin(final Long chatSpaceId, final FleenUser user)
      throws ChatSpaceNotFoundException {
    // Find the chat space by its ID or throw an exception if not found
    final ChatSpace chatSpace = findChatSpace(chatSpaceId);
    // Mark the chat space as deleted
    chatSpace.delete();
    // Save the updated chat space status to the repository
    chatSpaceRepository.save(chatSpace);
    // Create external request
    final DeleteChatSpaceRequest deleteChatSpaceRequest = DeleteChatSpaceRequest.of(chatSpace.getExternalIdOrName());
    // Send a request to delete the chat space from external systems
    chatSpaceUpdateService.deleteChatSpace(deleteChatSpaceRequest);
    // Return a localized response confirming the deletion
    return localizer.of(DeleteChatSpaceResponse.of(chatSpaceId));
  }

  /**
   * Enables a chat space by its ID.
   *
   * <p>This method retrieves the chat space using the specified ID, verifies that it has not been
   * deleted, and then enables it. The updated chat space is saved to the repository.</p>
   *
   * @param chatSpaceId The ID of the chat space to be enabled.
   * @param user The user performing the enable operation.
   * @return A response confirming the enabling of the chat space, localized based on the user's locale.
   * @throws ChatSpaceNotFoundException if the chat space with the specified ID is not found.
   * @throws ChatSpaceAlreadyDeletedException if the chat space has already been deleted.
   * @throws NotAnAdminOfChatSpaceException if the user is not authorized to disable the chat space.
   * @throws FailedOperationException if there is an invalid input
   */
  @Override
  @Transactional
  public EnableChatSpaceResponse enableChatSpace(final Long chatSpaceId, final FleenUser user)
    throws ChatSpaceNotFoundException, ChatSpaceAlreadyDeletedException, NotAnAdminOfChatSpaceException,
      FailedOperationException {
    // Find the chat space by its ID or throw an exception if not found
    final ChatSpace chatSpace = findChatSpace(chatSpaceId);
    // Verify if the chat space has already been deleted and that the user is the creator or an admin of the chat space
    verifyIfChatSpaceAlreadyDeletedAndCreatorOrAdminOfSpace(chatSpace, user);
    // Enable the chat space
    chatSpace.enable();
    // Save the updated chat space status to the repository
    chatSpaceRepository.save(chatSpace);
    // Return a localized response confirming the enabling of the chat space
    return localizer.of(EnableChatSpaceResponse.of(chatSpaceId));
  }

  /**
   * Enables a chat space by its ID.
   *
   * <p>This method retrieves the chat space using the specified ID, verifies that it has not been
   * deleted, and checks if the user is the creator or an admin of the chat space. If all checks pass,
   * the chat space is enabled. The updated chat space is then saved to the repository.</p>
   *
   * @param chatSpaceId The ID of the chat space to be enabled.
   * @param user The user performing the enable operation.
   * @return A response confirming the enabling of the chat space, localized based on the user's locale.
   * @throws ChatSpaceNotFoundException if the chat space with the specified ID is not found.
   * @throws ChatSpaceAlreadyDeletedException if the chat space has already been deleted.
   * @throws NotAnAdminOfChatSpaceException if the user is not authorized to enable the chat space.
   * @throws FailedOperationException if there is an invalid input
   */
  @Override
  @Transactional
  public DisableChatSpaceResponse disableChatSpace(final Long chatSpaceId, final FleenUser user)
    throws ChatSpaceNotFoundException, ChatSpaceAlreadyDeletedException, NotAnAdminOfChatSpaceException,
      FailedOperationException {
    // Find the chat space by its ID or throw an exception if not found
    final ChatSpace chatSpace = findChatSpace(chatSpaceId);
    // Verify if the chat space has already been deleted and that the user is the creator or an admin of the chat space
    verifyIfChatSpaceAlreadyDeletedAndCreatorOrAdminOfSpace(chatSpace, user);
    // Disable the chat space
    chatSpace.disable();
    // Save the updated chat space status to the repository
    chatSpaceRepository.save(chatSpace);
    // Return a localized response confirming the disabling of the chat space
    return localizer.of(DisableChatSpaceResponse.of(chatSpaceId));
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
  @Override
  public void verifyCreatorOrAdminOfSpace(final ChatSpace chatSpace, final FleenUser user)
      throws FailedOperationException, NotAnAdminOfChatSpaceException {
    // Throw an exception if the any of the provided values is null
    checkIsNullAny(Set.of(chatSpace, user), FailedOperationException::new);

    // Check if the user is the creator or an admin of the space
    if (chatSpace.isOrganizer(user.getId()) || checkIfUserIsAnAdminInSpace(chatSpace, user)) {
      return;
    }

    // If neither, throw exception
    throw new NotAnAdminOfChatSpaceException();
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
  protected void verifyIfChatSpaceAlreadyDeletedAndCreatorOrAdminOfSpace(final ChatSpace chatSpace, final FleenUser user)
    throws ChatSpaceAlreadyDeletedException, NotAnAdminOfChatSpaceException, FailedOperationException {
    // Verify if the chat space has already been deleted
    chatSpace.checkNotDeleted();
    // Verify that the user is the creator or an admin of the chat space
    verifyCreatorOrAdminOfSpace(chatSpace, user);
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
   * Increases the total number of members in the specified chat space and saves the updated chat space entity.
   *
   * @param chatSpace the chat space where the total number of members should be increased
   */
  @Override
  public void increaseTotalMembersAndSave(final ChatSpace chatSpace) {
    // Increase total members in chat space
    chatSpaceRepository.incrementTotalMembers(chatSpace.getChatSpaceId());
  }

}
