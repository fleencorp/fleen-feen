package com.fleencorp.feen.notification.model.domain;

import com.fleencorp.feen.notification.constant.NotificationStatus;
import com.fleencorp.feen.notification.constant.NotificationType;
import com.fleencorp.feen.contact.constant.ContactType;
import com.fleencorp.feen.follower.model.domain.Follower;
import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.chat.space.model.domain.ChatSpaceMember;
import com.fleencorp.feen.model.domain.social.ShareContactRequest;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.stream.model.domain.StreamAttendee;
import com.fleencorp.feen.user.model.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notification")
public class Notification extends FleenFeenEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "notification_id", nullable = false, updatable = false, unique = true)
  private Long notificationId;

  @Enumerated(STRING)
  @Column(name = "notification_type", updatable = false, nullable = false)
  private NotificationType notificationType;

  @Column(name = "receiver_id", insertable = false, updatable = false)
  private Long receiverId;

  @ManyToOne(fetch = LAZY, optional = false, targetEntity = Member.class)
  @JoinColumn(name = "receiver_id", referencedColumnName = "member_id", nullable = false, updatable = false)
  private Member receiver;

  @ManyToOne(fetch = LAZY, targetEntity = Member.class)
  @JoinColumn(name = "initiator_or_requester_id", referencedColumnName = "member_id")
  private Member initiatorOrRequester;

  @Column(name = "initiator_or_requester_name")
  private String initiatorOrRequesterName;

  @ManyToOne(fetch = LAZY, targetEntity = Member.class)
  @JoinColumn(name = "recipient_id", referencedColumnName = "member_id")
  private Member recipient;

  @Column(name = "recipient_name")
  private String recipientName;

  @Column(name = "message_key", nullable = false, updatable = false)
  private String messageKey;

  @Column(name = "other_comment")
  private String otherComment;

  @Column(name = "id_or_link_or_url", nullable = false, updatable = false)
  private String idOrLinkOrUrl;

  @Column(name = "is_read", nullable = false)
  private Boolean isRead = false;

  @Enumerated(STRING)
  @Column(name = "notification_status", nullable = false)
  private NotificationStatus notificationStatus;

  @Column(name = "notification_read_on")
  protected LocalDateTime notificationReadOn;

  @ManyToOne(fetch = LAZY, targetEntity = ShareContactRequest.class)
  @JoinColumn(name = "share_contact_request_id", referencedColumnName = "share_contact_request_id")
  private ShareContactRequest shareContactRequest;

  @Enumerated(STRING)
  @Column(name = "contact_type")
  private ContactType contactType;

  @ManyToOne(fetch = LAZY, targetEntity = FleenStream.class)
  @JoinColumn(name = "stream_id", referencedColumnName = "stream_id")
  private FleenStream stream;

  @Column(name = "stream_title")
  private String streamTitle;

  @ManyToOne(fetch = LAZY, targetEntity = StreamAttendee.class)
  @JoinColumn(name = "stream_attendee_id", referencedColumnName = "stream_attendee_id")
  private StreamAttendee streamAttendee;

  @Column(name = "stream_attendee_name")
  private String streamAttendeeName;

  @ManyToOne(fetch = LAZY, targetEntity = ChatSpace.class)
  @JoinColumn(name = "chat_space_id", referencedColumnName = "chat_space_id")
  private ChatSpace chatSpace;

  @Column(name = "chat_space_title")
  private String chatSpaceTitle;

  @ManyToOne(fetch = LAZY, targetEntity = ChatSpaceMember.class)
  @JoinColumn(name = "chat_space_member_id", referencedColumnName = "chat_space_member_id")
  private ChatSpaceMember chatSpaceMember;

  @Column(name = "chat_space_member_name")
  private String chatSpaceMemberName;

  @ManyToOne(fetch = LAZY, targetEntity = Follower.class)
  @JoinColumn(name = "follower_id", referencedColumnName = "follower_id")
  private Follower follower;

  @Column(name = "follower_name")
  private String followerName;

  /**
   * Marks the notification as read and updates the read timestamp.
   */
  public void markAsRead() {
    isRead = true;
    notificationStatus = NotificationStatus.read();
    notificationReadOn = LocalDateTime.now();
  }

  /**
   * Marks the notification as unread and clears the read timestamp.
   */
  public void markAsUnread() {
    isRead = false;
    notificationStatus = NotificationStatus.unread();
    notificationReadOn = null;
  }

  /**
   * Checks if the given user ID matches the notification receiver.
   *
   * @param userId the user ID to check
   * @return true if the user is the receiver of this notification
   */
  public boolean isOwner(final Long userId) {
    return receiverId.equals(userId);
  }


  /**
   * Retrieves the stream title if this notification is related to a stream.
   *
   * @return the stream title if available; otherwise, null
   */
  public String getStreamTitle() {
    return nonNull(stream) ? stream.getTitle() : streamTitle;
  }

  /**
   * Retrieves the chat space title if this notification is related to a chat space.
   *
   * @return the chat space title if available; otherwise, null
   */
  public String getChatSpaceTitle() {
    return nonNull(chatSpace) ? chatSpace.getTitle() : chatSpaceTitle;
  }

  /**
   * Creates a new Notification instance with the specified receiver.
   *
   * @param receiver the member who will receive the notification
   * @return a new Notification instance
   */
  public static Notification of(final Member receiver) {
    final Notification notification = new Notification();
    notification.setReceiver(receiver);
    notification.setReceiverId(receiver.getMemberId());
    return notification;
  }

  /**
   * Returns an empty Notification instance (null).
   *
   * @return null
   */
  public static Notification empty() {
    return null;
  }
}
