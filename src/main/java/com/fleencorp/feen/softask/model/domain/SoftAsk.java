package com.fleencorp.feen.softask.model.domain;

import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import com.fleencorp.feen.softask.constant.core.SoftAskParentType;
import com.fleencorp.feen.softask.constant.core.SoftAskStatus;
import com.fleencorp.feen.softask.constant.core.SoftAskVisibility;
import com.fleencorp.feen.softask.constant.other.ModerationStatus;
import com.fleencorp.feen.softask.contract.SoftAskCommonData;
import com.fleencorp.feen.softask.exception.core.SoftAskUpdateDeniedException;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.user.model.domain.Member;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;

import java.util.HashSet;
import java.util.Set;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "soft_ask")
public class SoftAsk extends FleenFeenEntity implements SoftAskCommonData {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "soft_ask_id", nullable = false, updatable = false, unique = true)
  private Long softAskId;

  @Column(name = "title", nullable = false, length = 500)
  private String title;

  @Column(name = "description", nullable = false, length = 4000)
  private String description;

  @Column(name = "summary", length = 4000)
  private String summary;

  @Column(name = "tags", length = 1000)
  private String tags;

  @Column(name = "other_text", nullable = false, length = 2000)
  private String otherText;

  @Column(name = "link", length = 1000)
  private String link;

  @Column(name = "parent_id", updatable = false)
  private Long parentId;

  @Column(name = "parent_title", length = 1000, updatable = false)
  private String parentTitle;

  @Enumerated(STRING)
  @Column(name = "parent_type", nullable = false)
  private SoftAskParentType softAskParentType = SoftAskParentType.NONE;

  @Enumerated(STRING)
  @Column(name = "visibility", nullable = false)
  private SoftAskVisibility softAskVisibility;

  @Enumerated(STRING)
  @Column(name = "status", nullable = false)
  private SoftAskStatus softAskStatus;

  @Enumerated(STRING)
  @Column(name = "moderation_status", nullable = false)
  private ModerationStatus moderationStatus;

  @Column(name = "chat_space_id", nullable = false, updatable = false, insertable = false)
  private Long chatSpaceId;

  @ToString.Exclude
  @ManyToOne(fetch = LAZY, optional = false, targetEntity = ChatSpace.class)
  @JoinColumn(name = "chat_space_id", referencedColumnName = "chat_space_id", nullable = false, updatable = false)
  private ChatSpace chatSpace;

  @Column(name = "stream_id", updatable = false, insertable = false)
  private Long streamId;

  @ToString.Exclude
  @ManyToOne(fetch = LAZY, targetEntity = FleenStream.class)
  @JoinColumn(name = "stream_id", referencedColumnName = "stream_id", updatable = false)
  private FleenStream stream;

  @Column(name = "author_id", nullable = false, updatable = false, insertable = false)
  private Long authorId;

  @CreatedBy
  @ToString.Exclude
  @ManyToOne(fetch = LAZY, optional = false, targetEntity = Member.class)
  @JoinColumn(name = "author_id", referencedColumnName = "member_id", nullable = false, updatable = false)
  private Member author;

  @Column(name = "user_other_name", nullable = false, updatable = false, insertable = false)
  private String userOtherName;

  @Column(name = "is_deleted", nullable = false)
  private Boolean deleted = false;

  @Column(name = "is_visible", nullable = false)
  private Boolean visible = true;

  @Column(name = "reply_count", nullable = false)
  private Integer replyCount = 0;

  @Column(name = "vote_count", nullable = false)
  private Integer voteCount = 0;

  @Column(name = "bookmark_count", nullable = false)
  private Integer bookmarkCount = 0;

  @Column(name = "share_count", nullable = false)
  private Integer shareCount = 0;

  @ToString.Exclude
  @OneToMany(fetch = LAZY, mappedBy = "softAsk", targetEntity = SoftAskReply.class, cascade = CascadeType.ALL)
  private Set<SoftAskReply> replies = new HashSet<>();

  public Long getId() {
    return softAskId;
  }

  public boolean isDeleted() {
    return nonNull(deleted) && deleted;
  }

  public void setContent(final String content) {
    this.description = content;
  }

  public void delete() {
    deleted = true;
  }

  public void checkIsAuthor(final Long userId) throws SoftAskUpdateDeniedException {
    if (nonNull(authorId) && !authorId.equals(userId)) {
      throw SoftAskUpdateDeniedException.of();
    }
  }

  public void checkIsReplyIsNotMoreThanOne() {
    if (nonNull(replyCount) && replyCount > 0) {
      throw SoftAskUpdateDeniedException.of();
    }
  }

  public static SoftAsk of(final Long softAskId) {
    final SoftAsk softAsk = new SoftAsk();
    softAsk.setSoftAskId(softAskId);

    return softAsk;
  }
}
