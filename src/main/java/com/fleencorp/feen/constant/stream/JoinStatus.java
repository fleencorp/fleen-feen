package com.fleencorp.feen.constant.stream;

import com.fleencorp.base.constant.base.ApiParameter;
import com.fleencorp.feen.constant.chat.space.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.constant.chat.space.ChatSpaceVisibility;
import lombok.Getter;

/**
 * Enum representing different join statuses for stream attendees.
 *
 * <p>This enum does not currently define any specific constants, but provides a static method to
 * map a {@link StreamAttendeeRequestToJoinStatus} to a string representation of the join status.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Getter
public enum JoinStatus implements ApiParameter {

  DISAPPROVED("Disapproved", "join.status.disapproved", "join.status.disapproved.2"),
  JOINED("Joined", "join.status.joined", "join.status.joined.2"),
  ATTENDED("Attended", "join.status.attended", "join.status.attended.2"),
  ATTENDING("Attending", "join.status.attending", "join.status.attending.2"),
  NOT_ATTENDING("Not Attending", "join.status.not.attending", "join.status.not.attending.2"),
  JOINED_EVENT_OR_STREAM("Going", "join.status.joined.event.or.stream", "join.status.joined.event.or.stream.2"),
  JOINED_CHAT_SPACE("Joined", "join.status.joined.chat.space", "join.status.joined.chat.space.2"),
  NOT_JOINED("Join", "join.status.not.joined", "join.status.not.joined.2"),
  NOT_JOINED_PUBLIC("Join", "join.status.not.joined.public", "join.status.not.joined.public.2"),
  NOT_JOINED_PRIVATE("Request to Join", "join.status.not.joined.private", "join.status.not.joined.private.2"),
  PENDING("Pending", "join.status.pending", "join.status.pending.2");

  private final String value;
  private final String messageCode;
  private final String messageCode2;

  JoinStatus(
      final String value,
      final String messageCode,
      final String messageCode2) {
    this.value = value;
    this.messageCode = messageCode;
    this.messageCode2 = messageCode2;
  }

  /**
   * Returns the join status based on the request-to-join status, stream visibility, attendance, and event occurrence.
   *
   * @param requestToJoinStatus the status of the request to join the stream
   * @param visibility the visibility of the stream, indicating whether it is public or private
   * @param hasHappened {@code true} if the event or stream has already occurred, {@code false} otherwise
   * @param isAttending {@code true} if the attendee is currently attending, {@code false} otherwise
   * @return the join status based on the parameters
   */
  public static JoinStatus getJoinStatus(final StreamAttendeeRequestToJoinStatus requestToJoinStatus, final StreamVisibility visibility, final boolean hasHappened, final boolean isAttending) {
    if (isApprovedAndAttended(requestToJoinStatus, hasHappened, isAttending)) {
      return JoinStatus.attended();
    } else if (isApprovedButNotAttending(requestToJoinStatus, isAttending)) {
      return JoinStatus.notAttending();
    } else if (isApprovedAndVisible(visibility, requestToJoinStatus)) {
      return JoinStatus.joinedEventOrStream();
    } else if (isDisapprovedForPrivate(visibility, requestToJoinStatus)) {
      return JoinStatus.notJoinedPrivate();
    } else if (isDisapprovedForPublic(visibility, requestToJoinStatus)) {
      return JoinStatus.notJoinedPublic();
    }
    return JoinStatus.pending();
  }

  /**
   * Checks if the request to join is approved, the attendee is currently attending, and the stream has already happened.
   *
   * @param status the request-to-join status of the stream attendee
   * @param hasHappened {@code true} if the stream has already occurred, {@code false} otherwise
   * @param isAttending {@code true} if the attendee is currently attending, {@code false} otherwise
   * @return {@code true} if the request is approved, the attendee is attending, and the stream has happened, {@code false} otherwise
   */
  private static boolean isApprovedAndAttended(final StreamAttendeeRequestToJoinStatus status, final boolean hasHappened, final boolean isAttending) {
    return StreamAttendeeRequestToJoinStatus.isApproved(status) && isAttending && hasHappened;
  }

  /**
   * Checks if the request to join is approved and the attendee is not currently attending.
   *
   * @param status the request-to-join status of the stream attendee
   * @param isAttending {@code true} if the attendee is currently attending, {@code false} otherwise
   * @return {@code true} if the request is approved and the attendee is not attending, {@code false} otherwise
   */
  private static boolean isApprovedButNotAttending(final StreamAttendeeRequestToJoinStatus status, final boolean isAttending) {
    return StreamAttendeeRequestToJoinStatus.isApproved(status) && !isAttending;
  }

  /**
   * Checks if the stream is either private, protected, or public and the request to join is approved.
   *
   * @param visibility the visibility of the stream
   * @param status the request-to-join status of the stream attendee
   * @return {@code true} if the stream is private, protected, or public and the request is approved, {@code false} otherwise
   */
  private static boolean isApprovedAndVisible(final StreamVisibility visibility, final StreamAttendeeRequestToJoinStatus status) {
    return StreamVisibility.isPrivateOrProtected(visibility) || StreamVisibility.isPublic(visibility)
      && StreamAttendeeRequestToJoinStatus.isApproved(status);
  }

  /**
   * Checks if the stream is private or protected and the request to join is disapproved.
   *
   * @param visibility the visibility of the stream
   * @param status the request-to-join status of the stream attendee
   * @return {@code true} if the stream is private or protected and the request is disapproved, {@code false} otherwise
   */
  private static boolean isDisapprovedForPrivate(final StreamVisibility visibility, final StreamAttendeeRequestToJoinStatus status) {
    return StreamVisibility.isPrivateOrProtected(visibility) && StreamAttendeeRequestToJoinStatus.isDisapproved(status);
  }

  /**
   * Checks if the stream is public and the request to join is disapproved.
   *
   * @param visibility the visibility of the stream
   * @param status the request-to-join status of the stream attendee
   * @return {@code true} if the stream is public and the request is disapproved, {@code false} otherwise
   */
  private static boolean isDisapprovedForPublic(final StreamVisibility visibility, final StreamAttendeeRequestToJoinStatus status) {
    return StreamVisibility.isPublic(visibility) && StreamAttendeeRequestToJoinStatus.isDisapproved(status);
  }

  /**
   * Returns the join status based on the chat space visibility and request-to-join status.
   *
   * @param requestToJoinStatus the status of the request to join the chat space
   * @param visibility the visibility of the chat space, indicating whether it is public or private
   * @return the join status based on the parameters
   */
  public static JoinStatus getJoinStatus(final ChatSpaceRequestToJoinStatus requestToJoinStatus, final ChatSpaceVisibility visibility) {
    if (isApprovedAndVisible(visibility, requestToJoinStatus)) {
      return JoinStatus.joinedChatSpace();
    } else if (isDisapprovedForPrivate(visibility, requestToJoinStatus)) {
      return JoinStatus.notJoinedPrivate();
    } else if (isDisapprovedForPublic(visibility, requestToJoinStatus)) {
      return JoinStatus.notJoinedPublic();
    }
    return JoinStatus.pending();
  }

  /**
   * Determines if a chat space is approved and has a visibility status that allows it
   * to be either public or private.
   *
   * @param visibility the visibility of the chat space, indicating whether it is public or private
   * @param status the request-to-join status of the chat space
   * @return {@code true} if the chat space is either public or private and the request-to-join
   *         status is approved; {@code false} otherwise
   */
  private static boolean isApprovedAndVisible(final ChatSpaceVisibility visibility, final ChatSpaceRequestToJoinStatus status) {
    return (ChatSpaceVisibility.isPrivate(visibility) || ChatSpaceVisibility.isPublic(visibility))
      && ChatSpaceRequestToJoinStatus.isApproved(status);
  }

  /**
   * Determines if a chat space with the specified visibility and request-to-join status
   * is disapproved for private access.
   *
   * @param visibility the visibility of the chat space, indicating whether it is public or private
   * @param status the request-to-join status of the chat space
   * @return {@code true} if the chat space is private and the request-to-join status is disapproved;
   *         {@code false} otherwise
   */
  private static boolean isDisapprovedForPrivate(final ChatSpaceVisibility visibility, final ChatSpaceRequestToJoinStatus status) {
    return ChatSpaceVisibility.isPrivate(visibility)
      && ChatSpaceRequestToJoinStatus.isDisapproved(status);
  }

  /**
   * Determines if a chat space with the specified visibility and request-to-join status
   * is disapproved for public access.
   *
   * @param visibility the visibility of the chat space, indicating whether it is public or private
   * @param status the request-to-join status of the chat space
   * @return {@code true} if the chat space is public and the request-to-join status is disapproved;
   *         {@code false} otherwise
   */
  private static boolean isDisapprovedForPublic(final ChatSpaceVisibility visibility, final ChatSpaceRequestToJoinStatus status) {
    return ChatSpaceVisibility.isPublic(visibility)
      && ChatSpaceRequestToJoinStatus.isDisapproved(status);
  }

  /**
   * Checks if the provided join status indicates that it is not approved.
   *
   * <p>This method returns {@code true} if the given {@code joinStatus} string is
   * not equivalent to the "Joined" status, meaning the user has not been approved.
   * Otherwise, it returns {@code false}.</p>
   *
   * @param joinStatus the status string to check
   * @return {@code true} if the status is not "Joined", {@code false} otherwise
   */
  public static boolean isNotApproved(final JoinStatus joinStatus) {
    return JOINED != joinStatus;
  }

  public static JoinStatus pending() {
    return PENDING;
  }

  public static JoinStatus notJoinedPrivate() {
    return NOT_JOINED_PRIVATE;
  }

  public static JoinStatus notJoinedPublic() {
    return NOT_JOINED_PUBLIC;
  }

  public static JoinStatus joinedChatSpace() {
    return JOINED_CHAT_SPACE;
  }

  public static JoinStatus joinedEventOrStream() {
    return JOINED_EVENT_OR_STREAM;
  }

  public static JoinStatus attended() {
    return ATTENDED;
  }

  public static JoinStatus notAttending() {
    return NOT_ATTENDING;
  }

  public static JoinStatus attending() {
    return ATTENDING;
  }
}
