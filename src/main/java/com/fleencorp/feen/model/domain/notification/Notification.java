package com.fleencorp.feen.model.domain.notification;

import com.fleencorp.feen.constant.notification.NotificationStatus;
import com.fleencorp.feen.constant.notification.NotificationType;
import com.fleencorp.feen.constant.social.ContactType;
import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.chat.ChatSpaceMember;
import com.fleencorp.feen.model.domain.social.ShareContactRequest;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.domain.user.Follower;
import com.fleencorp.feen.model.domain.user.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@SuperBuilder
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

  @Column(name = "other_comment", nullable = false, updatable = false)
  private String otherComment;

  @Column(name = "id_or_link_or_url", nullable = false, updatable = false)
  private String idOrLinkOrUrl;

  @Column(name = "is_read", nullable = false)
  private Boolean isRead;

  @Enumerated(STRING)
  @Column(name = "notification_status", updatable = false, nullable = false)
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
  @JoinColumn(name = "fleen_stream_id", referencedColumnName = "fleen_stream_id")
  private FleenStream fleenStream;

  @Column(name = "fleen_stream_title")
  private String fleenStreamTitle;

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

  public void setIsRead() {
    notificationStatus = NotificationStatus.read();
  }

  public boolean isOwner(final Long userId) {
    return receiverId.equals(userId);
  }

}
