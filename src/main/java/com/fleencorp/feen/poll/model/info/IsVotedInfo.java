package com.fleencorp.feen.poll.model.info;

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
  "voted",
  "voted_text",
  "voted_text_2",
  "voted_text_3",
  "voted_text_4"
})
public class IsVotedInfo {

  @JsonProperty("voted")
  private Boolean voted;

  @JsonProperty("voted_text")
  private String votedText;

  @JsonProperty("voted_text_2")
  private String votedText2;

  @JsonProperty("voted_text_3")
  private String votedText3;

  @JsonProperty("voted_text_4")
  private String votedText4;

  public static IsVotedInfo of(final Boolean voted, final String votedText, final String votedText2, final String votedText3, final String votedText4) {
    return new IsVotedInfo(voted, votedText, votedText2, votedText3, votedText4);
  }
}
