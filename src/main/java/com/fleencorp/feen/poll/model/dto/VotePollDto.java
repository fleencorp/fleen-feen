package com.fleencorp.feen.poll.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.validator.IsNumber;
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
import java.util.Objects;

import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VotePollDto {

  @Valid
  @NotEmpty(message = "{poll.vote.selectedOptions.NotEmpty}")
  @Size(min = 1, max = 10, message = "{poll.vote.selectedOptions.Size}")
  @JsonProperty("options")
  private List<PollOptionIdDto> pollOptions = new ArrayList<>();

  protected Collection<PollOptionIdDto> getPollOptions() {
    return pollOptions.stream()
      .filter(Objects::nonNull)
      .filter(optionDto -> nonNull(optionDto.pollOptionId))
      .toList();
  }

  public Collection<Long> getOptionIds() {
    return pollOptions.stream()
      .filter(Objects::nonNull)
      .map(PollOptionIdDto::getPollOptionId)
      .toList();
  }

  public Collection<PollVote> toPollVotes(final Poll poll, final Member member) {
    final List<PollVote> votes = new ArrayList<>();

    for (final PollOptionIdDto pollOptionIdDto : getPollOptions()) {
      final PollVote vote = new PollVote();
      final PollOption pollOption = pollOptionIdDto.toPollOption();
      vote.setPollId(poll.getPollId());
      vote.setPoll(poll);
      vote.setPollOptionId(pollOptionIdDto.getPollOptionId());
      vote.setPollOption(pollOption);
      vote.setVoterId(member.getMemberId());
      vote.setVoter(member);

      votes.add(vote);
    }

    return votes;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  public static class PollOptionIdDto {

    @NotNull(message = "{poll.vote.option.NotNull}")
    @IsNumber
    @JsonProperty("poll_option_id")
    private String pollOptionId;

    public Long getPollOptionId() {
      return nonNull(pollOptionId) ? Long.parseLong(pollOptionId) : null;
    }

    public PollOption toPollOption() {
      return PollOption.of(getPollOptionId());
    }
  }
}

