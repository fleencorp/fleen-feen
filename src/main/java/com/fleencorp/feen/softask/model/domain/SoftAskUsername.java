package com.fleencorp.feen.softask.model.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "soft_ask_username", uniqueConstraints = {
  @UniqueConstraint(columnNames = {"soft_ask_id", "user_id"}),
  @UniqueConstraint(columnNames = {"soft_ask_id", "username"})
})
public class SoftAskUsername {

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

  @CreatedDate
  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  public static SoftAskUsername of(final Long softAskId, final Long userId, final String username) {
    final SoftAsk softAsk = SoftAsk.of(softAskId);

    final SoftAskUsername softAskUsername = new SoftAskUsername();
    softAskUsername.setSoftAskId(softAskId);
    softAskUsername.setSoftAsk(softAsk);
    softAskUsername.setUserId(userId);
    softAskUsername.setUsername(username);

    return softAskUsername;
  }
}
