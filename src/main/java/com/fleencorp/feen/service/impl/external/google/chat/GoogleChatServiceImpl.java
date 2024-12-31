package com.fleencorp.feen.service.impl.external.google.chat;

import com.fleencorp.feen.aspect.MeasureExecutionTime;
import com.fleencorp.feen.constant.external.google.chat.space.ChatSpaceField;
import com.fleencorp.feen.exception.base.UnableToCompleteOperationException;
import com.fleencorp.feen.model.request.chat.space.CreateChatSpaceRequest;
import com.fleencorp.feen.model.request.chat.space.DeleteChatSpaceRequest;
import com.fleencorp.feen.model.request.chat.space.RetrieveChatSpaceRequest;
import com.fleencorp.feen.model.request.chat.space.UpdateChatSpaceRequest;
import com.fleencorp.feen.model.request.chat.space.message.GoogleChatSpaceMessageRequest;
import com.fleencorp.feen.model.response.external.google.chat.chat.GoogleCreateChatSpaceResponse;
import com.fleencorp.feen.model.response.external.google.chat.chat.GoogleDeleteChatSpaceResponse;
import com.fleencorp.feen.model.response.external.google.chat.chat.GoogleRetrieveChatSpaceResponse;
import com.fleencorp.feen.model.response.external.google.chat.chat.GoogleUpdateChatSpaceResponse;
import com.fleencorp.feen.service.external.google.chat.GoogleChatService;
import com.fleencorp.feen.service.report.ReporterService;
import com.fleencorp.feen.util.external.google.GoogleChatMessageBuilder;
import com.google.chat.v1.*;
import com.google.protobuf.FieldMask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.fleencorp.feen.constant.base.ReportMessageType.GOOGLE_CHAT;
import static com.fleencorp.feen.exception.RestExceptionHandler.toSnakeCase;
import static com.fleencorp.feen.mapper.external.GoogleChatSpaceMapper.toGoogleChatSpaceResponse;
import static com.fleencorp.feen.util.LoggingUtil.logIfEnabled;
import static com.fleencorp.feen.util.external.google.GoogleChatMessageBuilder.ofCardWithId;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

/**
 * Represents the Google Chat Service responsible for managing chat spaces and
 * memberships within Google Chat.
 *
 * <p>This service provides methods to create, update, and delete chat spaces,
 * as well as manage memberships for users and chat bots within those spaces.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Slf4j
@Component
public class GoogleChatServiceImpl implements GoogleChatService {

  private final ChatServiceClient chatBot;
  private final ChatServiceClient chatService;
  private final ReporterService reporterService;

  /**
   * Constructs a new instance of GoogleChatService.
   *
   * <p>This constructor initializes the GoogleChatService with the specified
   * ChatServiceClient instances for standard and chat bot services, as well as
   * the ReporterService for handling reporting operations.</p>
   *
   * @param chatService the ChatServiceClient instance for standard chat operations
   * @param reporterService the ReporterService instance for reporting operations
   */
  public GoogleChatServiceImpl(
      final ChatServiceClient chatBot,
      final ChatServiceClient chatService,
      final ReporterService reporterService) {
    this.chatBot = requireNonNull(chatBot);
    this.chatService = chatService;
    this.reporterService = reporterService;
  }

  /**
   * Creates a new chat space and configures it based on the provided request.
   *
   * <p>This method updates the space snippet and additional space information using the provided
   * {@link CreateChatSpaceRequest}. It then calls the chat service to create the space and checks
   * if it was successfully created.</p>
   *
   * <p>If successful, an update request is made to adjust the space's history state, and the space
   * is updated. Afterward, the chat app is added to the newly created space, and the method
   * returns a response containing the name of the updated space and additional details.</p>
   *
   * <p>If any exception occurs during the process, an error message is logged and sent to a
   * reporting service, and an {@link UnableToCompleteOperationException} is thrown.</p>
   *
   * @param createChatSpaceRequest the request object containing the necessary details for creating the chat space.
   * @return a {@link GoogleCreateChatSpaceResponse} containing the name of the created space and additional response details.
   * @throws UnableToCompleteOperationException if the chat space creation or update process cannot be completed.
   *
   * @see <a href="https://developers.google.com/workspace/chat/create-spaces">
   *   Create a space</a>
   * @see <a href="https://developers.google.com/workspace/chat/api/reference/rest/v1/spaces/create">
   *   Method: spaces.create</a>
   */
  @MeasureExecutionTime
  @Override
  public GoogleCreateChatSpaceResponse createSpace(final CreateChatSpaceRequest createChatSpaceRequest) {
    try {
      // Update the space snippet with details from the createChatSpaceRequest
      final Space.SpaceDetails spaceDetails = updateSpaceSnippet(createChatSpaceRequest);
      // Update additional space information based on the request and snippet details
      final Space space = updateSpaceInfo(createChatSpaceRequest, spaceDetails);

      // Create the space using the chat service and check if it was successfully created
      final Space createdSpace = getService().createSpace(space);

      // Check if the space is created
      if (nonNull(createdSpace)) {
        // Update the space history state
        updateNewSpaceHistoryState(createChatSpaceRequest, createdSpace);
        // Add the chat app to the newly created space
        addChatAppToSpace(createChatSpaceRequest.getChatAppOrBotName(), createdSpace.getName());
        // Return the response with the updated space information
        return GoogleCreateChatSpaceResponse.of(createdSpace.getName(), toGoogleChatSpaceResponse(createdSpace));
      }
    } catch (final RuntimeException ex) {
      final String errorMessage = String.format("Error occurred while create a space. Reason: %s", ex.getMessage());
      reporterService.sendMessage(errorMessage, GOOGLE_CHAT);
    }
    // Throw an exception if the space creation or update process cannot be completed
    throw new UnableToCompleteOperationException();
  }

  /**
   * Retrieves a chat space based on the provided request.
   *
   * <p>This method attempts to fetch the space identified by the specified space ID or name in the
   * {@link RetrieveChatSpaceRequest}. If the space is found, it returns a response containing
   * the name of the space and its details.</p>
   *
   * @param retrieveChatSpaceRequest the request object containing the ID or name of the space to be retrieved.
   * @return a {@link GoogleRetrieveChatSpaceResponse} containing the name of the retrieved space,
   *         the space details, and additional response information.
   * @throws UnableToCompleteOperationException if the retrieval process cannot be completed.
   *
   * @see <a href="https://developers.google.com/workspace/chat/get-spaces">
   *   Get details about a space</a>
   * @see <a href="https://developers.google.com/workspace/chat/api/reference/rest/v1/spaces/get">
   *   Method: spaces.get</a>
   */
  @Override
  public GoogleRetrieveChatSpaceResponse retrieveSpace(final RetrieveChatSpaceRequest retrieveChatSpaceRequest) {
    try {
      // Attempt to retrieve the space using the provided space ID or name
      final Space space = getService().getSpace(retrieveChatSpaceRequest.getSpaceIdOrName());
      if (nonNull(space)) {
        // Return the response with the space name and details if found
        return GoogleRetrieveChatSpaceResponse.of(space.getName(), requireNonNull(toGoogleChatSpaceResponse(space)), space);
      }
    } catch (final RuntimeException ex) {
      final String errorMessage = String.format("Error occurred while retrieving a space. Reason: %s", ex.getMessage());
      reporterService.sendMessage(errorMessage, GOOGLE_CHAT);
    }
    // Throw an exception if the space retrieval process cannot be completed
    throw new UnableToCompleteOperationException();
  }

  /**
   * Updates an existing chat space based on the provided request.
   *
   * <p>This method retrieves the current details of the chat space identified by the specified
   * space ID or name in the {@link UpdateChatSpaceRequest}. If the space is found, it updates
   * the space details and display name according to the request and returns a response containing
   * the updated space information.</p>
   *
   * @param updateChatSpaceRequest the request object containing the necessary details to update the chat space.
   * @return a {@link GoogleUpdateChatSpaceResponse} containing the name of the updated space
   *         and its updated details.
   * @throws UnableToCompleteOperationException if the update process cannot be completed.
   *
   * @see <a href="https://developers.google.com/workspace/chat/update-spaces">
   *   Update a space</a>
   * @see <a href="https://developers.google.com/workspace/chat/api/reference/rest/v1/spaces/patch">
   *   Method: spaces.patch</a>
   */
  @Override
  public GoogleUpdateChatSpaceResponse updateChatSpace(final UpdateChatSpaceRequest updateChatSpaceRequest) {
    try {
      // Retrieve the current details of the chat space using the space ID or name
      final GoogleRetrieveChatSpaceResponse retrieveChatSpaceResponse = retrieveSpace(RetrieveChatSpaceRequest.of(updateChatSpaceRequest.getSpaceIdOrName()));

      if (nonNull(retrieveChatSpaceResponse) && nonNull(retrieveChatSpaceResponse.chatSpace())) {
        // Update the space snippet with details from the update request
        final Space.SpaceDetails spaceDetails = updateSpaceSnippet(updateChatSpaceRequest);
        // Build a new Space object with the updated display name and details
        final Space space = Space.newBuilder(retrieveChatSpaceResponse.chatSpace())
          .setDisplayName(updateChatSpaceRequest.getDisplayName())
          .setSpaceDetails(spaceDetails)
          .build();

        // Create an update request for the chat space
        final UpdateSpaceRequest updateSpaceRequest = createUpdateSpaceRequest(updateChatSpaceRequest, space);
        // Update the space using the chat service
        final Space updatedSpace = getService().updateSpace(updateSpaceRequest);
        // Return the response with the name and details of the updated space
        return GoogleUpdateChatSpaceResponse.of(space.getName(), requireNonNull(toGoogleChatSpaceResponse(updatedSpace)));
      }
    } catch (final RuntimeException ex) {
      final String errorMessage = String.format("Error occurred while updating a space. Reason: %s", ex.getMessage());
      reporterService.sendMessage(errorMessage, GOOGLE_CHAT);
    }
    // Throw an exception if the space update process cannot be completed
    throw new UnableToCompleteOperationException();
  }

  /**
   * Deletes a chat space identified by the provided request.
   *
   * <p>This method attempts to delete the chat space specified by the space ID or name in the
   * {@link DeleteChatSpaceRequest}. If the deletion is successful, it returns a response containing
   * the ID or name of the deleted space.</p>
   *
   * @param deleteChatSpaceRequest the request object containing the necessary details to delete the chat space.
   * @return a {@link GoogleDeleteChatSpaceResponse} indicating the space has been successfully deleted.
   * @throws UnableToCompleteOperationException if the delete process cannot be completed.
   *
   * @see <a href="https://developers.google.com/workspace/chat/delete-spaces">
   *   Delete a space</a>
   * @see <a href="https://developers.google.com/workspace/chat/api/reference/rest/v1/spaces/delete">
   *   Method: spaces.delete</a>
   */
  @MeasureExecutionTime
  @Override
  public GoogleDeleteChatSpaceResponse deleteChatSpace(final DeleteChatSpaceRequest deleteChatSpaceRequest) {
    try {
      // Attempt to delete the specified chat space
      getService().deleteSpace(deleteChatSpaceRequest.getSpaceIdOrName());
      // Return the response indicating the space has been deleted
      return GoogleDeleteChatSpaceResponse.of(deleteChatSpaceRequest.getSpaceIdOrName());
    } catch (final RuntimeException ex) {
      logIfEnabled(log::isErrorEnabled, () -> log.info("An error occurred while deleting a space. Reason: {}", ex.getMessage()));
    }
    // Throw an exception if the delete process cannot be completed
    throw new UnableToCompleteOperationException();

  }

  /**
   * Asynchronously updates the history state of a newly created Google Chat space.
   *
   * @param createChatSpaceRequest The request containing the details of the newly created chat space.
   * @param createdSpace The Google Chat space that was created and needs to have its history state updated.
   */
  @Async
  public void updateNewSpaceHistoryState(final CreateChatSpaceRequest createChatSpaceRequest, final Space createdSpace) {
    // Create an update request to adjust the space's history state
    final UpdateSpaceRequest updateSpaceRequest = createUpdateSpaceRequestForHistoryState(createChatSpaceRequest, createdSpace);
    // Update the created space with the new request and verify if it was successfully updated
    getService().updateSpace(updateSpaceRequest);
  }

  /**
   * Adds a chat application to the specified chat space.
   *
   * <p>This method builds a user and membership details based on the provided space name.
   * It then creates a membership in the chat space. If the membership is created successfully,
   * it logs the created membership information.</p>
   *
   * @param chatAppOrBotUsername the name of the chat app or bot user to add to the chat space
   * @param spaceName the name of the chat space to which the app will be added.
   *
   * @see <a href="https://developers.google.com/workspace/chat/authenticate-authorize-chat-app">
   *   Authenticate as a Google Chat app</a>
   * @see <a href="https://developers.google.com/workspace/chat/create-members#create-membership-calling-api">
   *   Add a Chat app to a space</a>
   */
  @Async
  @MeasureExecutionTime
  public void addChatAppToSpace(final String chatAppOrBotUsername, final String spaceName) {
    try {
      // Build user details based on the space name
      final User user = buildUser(chatAppOrBotUsername);
      // Build membership details for the user
      final Membership membership = buildMembership(user);
      // Create a membership request for the specified space
      final CreateMembershipRequest createMembershipRequest = getCreateMembershipRequest(spaceName, membership);
      // Create the membership in the chat service
      getService().createMembership(createMembershipRequest);
    } catch (final RuntimeException ex) {
      final String errorMessage = String.format("Error occurred while adding app to space. Reason: %s", ex.getMessage());
      reporterService.sendMessage(errorMessage, GOOGLE_CHAT);
    }
  }

  /**
   * Builds a membership object for the specified user.
   *
   * <p>This method creates a new membership by setting the provided user as the member.</p>
   *
   * @param user the user to be added as a member in the membership.
   * @return a {@link Membership} object representing the membership of the specified user.
   *
   * @see <a href="https://developers.google.com/workspace/chat/api/reference/rest/v1/spaces.members/create">
   *   Method: spaces.members.create</a>
   */
  protected static Membership buildMembership(final User user) {
    return Membership.newBuilder()
      .setMember(user)
      .build();
  }

  /**
   * Builds a user object for a bot associated with the specified space name.
   *
   * <p>This method creates a new user with the provided space name and sets the user type to BOT.</p>
   *
   * @param chatAppOrBotUsername the name of the chat app or bot user .
   * @return a {@link User} object representing the bot user for the specified space.
   *
   * @see <a href="https://developers.google.com/workspace/chat/api/reference/rest/v1/User">
   *   User</a>
   */
  protected static User buildUser(final String chatAppOrBotUsername) {
    return User.newBuilder()
      .setName(chatAppOrBotUsername)
      .setType(User.Type.BOT)
      .build();
  }

  /**
   * Builds a request to create a membership for a specified chat space.
   *
   * <p>This method constructs a {@link CreateMembershipRequest} using the provided space ID or name
   * and the membership details. If either the space ID or membership is null,
   * an {@link UnableToCompleteOperationException} is thrown.</p>
   *
   * @param spaceIdOrName the ID or name of the chat space where the membership is to be created.
   * @param membership the membership details to be associated with the new request.
   * @return a {@link CreateMembershipRequest} configured with the specified space ID and membership details.
   * @throws UnableToCompleteOperationException if the space ID or membership is null.
   */
  public static CreateMembershipRequest getCreateMembershipRequest(final String spaceIdOrName, final Membership membership) {
    if (nonNull(spaceIdOrName) && nonNull(membership)) {
      return CreateMembershipRequest.newBuilder()
        .setParent(spaceIdOrName)
        .setMembership(membership)
        .build();
    }
    return null;
  }

  /**
   * Creates an update request for the chat space with the history state based on the provided details.
   *
   * <p>This method constructs an {@link UpdateSpaceRequest} by utilizing the given create chat space
   * request and existing space. It checks for null inputs and generates an update mask that includes
   * the space history state field.</p>
   *
   * @param createChatSpaceRequest the request containing details for creating the chat space.
   * @param space the existing space to be updated.
   * @return an {@link UpdateSpaceRequest} containing the updated space and fields to be updated, or null if the input is invalid.
   */
  protected static UpdateSpaceRequest createUpdateSpaceRequestForHistoryState(final CreateChatSpaceRequest createChatSpaceRequest, final Space space) {
    if (nonNull(createChatSpaceRequest) && nonNull(space)) {
      // Create the updated space with history state
      final Space updatedSpace = createSpaceWithHistoryState(createChatSpaceRequest, space);
      // Retrieve the fields to update for the chat space
      final List<String> updateFields = getUpdateSpaceFields(List.of(ChatSpaceField.spaceHistoryState()));

      return getUpdateSpaceRequest(updateFields, updatedSpace);
    }
    return null;
  }

  /**
   * Creates a new Space object with the specified history state based on the given request details.
   *
   * <p>This method builds a Space instance using the name from the existing space and the history state
   * from the create chat space request. It checks for null inputs before proceeding with the creation.</p>
   *
   * @param createChatSpaceRequest the request containing details for creating a chat space
   * @param space the existing space to be updated
   * @return a Space object with the specified history state, or null if inputs are invalid
   */
  protected static Space createSpaceWithHistoryState(final CreateChatSpaceRequest createChatSpaceRequest, final Space space) {
    if (nonNull(createChatSpaceRequest) && nonNull(space)) {
      // Build the new Space object with the existing name and updated history state
      return Space.newBuilder()
        .setName(space.getName())
        .setSpaceHistoryState(HistoryState.valueOf(createChatSpaceRequest.getHistoryState()))
        .build();
    }
    return null;
  }

  /**
   * Creates an UpdateSpaceRequest based on the provided chat space request and existing space details.
   *
   * <p>This method constructs an UpdateSpaceRequest object using the specified chat space request and
   * the existing space. It checks for null inputs before proceeding with the creation.</p>
   *
   * @param createChatSpaceRequest the request containing details for creating a chat space
   * @param space the existing space to be updated
   * @return an UpdateSpaceRequest object containing the necessary update information, or null if inputs are invalid
   */
  protected static UpdateSpaceRequest createUpdateSpaceRequest(final CreateChatSpaceRequest createChatSpaceRequest, final Space space) {
    if (nonNull(createChatSpaceRequest) && nonNull(space)) {
      // Create the UpdateSpaceRequest using the fields that need to be updated
      return getUpdateSpaceRequest(getUpdateSpaceFields(), space);
    }
    return null;
  }

  /**
   * Creates an UpdateSpaceRequest object for updating a space with the specified fields.
   *
   * <p>This method constructs an UpdateSpaceRequest by building an update mask using the provided
   * list of fields that need to be updated, along with the updated space information.</p>
   *
   * @param updateFields the list of fields that need to be updated
   * @param updatedSpace the space object containing the updated information
   * @return an UpdateSpaceRequest object containing the update mask and updated space
   */
  protected static UpdateSpaceRequest getUpdateSpaceRequest(final List<String> updateFields, final Space updatedSpace) {
    // Build and return the UpdateSpaceRequest with the specified update fields and updated space
    return UpdateSpaceRequest.newBuilder()
      .setUpdateMask(FieldMask.newBuilder().addAllPaths(updateFields))
      .setSpace(updatedSpace)
      .build();
  }

  /**
   * Updates the SpaceDetails based on the provided CreateChatSpaceRequest.
   *
   * <p>This method constructs a SpaceDetails object using the description and guidelines
   * from the provided CreateChatSpaceRequest.</p>
   *
   * @param createChatSpaceRequest the request containing the details for creating a chat space
   * @return a Space.SpaceDetails object with updated description and guidelines, or null if the input is invalid
   */
  protected static Space.SpaceDetails updateSpaceSnippet(final CreateChatSpaceRequest createChatSpaceRequest) {
    if (nonNull(createChatSpaceRequest)) {
      // Set description and guidelines and return a SpaceDetails
      return Space.SpaceDetails.newBuilder()
        .setDescription(createChatSpaceRequest.getDescription())
        .setGuidelines(createChatSpaceRequest.getGuidelinesOrRules())
        .build();
    }
    return null;
  }

  /**
   * Updates the Space information based on the provided CreateChatSpaceRequest and SpaceDetails.
   *
   * <p>This method constructs a Space object using the display name, space details,
   * and other parameters from the provided CreateChatSpaceRequest.</p>
   *
   * @param createChatSpaceRequest the request containing details for creating a chat space
   * @param spaceDetails the details of the space to be updated
   * @return a Space object with updated information, or null if the inputs are invalid
   */
  protected static Space updateSpaceInfo(final CreateChatSpaceRequest createChatSpaceRequest, final Space.SpaceDetails spaceDetails) {
    if (nonNull(createChatSpaceRequest) && nonNull(spaceDetails)) {
      // Return updated Space if both inputs are valid
      return Space.newBuilder()
        .setDisplayName(createChatSpaceRequest.getDisplayName())
        .setSpaceDetails(spaceDetails)
        .setExternalUserAllowed(createChatSpaceRequest.isExternalUsersAllowed())
        .setSpaceThreadingState(Space.SpaceThreadingState.valueOf(createChatSpaceRequest.getThreadState()))
        .setSpaceType(Space.SpaceType.valueOf(createChatSpaceRequest.getSpaceType()))
        .build();
    }
    return null;
  }

  /**
   * Retrieves the list of fields that can be updated in a Space.
   *
   * <p>This method creates a list of fields, including display name and space details,
   * and passes it to another method for further processing.</p>
   *
   * @return a List of Strings representing the fields to update
   */
  protected static List<String> getUpdateSpaceFields() {
    // Create a list of fields that can be updated in a Space
    final List<String> updateSpaceFields = List.of(ChatSpaceField.displayName(), ChatSpaceField.spaceDetails());
    // Return the processed list of update fields
    return getUpdateSpaceFields(updateSpaceFields);
  }

  /**
   * Converts a list of field names to snake_case format for update operations.
   *
   * <p>This method iterates through the provided list of fields, converts each field name
   * to snake_case using a utility method, and collects the results in a new list.</p>
   *
   * @param fields a List of Strings representing field names to be converted
   * @return a List of Strings containing the field names in snake_case format
   */
  protected static List<String> getUpdateSpaceFields(final List<String> fields) {
    // Create a new list to hold the converted field names
    final List<String> updatedFields = new ArrayList<>();
    // Convert each field name to snake_case and add it to the updated fields list
    for (final String field : fields) {
      updatedFields.add(toSnakeCase(field));
    }
    // Return the list of updated fields
    return updatedFields.stream()
      .filter(Objects::nonNull)
      .toList();
  }

  /**
   * Creates a calendar event message and sends it to a specified chat space.
   *
   * <p>This method constructs a message that includes a card with event details
   * and sends it to the chat space identified by the provided space ID or name.</p>
   *
   * @param chatSpaceMessageRequest The request object containing details
   *                                     for the calendar event message.
   * @see <a href="https://developers.google.com/workspace/chat/create-messages">
   *   Send a message using the Google Chat API</a>
   * @see <a href="https://developers.google.com/workspace/chat/add-text-image-card-dialog">
   *   Add text and images to cards</a>
   * @see <a href="https://developers.google.com/workspace/chat/format-messages">
   *   Format messages</a>
   */
  @Override
  @MeasureExecutionTime
  public void createCalendarEventMessageAndSendToChatSpace(final GoogleChatSpaceMessageRequest chatSpaceMessageRequest) {
    // Build the card with the event description
    final CardWithId.Builder cardWithId = ofCardWithId(chatSpaceMessageRequest);

    // Create the message request with the calendar event details
    final CreateMessageRequest request = CreateMessageRequest.newBuilder()
      .setParent(chatSpaceMessageRequest.getSpaceIdOrName())
      .setMessage(GoogleChatMessageBuilder.ofMessage(chatSpaceMessageRequest.getTitle(), cardWithId, chatSpaceMessageRequest))
      .build();

    // Send the message to the chat bot service
    getChatBotService().createMessage(request);
  }

  /**
   * Retrieves the ChatServiceClient instance.
   *
   * <p>This method provides access to the ChatServiceClient used for interacting
   * with the chat service in Google Chat API.</p>
   *
   * @return the ChatServiceClient instance
   */
  public ChatServiceClient getService() {
    return chatService;
  }

  /**
   * Retrieves the ChatServiceClient instance for the chat bot.
   *
   * <p>This method provides access to the ChatServiceClient used for interacting
   * with the chat bot service.</p>
   *
   * @return the ChatServiceClient instance for the chat bot
   */
  public ChatServiceClient getChatBotService() {
    return chatBot;
  }
}
