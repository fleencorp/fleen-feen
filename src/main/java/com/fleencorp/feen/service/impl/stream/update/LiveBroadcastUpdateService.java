package com.fleencorp.feen.service.impl.stream.update;

import com.fleencorp.feen.constant.base.ResultType;
import com.fleencorp.feen.event.broadcast.BroadcastService;
import com.fleencorp.feen.event.model.stream.EventStreamCreatedResult;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.request.youtube.broadcast.*;
import com.fleencorp.feen.model.response.external.google.youtube.CreateYouTubeLiveBroadcastResponse;
import com.fleencorp.feen.model.response.external.google.youtube.DeleteYouTubeLiveBroadcastResponse;
import com.fleencorp.feen.model.response.external.google.youtube.RescheduleYouTubeLiveBroadcastResponse;
import com.fleencorp.feen.model.response.external.google.youtube.UpdateYouTubeLiveBroadcastResponse;
import com.fleencorp.feen.service.external.google.youtube.YouTubeLiveBroadcastService;
import com.fleencorp.feen.service.impl.external.google.youtube.YouTubeLiveBroadcastServiceImpl;
import com.fleencorp.feen.service.stream.StreamOperationsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class responsible for handling updates to live broadcasts on platforms such as YouTube.
 *
 * <p>The {@code LiveBroadcastUpdateService} provides various methods to manage live broadcasts,
 * including updating visibility, modifying broadcast details, and handling related operations.
 * This service interacts with external APIs like YouTube's Data API to perform updates on live
 * broadcasts based on user requests.</p>
 *
 * <p>This service ensures that broadcast updates are performed in a transactional and secure manner,
 * leveraging authentication and authorization mechanisms as required by external APIs.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Slf4j
@Service
public class LiveBroadcastUpdateService {

  private final BroadcastService broadcastService;
  private final YouTubeLiveBroadcastService youTubeLiveBroadcastService;
  private final StreamOperationsService streamOperationsService;

  public LiveBroadcastUpdateService(
      @Lazy final BroadcastService broadcastService,
      @Lazy final YouTubeLiveBroadcastService youTubeLiveBroadcastService,
      final StreamOperationsService streamOperationsService) {
    this.broadcastService = broadcastService;
    this.youTubeLiveBroadcastService = youTubeLiveBroadcastService;
    this.streamOperationsService = streamOperationsService;
  }

  /**
   * Asynchronously creates a live stream in YouTube for the given FleenStream using the provided
   * CreateLiveBroadcastRequest. Updates the stream details with the created stream ID and HTML link,
   * then saves the stream and broadcasts the stream creation result.
   *
   * @param stream                   The FleenStream to be updated with YouTube live stream details.
   * @param createLiveBroadcastRequest The request object containing details for creating the live broadcast.
   */
  @Async
  @Transactional
  public void createLiveBroadcastAndStream(final FleenStream stream, final CreateLiveBroadcastRequest createLiveBroadcastRequest) {
    // Create the live broadcast using YouTubeLiveBroadcastService
    final CreateYouTubeLiveBroadcastResponse createYouTubeLiveBroadcastResponse = youTubeLiveBroadcastService.createBroadcast(createLiveBroadcastRequest);
    // Update the stream with the event ID and HTML link from the created YouTube live broadcast
    stream.update(createYouTubeLiveBroadcastResponse.liveBroadcastId(), createYouTubeLiveBroadcastResponse.liveStreamLink());
    // Save the stream
    streamOperationsService.save(stream);

    // Create an event stream created result
    final EventStreamCreatedResult eventStreamCreatedResult = EventStreamCreatedResult
      .of(stream.getOrganizerId(),
        stream.getStreamId(),
        stream.getExternalId(),
        stream.getStreamLink(),
        ResultType.EVENT_STREAM_CREATED);
    // Broadcast the event creation result to notify relevant services or users
    broadcastService.broadcastEventCreated(eventStreamCreatedResult);
  }

  /**
   * Asynchronously updates an existing live stream in YouTube using the provided UpdateLiveBroadcastRequest.
   * Updates the FleenStream with the latest details from YouTube and saves the stream to the repository.
   *
   * @param stream                      The FleenStream to be updated with the latest YouTube broadcast details.
   * @param updateLiveBroadcastRequest  The request object containing updated details for the live broadcast in YouTube.
   */
  @Async
  @Transactional
  public void updateLiveBroadcastAndStream(final FleenStream stream, final UpdateLiveBroadcastRequest updateLiveBroadcastRequest) {
    // Send the update request to YouTube Live Broadcast Service
    final UpdateYouTubeLiveBroadcastResponse updateYouTubeLiveBroadcastResponse = youTubeLiveBroadcastService.updateLiveBroadcast(updateLiveBroadcastRequest);
    log.info("Updated broadcast: {}", updateYouTubeLiveBroadcastResponse);

    // Update the FleenStream entity with the external ID from the updated YouTube broadcast
    stream.setExternalId(updateYouTubeLiveBroadcastResponse.liveBroadcastId());
    // Save the updated FleenStream entity to the repository
    streamOperationsService.save(stream);
  }

  /**
   * Asynchronously reschedules a live stream in YouTube for the given FleenStream using the provided
   * RescheduleLiveBroadcastRequest. Updates the stream's schedule details with the new start and end times,
   * and timezone, then saves the stream.
   *
   * @param stream                        The FleenStream to be rescheduled with new timing details.
   * @param rescheduleLiveBroadcastRequest The request object containing the updated schedule details for the live broadcast.
   */
  @Async
  @Transactional
  public void rescheduleLiveBroadcastAndStream(final FleenStream stream, final RescheduleLiveBroadcastRequest rescheduleLiveBroadcastRequest) {
    // Create the reschedule request to update the live stream in YouTube
    final RescheduleYouTubeLiveBroadcastResponse rescheduleYouTubeLiveBroadcastResponse = youTubeLiveBroadcastService.rescheduleLiveBroadcast(rescheduleLiveBroadcastRequest);
    log.info("Rescheduled broadcast: {}", rescheduleYouTubeLiveBroadcastResponse);

    // Update the FleenStream entity with the external ID from the updated YouTube broadcast
    stream.setExternalId(rescheduleYouTubeLiveBroadcastResponse.liveBroadcastId());
    // Save the updated FleenStream entity to the repository
    streamOperationsService.save(stream);
  }

  /**
   * Deletes a live broadcast from YouTube based on the provided DeleteLiveBroadcastRequest.
   * Logs the details of the deleted broadcast response.
   *
   * @param deleteLiveBroadcastRequest The request object containing details needed to delete the live broadcast on YouTube.
   */
  @Async
  @Transactional
  public void deleteLiveBroadcast(final DeleteLiveBroadcastRequest deleteLiveBroadcastRequest) {
    // Send the delete request to the YouTube Live Broadcast Service
    final DeleteYouTubeLiveBroadcastResponse deleteYouTubeLiveBroadcastResponse = youTubeLiveBroadcastService.deleteLiveBroadcast(deleteLiveBroadcastRequest);
    // Log the details of the deleted broadcast response
    log.info("Deleted broadcast: {}", deleteYouTubeLiveBroadcastResponse);
  }

  /**
   * Updates the visibility of a live broadcast stream on YouTube.
   *
   * <p>This method asynchronously updates the visibility status of a live broadcast stream by calling the
   * {@link YouTubeLiveBroadcastServiceImpl#updateLiveBroadcastVisibility(UpdateLiveBroadcastVisibilityRequest)}
   * service method. The visibility update is performed in a separate thread to avoid blocking the main execution
   * flow, and the transaction ensures consistency in the database if any operations are performed.</p>
   *
   * <p>The method logs the details of the update request for visibility changes, which is useful for monitoring
   * and debugging purposes. It handles visibility updates based on the provided request containing details like
   * access token, broadcast ID, and the new visibility status.</p>
   *
   * @param updateCalendarEventVisibilityRequest the request object containing details for updating the
   *                                             live broadcast visibility. This includes the broadcast ID,
   *                                             new visibility status, and access token for authentication.
   *
   * @see YouTubeLiveBroadcastServiceImpl#updateLiveBroadcastVisibility(UpdateLiveBroadcastVisibilityRequest)
   */
  @Async
  @Transactional
  public void updateStreamVisibility(final UpdateLiveBroadcastVisibilityRequest updateCalendarEventVisibilityRequest) {
    // Call the YouTubeLiveBroadcastService to update the live broadcast visibility
    final UpdateYouTubeLiveBroadcastResponse updateYouTubeLiveBroadcastResponse = youTubeLiveBroadcastService.updateLiveBroadcastVisibility(updateCalendarEventVisibilityRequest);
    log.info("Updated stream visibility: {}", updateYouTubeLiveBroadcastResponse);
  }
}
