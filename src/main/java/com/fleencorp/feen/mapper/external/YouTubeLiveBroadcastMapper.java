package com.fleencorp.feen.mapper.external;

import com.fleencorp.feen.model.response.external.google.youtube.base.YouTubeLiveBroadcastResponse;
import com.google.api.services.youtube.model.*;

import static java.util.Objects.nonNull;

/**
* The {@code LiveBroadcastMapper} class provides utility methods to map {@link LiveBroadcast} objects to other
* representations, such as DTOs or response objects.
*
* <p>This class is intended to be used as a helper for converting {@link LiveBroadcast} instances retrieved from the
* YouTube API into application-specific objects that can be returned to clients or used internally.</p>
*
* <p>Note: This class currently has no implementation. Future methods for mapping live broadcast data should be added here.</p>
*
* <p>This class does not currently contain any methods or example usages.</p>
*
* @author Yusuf Alamu Musa
* @version 1.0
*/
public class YouTubeLiveBroadcastMapper {

  /**
  * Maps a {@link LiveBroadcast} object to a {@link YouTubeLiveBroadcastResponse}.
  *
  * <p>This method takes a {@link LiveBroadcast} object as input and converts it into a {@link YouTubeLiveBroadcastResponse}
  * object. It extracts relevant fields from the {@link LiveBroadcast} and sets them in the {@link YouTubeLiveBroadcastResponse}
  * using a builder pattern.</p>
  *
  * @param liveBroadcast The {@link LiveBroadcast} object to be mapped.
  * @return A {@link YouTubeLiveBroadcastResponse} object containing the mapped data.
  */
  public static YouTubeLiveBroadcastResponse mapToLiveBroadcastResponse(final LiveBroadcast liveBroadcast) {
    if (nonNull(liveBroadcast)) {
      return YouTubeLiveBroadcastResponse.builder()
          .kind(liveBroadcast.getKind())
          .etag(liveBroadcast.getEtag())
          .id(liveBroadcast.getId())
          .snippetTitle(liveBroadcast.getSnippet().getTitle())
          .snippetDescription(liveBroadcast.getSnippet().getDescription())
          .snippetPublishedAt(liveBroadcast.getSnippet().getPublishedAt())
          .snippetChannelId(liveBroadcast.getSnippet().getChannelId())
          .liveBroadcastStatus(mapToStatus(liveBroadcast.getStatus()))
          .contentDetails(mapToContentDetails(liveBroadcast.getContentDetails()))
          .snippet(mapToSnippet(liveBroadcast.getSnippet()))
          .build();
    }
    return null;
  }

  /**
  * Maps a {@link LiveBroadcastStatus} object to a {@link YouTubeLiveBroadcastResponse.LiveBroadcastStatus}.
  *
  * <p>This method takes a {@link LiveBroadcastStatus} object as input and converts it into a {@link YouTubeLiveBroadcastResponse.LiveBroadcastStatus}
  * object. It extracts relevant fields from the {@link LiveBroadcastStatus} and sets them in the {@link YouTubeLiveBroadcastResponse.LiveBroadcastStatus}
  * using a builder pattern.</p>
  *
  * @param status The {@link LiveBroadcastStatus} object to be mapped.
  * @return A {@link YouTubeLiveBroadcastResponse.LiveBroadcastStatus} object containing the mapped data.
  */
  private static YouTubeLiveBroadcastResponse.LiveBroadcastStatus mapToStatus(final LiveBroadcastStatus status) {
    if (nonNull(status)) {
      return YouTubeLiveBroadcastResponse.LiveBroadcastStatus.builder()
          .lifeCycleStatus(status.getLifeCycleStatus())
          .privacyStatus(status.getPrivacyStatus())
          .recordingStatus(status.getRecordingStatus())
          .build();
    }
    return null;
  }

  /**
  * Maps a {@link LiveBroadcastContentDetails} object to a {@link YouTubeLiveBroadcastResponse.ContentDetails}.
  *
  * <p>This method takes a {@link LiveBroadcastContentDetails} object as input and converts it into a {@link YouTubeLiveBroadcastResponse.ContentDetails}
  * object. It extracts relevant fields from the {@link LiveBroadcastContentDetails} and sets them in the {@link YouTubeLiveBroadcastResponse.ContentDetails}
  * using a builder pattern.</p>
  *
  * @param contentDetails The {@link LiveBroadcastContentDetails} object to be mapped.
  * @return A {@link YouTubeLiveBroadcastResponse.ContentDetails} object containing the mapped data.
  */
  private static YouTubeLiveBroadcastResponse.ContentDetails mapToContentDetails(final LiveBroadcastContentDetails contentDetails) {
    if (nonNull(contentDetails)) {
      return YouTubeLiveBroadcastResponse.ContentDetails.builder()
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
  * Maps a {@link MonitorStreamInfo} object to a {@link YouTubeLiveBroadcastResponse.MonitorStream}.
  *
  * <p>This method takes a {@link MonitorStreamInfo} object as input and converts it into a {@link YouTubeLiveBroadcastResponse.MonitorStream}
  * object. It extracts relevant fields from the {@link MonitorStreamInfo} and sets them in the {@link YouTubeLiveBroadcastResponse.MonitorStream}
  * using a builder pattern.</p>
  *
  * @param monitorStream The {@link MonitorStreamInfo} object to be mapped.
  * @return A {@link YouTubeLiveBroadcastResponse.MonitorStream} object containing the mapped data.
  */
  private static YouTubeLiveBroadcastResponse.MonitorStream mapToMonitorStream(final MonitorStreamInfo monitorStream) {
    if (nonNull(monitorStream)) {
      return YouTubeLiveBroadcastResponse.MonitorStream.builder()
          .enableMonitorStream(monitorStream.getEnableMonitorStream())
          .broadcastStreamDelayMs(monitorStream.getBroadcastStreamDelayMs())
          .embedHtml(monitorStream.getEmbedHtml())
          .build();
    }
    return null;
  }

  /**
  * Maps a {@link LiveBroadcastSnippet} object to a {@link YouTubeLiveBroadcastResponse.Snippet}.
  *
  * <p>This method takes a {@link LiveBroadcastSnippet} object as input and converts it into a {@link YouTubeLiveBroadcastResponse.Snippet}
  * object. It extracts relevant fields from the {@link LiveBroadcastSnippet} and sets them in the {@link YouTubeLiveBroadcastResponse.Snippet}
  * using a builder pattern.</p>
  *
  * @param snippet The {@link LiveBroadcastSnippet} object to be mapped.
  * @return A {@link YouTubeLiveBroadcastResponse.Snippet} object containing the mapped data.
  */
  private static YouTubeLiveBroadcastResponse.Snippet mapToSnippet(final LiveBroadcastSnippet snippet) {
    if (nonNull(snippet)) {
      return YouTubeLiveBroadcastResponse.Snippet.builder()
          .title(snippet.getTitle())
          .description(snippet.getDescription())
          .publishedAt(snippet.getPublishedAt())
          .channelId(snippet.getChannelId())
          .scheduledStartTime(snippet.getScheduledStartTime())
          .scheduledEndTime(snippet.getScheduledEndTime())
          .actualStartTime(snippet.getActualStartTime())
          .actualEndTime(snippet.getActualEndTime())
          .liveChatId(snippet.getLiveChatId())
          .build();
    }
    return null;
  }

}

