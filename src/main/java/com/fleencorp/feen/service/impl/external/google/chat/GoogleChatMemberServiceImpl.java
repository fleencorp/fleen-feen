package com.fleencorp.feen.service.impl.external.google.chat;

import com.fleencorp.feen.aspect.MeasureExecutionTime;
import com.fleencorp.feen.exception.base.UnableToCompleteOperationException;
import com.fleencorp.feen.model.request.chat.space.membership.AddChatSpaceMemberRequest;
import com.fleencorp.feen.model.request.chat.space.membership.RemoveChatSpaceMemberRequest;
import com.fleencorp.feen.model.request.chat.space.membership.RetrieveChatSpaceMemberRequest;
import com.fleencorp.feen.model.response.external.google.chat.membership.GoogleAddChatSpaceMemberResponse;
import com.fleencorp.feen.model.response.external.google.chat.membership.GoogleRemoveChatSpaceMemberResponse;
import com.fleencorp.feen.model.response.external.google.chat.membership.base.GoogleChatSpaceMemberResponse;
import com.fleencorp.feen.service.external.google.chat.GoogleChatMemberService;
import com.fleencorp.feen.service.report.ReporterService;
import com.google.chat.v1.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.fleencorp.feen.constant.base.ReportMessageType.GOOGLE_CHAT;
import static com.fleencorp.feen.mapper.external.GoogleChatSpaceMemberMapper.toGoogleChatSpaceMemberResponse;
import static com.fleencorp.feen.service.impl.external.google.chat.GoogleChatServiceImpl.getCreateMembershipRequest;
import static com.fleencorp.feen.util.external.google.GoogleApiUtil.convertToProtobufTimestamp;
import static java.util.Objects.nonNull;

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
public class GoogleChatMemberServiceImpl implements GoogleChatMemberService {

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
  public GoogleChatMemberServiceImpl(
      final ChatServiceClient chatService,
      final ReporterService reporterService) {
    this.chatService = chatService;
    this.reporterService = reporterService;
  }

  /**
   * Adds a member to a chat space based on the provided request.
   *
   * <p>This method creates a membership for a user in the specified chat space.
   * If the membership is created successfully, it returns a response containing
   * the space ID or name and the member's details.</p>
   *
   * @param addChatSpaceMemberRequest the request object containing the necessary details to add a member.
   * @return a {@link GoogleAddChatSpaceMemberResponse} indicating the member has been successfully added to the chat space.
   * @throws UnableToCompleteOperationException if the membership creation process cannot be completed.
   *
   * @see <a href="https://developers.google.com/workspace/chat/create-members">
   *   Invite or add a user, Google Group, or Google Chat app to a space</a>
   * @see <a href="https://developers.google.com/workspace/chat/api/reference/rest/v1/spaces.members/create">
   *   Method: spaces.members.create</a>
   */
  @MeasureExecutionTime
  @Override
  public GoogleAddChatSpaceMemberResponse addMember(final AddChatSpaceMemberRequest addChatSpaceMemberRequest) {
    try {
      // Get the space ID or name from the request
      final String spaceIdOrName = addChatSpaceMemberRequest.getSpaceIdOrName();
      // Create a user object for the member
      final User user = createMemberUser(addChatSpaceMemberRequest);
      // Build the membership object for the user
      final Membership membership = buildMembership(addChatSpaceMemberRequest, user);
      // Create a membership request for the specified space
      final CreateMembershipRequest createMembershipRequest = getCreateMembershipRequest(spaceIdOrName, membership);
      // Create the membership in the chat service
      final Membership createdMember = getService().createMembership(createMembershipRequest);

      if (nonNull(createdMember)) {
        // Return the response with member details if created successfully
        final String memberName = createdMember.getMember().getName();
        return GoogleAddChatSpaceMemberResponse.of(spaceIdOrName, memberName, toGoogleChatSpaceMemberResponse(createdMember));
      }
    } catch (final RuntimeException ex) {
      final String errorMessage = String.format("Error occurred while creating a membership. Reason: %s", ex.getMessage());
      reporterService.sendMessage(errorMessage, GOOGLE_CHAT);
    }
    // Throw an exception if the membership creation process cannot be completed
    throw new UnableToCompleteOperationException();
  }

  /**
   * Retrieves the membership details for a given chat space member and converts the result to a {@link GoogleChatSpaceMemberResponse}.
   *
   * <p>This method attempts to retrieve an existing membership from the chat service using the provided
   * {@link RetrieveChatSpaceMemberRequest}. If the membership is found, it is converted to a {@code GoogleChatSpaceMemberResponse}.</p>
   *
   * <p>In case of an error during the retrieval process, the error message is reported through the {@code reporterService},
   * and an {@link UnableToCompleteOperationException} is thrown if the membership retrieval cannot be completed.</p>
   *
   * @param retrieveChatSpaceMemberRequest the request object containing information about the chat space member to be retrieved
   * @return the {@code GoogleChatSpaceMemberResponse} if the membership is successfully retrieved
   * @throws UnableToCompleteOperationException if the membership retrieval process cannot be completed
   */
  @Override
  public GoogleChatSpaceMemberResponse retrieveMember(final RetrieveChatSpaceMemberRequest retrieveChatSpaceMemberRequest) {
    try {
      // Retrieve the membership in the chat space or room service
      final Membership existingMember = getService().getMembership(retrieveChatSpaceMemberRequest.getMemberSpaceIdOrName());

      // Return the membership response if a member exists with the specific ID
      if (nonNull(existingMember)) {
        return toGoogleChatSpaceMemberResponse(existingMember);
      }
    } catch (final RuntimeException ex) {
      final String errorMessage = String.format("Error occurred while retrieving membership. Reason: %s", ex.getMessage());
      reporterService.sendMessage(errorMessage, GOOGLE_CHAT);
    }
    // Throw an exception if the membership retrieval process cannot be completed
    throw new UnableToCompleteOperationException();
  }

  /**
   * Deletes a member from a chat space based on the provided request.
   *
   * <p>This method attempts to delete the specified member from the chat space.
   * If successful, it returns a response confirming the deletion.</p>
   *
   * @param removeChatSpaceMemberRequest the request object containing the necessary details to delete a member.
   * @return a {@link GoogleRemoveChatSpaceMemberResponse} indicating the member has been successfully deleted from the chat space.
   * @throws UnableToCompleteOperationException if the membership deletion process cannot be completed.
   *
   * @see <a href="https://developers.google.com/workspace/chat/delete-members">
   *   Remove a member from a space</a>
   * @see <a href="https://developers.google.com/workspace/chat/api/reference/rest/v1/spaces.members/delete">
   *   Method: spaces.members.delete</a>
   */
  @MeasureExecutionTime
  @Override
  public GoogleRemoveChatSpaceMemberResponse removeMember(final RemoveChatSpaceMemberRequest removeChatSpaceMemberRequest) {
    try {
      if (nonNull(removeChatSpaceMemberRequest.getSpaceIdOrName()) && nonNull(removeChatSpaceMemberRequest.getMemberSpaceIdOrName())) {
        // Create a request to retrieve and check if a member exists before deleting
        final RetrieveChatSpaceMemberRequest retrieveChatSpaceMemberRequest = RetrieveChatSpaceMemberRequest.of(
          removeChatSpaceMemberRequest.getSpaceIdOrName(),
          removeChatSpaceMemberRequest.getMemberSpaceIdOrName());
        // Attempt to retrieve chat space member
        final GoogleChatSpaceMemberResponse chatSpaceMemberResponse = retrieveMember(retrieveChatSpaceMemberRequest);

        if (nonNull(chatSpaceMemberResponse)) {
          // Verify the space exists
          getService().getSpace(removeChatSpaceMemberRequest.getSpaceIdOrName());
          // Create a request to delete the membership
          final DeleteMembershipRequest deleteMembershipRequest = getDeleteMembershipRequest(removeChatSpaceMemberRequest);
          // Delete the membership from the chat service
          getService().deleteMembership(deleteMembershipRequest);
          // Return the response confirming the deletion
          return GoogleRemoveChatSpaceMemberResponse.of(removeChatSpaceMemberRequest.getMemberSpaceIdOrName(), removeChatSpaceMemberRequest.getSpaceIdOrName());
        }
      }
    } catch (final RuntimeException ex) {
      final String errorMessage = String.format("Error occurred while deleting a membership. Reason: %s", ex.getMessage());
      reporterService.sendMessage(errorMessage, GOOGLE_CHAT);
    }
    // Throw an exception if the membership deletion process cannot be completed
    throw new UnableToCompleteOperationException();
  }

  /**
   * Builds a request to delete a membership based on the provided member deletion request.
   *
   * <p>This method creates a {@link DeleteMembershipRequest} using the member's space ID or name
   * from the provided delete chat space member request.</p>
   *
   * @param removeChatSpaceMemberRequest the request object containing the details of the member to be deleted.
   * @return a {@link DeleteMembershipRequest} ready for deletion of the specified membership.
   */
  protected static DeleteMembershipRequest getDeleteMembershipRequest(final RemoveChatSpaceMemberRequest removeChatSpaceMemberRequest) {
    return DeleteMembershipRequest.newBuilder()
      .setName(removeChatSpaceMemberRequest.getMemberSpaceIdOrName())
      .build();
  }

  /**
   * Constructs a membership object for a chat space based on the provided member request and user details.
   *
   * <p>This method creates a {@link Membership} instance using the user and the membership role from the
   * provided add chat space member request. If either the request or user is null, the method returns null.</p>
   *
   * @param addChatSpaceMemberRequest the request containing details for adding a member to the chat space.
   * @param user the user to be added as a member to the chat space.
   * @return a {@link Membership} object configured with the specified user and membership details, or null if the input is invalid.
   */
  protected static Membership buildMembership(final AddChatSpaceMemberRequest addChatSpaceMemberRequest, final User user) {
    if (nonNull(addChatSpaceMemberRequest) && nonNull(user)) {
      return Membership.newBuilder()
        .setMember(user)
        .setCreateTime(convertToProtobufTimestamp(addChatSpaceMemberRequest.getCreateTime()))
        .setRole(Membership.MembershipRole.valueOf(addChatSpaceMemberRequest.getRole()))
        .build();
    }
    return null;
  }

  /**
   * Creates a user object for a member based on the provided request details.
   *
   * <p>This method constructs a {@link User} instance using the username and user type from the
   * specified add chat space member request. If the request is null, the method returns null.</p>
   *
   * @param addChatSpaceMemberRequest the request containing details for creating a member user.
   * @return a {@link User} object configured with the specified username and user type, or null if the input is invalid.
   */
  protected static User createMemberUser(final AddChatSpaceMemberRequest addChatSpaceMemberRequest) {
    if (nonNull(addChatSpaceMemberRequest)) {
      return User.newBuilder()
        .setName(addChatSpaceMemberRequest.getUsername())
        .setType(User.Type.valueOf(addChatSpaceMemberRequest.getUserType()))
        .build();
    }
    return null;
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
