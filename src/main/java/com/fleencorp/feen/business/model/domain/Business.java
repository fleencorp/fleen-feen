package com.fleencorp.feen.business.model.domain;

import com.fleencorp.feen.business.constant.BusinessChannelType;
import com.fleencorp.feen.business.constant.BusinessStatus;
import com.fleencorp.feen.business.exception.BusinessNotOwnerException;
import com.fleencorp.feen.link.model.domain.Link;
import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import com.fleencorp.feen.softask.exception.core.SoftAskUpdateDeniedException;
import com.fleencorp.feen.user.model.domain.Member;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;

import java.util.HashSet;
import java.util.Set;

import static com.fleencorp.feen.common.util.common.HybridSlugGenerator.generateHybridSlug;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "business")
public class Business extends FleenFeenEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "business_id", nullable = false)
  private Long businessId;

  @Column(name = "title", nullable = false, length = 300)
  private String title;

  @Column(name = "motto", length = 500)
  private String motto;

  @Column(name = "description", nullable = false, length = 3000)
  private String description;

  @Column(name = "other_details", length = 3000)
  private String otherDetails;

  @Column(name = "founding_year")
  private Integer foundingYear;

  @Column(name = "address", length = 500)
  private String address;

  @Column(name = "country", length = 300)
  private String country;

  @Column(name = "logo_url", length = 1500)
  private String logoUrl;

  @Enumerated(STRING)
  @Column(name = "channel_type", nullable = false)
  private BusinessChannelType channelType;

  @Enumerated(STRING)
  @Column(name = "status", nullable = false)
  private BusinessStatus status;

  @Column(name = "deleted", nullable = false)
  private boolean deleted = false;

  @ToString.Exclude
  @OneToMany(fetch = LAZY, mappedBy = "business", targetEntity = Link.class, cascade = CascadeType.PERSIST)
  private Set<Link> links = new HashSet<>();

  @Column(name = "owner_id", updatable = false, insertable = false)
  private Long ownerId;

  @ToString.Exclude
  @CreatedBy
  @ManyToOne(fetch = LAZY, optional = false, targetEntity = Member.class)
  @JoinColumn(name = "owner_id", referencedColumnName = "member_id", nullable = false, updatable = false)
  private Member owner;

  @Column(name = "share_count", nullable = false)
  private Integer shareCount = 0;

  @Column(name = "slug", nullable = false, unique = true, updatable = false)
  private String slug;

  public boolean checkIsOwner(final Long userId) {
    return nonNull(ownerId) && !ownerId.equals(userId);
  }

  public void update(
      final String title,
      final String motto,
      final String description,
      final String otherDetails,
      final Integer foundingYear,
      final BusinessStatus status) {
    this.title = title;
    this.motto = motto;
    this.description = description;
    this.otherDetails = otherDetails;
    this.foundingYear = foundingYear;
    this.status = status;
  }

  public void delete() {
    deleted = true;
  }

  public void verifyIsOwner(final Long userId) throws SoftAskUpdateDeniedException {
    if (nonNull(ownerId) && !ownerId.equals(userId)) {
      throw BusinessNotOwnerException.of();
    }
  }

  @PrePersist
  public void prePersist() {
    slug = generateHybridSlug(description);
  }

}
