package com.fleencorp.feen.softask.model.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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
  private Long id;

  @Column(name = "soft_ask_id", nullable = false, updatable = false, insertable = false)
  private Long softAskId;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "soft_ask_id", nullable = false, updatable = false)
  private SoftAsk softAsk;

  @Column(name = "user_id", nullable = false, updatable = false)
  private Long userId;

  @Column(name = "username", nullable = false, length = 100)
  private String username;

  @Column(name = "display_name", nullable = false, length = 100)
  private String displayName;

  @Column(name = "avatar", nullable = false, length = 1000)
  private String avatarUrl;

  @CreatedDate
  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  public static SoftAskParticipantDetail of(final Long softAskId, final Long userId, final String username, final String displayName) {
    final SoftAsk softAsk = SoftAsk.of(softAskId);

    final SoftAskParticipantDetail softAskParticipantDetail = new SoftAskParticipantDetail();
    softAskParticipantDetail.setSoftAskId(softAskId);
    softAskParticipantDetail.setSoftAsk(softAsk);
    softAskParticipantDetail.setUserId(userId);
    softAskParticipantDetail.setUsername(username);
    softAskParticipantDetail.setDisplayName(displayName);

    return softAskParticipantDetail;
  }
}
