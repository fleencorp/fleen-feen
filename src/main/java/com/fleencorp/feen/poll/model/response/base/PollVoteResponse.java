package com.fleencorp.feen.poll.model.response.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.poll.model.info.IsVotedInfo;
import com.fleencorp.feen.poll.model.info.TotalPollVoteEntriesInfo;
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
  "total_vote_entries_info",
  "poll_options"
})
public class PollVoteResponse extends LocalizedResponse {

  @JsonProperty("poll_id")
  private Long pollId;

  @JsonProperty("poll")
  private PollResponse poll;

  @JsonProperty("vote_info")
  private IsVotedInfo isVotedInfo;

  @JsonProperty("total_vote_entries_info")
  private TotalPollVoteEntriesInfo totalPollVoteEntriesInfo;

  @JsonProperty("poll_options")
  private Collection<PollOptionResponse> pollOptions = new ArrayList<>();

  @Override
  public String getMessageCode() {
    return "poll.vote";
  }

  public static PollVoteResponse of(final IsVotedInfo isVotedInfo, final TotalPollVoteEntriesInfo totalPollVoteEntriesInfo) {
    final PollVoteResponse voteResponse = new PollVoteResponse();
    voteResponse.setIsVotedInfo(isVotedInfo);
    voteResponse.setTotalPollVoteEntriesInfo(totalPollVoteEntriesInfo);

    return voteResponse;
  }

  public static PollVoteResponse of(final Collection<PollOptionResponse> pollOptions, final IsVotedInfo isVotedInfo) {
    final PollVoteResponse pollVoteResponse = new PollVoteResponse();
    pollVoteResponse.setPollOptions(pollOptions);
    pollVoteResponse.setIsVotedInfo(isVotedInfo);

    return pollVoteResponse;
  }

  public static PollVoteResponse of(final Long pollId, final TotalPollVoteEntriesInfo totalPollVoteEntriesInfo, final IsVotedInfo isVotedInfo, final Collection<PollOptionResponse> pollOptions) {
    final PollVoteResponse pollVoteResponse = new PollVoteResponse();
    pollVoteResponse.setPollId(pollId);
    pollVoteResponse.setTotalPollVoteEntriesInfo(totalPollVoteEntriesInfo);
    pollVoteResponse.setIsVotedInfo(isVotedInfo);
    pollVoteResponse.setPollOptions(pollOptions);

    return pollVoteResponse;
  }
}
