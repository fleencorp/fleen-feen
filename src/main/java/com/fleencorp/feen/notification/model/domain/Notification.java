package com.fleencorp.feen.notification.model.domain;

import com.fleencorp.feen.contact.constant.ContactType;
import com.fleencorp.feen.follower.model.domain.Follower;
import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import com.fleencorp.feen.model.domain.social.ShareContactRequest;
import com.fleencorp.feen.notification.constant.NotificationStatus;
import com.fleencorp.feen.notification.constant.NotificationType;
import com.fleencorp.feen.user.model.domain.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

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

  @Column(name = "receiver_id", updatable = false)
  private Long receiverId;

  @Column(name = "initiator_or_requester_id", updatable = false)
  private Long initiatorOrRequesterId;

  @Column(name = "initiator_or_requester_name")
  private String initiatorOrRequesterName;

  @ToString.Exclude
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

  @Column(name = "stream_id", updatable = false)
  private Long streamId;

  @Column(name = "stream_title")
  private String streamTitle;

  @Column(name = "stream_attendee_id", updatable = false)
  private Long streamAttendeeId;

  @Column(name = "stream_attendee_name")
  private String streamAttendeeName;

  @Column(name = "chat_space_id", updatable = false)
  private Long chatSpaceId;

  @Column(name = "chat_space_title")
  private String chatSpaceTitle;

  @Column(name = "chat_space_member_id", updatable = false)
  private Long chatSpaceMemberId;

  @Column(name = "chat_space_member_name")
  private String chatSpaceMemberName;

  @ToString.Exclude
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
   * Creates a new Notification instance with the specified receiver.
   *
   * @param receiver the member who will receive the notification
   * @return a new Notification instance
   */
  public static Notification of(final Member receiver) {
    final Notification notification = new Notification();
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
