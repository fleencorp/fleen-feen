package com.fleencorp.feen.shared.stream.model;

import com.fleencorp.feen.shared.stream.contract.IsAttendee;
import com.fleencorp.feen.stream.constant.attendee.StreamAttendeeRequestToJoinStatus;
import lombok.*;

import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StreamAttendeeData implements IsAttendee {

  private Long attendeeId;
  private Long streamId;
  private Long memberId;

  private Boolean attending;
  private Boolean aSpeaker;
  private Boolean isOrganizer;

  private String attendeeComment;
  private String organizerComment;

  private StreamAttendeeRequestToJoinStatus requestToJoinStatus;

  private String emailAddress;
  private String fullName;
  private String username;
  private String profilePhoto;

  @Override
  public boolean isRequestToJoinDisapprovedOrPending() {
    return isRequestToJoinDisapproved() || isRequestToJoinPending();
  }

  @Override
  public boolean isAttending() {
    return nonNull(attending) && attending;
  }

  @Override
  public boolean isOrganizer() {
    return nonNull(isOrganizer) && isOrganizer;
  }

  @Override
  public boolean isASpeaker() {
    return nonNull(aSpeaker) && aSpeaker;
  }

  @Override
  public boolean isRequestToJoinPending() {
    return StreamAttendeeRequestToJoinStatus.isPending(requestToJoinStatus);
  }

  @Override
  public boolean isRequestToJoinDisapproved() {
    return StreamAttendeeRequestToJoinStatus.isDisapproved(requestToJoinStatus);
  }

  @Override
  public void approveUserAttendance() {}
}

