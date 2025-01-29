package com.fleencorp.feen.model.domain.user;

import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

  @ManyToOne(fetch = LAZY, optional = false, targetEntity = Member.class)
  @JoinColumn(name = "following_id", referencedColumnName = "member_id", nullable = false, updatable = false)
  private Member following;

  @ManyToOne(fetch = LAZY, optional = false, targetEntity = Member.class)
  @JoinColumn(name = "followed_id", referencedColumnName = "member_id", nullable = false, updatable = false)
  private Member followed;

  public String getFollowingName() {
    return nonNull(following) ? following.getFullName() : null;
  }

  public static Follower of(final Member following, final Member followed) {
    final Follower follower = new Follower();
    follower.setFollowing(following);
    follower.setFollowed(followed);

    return follower;
  }
}
