package com.fleencorp.feen.adapter.spotify.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CurrentlyPlayingResponse {

  @JsonProperty("is_playing")
  private boolean isPlaying;

  @JsonProperty("timestamp")
  private long timestamp;

  @JsonProperty("context")
  private Context context;

  @JsonProperty("progress_ms")
  private int progressMs;

  @JsonProperty("item")
  private Track item;

  @JsonProperty("currently_playing_type")
  private String currentlyPlayingType;

  @JsonProperty("actions")
  private Actions actions;
}

@Getter
@Setter
class Context {
  @JsonProperty("external_urls")
  private ExternalUrls externalUrls;

  private String href;
  private String type;
  private String uri;
}

@Getter
@Setter
class ExternalUrls {
  private String spotify;
}

@Getter
@Setter
class Actions {
  private Disallows disallows;
}

@Getter
@Setter
class Disallows {
  private boolean resuming;
}

@Getter
@Setter
class Track {
  private Album album;
  private List<Artist> artists;

  @JsonProperty("available_markets")
  private List<String> availableMarkets;

  @JsonProperty("disc_number")
  private int discNumber;

  @JsonProperty("duration_ms")
  private int durationMs;

  private boolean explicit;

  @JsonProperty("external_ids")
  private ExternalIds externalIds;

  @JsonProperty("external_urls")
  private ExternalUrls externalUrls;

  private String href;
  private String id;

  @JsonProperty("is_local")
  private boolean isLocal;

  private String name;
  private int popularity;

  @JsonProperty("preview_url")
  private String previewUrl;

  @JsonProperty("track_number")
  private int trackNumber;

  private String type;
  private String uri;
}

@Getter
@Setter
class Album {
  @JsonProperty("album_type")
  private String albumType;

  private List<Artist> artists;

  @JsonProperty("available_markets")
  private List<String> availableMarkets;

  @JsonProperty("external_urls")
  private ExternalUrls externalUrls;

  private String href;
  private String id;
  private List<Image> images;
  private String name;

  @JsonProperty("release_date")
  private String releaseDate;

  @JsonProperty("release_date_precision")
  private String releaseDatePrecision;

  @JsonProperty("total_tracks")
  private int totalTracks;

  private String type;
  private String uri;
}

@Getter
@Setter
class Artist {
  @JsonProperty("external_urls")
  private ExternalUrls externalUrls;

  private String href;
  private String id;
  private String name;
  private String type;
  private String uri;
}

@Getter
@Setter
class Image {
  private int height;
  private String url;
  private int width;
}

@Getter
@Setter
class ExternalIds {
  private String isrc;
}

