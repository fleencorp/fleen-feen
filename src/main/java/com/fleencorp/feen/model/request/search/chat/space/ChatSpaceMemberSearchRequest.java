package com.fleencorp.feen.model.request.search.chat.space;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.model.request.search.SearchRequest;
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
public class ChatSpaceMemberSearchRequest extends SearchRequest {

  @JsonProperty("member_name")
  private String memberName;
}
