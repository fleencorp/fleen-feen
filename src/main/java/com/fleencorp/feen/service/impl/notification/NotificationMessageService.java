package com.fleencorp.feen.service.impl.notification;

import com.fleencorp.feen.constant.notification.NotificationStatus;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.chat.ChatSpaceMember;
import com.fleencorp.feen.model.domain.notification.Notification;
import com.fleencorp.feen.model.domain.social.ShareContactRequest;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.domain.user.Follower;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.localizer.service.Localizer;
import com.fleencorp.localizer.service.adapter.DefaultLocalizer;
import org.springframework.stereotype.Component;

import static com.fleencorp.feen.constant.notification.NotificationType.*;
import static java.util.Objects.nonNull;

/**
 * Service component responsible for handling notification messages.
 *
 * <p>The {@code NotificationMessageService} class provides functionality to manage localized notification messages
 * within the application. It leverages the {@link Localizer} to generate localized responses for
 * notifications that are tailored to the user's locale.</p>
 *
 * <p>This class is designed to be used as a Spring {@link Component}, allowing it to be automatically detected
 * and managed by the Spring container.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Component
public class NotificationMessageService {

  private final DefaultLocalizer localizer;

  /**
   * Constructs a new {@code NotificationMessageService} with the provided {@link Localizer}.
   *
   * @param localizer the service responsible for handling localized responses for notifications
   */
  public NotificationMessageService(final DefaultLocalizer localizer) {
    this.localizer = localizer;
  }

  /**
   * Retrieves the localized message for an approved request to join a chat space.
   *
   * @param notification the notification object containing details such as the message key and chat space title
   * @return the localized message indicating the approval of the request to join the chat space
   */
  public String ofApprovedRequestToJoinChatSpace(final Notification notification) {
    return localizer.getMessage(notification.getMessageKey(), notification.getChatSpaceTitle());
  }

  /**
   * Retrieves the localized message for a disapproved request to join a chat space.
   *
   * @param notification the notification object containing details such as the message key and chat space title
   * @return the localized message indicating the disapproval of the request to join the chat space
   */
  public String ofDisapprovedRequestToJoinChatSpace(final Notification notification) {
    return localizer.getMessage(notification.getMessageKey(), notification.getChatSpaceTitle());
  }

  /**
   * Retrieves the localized message for a received request to join a chat space.
   *
   * @param notification the notification object containing details such as the message key, initiator's name, and chat space title
   * @return the localized message indicating the reception of the request to join the chat space
   */
  public String ofReceivedRequestToJoinChatSpace(final Notification notification) {
    return localizer.getMessage(notification.getMessageKey(), notification.getInitiatorOrRequesterName(), notification.getChatSpaceTitle());
  }

  /**
   * Retrieves the localized message for an approved request to join an event.
   *
   * @param notification the notification object containing details such as the message key and event title (FleenStream)
   * @return the localized message indicating the approval of the request to join the event
   */
  public String ofApprovedRequestToJoinEvent(final Notification notification) {
    return localizer.getMessage(notification.getMessageKey(), notification.getFleenStreamTitle());
  }

  /**
   * Retrieves the localized message for a disapproved request to join an event.
   *
   * @param notification the notification object containing details such as the message key and event title (FleenStream)
   * @return the localized message indicating the disapproval of the request to join the event
   */
  public String ofDisapprovedRequestToJoinEvent(final Notification notification) {
    return localizer.getMessage(notification.getMessageKey(), notification.getFleenStreamTitle());
  }

  /**
   * Retrieves the localized message for a received request to join an event.
   *
   * @param notification the notification object containing details such as the message key, initiator's name, and event title (FleenStream)
   * @return the localized message indicating the reception of the request to join the event
   */
  public String ofReceivedRequestToJoinEvent(final Notification notification) {
    return localizer.getMessage(notification.getMessageKey(), notification.getInitiatorOrRequesterName(), notification.getFleenStreamTitle());
  }

  /**
   * Retrieves the localized message for an approved request to join a live broadcast.
   *
   * @param notification the notification object containing details such as the message key and live broadcast title (FleenStream)
   * @return the localized message indicating the approval of the request to join the live broadcast
   */
  public String ofApprovedRequestToJoinLiveBroadcast(final Notification notification) {
    return localizer.getMessage(notification.getMessageKey(), notification.getFleenStreamTitle());
  }

  /**
   * Retrieves the localized message for a disapproved request to join a live broadcast.
   *
   * @param notification the notification object containing details such as the message key and live broadcast title (FleenStream)
   * @return the localized message indicating the disapproval of the request to join the live broadcast
   */
  public String ofDisapprovedRequestToJoinLiveBroadcast(final Notification notification) {
    return localizer.getMessage(notification.getMessageKey(), notification.getFleenStreamTitle());
  }

  /**
   * Retrieves the localized message for a received request to join a live broadcast.
   *
   * @param notification the notification object containing details such as the message key, initiator's name, and live broadcast title (FleenStream)
   * @return the localized message indicating the reception of the request to join the live broadcast
   */
  public String ofReceivedRequestToJoinLiveBroadcast(final Notification notification) {
    return localizer.getMessage(notification.getMessageKey(), notification.getInitiatorOrRequesterName(), notification.getFleenStreamTitle());
  }

  /**
   * Retrieves the localized message for an approved contact sharing request.
   *
   * @param notification the notification object containing details such as the message key and recipient's name
   * @return the localized message indicating the approval of the contact sharing request
   */
  public String ofApprovedShareContactRequest(final Notification notification) {
    return localizer.getMessage(notification.getMessageKey(), notification.getRecipientName());
  }

  /**
   * Retrieves the localized message for a disapproved contact sharing request.
   *
   * @param notification the notification object containing details such as the message key and recipient's name
   * @return the localized message indicating the disapproval of the contact sharing request
   */
  public String ofDisapprovedShareContactRequest(final Notification notification) {
    return localizer.getMessage(notification.getMessageKey(), notification.getRecipientName());
  }

  /**
   * Retrieves the localized message for a received contact sharing request.
   *
   * @param notification the notification object containing details such as the message key, recipient's name, and contact type
   * @return the localized message indicating the reception of the contact sharing request
   */
  public String ofReceivedShareContactRequest(final Notification notification) {
    return localizer.getMessage(notification.getMessageKey(), notification.getRecipientName(), notification.getContactType().getValue());
  }

  /**
   * Retrieves the localized message for a user following another user.
   *
   * @param notification the notification object containing details such as the message key and follower's name
   * @return the localized message indicating the user following action
   */
  public String ofUserFollowing(final Notification notification) {
    return localizer.getMessage(notification.getMessageKey(), notification.getFollowerName());
  }

  /**
   * Creates a notification based on whether a request to join a chat space has been approved or disapproved.
   *
   * @param chatSpace the chat space the member is requesting to join
   * @param chatSpaceMember the chat space member whose join request is being processed
   * @param member the member receiving the notification
   * @return a notification indicating whether the request to join the chat space was approved or disapproved
   */
  public Notification ofApprovedOrDisapproved(final ChatSpace chatSpace, final ChatSpaceMember chatSpaceMember, final Member member) {
    final Notification notification = ofApproved(chatSpace, chatSpaceMember, member);
    if (chatSpaceMember.isRequestToJoinDisapproved()) {
      notification.setMessageKey(requestToJoinChatSpaceDisapproved().getCode());
      notification.setNotificationType(requestToJoinChatSpaceDisapproved());
    }
    return notification;
  }

  /**
   * Builds a notification for an approved request to join a chat space.
   *
   * @param chatSpace      the chat space associated with the approved request
   * @param chatSpaceMember the member of the chat space receiving the notification
   * @param member         the member who approved the request
   * @return a Notification object representing the approved request to join the chat space
   */
  public Notification ofApproved(final ChatSpace chatSpace, final ChatSpaceMember chatSpaceMember, final Member member) {
    return Notification.builder()
      .notificationType(requestToJoinChatSpaceApproved())
      .receiverId(member.getMemberId())
      .receiver(member)
      .messageKey(requestToJoinChatSpaceApproved().getCode())
      .otherComment(chatSpaceMember.getSpaceAdminComment())
      .isRead(false)
      .idOrLinkOrUrl(String.valueOf(chatSpace.getChatSpaceId()))
      .notificationStatus(NotificationStatus.unread())
      .chatSpace(chatSpace)
      .chatSpaceTitle(chatSpace.getTitle())
      .chatSpaceMember(chatSpaceMember)
      .chatSpaceMemberName(chatSpaceMember.getFullName())
      .build();
  }

  /**
   * Builds a notification for a received request to join a chat space.
   *
   * @param chatSpace      the chat space associated with the received request
   * @param chatSpaceMember the member of the chat space receiving the notification
   * @param member         the member to whom the notification is sent
   * @param requester        the member who made the request to join the chat space
   * @return a Notification object representing the received request to join the chat space
   */
  public Notification ofReceived(final ChatSpace chatSpace, final ChatSpaceMember chatSpaceMember, final Member member, final Member requester) {
    return Notification.builder()
      .notificationType(requestToJoinChatSpaceReceived())
      .receiverId(member.getMemberId())
      .receiver(member)
      .initiatorOrRequester(requester)
      .initiatorOrRequesterName(requester.getFullName())
      .messageKey(requestToJoinChatSpaceReceived().getCode())
      .otherComment(chatSpaceMember.getMemberComment())
      .isRead(false)
      .idOrLinkOrUrl(String.valueOf(chatSpace.getChatSpaceId()))
      .notificationStatus(NotificationStatus.unread())
      .chatSpace(chatSpace)
      .chatSpaceTitle(chatSpace.getTitle())
      .chatSpaceMember(chatSpaceMember)
      .chatSpaceMemberName(chatSpaceMember.getFullName())
      .build();
  }

  /**
   * Creates a notification based on whether the request to join a FleenStream has been approved or disapproved.
   *
   * @param fleenStream the FleenStream for which the request was made
   * @param streamAttendee the attendee whose request to join is being processed
   * @param member the member receiving the notification
   * @return a notification indicating whether the request to join was approved or disapproved
   */
  public Notification ofApprovedOrDisapproved(final FleenStream fleenStream, final StreamAttendee streamAttendee, final Member member) {
    final Notification notification = ofApproved(fleenStream, streamAttendee, member);
    updateNotificationTypeAndMessageKey(fleenStream, notification, streamAttendee.isRequestToJoinApproved());
    return notification;
  }

  /**
   * Builds a notification for an approved request to join an event.
   *
   * @param fleenStream      the event associated with the approved request
   * @param streamAttendee   the attendee of the event receiving the notification
   * @param member           the member who approved the request
   * @return a Notification object representing the approved request to join the event
   */
  public Notification ofApproved(final FleenStream fleenStream, final StreamAttendee streamAttendee, final Member member) {
    return Notification.builder()
      .notificationType(requestToJoinEventApproved())
      .receiverId(member.getMemberId())
      .receiver(member)
      .messageKey(requestToJoinEventApproved().getCode())
      .otherComment(streamAttendee.getOrganizerComment())
      .isRead(false)
      .idOrLinkOrUrl(String.valueOf(fleenStream.getStreamId()))
      .notificationStatus(NotificationStatus.unread())
      .fleenStream(fleenStream)
      .fleenStreamTitle(fleenStream.getTitle())
      .streamAttendee(streamAttendee)
      .streamAttendeeName(streamAttendee.getFullName())
      .build();
  }

  /**
   * Builds a notification for a received request to join an event.
   *
   * @param fleenStream      the event associated with the received request
   * @param streamAttendee   the attendee of the event receiving the notification
   * @param member           the member to whom the notification is sent
   * @param requester          the member who made the request to join the event
   * @return a Notification object representing the received request to join the event
   */
  public Notification ofReceived(final FleenStream fleenStream, final StreamAttendee streamAttendee, final Member member, final Member requester) {
    return Notification.builder()
      .notificationType(requestToJoinEventReceived())
      .receiverId(member.getMemberId())
      .receiver(member)
      .initiatorOrRequester(requester)
      .initiatorOrRequesterName(requester.getFullName())
      .messageKey(requestToJoinEventReceived().getCode())
      .otherComment(streamAttendee.getAttendeeComment())
      .isRead(false)
      .idOrLinkOrUrl(String.valueOf(fleenStream.getStreamId()))
      .notificationStatus(NotificationStatus.unread())
      .fleenStream(fleenStream)
      .fleenStreamTitle(fleenStream.getTitle())
      .streamAttendee(streamAttendee)
      .streamAttendeeName(streamAttendee.getFullName())
      .build();
  }

  /**
   * Updates the notification type and message key based on the given FleenStream and approval status.
   *
   * <p>This method sets the notification type and corresponding message key for the given notification
   * depending on whether the stream is a live broadcast or a standard event, and whether the request was approved.</p>
   *
   * @param fleenStream the stream entity representing the event or broadcast to which the notification is linked.
   * @param notification the notification that will be updated with the appropriate type and message key.
   * @param approved a boolean indicating whether the request to join the event or broadcast was approved.
   */
  public void updateNotificationTypeAndMessageKey(final FleenStream fleenStream, final Notification notification, final boolean approved) {
    if (nonNull(fleenStream) && nonNull(notification)) {
      if (fleenStream.isALiveStream() && approved) {
        notification.setNotificationType(requestToJoinLiveBroadcastApproved());
        notification.setMessageKey(requestToJoinLiveBroadcastApproved().getCode());
      } else if (fleenStream.isALiveStream() && !approved) {
        notification.setNotificationType(requestToJoinLiveBroadcastDisapproved());
        notification.setMessageKey(requestToJoinLiveBroadcastDisapproved().getCode());
      } else {
        notification.setNotificationType(requestToJoinEventDisapproved());
        notification.setMessageKey(requestToJoinEventDisapproved().getCode());
      }
    }
  }

  /**
   * Creates a notification based on whether a share contact request has been approved or disapproved.
   *
   * @param shareContactRequest the contact sharing request being processed
   * @param member the member receiving the notification
   * @return a notification indicating whether the share contact request was approved or disapproved
   */
  public Notification ofApprovedOrDisapproved(final ShareContactRequest shareContactRequest, final Member member) {
    final Notification notification = ofApproved(shareContactRequest, member);
    if (shareContactRequest.isRejected()) {
      notification.setNotificationType(shareContactRequestDisapproved());
      notification.setMessageKey(shareContactRequestDisapproved().getCode());
    }
    return notification;
  }

  /**
   * Builds a notification for an approved share contact request.
   *
   * @param shareContactRequest the share contact request that was approved
   * @param member              the member who approved the request
   * @return a Notification object representing the approved share contact request
   */
  public Notification ofApproved(final ShareContactRequest shareContactRequest, final Member member) {
    return Notification.builder()
      .notificationType(shareContactRequestApproved())
      .receiverId(member.getMemberId())
      .receiver(member)
      .recipient(shareContactRequest.getRecipient())
      .recipientName(shareContactRequest.getRecipient().getFullName())
      .messageKey(shareContactRequestApproved().getCode())
      .otherComment(shareContactRequest.getRecipientComment())
      .isRead(false)
      .idOrLinkOrUrl(String.valueOf(shareContactRequest.getShareContactRequestId()))
      .notificationStatus(NotificationStatus.unread())
      .shareContactRequest(shareContactRequest)
      .contactType(shareContactRequest.getContactType())
      .build();
  }

  /**
   * Builds a notification for a received share contact request.
   *
   * @param shareContactRequest the share contact request that was received
   * @param member              the member to whom the notification is sent
   * @param requester           the member who is requesting for a contact
   * @return a Notification object representing the received share contact request
   */
  public Notification ofReceived(final ShareContactRequest shareContactRequest, final Member member, final Member requester) {
    return Notification.builder()
      .notificationType(shareContactRequestReceived())
      .receiverId(member.getMemberId())
      .receiver(member)
      .initiatorOrRequester(requester)
      .initiatorOrRequesterName(requester.getFullName())
      .messageKey(shareContactRequestReceived().getCode())
      .otherComment(shareContactRequest.getInitiatorComment())
      .isRead(false)
      .idOrLinkOrUrl(String.valueOf(shareContactRequest.getShareContactRequestId()))
      .notificationStatus(NotificationStatus.unread())
      .shareContactRequest(shareContactRequest)
      .contactType(shareContactRequest.getContactType())
      .build();
  }

  /**
   * Builds a notification for a user following another user.
   *
   * @param follower the follower object representing the user who is following
   * @param member   the member to whom the notification is sent
   * @return a Notification object representing the following action
   */
  public Notification ofFollowing(final Follower follower, final Member member) {
    return Notification.builder()
      .notificationType(userFollowing())
      .receiverId(member.getMemberId())
      .receiver(member)
      .initiatorOrRequester(follower.getFollowing())
      .initiatorOrRequesterName(follower.getFollowing().getFullName())
      .messageKey(userFollowing().getCode())
      .idOrLinkOrUrl(String.valueOf(follower.getFollowerId()))
      .notificationStatus(NotificationStatus.unread())
      .follower(follower)
      .build();
  }

}
