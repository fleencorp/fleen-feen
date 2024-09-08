package com.fleencorp.feen.model.response.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.response.base.ApiResponse;
import com.fleencorp.base.model.view.search.SearchResultView;
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
  "followers"
})
public class FollowingsResponse extends ApiResponse {

  private SearchResultView result;

  @Override
  public String getMessageCode() {
    return "followings";
  }

  public static FollowingsResponse of(final SearchResultView result) {
    return FollowingsResponse.builder()
      .result(result)
      .build();
  }
}
