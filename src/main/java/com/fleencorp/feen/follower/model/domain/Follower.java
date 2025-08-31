package com.fleencorp.feen.follower.model.domain;

import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import com.fleencorp.feen.user.model.domain.Member;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static java.util.Objects.nonNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "follower")
public class Follower extends FleenFeenEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "follower_id", nullable = false, updatable = false, unique = true)
  private Long followerId;

  @Column(name = "following_id", nullable = false, updatable = false, insertable = false)
  private Long followingId;

  @ToString.Exclude
  @ManyToOne(fetch = LAZY, optional = false, targetEntity = Member.class)
  @JoinColumn(name = "following_id", referencedColumnName = "member_id", nullable = false, updatable = false)
  private Member following;

  @Column(name = "followed_id", nullable = false, updatable = false, insertable = false)
  private Long followedId;

  @ToString.Exclude
  @ManyToOne(fetch = LAZY, optional = false, targetEntity = Member.class)
  @JoinColumn(name = "followed_id", referencedColumnName = "member_id", nullable = false, updatable = false)
  private Member followed;

  public String getFollowingName() {
    return nonNull(following) ? following.getFullName() : null;
  }

  public String getFollowedName() {
    return nonNull(followed) ? followed.getFullName() : null;
  }

  public static Follower of(final Member following, final Member followed) {
    final Follower follower = new Follower();
    follower.setFollowing(following);
    follower.setFollowed(followed);

    return follower;
  }
}
