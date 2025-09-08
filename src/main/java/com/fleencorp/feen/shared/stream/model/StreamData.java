package com.fleencorp.feen.shared.stream.model;

import com.fleencorp.feen.common.constant.mask.MaskedStreamLinkUri;
import com.fleencorp.feen.shared.stream.contract.IsAStream;
import com.fleencorp.feen.stream.constant.core.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StreamData implements IsAStream {

  private Long streamId;
  private String externalId;
  private Long chatSpaceId;
  private String title;
  private String description;
  private String tags;
  private String location;
  private Integer totalSpeakers;
  private Integer totalAttendees;
  private Integer bookmarkCount;
  private Integer likeCount;
  private Integer shareCount;
  private String timezone;
  private LocalDateTime scheduledStartDate;
  private LocalDateTime scheduledEndDate;
  private String streamLink;
  private String thumbnailLink;
  private String otherDetails;
  private String otherLink;
  private String groupOrOrganizationName;
  private String musicLink;
  private StreamSource streamSource;
  private StreamType streamType;
  private StreamCreationType streamCreationType;
  private StreamVisibility streamVisibility;
  private StreamStatus streamStatus;
  private String organizerName;
  private String organizerEmail;
  private String organizerPhone;
  private Long memberId;
  private Boolean deleted;
  private Boolean forKids;
  private String slug;
  private LocalDateTime createdOn;
  private LocalDateTime updatedOn;

  private String externalSpaceIdOrName;

  @Override
  public MaskedStreamLinkUri getMaskedStreamLink() {
    return MaskedStreamLinkUri.of(streamLink, streamSource);
  }

  @Override
  public StreamTimeType getStreamSchedule() {
    return null;
  }

  @Override
  public boolean isForKids() {
    return nonNull(forKids) && forKids;
  }

  @Override
  public boolean isPrivateOrProtected() {
    return StreamVisibility.isPrivateOrProtected(streamVisibility);
  }

  @Override
  public void checkIsOrganizer(final Long memberOrUserId) {}

  @Override
  public boolean isALiveStream() {
    return StreamType.isLiveStream(streamType);
  }

  public static StreamData empty() {
    return new StreamData();
  }
}

