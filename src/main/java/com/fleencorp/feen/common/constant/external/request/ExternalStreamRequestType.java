package com.fleencorp.feen.common.constant.external.request;

import com.fleencorp.feen.stream.model.request.external.ExternalStreamRequest;

/**
 * Describes the various types of actions that can be requested for external
 * stream or event management. These actions include creating or updating streams,
 * managing attendance, and rescheduling events. Each action corresponds to a specific
 * type of request that can be made, helping the system understand what operation is
 * intended by the user or process.
 *
 * <p>Some examples of possible actions are:</p>
 * <ul>
 *   <li>Canceling a scheduled event or stream.</li>
 *   <li>Creating a new event, whether scheduled or instant.</li>
 *   <li>Launching a live broadcast.</li>
 *   <li>Rescheduling an event to a different time or date.</li>
 *   <li>Updating stream visibility settings (e.g., from public to private).</li>
 *   <li>Modifying event or stream details like title or description.</li>
 *   <li>Indicating that someone will no longer attend an event.</li>
 *   <li>Removing an existing event or stream entirely.</li>
 * </ul>
 *
 * <p>This set of actions allows the system to handle a variety of stream and event
 * workflows, helping to process requests in a structured and understandable way.</p>
 *
 * The {@link ExternalStreamRequest} class utilizes these actions to define the type
 * of operation associated with each request.
 *
 * @author Yusuf Àlàmu Musa
 * @version 1.0
 */
public enum ExternalStreamRequestType {

  PROCESS_ATTENDEE_JOIN_REQUEST,
  CANCEL,
  CREATE_EVENT,
  CREATE_INSTANT_EVENT,
  CREATE_LIVE_BROADCAST,
  DELETE,
  JOIN_STREAM,
  NOT_ATTENDING,
  PATCH,
  RESCHEDULE,
  VISIBILITY_UPDATE;

  public static ExternalStreamRequestType cancel() {
    return CANCEL;
  }

  public static ExternalStreamRequestType createEvent() {
    return CREATE_EVENT;
  }

  public static ExternalStreamRequestType createLiveBroadcast() {
    return CREATE_LIVE_BROADCAST;
  }

  public static ExternalStreamRequestType delete() {
    return DELETE;
  }

  public static ExternalStreamRequestType createInstantEvent() {
    return CREATE_INSTANT_EVENT;
  }

  public static ExternalStreamRequestType joinStream() {
    return JOIN_STREAM;
  }

  public static ExternalStreamRequestType notAttending() {
    return NOT_ATTENDING;
  }

  public static ExternalStreamRequestType patch() {
    return PATCH;
  }

  public static ExternalStreamRequestType processAttendeeJoinRequest() {
    return PROCESS_ATTENDEE_JOIN_REQUEST;
  }

  public static ExternalStreamRequestType reschedule() {
    return RESCHEDULE;
  }

  public static ExternalStreamRequestType visibilityUpdate() {
    return VISIBILITY_UPDATE;
  }

  public static boolean isProcessAttendeeJoinRequest(final ExternalStreamRequestType requestType) {
    return requestType == PROCESS_ATTENDEE_JOIN_REQUEST;
  }

  public static boolean isCancelRequest(final ExternalStreamRequestType requestType) {
    return requestType == CANCEL;
  }

  public static boolean isCreateEventRequest(final ExternalStreamRequestType requestType) {
    return requestType == CREATE_EVENT;
  }

  public static boolean isCreateInstantEvent(final ExternalStreamRequestType requestType) {
    return requestType == CREATE_INSTANT_EVENT;
  }

  public static boolean isCreateLiveBroadcastRequest(final ExternalStreamRequestType requestType) {
    return requestType == CREATE_LIVE_BROADCAST;
  }

  public static boolean isDeleteRequest(final ExternalStreamRequestType requestType) {
    return requestType == DELETE;
  }

  public static boolean isJoinStreamRequest(final ExternalStreamRequestType requestType) {
    return requestType == JOIN_STREAM;
  }

  public static boolean isNotAttendingRequest(final ExternalStreamRequestType requestType) {
    return requestType == NOT_ATTENDING;
  }

  public static boolean isPatchRequest(final ExternalStreamRequestType requestType) {
    return requestType == PATCH;
  }

  public static boolean isRescheduleRequest(final ExternalStreamRequestType requestType) {
    return requestType == RESCHEDULE;
  }

  public static boolean isVisibilityUpdate(final ExternalStreamRequestType requestType) {
    return requestType == VISIBILITY_UPDATE;
  }
}

