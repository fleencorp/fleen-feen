package com.fleencorp.feen.common.constant.common;

import com.fleencorp.base.constant.base.ApiParameter;
import com.fleencorp.feen.chat.space.constant.core.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.chat.space.constant.core.ChatSpaceVisibility;
import com.fleencorp.feen.stream.constant.attendee.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.stream.constant.core.StreamVisibility;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Enum representing different join statuses for stream attendees.
 *
 * <p>This enum does not currently define any specific constants, but provides a static method to
 * map a {@link StreamAttendeeRequestToJoinStatus} to a string representation of the join status.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Slf4j
@Getter
public enum JoinStatus implements ApiParameter {

  DISAPPROVED(
    "Disapproved",
    "join.status.disapproved",
    "join.status.disapproved.2"),

  JOINED(
    "Joined",
    "join.status.joined",
    "join.status.joined.2"),

  ATTENDED(
    "Attended",
    "join.status.attended.stream",
    "join.status.attended.2",
    "join.status.attended.3"),

  ATTENDING_STREAM(
    "Attending",
    "join.status.attending.stream",
    "join.status.attending.stream.2",
    "join.status.attending.stream.3"),

  NOT_ATTENDING_STREAM(
    "Not Attending",
    "join.status.not.attending.stream",
    "join.status.not.attending.stream.2",
    "join.status.not.attending.stream.3"),

  JOINED_STREAM(
    "Going",
    "join.status.joined.stream",
    "join.status.joined.stream.2",
    "join.status.joined.stream.3"),

  JOINED_CHAT_SPACE(
    "Joined",
    "join.status.joined.chat.space",
    "join.status.joined.chat.space.2"),

  NOT_JOINED(
    "Join",
    "join.status.not.joined",
    "join.status.not.joined.2"),

  NOT_JOINED_PUBLIC(
    "Join",
    "join.status.not.joined.public",
    "join.status.not.joined.public.2"),

  NOT_JOINED_PRIVATE(
    "Request to Join",
    "join.status.not.joined.private",
    "join.status.not.joined.private.2"),

  LEFT_CHAT_SPACE(
    "Left Chat Space",
    "left.chat.space",
    "left.chat.space.2"),

  REMOVED_CHAT_SPACE(
    "Left Chat Space",
    "removed.from.chat.space",
    "removed.from.chat.space.2"),

  PENDING(
    "Pending",
    "join.status.pending",
    "join.status.pending.2");


  private final String value;
  private final String messageCode;
  private final String messageCode2;
  private final String messageCode3;

  JoinStatus(
      final String value,
      final String messageCode,
      final String messageCode2) {
    this.value = value;
    this.messageCode = messageCode;
    this.messageCode2 = messageCode2;
    this.messageCode3 = "empty";
  }

  JoinStatus(
      final String value,
      final String messageCode,
      final String messageCode2,
      final String messageCode3) {
    this.value = value;
    this.messageCode = messageCode;
    this.messageCode2 = messageCode2;
    this.messageCode3 = messageCode3;
  }

  /**
   * Returns the join status based on the request-to-join status, stream visibility, attendance, and stream occurrence.
   *
   * @param requestToJoinStatus the status of the request to join the stream
   * @param visibility the visibility of the stream, indicating whether it is public or private
   * @param hasHappened {@code true} if the stream has already occurred, {@code false} otherwise
   * @param isAttending {@code true} if the attendee is currently attending, {@code false} otherwise
   * @return the join status based on the parameters
   */
  public static JoinStatus getJoinStatus(final StreamAttendeeRequestToJoinStatus requestToJoinStatus, final StreamVisibility visibility, final boolean hasHappened, final boolean isAttending) {
    if (isApprovedAndAttended(requestToJoinStatus, hasHappened, isAttending)) {
      return JoinStatus.attended();
    } else if (isApprovedButNotAttending(requestToJoinStatus, isAttending)) {
      return JoinStatus.notAttendingStream();
    } else if (isApprovedAndVisible(visibility, requestToJoinStatus)) {
      return JoinStatus.joinedStream();
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
    return (StreamVisibility.isPrivateOrProtected(visibility) || StreamVisibility.isPublic(visibility))
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
   * Determines and returns the appropriate {@link JoinStatus} for a user based on the given
   * request status, chat space visibility, and membership conditions.
   *
   * <p>
   * If the user is approved, the chat space is visible, and the user is a member, it returns {@code joinedChatSpace}.
   * If the user is approved, the chat space is visible, and the user has been removed, it returns {@code removedFromChatSpace}.
   * If the user is approved, the chat space is visible, and the user has left, it returns {@code leftChatSpace}.
   * If the request is disapproved and the chat space is private, it returns {@code notJoinedPrivate}.
   * If the request is disapproved and the chat space is public, it returns {@code notJoinedPublic}.
   * If none of these conditions match, it defaults to returning {@code pending}.
   * </p>
   *
   * @param requestToJoinStatus the status of the user's request to join the chat space
   * @param visibility the visibility setting of the chat space (public or private)
   * @param aMember {@code true} if the user is currently a member of the chat space
   * @param hasLeft {@code true} if the user has previously left the chat space
   * @param isRemoved {@code true} if the user was removed from the chat space
   * @return the appropriate {@link JoinStatus} based on the evaluated conditions
   */
  public static JoinStatus getJoinStatus(final ChatSpaceRequestToJoinStatus requestToJoinStatus, final ChatSpaceVisibility visibility, final boolean aMember, final boolean hasLeft, final boolean isRemoved) {
    if (isApprovedAndVisibleAndAMember(visibility, requestToJoinStatus, aMember)) {
      return JoinStatus.joinedChatSpace();
    } else if (isApprovedAndVisibleAndRemoved(visibility, requestToJoinStatus, isRemoved)) {
      return JoinStatus.removedFromChatSpace();
    } else if (isApprovedAndVisibleAndLeft(visibility, requestToJoinStatus, hasLeft)) {
      return JoinStatus.leftChatSpace();
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

  private static boolean isApprovedAndVisibleAndRemoved(final ChatSpaceVisibility visibility, final ChatSpaceRequestToJoinStatus status, final Boolean removed) {
    return (isApprovedAndVisible(visibility, status)) && removed;
  }

  private static boolean isApprovedAndVisibleAndLeft(final ChatSpaceVisibility visibility, final ChatSpaceRequestToJoinStatus status, final Boolean left) {
    return (isApprovedAndVisible(visibility, status)) && left;
  }

  private static boolean isApprovedAndVisibleAndAMember(final ChatSpaceVisibility visibility, final ChatSpaceRequestToJoinStatus status, final Boolean aMember) {
    return (isApprovedAndVisible(visibility, status)) && aMember;
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
    return JOINED_CHAT_SPACE != joinStatus && JOINED_STREAM != joinStatus;
  }

  public static JoinStatus byStatus(final boolean isPrivateOrProtected) {
    if (isPrivateOrProtected) {
      return JoinStatus.notJoinedPrivate();
    }
    return JoinStatus.notJoinedPublic();
  }

  public static JoinStatus byStreamStatus(final boolean isStreamPrivateOrProtected) {
    return byStatus(isStreamPrivateOrProtected);
  }

  public static JoinStatus byChatSpaceStatus(final boolean isChatSpacePrivateOrProtected) {
    return byStatus(isChatSpacePrivateOrProtected);
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

  public static JoinStatus joinedStream() {
    return JOINED_STREAM;
  }

  public static JoinStatus attended() {
    return ATTENDED;
  }

  public static JoinStatus notAttendingStream() {
    return NOT_ATTENDING_STREAM;
  }

  public static JoinStatus attending() {
    return ATTENDING_STREAM;
  }

  public static JoinStatus leftChatSpace() {
    return LEFT_CHAT_SPACE;
  }

  public static JoinStatus removedFromChatSpace() {
    return REMOVED_CHAT_SPACE;
  }
}
