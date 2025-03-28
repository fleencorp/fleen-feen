package com.fleencorp.feen.model.request.search.chat.space;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.feen.constant.chat.space.ChatSpaceStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

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

  public ChatSpaceStatus getDefaultActive() {
    return ChatSpaceStatus.ACTIVE;
  }
}
