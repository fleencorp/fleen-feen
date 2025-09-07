package com.fleencorp.feen.softask.contract;

import com.fleencorp.feen.common.model.info.ParentInfo;
import com.fleencorp.feen.common.model.info.ShareCountInfo;
import com.fleencorp.feen.common.model.info.UserLocationInfo;
import com.fleencorp.feen.model.contract.Bookmarkable;
import com.fleencorp.feen.model.contract.Updatable;
import com.fleencorp.feen.softask.constant.core.SoftAskType;
import com.fleencorp.feen.softask.model.info.vote.SoftAskUserVoteInfo;
import com.fleencorp.feen.softask.model.info.vote.SoftAskVoteCountInfo;
import com.fleencorp.feen.softask.model.response.participant.SoftAskParticipantResponse;

public interface SoftAskCommonResponse extends Bookmarkable, HasMood, Updatable {

  Long getParentId();

  UserLocationInfo getUserLocationInfo();

  boolean hasLatitudeAndLongitude();

  void setDisplayTimeLabel(String displayTimeLabel);

  void setVoteCountInfo(SoftAskVoteCountInfo voteCountInfo);

  void setParentInfo(ParentInfo parentInfo);

  void setSoftAskUserVoteInfo(SoftAskUserVoteInfo softAskUserVoteInfo);

  void setSoftAskParticipantResponse(SoftAskParticipantResponse softAskParticipantResponse);

  void setUserLocationInfo(UserLocationInfo userLocationInfo);

  SoftAskType getSoftAskType();

  void setShareCountInfo(ShareCountInfo shareCountInfo);
}
