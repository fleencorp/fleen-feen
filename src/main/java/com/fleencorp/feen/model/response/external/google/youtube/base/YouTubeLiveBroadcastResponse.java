package com.fleencorp.feen.model.response.external.google.youtube.base;

import com.google.api.client.util.DateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Builder
@Getter
@Setter
public class YouTubeLiveBroadcastResponse {

  private String kind;
  private String etag;
  private String id;
  private String snippetTitle;
  private String snippetDescription;
  private DateTime snippetPublishedAt;
  private String snippetChannelId;
  private LiveBroadcastStatus liveBroadcastStatus;
  private ContentDetails contentDetails;

  @Builder
  @Getter
  @Setter
  public static class LiveBroadcastStatus {
    private String lifeCycleStatus;
    private String privacyStatus;
    private String recordingStatus;
  }

  @Builder
  @Getter
  @Setter
  public static class ContentDetails {
    private Boolean enableClosedCaptions;
    private Boolean enableContentEncryption;
    private Boolean enableDvr;
    private Boolean enableEmbed;
    private Boolean recordFromStart;
    private Boolean startWithSlate;
    private MonitorStream monitorStream;
    private String projection;
    private Boolean enableAutoStart;
  }

  @Builder
  @Getter
  @Setter
  public static class MonitorStream {
    private Boolean enableMonitorStream;
    private Long broadcastStreamDelayMs;
    private String embedHtml;
  }

  @Builder
  @Getter
  @Setter
  public static class Snippet {
    private String title;
    private String description;
    private DateTime publishedAt;
    private String channelId;
    private List<String> tags;
    private DateTime scheduledStartTime;
    private DateTime scheduledEndTime;
    private DateTime actualStartTime;
    private DateTime actualEndTime;
    private String liveChatId;
  }

  @Builder
  @Getter
  @Setter
  public static class Status {
    private String lifeCycleStatus;
    private String privacyStatus;
    private String recordingStatus;
    private Boolean madeForKids;
    private Boolean selfDeclaredMadeForKids;
  }

  @Builder
  @Getter
  @Setter
  public static class LiveStreamingDetails {
    private String actualStartTime;
    private String actualEndTime;
    private String scheduledStartTime;
    private String scheduledEndTime;
    private String concurrentViewers;
    private String activeLiveChatId;
  }

  private Snippet snippet;
  private Status status;
  private LiveStreamingDetails liveStreamingDetails;
}