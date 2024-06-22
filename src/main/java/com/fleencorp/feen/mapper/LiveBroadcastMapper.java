package com.fleencorp.feen.mapper;

import com.fleencorp.feen.model.response.google.youtube.base.LiveBroadcastResponse;
import com.google.api.services.youtube.model.*;

import static java.util.Objects.nonNull;

/**
 * The {@code LiveBroadcastMapper} class provides utility methods to map {@link LiveBroadcast} objects to other
 * representations, such as DTOs or response objects.
 *
 * <p> This class is intended to be used as a helper for converting {@link LiveBroadcast} instances retrieved from the
 * YouTube API into application-specific objects that can be returned to clients or used internally.</p>
 *
 * <p> Note: This class currently has no implementation. Future methods for mapping live broadcast data should be added here.</p>
 *
 * <p> This class does not currently contain any methods or example usages.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
public class LiveBroadcastMapper {

  private LiveBroadcastMapper() {}

  /**
   * Maps a {@link LiveBroadcast} object to a {@link LiveBroadcastResponse}.
   *
   * <p> This method takes a {@link LiveBroadcast} object as input and converts it into a {@link LiveBroadcastResponse}
   * object. It extracts relevant fields from the {@link LiveBroadcast} and sets them in the {@link LiveBroadcastResponse}
   * using a builder pattern.</p>
   *
   * @param liveBroadcast The {@link LiveBroadcast} object to be mapped.
   * @return A {@link LiveBroadcastResponse} object containing the mapped data.
   */
  public static LiveBroadcastResponse mapToLiveBroadcastResponse(LiveBroadcast liveBroadcast) {
    if (nonNull(liveBroadcast)) {
      return LiveBroadcastResponse.builder()
              .kind(liveBroadcast.getKind())
              .etag(liveBroadcast.getEtag())
              .id(liveBroadcast.getId())
              .snippetTitle(liveBroadcast.getSnippet().getTitle())
              .snippetDescription(liveBroadcast.getSnippet().getDescription())
              .snippetPublishedAt(liveBroadcast.getSnippet().getPublishedAt().toStringRfc3339())
              .snippetChannelId(liveBroadcast.getSnippet().getChannelId())
              .liveBroadcastStatus(mapToStatus(liveBroadcast.getStatus()))
              .contentDetails(mapToContentDetails(liveBroadcast.getContentDetails()))
              .snippet(mapToSnippet(liveBroadcast.getSnippet()))
              .build();
    }
    return null;
  }

  /**
   * Maps a {@link LiveBroadcastStatus} object to a {@link LiveBroadcastResponse.LiveBroadcastStatus}.
   *
   * <p> This method takes a {@link LiveBroadcastStatus} object as input and converts it into a {@link LiveBroadcastResponse.LiveBroadcastStatus}
   * object. It extracts relevant fields from the {@link LiveBroadcastStatus} and sets them in the {@link LiveBroadcastResponse.LiveBroadcastStatus}
   * using a builder pattern.</p>
   *
   * @param status The {@link LiveBroadcastStatus} object to be mapped.
   * @return A {@link LiveBroadcastResponse.LiveBroadcastStatus} object containing the mapped data.
   */
  private static LiveBroadcastResponse.LiveBroadcastStatus mapToStatus(LiveBroadcastStatus status) {
    if (nonNull(status)) {
      return LiveBroadcastResponse.LiveBroadcastStatus.builder()
              .lifeCycleStatus(status.getLifeCycleStatus())
              .privacyStatus(status.getPrivacyStatus())
              .recordingStatus(status.getRecordingStatus())
              .build();
    }
    return null;
  }

  /**
   * Maps a {@link LiveBroadcastContentDetails} object to a {@link LiveBroadcastResponse.ContentDetails}.
   *
   * <p> This method takes a {@link LiveBroadcastContentDetails} object as input and converts it into a {@link LiveBroadcastResponse.ContentDetails}
   * object. It extracts relevant fields from the {@link LiveBroadcastContentDetails} and sets them in the {@link LiveBroadcastResponse.ContentDetails}
   * using a builder pattern.</p>
   *
   * @param contentDetails The {@link LiveBroadcastContentDetails} object to be mapped.
   * @return A {@link LiveBroadcastResponse.ContentDetails} object containing the mapped data.
   */
  private static LiveBroadcastResponse.ContentDetails mapToContentDetails(LiveBroadcastContentDetails contentDetails) {
    if (nonNull(contentDetails)) {
      return LiveBroadcastResponse.ContentDetails.builder()
              .enableClosedCaptions(contentDetails.getEnableClosedCaptions())
              .enableContentEncryption(contentDetails.getEnableContentEncryption())
              .enableDvr(contentDetails.getEnableDvr())
              .enableEmbed(contentDetails.getEnableEmbed())
              .recordFromStart(contentDetails.getRecordFromStart())
              .startWithSlate(contentDetails.getStartWithSlate())
              .monitorStream(mapToMonitorStream(contentDetails.getMonitorStream()))
              .projection(contentDetails.getProjection())
              .enableAutoStart(contentDetails.getEnableAutoStart())
              .build();
    }
    return null;
  }

  /**
   * Maps a {@link MonitorStreamInfo} object to a {@link LiveBroadcastResponse.MonitorStream}.
   *
   * <p> This method takes a {@link MonitorStreamInfo} object as input and converts it into a {@link LiveBroadcastResponse.MonitorStream}
   * object. It extracts relevant fields from the {@link MonitorStreamInfo} and sets them in the {@link LiveBroadcastResponse.MonitorStream}
   * using a builder pattern.</p>
   *
   * @param monitorStream The {@link MonitorStreamInfo} object to be mapped.
   * @return A {@link LiveBroadcastResponse.MonitorStream} object containing the mapped data.
   */
  private static LiveBroadcastResponse.MonitorStream mapToMonitorStream(MonitorStreamInfo monitorStream) {
    if (nonNull(monitorStream)) {
      return LiveBroadcastResponse.MonitorStream.builder()
              .enableMonitorStream(monitorStream.getEnableMonitorStream())
              .broadcastStreamDelayMs(monitorStream.getBroadcastStreamDelayMs())
              .embedHtml(monitorStream.getEmbedHtml())
              .build();
    }
    return null;
  }

  /**
   * Maps a {@link LiveBroadcastSnippet} object to a {@link LiveBroadcastResponse.Snippet}.
   *
   * <p> This method takes a {@link LiveBroadcastSnippet} object as input and converts it into a {@link LiveBroadcastResponse.Snippet}
   * object. It extracts relevant fields from the {@link LiveBroadcastSnippet} and sets them in the {@link LiveBroadcastResponse.Snippet}
   * using a builder pattern.</p>
   *
   * @param snippet The {@link LiveBroadcastSnippet} object to be mapped.
   * @return A {@link LiveBroadcastResponse.Snippet} object containing the mapped data.
   */
  private static LiveBroadcastResponse.Snippet mapToSnippet(LiveBroadcastSnippet snippet) {
    if (nonNull(snippet)) {
      return LiveBroadcastResponse.Snippet.builder()
              .title(snippet.getTitle())
              .description(snippet.getDescription())
              .publishedAt(snippet.getPublishedAt().toStringRfc3339())
              .channelId(snippet.getChannelId())
              .scheduledStartTime(snippet.getScheduledStartTime().toStringRfc3339())
              .scheduledEndTime(snippet.getScheduledEndTime().toStringRfc3339())
              .actualStartTime(snippet.getActualStartTime().toStringRfc3339())
              .actualEndTime(snippet.getActualEndTime().toStringRfc3339())
              .liveChatId(snippet.getLiveChatId())
              .build();
    }
    return null;
  }

}

