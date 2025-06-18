package com.fleencorp.feen.poll.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.feen.poll.model.domain.Poll;
import com.fleencorp.feen.poll.model.domain.PollOption;
import com.fleencorp.feen.poll.model.domain.PollVote;
import com.fleencorp.feen.user.model.domain.Member;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VotePollDto {

  @Valid
  @NotEmpty(message = "{poll.vote.selectedOptions.NotEmpty}")
  @Size(min = 1, max = 10, message = "{poll.vote.selectedOptions.Size}")
  @JsonProperty("option_ids")
  private List<@NotNull(message = "{poll.vote.optionsIds.NotNull}") Long> optionIds = new ArrayList<>();

  public Collection<PollVote> toPollVotes(final Poll poll, final Member member) {
    final List<PollVote> votes = new ArrayList<>();

    for (final Long optionId : optionIds) {
      final PollVote vote = new PollVote();
      final PollOption pollOption = PollOption.of(optionId);
      vote.setPollId(poll.getPollId());
      vote.setPoll(poll);
      vote.setPollOptionId(optionId);
      vote.setPollOption(pollOption);
      vote.setVoterId(member.getMemberId());
      vote.setVoter(member);

      votes.add(vote);
    }

    return votes;
  }
}

