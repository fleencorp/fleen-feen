package com.fleencorp.feen.model.request.search.chat.space;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.base.validator.ValidBoolean;
import com.fleencorp.feen.constant.chat.space.ChatSpaceRequestToJoinStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Set;

import static com.fleencorp.feen.constant.chat.space.ChatSpaceRequestToJoinStatus.DISAPPROVED;
import static com.fleencorp.feen.constant.chat.space.ChatSpaceRequestToJoinStatus.PENDING;
import static java.util.Objects.nonNull;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatSpaceMemberSearchRequest extends SearchRequest {

  @JsonProperty("member_name")
  private String memberName;

  @JsonProperty("disapproved")
  @ValidBoolean
  protected String disapproved;

  public boolean isDisapproved() {
    return nonNull(disapproved) && Boolean.parseBoolean(disapproved);
  }

  public Set<ChatSpaceRequestToJoinStatus> forPendingOrDisapprovedRequestToJoinStatus() {
    if (isDisapproved()) {
      return Set.of(DISAPPROVED);
    }
    return Set.of(PENDING);
  }
}
