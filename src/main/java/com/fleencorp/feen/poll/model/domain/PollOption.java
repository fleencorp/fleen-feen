package com.fleencorp.feen.poll.model.domain;

import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "poll_option", uniqueConstraints = {
  @UniqueConstraint(columnNames = {"poll_id", "member_id", "option_id"})
})
public class PollOption extends FleenFeenEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "poll_option_id", nullable = false, updatable = false, unique = true)
  private Long pollOptionId;

  @Column(name = "option_text", nullable = false, length = 1000)
  private String optionText;

  @Column(name = "poll_id", insertable = false, updatable = false)
  private Long pollId;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "poll_id", nullable = false, updatable = false)
  private Poll poll;

  @Column(name = "vote_count", nullable = false)
  private Integer voteCount = 0;

  public boolean hasChanged(final String newText) {
    return !Objects.equals(optionText, newText);
  }

  public static PollOption of(final Long optionId) {
    final PollOption pollOption = new PollOption();
    pollOption.setPollOptionId(optionId);

    return pollOption;
  }
}
