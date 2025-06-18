package com.fleencorp.feen.poll.model.projection;

public record PollVoteAggregate(Long optionId, String optionText, Long voteCount, Long totalVotes) {}

