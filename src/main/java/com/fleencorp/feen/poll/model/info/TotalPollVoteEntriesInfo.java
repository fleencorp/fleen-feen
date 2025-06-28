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
  "total_entries",
  "total_vote_entries_text",
  "total_vote_entries_text_2",
  "total_vote_entries_other_text"
})
public class TotalPollVoteEntriesInfo {

  @JsonProperty("total_entries")
  private Integer totalEntries;

  @JsonProperty("total_vote_entries_text")
  private String totalVoteEntriesText;

  @JsonProperty("total_vote_entries_text_2")
  private String totalVoteEntriesText2;

  @JsonProperty("total_vote_entries_other_text")
  private String totalVoteEntriesOtherText;

  public static TotalPollVoteEntriesInfo of(final Integer totalEntries, final String totalVoteEntriesText, final String totalVoteEntriesText2, final String totalVoteEntriesOtherText) {
    return new TotalPollVoteEntriesInfo(totalEntries, totalVoteEntriesText, totalVoteEntriesText2, totalVoteEntriesOtherText);
  }
}
