package com.fleencorp.feen.model.response.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.view.search.SearchResultView;
import com.fleencorp.feen.model.response.base.ApiResponse;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "result"
})
public class FollowersResponse extends ApiResponse {

  private SearchResultView result;

  @Override
  public String getMessageKey() {
    return "followers";
  }

  public static FollowersResponse of(final SearchResultView result) {
    return FollowersResponse.builder()
      .result(result)
      .build();
  }
}
