package com.fleencorp.feen.model.info.user.profile;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "total_followed",
  "total_followed_text",
  "total_followed_text_2"
})
public class TotalFollowedInfo {

  @JsonProperty("total_followed")
  private Long totalFollowed;

  @JsonProperty("total_followed_text")
  private String totalFollowedText;

  @JsonProperty("total_followed_text_2")
  private String totalFollowedText2;

  public static TotalFollowedInfo of(final Long totalFollowed, final String totalFollowedText, final String totalFollowedText2) {
    return new TotalFollowedInfo(totalFollowed, totalFollowedText, totalFollowedText2);
  }
}
