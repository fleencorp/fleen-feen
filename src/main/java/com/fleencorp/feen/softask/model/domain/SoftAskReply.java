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
import static jakarta.persistence.GenerationType.IDENTITY;
import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "soft_ask_reply")
public class SoftAskReply extends FleenFeenEntity implements SoftAskCommonData {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "soft_ask_reply_id", nullable = false, updatable = false, unique = true)
  private Long softAskReplyId;

  @Column(name = "content", nullable = false, length = 3000)
  private String content;

  @Column(name = "soft_ask_id", nullable = false, updatable = false, insertable = false)
  private Long softAskId;

  @ToString.Exclude
  @ManyToOne(fetch = LAZY, optional = false)
  @JoinColumn(name = "soft_ask_id", referencedColumnName = "soft_ask_id", nullable = false, updatable = false)
  private SoftAsk softAsk;

  @Column(name = "soft_ask_answer_id", nullable = false, updatable = false, insertable = false)
  private Long softAnswerId;

  @ToString.Exclude
  @ManyToOne(fetch = LAZY, optional = false)
  @JoinColumn(name = "soft_ask_answer_id", referencedColumnName = "soft_ask_answer_id", nullable = false, updatable = false)
  private SoftAskAnswer softAnswer;

  @Column(name = "author_id", nullable = false, updatable = false, insertable = false)
  private Long authorId;

  @CreatedBy
  @ToString.Exclude
  @ManyToOne(fetch = LAZY, optional = false)
  @JoinColumn(name = "author_id", referencedColumnName = "member_id", nullable = false, updatable = false)
  private Member author;

  @OneToMany(mappedBy = "parentReply", cascade = CascadeType.ALL, orphanRemoval = true)
  @ToString.Exclude
  private Set<SoftAskReply> childReplies = new HashSet<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_reply_id", referencedColumnName = "soft_ask_reply_id")
  @ToString.Exclude
  private SoftAskReply parentReply;

  @Column(name = "user_other_name", nullable = false, updatable = false, insertable = false)
  private String userOtherName;

  @Column(name = "is_deleted", nullable = false)
  private Boolean deleted = false;

  @Column(name = "vote_count", nullable = false)
  private Integer voteCount = 0;

  public Long getId() {
    return softAskReplyId;
  }

  public Long getParentId() {
    return nonNull(softAnswerId) ? softAnswerId : null;
  }

  @Override
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
