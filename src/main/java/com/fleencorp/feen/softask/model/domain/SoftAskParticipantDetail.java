package com.fleencorp.feen.softask.model.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "soft_ask_participant_detail", uniqueConstraints = {
  @UniqueConstraint(columnNames = {"soft_ask_id", "user_id"}),
  @UniqueConstraint(columnNames = {"soft_ask_id", "username"})
})
public class SoftAskParticipantDetail {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "soft_ask_id", nullable = false, updatable = false)
  private Long softAskId;

  @Column(name = "user_id", nullable = false, updatable = false)
  private Long userId;

  @Column(name = "username", length = 100, nullable = false, updatable = false)
  private String username;

  @Column(name = "display_name", length = 100, nullable = false, updatable = false)
  private String displayName;

  @Column(name = "avatar", length = 1000, nullable = false, updatable = false)
  private String avatarUrl;

  @CreatedDate
  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  public static SoftAskParticipantDetail of(final Long softAskId, final Long userId, final String username, final String displayName) {
    final SoftAskParticipantDetail participantDetail = new SoftAskParticipantDetail();
    participantDetail.setSoftAskId(softAskId);
    participantDetail.setUserId(userId);
    participantDetail.setUsername(username);
    participantDetail.setDisplayName(displayName);

    return participantDetail;
  }

  public static SoftAskParticipantDetail of(final String username, final String displayName, final String avatarUrl) {
    final SoftAskParticipantDetail participantDetail = new SoftAskParticipantDetail();
    participantDetail.setUsername(username);
    participantDetail.setDisplayName(displayName);
    participantDetail.setAvatarUrl(avatarUrl);

    return participantDetail;
  }
}
