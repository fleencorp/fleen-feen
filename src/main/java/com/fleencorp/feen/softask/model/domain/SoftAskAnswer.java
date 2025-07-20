package com.fleencorp.feen.softask.model.domain;

import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import com.fleencorp.feen.softask.contract.SoftAskCommonData;
import com.fleencorp.feen.softask.exception.core.SoftAskUpdateDeniedException;
import com.fleencorp.feen.user.model.domain.Member;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;

import java.util.HashSet;
import java.util.Set;

import static jakarta.persistence.FetchType.LAZY;
import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "soft_ask_answer")
public class SoftAskAnswer extends FleenFeenEntity implements SoftAskCommonData {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "soft_ask_answer_id", nullable = false, updatable = false, unique = true)
  private Long softAskAnswerId;

  @Column(name = "content", nullable = false, length = 4000)
  private String content;

  @Column(name = "soft_ask_id", nullable = false, updatable = false, insertable = false)
  private Long softAskId;

  @ToString.Exclude
  @ManyToOne(fetch = LAZY, optional = false)
  @JoinColumn(name = "soft_ask_id", referencedColumnName = "soft_ask_id", nullable = false, updatable = false)
  private SoftAsk softAsk;

  @Column(name = "author_id", nullable = false, updatable = false, insertable = false)
  private Long authorId;

  @CreatedBy
  @ToString.Exclude
  @ManyToOne(fetch = LAZY, optional = false)
  @JoinColumn(name = "author_id", referencedColumnName = "member_id", nullable = false, updatable = false)
  private Member author;

  @Column(name = "user_other_name", nullable = false, updatable = false, insertable = false)
  private String userOtherName;

  @OneToMany(fetch = LAZY, mappedBy = "softAnswer", orphanRemoval = true, targetEntity = SoftAskReply.class, cascade = CascadeType.ALL)
  private Set<SoftAskReply> replies = new HashSet<>();

  @Column(name = "is_deleted", nullable = false)
  private Boolean deleted = false;

  @Column(name = "reply_count", nullable = false)
  private Integer replyCount = 0;

  @Column(name = "vote_count", nullable = false)
  private Integer voteCount = 0;

  public Long getId() {
    return softAskAnswerId;
  }

  public Long getParentId() {
    return nonNull(softAskId) ? softAskId : null;
  }

  public String getParentTitle() {
    return "";
  }

  public boolean isDeleted() {
    return nonNull(deleted) && deleted;
  }

  public void delete() {
    deleted = true;
  }

  public void checkIsAuthor(final Long userId) throws SoftAskUpdateDeniedException {
    if (nonNull(authorId) && !authorId.equals(userId)) {
      throw SoftAskUpdateDeniedException.of();
    }
  }
}