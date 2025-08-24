package com.fleencorp.feen.notification.service.impl;

import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.chat.space.model.domain.ChatSpaceMember;
import com.fleencorp.feen.follower.model.domain.Follower;
import com.fleencorp.feen.model.domain.social.ShareContactRequest;
import com.fleencorp.feen.notification.constant.NotificationStatus;
import com.fleencorp.feen.notification.model.domain.Notification;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.stream.model.domain.StreamAttendee;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.localizer.service.Localizer;
import com.fleencorp.localizer.service.adapter.DefaultLocalizer;
import org.springframework.stereotype.Component;

import static com.fleencorp.feen.notification.constant.NotificationType.*;
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
    return localizer.getMessage(notification.getMessageKey(), notification.getStreamTitle());
  }

  /**
   * Retrieves the localized message for a disapproved request to join an event.
   *
   * @param notification the notification object containing details such as the message key and event title (FleenStream)
   * @return the localized message indicating the disapproval of the request to join the event
   */
  public String ofDisapprovedRequestToJoinEvent(final Notification notification) {
    return localizer.getMessage(notification.getMessageKey(), notification.getStreamTitle());
  }

  /**
   * Retrieves the localized message for a received request to join an event.
   *
   * @param notification the notification object containing details such as the message key, initiator's name, and event title (FleenStream)
   * @return the localized message indicating the reception of the request to join the event
   */
  public String ofReceivedRequestToJoinEvent(final Notification notification) {
    return localizer.getMessage(notification.getMessageKey(), notification.getInitiatorOrRequesterName(), notification.getStreamTitle());
  }

  /**
   * Retrieves the localized message for an approved request to join a live broadcast.
   *
   * @param notification the notification object containing details such as the message key and live broadcast title (FleenStream)
   * @return the localized message indicating the approval of the request to join the live broadcast
   */
  public String ofApprovedRequestToJoinLiveBroadcast(final Notification notification) {
    return localizer.getMessage(notification.getMessageKey(), notification.getStreamTitle());
  }

  /**
   * Retrieves the localized message for a disapproved request to join a live broadcast.
   *
   * @param notification the notification object containing details such as the message key and live broadcast title (FleenStream)
   * @return the localized message indicating the disapproval of the request to join the live broadcast
   */
  public String ofDisapprovedRequestToJoinLiveBroadcast(final Notification notification) {
    return localizer.getMessage(notification.getMessageKey(), notification.getStreamTitle());
  }

  /**
   * Retrieves the localized message for a received request to join a live broadcast.
   *
   * @param notification the notification object containing details such as the message key, initiator's name, and live broadcast title (FleenStream)
   * @return the localized message indicating the reception of the request to join the live broadcast
   */
  public String ofReceivedRequestToJoinLiveBroadcast(final Notification notification) {
    return localizer.getMessage(notification.getMessageKey(), notification.getInitiatorOrRequesterName(), notification.getStreamTitle());
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
  public Notification ofApprovedOrDisapprovedChatSpaceJoinRequest(final ChatSpace chatSpace, final ChatSpaceMember chatSpaceMember, final Member member) {
    final Notification notification = ofApprovedChatSpaceJoinRequest(chatSpace, chatSpaceMember, member);
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
  public Notification ofApprovedChatSpaceJoinRequest(final ChatSpace chatSpace, final ChatSpaceMember chatSpaceMember, final Member member) {
    final Notification notification = new Notification();
    notification.markAsUnread();
    notification.setReceiver(member);
    notification.setChatSpace(chatSpace);
    notification.setChatSpaceMember(chatSpaceMember);
    notification.setReceiverId(member.getMemberId());
    notification.setChatSpaceTitle(chatSpace.getTitle());
    notification.setNotificationStatus(NotificationStatus.unread());
    notification.setChatSpaceMemberName(chatSpaceMember.getFullName());
    notification.setNotificationType(requestToJoinChatSpaceApproved());
    notification.setOtherComment(chatSpaceMember.getSpaceAdminComment());
    notification.setMessageKey(requestToJoinChatSpaceApproved().getCode());
    notification.setIdOrLinkOrUrl(String.valueOf(chatSpace.getChatSpaceId()));

    return notification;
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
  public Notification ofReceivedChatSpaceJoinRequest(final ChatSpace chatSpace, final ChatSpaceMember chatSpaceMember, final Member member, final Member requester) {
    final Notification notification = new Notification();
    notification.markAsUnread();
    notification.setReceiver(member);
    notification.setChatSpace(chatSpace);
    notification.setInitiatorOrRequester(requester);
    notification.setReceiverId(member.getMemberId());
    notification.setChatSpaceMember(chatSpaceMember);
    notification.setChatSpaceTitle(chatSpace.getTitle());
    notification.setNotificationStatus(NotificationStatus.unread());
    notification.setOtherComment(chatSpaceMember.getMemberComment());
    notification.setInitiatorOrRequesterName(requester.getFullName());
    notification.setNotificationType(requestToJoinChatSpaceReceived());
    notification.setChatSpaceMemberName(chatSpaceMember.getFullName());
    notification.setMessageKey(requestToJoinChatSpaceReceived().getCode());
    notification.setIdOrLinkOrUrl(String.valueOf(chatSpace.getChatSpaceId()));

    return notification;
  }

  /**
   * Creates a notification based on whether the request to join a FleenStream has been approved or disapproved.
   *
   * @param stream the FleenStream for which the request was made
   * @param streamAttendee the attendee whose request to join is being processed
   * @param member the member receiving the notification
   * @return a notification indicating whether the request to join was approved or disapproved
   */
  public Notification ofApprovedOrDisapprovedStreamJoinRequest(final FleenStream stream, final StreamAttendee streamAttendee, final Member member) {
    final Notification notification = ofApprovedStreamJoinRequest(stream, streamAttendee, member);
    updateNotificationTypeAndMessageKey(stream, notification, streamAttendee.isRequestToJoinApproved());
    return notification;
  }

  /**
   * Builds a notification for an approved request to join an event.
   *
   * @param stream      the stream associated with the approved request
   * @param streamAttendee   the attendee of the event receiving the notification
   * @param member           the member who approved the request
   * @return a Notification object representing the approved request to join the event
   */
  public Notification ofApprovedStreamJoinRequest(final FleenStream stream, final StreamAttendee streamAttendee, final Member member) {
    final Notification notification = new Notification();
    notification.markAsUnread();
    notification.setReceiver(member);
    notification.setStream(stream);
    notification.setStreamAttendee(streamAttendee);
    notification.setReceiverId(member.getMemberId());
    notification.setStreamTitle(stream.getTitle());
    notification.setNotificationType(requestToJoinEventApproved());
    notification.setNotificationStatus(NotificationStatus.unread());
    notification.setStreamAttendeeName(streamAttendee.getFullName());
    notification.setMessageKey(requestToJoinEventApproved().getCode());
    notification.setOtherComment(streamAttendee.getOrganizerComment());
    notification.setIdOrLinkOrUrl(String.valueOf(stream.getStreamId()));

    return notification;
  }

  /**
   * Builds a notification for a received request to join an event.
   *
   * @param stream      the event associated with the received request
   * @param streamAttendee   the attendee of the event receiving the notification
   * @param member           the member to whom the notification is sent
   * @param requester          the member who made the request to join the event
   * @return a Notification object representing the received request to join the event
   */
  public Notification ofReceivedStreamJoinRequest(final FleenStream stream, final StreamAttendee streamAttendee, final Member member, final Member requester) {
    final Notification notification = new Notification();
    notification.markAsUnread();
    notification.setReceiver(member);
    notification.setStream(stream);
    notification.setStreamAttendee(streamAttendee);
    notification.setInitiatorOrRequester(requester);
    notification.setReceiverId(member.getMemberId());
    notification.setStreamTitle(stream.getTitle());
    notification.setNotificationType(requestToJoinEventReceived());
    notification.setNotificationStatus(NotificationStatus.unread());
    notification.setStreamAttendeeName(streamAttendee.getFullName());
    notification.setInitiatorOrRequesterName(requester.getFullName());
    notification.setOtherComment(streamAttendee.getAttendeeComment());
    notification.setMessageKey(requestToJoinEventReceived().getCode());
    notification.setIdOrLinkOrUrl(String.valueOf(stream.getStreamId()));

    return notification;
  }

  /**
   * Updates the notification type and message key based on the given FleenStream and approval status.
   *
   * <p>This method sets the notification type and corresponding message key for the given notification
   * depending on whether the stream is a live broadcast or a standard event, and whether the request was approved.</p>
   *
   * @param stream the stream entity representing the event or broadcast to which the notification is linked.
   * @param notification the notification that will be updated with the appropriate type and message key.
   * @param approved a boolean indicating whether the request to join the event or broadcast was approved.
   */
  public void updateNotificationTypeAndMessageKey(final FleenStream stream, final Notification notification, final boolean approved) {
    if (nonNull(stream) && nonNull(notification)) {
      if (stream.isALiveStream() && approved) {
        notification.setNotificationType(requestToJoinLiveBroadcastApproved());
        notification.setMessageKey(requestToJoinLiveBroadcastApproved().getCode());
      } else if (stream.isALiveStream() && !approved) {
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
    final Notification notification = ofApprovedShareContactRequest(shareContactRequest, member);
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
  public Notification ofApprovedShareContactRequest(final ShareContactRequest shareContactRequest, final Member member) {
    final Notification notification = new Notification();
    notification.markAsUnread();
    notification.setReceiver(member);
    notification.setReceiverId(member.getMemberId());
    notification.setShareContactRequest(shareContactRequest);
    notification.setRecipient(shareContactRequest.getRecipient());
    notification.setNotificationType(shareContactRequestApproved());
    notification.setNotificationStatus(NotificationStatus.unread());
    notification.setContactType(shareContactRequest.getContactType());
    notification.setMessageKey(shareContactRequestApproved().getCode());
    notification.setRecipientName(shareContactRequest.getRecipientName());
    notification.setOtherComment(shareContactRequest.getRecipientComment());
    notification.setIdOrLinkOrUrl(String.valueOf(shareContactRequest.getShareContactRequestId()));

    return notification;
  }

  /**
   * Builds a notification for a received share contact request.
   *
   * @param shareContactRequest the share contact request that was received
   * @param member              the member to whom the notification is sent
   * @param requester           the member who is requesting for a contact
   * @return a Notification object representing the received share contact request
   */
  public Notification ofReceivedShareContactRequest(final ShareContactRequest shareContactRequest, final Member member, final Member requester) {
    final Notification notification = new Notification();
    notification.markAsUnread();
    notification.setReceiver(member);
    notification.setInitiatorOrRequester(requester);
    notification.setReceiverId(member.getMemberId());
    notification.setShareContactRequest(shareContactRequest);
    notification.setNotificationStatus(NotificationStatus.unread());
    notification.setNotificationType(shareContactRequestReceived());
    notification.setInitiatorOrRequesterName(requester.getFullName());
    notification.setContactType(shareContactRequest.getContactType());
    notification.setMessageKey(shareContactRequestReceived().getCode());
    notification.setOtherComment(shareContactRequest.getInitiatorComment());
    notification.setIdOrLinkOrUrl(String.valueOf(shareContactRequest.getShareContactRequestId()));

    return notification;
  }

  /**
   * Builds a notification for a user following another user.
   *
   * @param follower the follower object representing the user who is following
   * @param member   the member to whom the notification is sent
   * @return a Notification object representing the following action
   */
  public Notification ofFollowing(final Follower follower, final Member member) {
    final Notification notification = new Notification();
    notification.markAsUnread();
    notification.setReceiver(member);
    notification.setFollower(follower);
    notification.setReceiverId(member.getMemberId());
    notification.setNotificationType(userFollowing());
    notification.setMessageKey(userFollowing().getCode());
    notification.setInitiatorOrRequester(follower.getFollowing());
    notification.setNotificationStatus(NotificationStatus.unread());
    notification.setInitiatorOrRequesterName(follower.getFollowingName());
    notification.setIdOrLinkOrUrl(String.valueOf(follower.getFollowerId()));

    return notification;
  }

}
