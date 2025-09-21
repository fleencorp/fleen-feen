package com.fleencorp.feen.softask.model.domain;

import com.fleencorp.feen.common.constant.location.LocationVisibility;
import com.fleencorp.feen.model.contract.HasTitle;
import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import com.fleencorp.feen.softask.constant.core.SoftAskParentType;
import com.fleencorp.feen.softask.constant.core.SoftAskStatus;
import com.fleencorp.feen.softask.constant.core.SoftAskVisibility;
import com.fleencorp.feen.softask.constant.other.ModerationStatus;
import com.fleencorp.feen.softask.constant.other.MoodTag;
import com.fleencorp.feen.softask.contract.SoftAskCommonData;
import com.fleencorp.feen.softask.exception.core.SoftAskUpdateDeniedException;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

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
@Table(name = "soft_ask")
public class SoftAsk extends FleenFeenEntity
  implements HasTitle, SoftAskCommonData {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "soft_ask_id", nullable = false, updatable = false, unique = true)
  private Long softAskId;

  @Column(name = "title", nullable = false, length = 500)
  private String title;

  @Column(name = "description", nullable = false, length = 2000)
  private String description;

  @Column(name = "tags", length = 1000)
  private String tags;

  @Column(name = "link", length = 1000)
  private String link;

  @Column(name = "parent_id", updatable = false)
  private Long parentId;

  @Column(name = "parent_title", length = 500, updatable = false)
  private String parentTitle;

  @Enumerated(STRING)
  @Column(name = "parent_type")
  private SoftAskParentType softAskParentType;

  @Enumerated(STRING)
  @Column(name = "visibility", nullable = false, updatable = false)
  private SoftAskVisibility softAskVisibility;

  @Enumerated(STRING)
  @Column(name = "status", nullable = false, updatable = false)
  private SoftAskStatus softAskStatus;

  @Enumerated(STRING)
  @Column(name = "moderation_status", nullable = false, updatable = false)
  private ModerationStatus moderationStatus;

  @Enumerated(STRING)
  @Column(name = "location_visibility", nullable = false, updatable = false)
  private LocationVisibility locationVisibility;

  @Enumerated(STRING)
  @Column(name = "mood_tag", updatable = false)
  private MoodTag moodTag;

  @Column(name = "chat_space_id", updatable = false)
  private Long chatSpaceId;

  @Column(name = "poll_id", updatable = false)
  private Long pollId;

  @Column(name = "stream_id", updatable = false)
  private Long streamId;

  @Column(name = "author_id", nullable = false, updatable = false)
  private Long authorId;

  @Column(name = "geohash", length = 9, updatable = false)
  private String geoHash;

  @Column(name = "geohash_prefix", length = 5, updatable = false)
  private String geoHashPrefix;

  @Column(columnDefinition = "geography(Point, 4326)", updatable = false)
  private Point location;

  @Column(name = "is_deleted", nullable = false)
  private Boolean deleted = false;

  @Column(name = "is_visible", nullable = false)
  private Boolean visible = true;

  @Column(name = "bookmark_count", nullable = false)
  private Integer bookmarkCount = 0;

  @Column(name = "participant_count", nullable = false)
  private Integer participantCount = 1;

  @Column(name = "reply_count", nullable = false)
  private Integer replyCount = 0;

  @Column(name = "share_count", nullable = false)
  private Integer shareCount = 0;

  @Column(name = "vote_count", nullable = false)
  private Integer voteCount = 0;

  @Column(name = "latitude", precision = 3, scale = 1, updatable = false)
  private BigDecimal latitude;

  @Column(name = "longitude", precision = 4, scale = 1, updatable = false)
  private BigDecimal longitude;

  @Column(name = "slug", nullable = false, unique = true, updatable = false)
  private String slug;

  @ToString.Exclude
  @OneToMany(fetch = LAZY, mappedBy = "softAsk", targetEntity = SoftAskReply.class, cascade = CascadeType.ALL)
  private Set<SoftAskReply> replies = new HashSet<>();

  @Transient
  private SoftAskParticipantDetail participant;

  @Override
  public Long getId() {
    return softAskId;
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
  public String getUserAliasOrUsername() {
    return nonNull(participant) ? participant.getUsername() : null;
  }

  @Override
  public String getUserDisplayName() {
    return nonNull(participant) ? participant.getDisplayName() : null;
  }

  @Override
  public String getAvatarUrl() {
    return nonNull(participant) ? participant.getAvatarUrl() : null;
  }

  @Override
  public String getSummary() {
    return description;
  }

  public boolean isDeleted() {
    return nonNull(deleted) && deleted;
  }

  @Override
  public boolean hasLatitudeAndLongitude() {
    return nonNull(latitude) && nonNull(longitude);
  }

  @Override
  public void setContent(final String content) {
    this.description = content;
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

  @PrePersist
  public void prePersist() {
    slug = generateHybridSlug(description);
    if (hasLatitudeAndLongitude()) {
      final PrecisionModel precisionModel = new PrecisionModel();
      final GeometryFactory factory = new GeometryFactory(precisionModel, 4326);
      final Coordinate coordinate = new Coordinate(getLongitude(), getLatitude());

      location = factory.createPoint(coordinate);
    }
  }

}
