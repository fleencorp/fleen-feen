package com.fleencorp.feen.model.request.search.chat.space;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.feen.constant.chat.space.ChatSpaceStatus;
import com.fleencorp.feen.user.model.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static java.util.Objects.nonNull;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatSpaceSearchRequest extends SearchRequest {

  @JsonProperty("title")
  private String title;

  @JsonProperty("is_active")
  private Boolean isActive;

  @JsonProperty("another_user_id")
  protected Long anotherUserId;

  public ChatSpaceStatus getDefaultActive() {
    return ChatSpaceStatus.ACTIVE;
  }

  public boolean hasAnotherUser() {
    return nonNull(anotherUserId);
  }

  public Member getAnotherUser() {
    return hasAnotherUser() ? Member.of(anotherUserId) : null;
  }
}
