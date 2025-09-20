package com.fleencorp.feen.softask.model.domain;

import com.fleencorp.feen.common.constant.location.LocationVisibility;
import com.fleencorp.feen.model.contract.HasTitle;
import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import com.fleencorp.feen.softask.constant.other.ModerationStatus;
import com.fleencorp.feen.softask.constant.other.MoodTag;
import com.fleencorp.feen.softask.contract.SoftAskCommonData;
import com.fleencorp.feen.softask.exception.core.SoftAskUpdateDeniedException;
import com.fleencorp.feen.shared.common.util.ParentInfoUtil;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static com.fleencorp.feen.common.util.common.HybridSlugGenerator.generateHybridSlug;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "soft_ask_reply")
public class SoftAskReply extends FleenFeenEntity
  implements HasTitle, SoftAskCommonData {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "soft_ask_reply_id", nullable = false, updatable = false, unique = true)
  private Long softAskReplyId;

  @Column(name = "content", nullable = false, length = 3000)
  private String content;

  @Enumerated(STRING)
  @Column(name = "moderation_status", nullable = false)
  private ModerationStatus moderationStatus;

  @Enumerated(STRING)
  @Column(name = "location_visibility", nullable = false)
  private LocationVisibility locationVisibility;

  @Enumerated(STRING)
  @Column(name = "mood_tag")
  private MoodTag moodTag;

  @Column(name = "soft_ask_id", nullable = false, insertable = false, updatable = false)
  private Long softAskId;

  @ToString.Exclude
  @ManyToOne(fetch = LAZY, optional = false)
  @JoinColumn(name = "soft_ask_id", referencedColumnName = "soft_ask_id", nullable = false, updatable = false)
  private SoftAsk softAsk;

  @Column(name = "author_id", nullable = false, updatable = false)
  private Long authorId;

  @Column(name = "geohash", length = 9, updatable = false)
  private String geoHash;

  @Column(name = "geohash_prefix", length = 5, updatable = false)
  private String geoHashPrefix;

  @ToString.Exclude
  @OneToMany(mappedBy = "parentReply", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<SoftAskReply> childReplies = new HashSet<>();

  @Column(name = "parent_reply_id", nullable = false, insertable = false, updatable = false)
  private Long parentReplyId;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_reply_id", referencedColumnName = "soft_ask_reply_id")
  private SoftAskReply parentReply;

  @Column(name = "is_deleted", nullable = false)
  private Boolean deleted = false;

  @Column(name = "is_visible", nullable = false)
  private Boolean visible = true;

  @Column(name = "child_reply_count", nullable = false)
  private Integer childReplyCount = 0;

  @Column(name = "vote_count", nullable = false)
  private Integer voteCount = 0;

  @Column(name = "bookmark_count", nullable = false)
  private Integer bookmarkCount = 0;

  @Column(name = "share_count", nullable = false)
  private Integer shareCount = 0;

  @Column(name = "latitude", precision = 3, scale = 1, updatable = false)
  private BigDecimal latitude;

  @Column(name = "longitude", precision = 4, scale = 1, updatable = false)
  private BigDecimal longitude;

  @Column(name = "slug", nullable = false, unique = true, updatable = false)
  private String slug;

  @Transient
  private SoftAskParticipantDetail softAskParticipantDetail;

  @Override
  public Long getId() {
    return softAskReplyId;
  }

  @Override
  public Long getParentId() {
    return nonNull(parentReplyId) ? parentReplyId : null;
  }

  public Long getOtherParentId() {
    return nonNull(softAskId) ? softAskId : null;
  }

  @Override
  public String getTitle() {
    return ParentInfoUtil.getParentSummary(content);
  }

  @Override
  public String getUserAliasOrUsername() {
    return nonNull(softAskParticipantDetail) ? softAskParticipantDetail.getUsername() : null;
  }

  @Override
  public String getUserDisplayName() {
    return nonNull(softAskParticipantDetail) ? softAskParticipantDetail.getDisplayName() : null;
  }

  @Override
  public String getAvatarUrl() {
    return nonNull(softAskParticipantDetail) ? softAskParticipantDetail.getAvatarUrl() : null;
  }

  @Override
  public String getParentTitle() {
    return "";
  }

  @Override
  public Double getLatitude() {
    return nonNull(latitude) ?  latitude.doubleValue() : null;
  }

  @Override
  public Double getLongitude() {
    return nonNull(longitude) ?  longitude.doubleValue() : null;
  }

  @Override
  public String getSummary() {
    return content;
  }

  public boolean isDeleted() {
    return nonNull(deleted) && deleted;
  }

  @Override
  public boolean hasLatitudeAndLongitude() {
    return nonNull(latitude) && nonNull(longitude);
  }

  public void delete() {
    deleted = true;
  }

  @Override
  public void checkIsAuthor(final Long userId) throws SoftAskUpdateDeniedException {
    if (nonNull(authorId) && !authorId.equals(userId)) {
      throw SoftAskUpdateDeniedException.of();
    }
  }

  @PrePersist
  public void prePersist() {
    slug = generateHybridSlug(content);
  }

}
