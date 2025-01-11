package com.fleencorp.feen.service.impl.external.google.chat;

import com.fleencorp.feen.aspect.MeasureExecutionTime;
import com.fleencorp.feen.model.request.chat.space.CreateChatSpaceRequest;
import com.fleencorp.feen.service.external.google.chat.GoogleChatUpdateService;
import com.fleencorp.feen.service.report.ReporterService;
import com.google.chat.v1.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static com.fleencorp.feen.constant.base.ReportMessageType.GOOGLE_CHAT;
import static com.fleencorp.feen.service.impl.external.google.chat.GoogleChatServiceImpl.*;

/**
 * Implementation of the GoogleChatUpdateService interface.
 *
 * <p>This class provides the functionality for updating Google Chat messages and
 * interactions. It implements the methods defined in the GoogleChatUpdateService
 * interface to handle updates and interactions within Google Chat.</p>
 *
 * @author Yusuf Àlàmú Musa
 * @version 1.0
 */
@Component
public class GoogleChatUpdateServiceImpl implements GoogleChatUpdateService {

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
  public GoogleChatUpdateServiceImpl(
      final ChatServiceClient chatService,
      final ReporterService reporterService) {
    this.chatService = chatService;
    this.reporterService = reporterService;
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
  @Override
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
   * Asynchronously updates the history state of a newly created Google Chat space.
   *
   * @param createChatSpaceRequest The request containing the details of the newly created chat space.
   * @param createdSpace The Google Chat space that was created and needs to have its history state updated.
   */
  @Async
  @Override
  public void updateNewSpaceHistoryState(final CreateChatSpaceRequest createChatSpaceRequest, final Space createdSpace) {
    // Create an update request to adjust the space's history state
    final UpdateSpaceRequest updateSpaceRequest = createUpdateSpaceRequestForHistoryState(createChatSpaceRequest, createdSpace);
    // Update the created space with the new request and verify if it was successfully updated
    getService().updateSpace(updateSpaceRequest);
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
}
