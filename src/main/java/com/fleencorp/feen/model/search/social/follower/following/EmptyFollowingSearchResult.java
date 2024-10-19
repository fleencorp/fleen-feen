package com.fleencorp.feen.model.search.social.follower.following;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.view.search.SearchResultView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.function.Supplier;

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
public class EmptyFollowingSearchResult extends FollowingSearchResult {

  @JsonProperty("result")
  private SearchResultView result;

  @Override
  public String getMessageCode() {
    return "empty.following.search";
  }

  public static Supplier<FollowingSearchResult> of(final SearchResultView result) {
    return () -> new EmptyFollowingSearchResult(result);
  }
}
