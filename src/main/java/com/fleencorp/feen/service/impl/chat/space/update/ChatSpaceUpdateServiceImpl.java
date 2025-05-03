package com.fleencorp.feen.service.impl.chat.space.update;

import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.chat.ChatSpaceMember;
import com.fleencorp.feen.model.request.chat.space.CreateChatSpaceRequest;
import com.fleencorp.feen.model.request.chat.space.DeleteChatSpaceRequest;
import com.fleencorp.feen.model.request.chat.space.UpdateChatSpaceRequest;
import com.fleencorp.feen.model.request.chat.space.membership.AddChatSpaceMemberRequest;
import com.fleencorp.feen.model.request.chat.space.membership.RemoveChatSpaceMemberRequest;
import com.fleencorp.feen.model.response.external.google.chat.chat.GoogleCreateChatSpaceResponse;
import com.fleencorp.feen.model.response.external.google.chat.chat.GoogleDeleteChatSpaceResponse;
import com.fleencorp.feen.model.response.external.google.chat.chat.GoogleUpdateChatSpaceResponse;
import com.fleencorp.feen.model.response.external.google.chat.membership.GoogleAddChatSpaceMemberResponse;
import com.fleencorp.feen.model.response.external.google.chat.membership.GoogleRemoveChatSpaceMemberResponse;
import com.fleencorp.feen.repository.chat.space.member.ChatSpaceMemberRepository;
import com.fleencorp.feen.repository.chat.space.ChatSpaceRepository;
import com.fleencorp.feen.service.chat.space.update.ChatSpaceUpdateService;
import com.fleencorp.feen.service.external.google.chat.GoogleChatMemberService;
import com.fleencorp.feen.service.external.google.chat.GoogleChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for updating chat spaces.
 *
 * <p>This class provides functionalities related to updating chat spaces,
 * such as adding members and synchronizing with external services.</p>
 *
 * <p>It serves as a central point for managing updates and interactions
 * within the chat space, ensuring that all changes are handled
 * consistently and efficiently.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Slf4j
@Component
public class ChatSpaceUpdateServiceImpl implements ChatSpaceUpdateService {

  private final GoogleChatService googleChatService;
  private final GoogleChatMemberService googleChatMemberService;
  private final ChatSpaceRepository chatSpaceRepository;
  private final ChatSpaceMemberRepository chatSpaceMemberRepository;

  /**
   * Constructs a {@link ChatSpaceUpdateServiceImpl} instance with the specified services.
   *
   * @param googleChatService the service used to interact with Google Chat.
   * @param chatSpaceRepository the repository for managing chat spaces in the database.
   * @param chatSpaceMemberRepository Repository for managing chat space members.
   */
  public ChatSpaceUpdateServiceImpl(
      final GoogleChatService googleChatService,
      final GoogleChatMemberService googleChatMemberService,
      final ChatSpaceRepository chatSpaceRepository,
      final ChatSpaceMemberRepository chatSpaceMemberRepository) {
    this.googleChatService = googleChatService;
    this.googleChatMemberService = googleChatMemberService;
    this.chatSpaceRepository = chatSpaceRepository;
    this.chatSpaceMemberRepository = chatSpaceMemberRepository;
  }

  /**
   * Creates a new chat space and updates the provided chat space details.
   *
   * <p>This method invokes the Google Chat service to create a chat space
   * using the specified request and then updates the provided chat space
   * with the response details.</p>
   *
   * @param chatSpace The chat space object to be updated with the new details.
   * @param request The request containing the details needed to create the chat space.
   */
  @Override
  @Async
  @Transactional
  public void createChatSpace(final ChatSpace chatSpace, final CreateChatSpaceRequest request) {
    // Create the chat space using the Google Chat service
    final GoogleCreateChatSpaceResponse response = googleChatService.createSpace(request);

    // Update the chat space details with the response
    chatSpace.updateDetails(response.name(), response.spaceUri());
    chatSpaceRepository.save(chatSpace);

    // Create add chat space member request to add organizer as a member of chat space
    final AddChatSpaceMemberRequest addChatSpaceMemberRequest = AddChatSpaceMemberRequest.of(response.name(), request.getUserEmailAddress());

    // Find the chat space member associated with the chat space and current member
    chatSpaceMemberRepository.findByChatSpaceAndMember(chatSpace, chatSpace.getMember())
      .ifPresent(chatSpaceMember -> addMember(chatSpaceMember, addChatSpaceMemberRequest));
  }

  /**
   * Updates the specified chat space with the provided request details.
   *
   * <p>This method invokes the Google Chat service to update a chat space
   * using the specified request and logs the response.</p>
   *
   * @param request The request containing the details needed to update the chat space.
   */
  @Override
  @Async
  @Transactional
  public void updateChatSpace(final UpdateChatSpaceRequest request) {
    // Update the chat space using the Google Chat service
    final GoogleUpdateChatSpaceResponse response = googleChatService.updateChatSpace(request);
    log.info("Chat space updated: {}", response);
  }

  /**
   * Deletes the specified chat space using the provided request details.
   *
   * <p>This method invokes the Google Chat service to delete a chat space
   * and logs the response indicating the result of the deletion.</p>
   *
   * @param request The request containing the details needed to delete the chat space.
   */
  @Override
  @Async
  @Transactional
  public void deleteChatSpace(final DeleteChatSpaceRequest request) {
    // Delete the chat space using the Google Chat service
    final GoogleDeleteChatSpaceResponse response = googleChatService.deleteChatSpace(request);
    log.info("Chat space deleted: {}", response);
  }

  /**
   * Adds a member to the specified chat space using the provided request details.
   *
   * <p>This method invokes the Google Chat service to add a member
   * and logs the response indicating the result of the addition.</p>
   *
   * @param chatSpaceMember The chat space member object to be added. This
   *                        parameter is updated with details from the response.
   * @param request The request containing the details needed to add the member to the chat space.
   */
  @Override
  @Async
  @Transactional
  public void addMember(final ChatSpaceMember chatSpaceMember, final AddChatSpaceMemberRequest request) {
    // Add the member using the Google Chat service to a Space
    final GoogleAddChatSpaceMemberResponse response = googleChatMemberService.addMember(request);
    log.info("Member added: {}", response);

    // Update the chat space member details with response information
    chatSpaceMember.updateDetails(response.spaceIdOrName(), response.memberIdOrName());
    // Save chat space member to repository
    chatSpaceMemberRepository.save(chatSpaceMember);
  }

  /**
   * Removes a member from the chat space using the provided request details.
   *
   * <p>This method calls the Google Chat service to remove a member
   * and logs the response indicating the result of the removal.</p>
   *
   * @param request The request containing the details needed to remove
   *                the member from the chat space.
   */
  @Override
  @Async
  @Transactional
  public void removeMember(final RemoveChatSpaceMemberRequest request) {
    // Remove the member using the Google Chat service from the Space
    final GoogleRemoveChatSpaceMemberResponse response = googleChatMemberService.removeMember(request);
    log.info("Member deleted: {}", response);
  }

}
