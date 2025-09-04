package com.fleencorp.feen.shared.stream.contract;

import com.fleencorp.feen.common.constant.mask.MaskedStreamLinkUri;
import com.fleencorp.feen.model.contract.HasTitle;
import com.fleencorp.feen.stream.constant.core.*;

import java.time.LocalDateTime;

public interface IsAStream extends HasTitle {

  Long getStreamId();

  String getExternalId();

  Long getChatSpaceId();

  String getTitle();

  String getDescription();

  String getTags();

  String getLocation();

  Integer getTotalSpeakers();

  Integer getTotalAttendees();

  Integer getBookmarkCount();

  Integer getLikeCount();

  Integer getShareCount();

  String getTimezone();

  LocalDateTime getScheduledStartDate();

  LocalDateTime getScheduledEndDate();

  MaskedStreamLinkUri getMaskedStreamLink();

  String getOtherDetails();

  String getOtherLink();

  String getGroupOrOrganizationName();

  String getMusicLink();

  StreamSource getStreamSource();

  StreamTimeType getStreamSchedule();

  StreamVisibility getStreamVisibility();

  StreamStatus getStreamStatus();

  StreamType getStreamType();

  Boolean getDeleted();

  boolean isForKids();

  boolean isPrivateOrProtected();

  String getOrganizerName();

  String getOrganizerEmail();

  String getOrganizerPhone();

  Long getMemberId();

  String getStreamLink();

  LocalDateTime getCreatedOn();

  LocalDateTime getUpdatedOn();

  String getSlug();

  default Long getOrganizerId() {
    return getMemberId();
  }

  void checkIsOrganizer(Long organizerId);

  boolean isALiveStream();
}
