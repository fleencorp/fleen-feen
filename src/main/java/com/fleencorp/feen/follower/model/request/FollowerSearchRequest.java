package com.fleencorp.feen.follower.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.base.validator.IsNumber;
import com.fleencorp.feen.user.model.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FollowerSearchRequest extends SearchRequest {

  @IsNumber
  @JsonProperty("member_id")
  private String memberId;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Member member;

  public Member getMember() {
    return nonNull(getMemberId())
      ? Member.of(getMemberId())
      : nonNull(member) ? member : null;
  }

  protected Long getMemberId() {
    return nonNull(memberId) ? Long.valueOf(memberId) : null;
  }

  public static FollowerSearchRequest of(final Member member) {
    final FollowerSearchRequest followerSearchRequest = new FollowerSearchRequest();
    followerSearchRequest.setMember(member);

    return followerSearchRequest;
  }
}
