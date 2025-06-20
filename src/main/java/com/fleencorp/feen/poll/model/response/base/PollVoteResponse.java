package com.fleencorp.feen.poll.model.response.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.poll.model.info.IsVotedInfo;
import com.fleencorp.localizer.model.response.LocalizedResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "poll_id",
  "poll",
  "poll_total_entries",
  "poll_options"
})
public class PollVoteResponse extends LocalizedResponse {

  @JsonProperty("poll_id")
  private Long pollId;

  @JsonProperty("poll")
  private PollResponse poll;

  @JsonProperty("vote_info")
  private IsVotedInfo isVotedInfo;

  @JsonProperty("poll_total_entries")
  private Integer pollTotalEntries;

  @JsonProperty("poll_options")
  private Collection<PollOptionResponse> pollOptions = new ArrayList<>();

  @Override
  public String getMessageCode() {
    return "poll.vote";
  }

  public static PollVoteResponse of(final IsVotedInfo isVotedInfo, final Integer pollTotalEntries) {
    final PollVoteResponse voteResponse = new PollVoteResponse();
    voteResponse.setIsVotedInfo(isVotedInfo);
    voteResponse.setPollTotalEntries(pollTotalEntries);

    return voteResponse;
  }

  public static PollVoteResponse of(final Collection<PollOptionResponse> pollOptions, final IsVotedInfo isVotedInfo) {
    final PollVoteResponse pollVoteResponse = new PollVoteResponse();
    pollVoteResponse.setPollOptions(pollOptions);
    pollVoteResponse.setIsVotedInfo(isVotedInfo);

    return pollVoteResponse;
  }

  public static PollVoteResponse of(final Long pollId, final Integer pollTotalEntries, final Collection<PollOptionResponse> pollOptions) {
    final PollVoteResponse pollVoteResponse = new PollVoteResponse();
    pollVoteResponse.setPollId(pollId);
    pollVoteResponse.setPollTotalEntries(pollTotalEntries);
    pollVoteResponse.setPollOptions(pollOptions);

    return pollVoteResponse;
  }
}
