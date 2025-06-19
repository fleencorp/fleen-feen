package com.fleencorp.feen.follower.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.base.validator.IsNumber;
import com.fleencorp.feen.user.model.domain.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static java.util.Objects.nonNull;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class FollowerSearchRequest extends SearchRequest {

  @IsNumber
  @JsonProperty("member_id")
  private String memberId;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Member member;

  public Member getMember() {
    return nonNull(member) ? member : Member.of(getMemberId());
  }

  protected Long getMemberId() {
    return Long.valueOf(memberId);
  }

  public static FollowerSearchRequest of(final Member member) {
    final FollowerSearchRequest followerSearchRequest = new FollowerSearchRequest();
    followerSearchRequest.setMember(member);

    return followerSearchRequest;
  }
}
