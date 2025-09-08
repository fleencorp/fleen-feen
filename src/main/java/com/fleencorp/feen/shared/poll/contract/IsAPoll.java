package com.fleencorp.feen.shared.poll.contract;

import com.fleencorp.feen.model.contract.HasTitle;

public interface IsAPoll extends HasTitle {

  Long getPollId();

  String getQuestion();

  String getDescription();

  Long getAuthorId();

  Long getParentId();

  String getParentTitle();

  Long getStreamId();

  Long getChatSpaceId();
}
