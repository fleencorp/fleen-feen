package com.fleencorp.feen.common.constant.mask;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fleencorp.base.constant.base.ApiParameter;
import com.fleencorp.base.serializer.ToStringEnumSerializer;
import com.fleencorp.feen.stream.constant.core.StreamSource;
import lombok.Getter;
import lombok.Setter;

import static com.fleencorp.feen.common.constant.external.google.calendar.GoogleCalendarParameter.googleMeetLink;
import static com.fleencorp.feen.common.constant.external.google.youtube.base.YouTubeParameter.liveStreamLink;
import static com.fleencorp.feen.stream.constant.core.StreamSource.*;
import static java.util.Objects.isNull;

/**
 * Represents a masked version of stream link URIs for different streaming platforms.
 *
 * <p>Each enum constant corresponds to a specific streaming platform (e.g., Google Meet, YouTube Live)
 * and provides functionality to mask the sensitive parts of the stream links.
 * This is used to protect sensitive information such as meeting codes or video IDs.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Getter
@JsonSerialize(using = ToStringEnumSerializer.class)
public enum MaskedStreamLinkUri implements ApiParameter {

  /**
   * Enum constants for stream sources with custom URL masking logic.
   * The `maskLink` method is implemented differently for each source.
   */
  GOOGLE_MEET(googleMeet(), googleMeetLink()) {

    /**
     * Masks the given Google Meet URL by hiding parts of the meeting code.
     *
     * @param url the original Google Meet URL to be masked
     * @return the masked Google Meet URL with part of the meeting code replaced by "***"
     * @throws IllegalArgumentException if the provided URL is not a valid Google Meet URL
     */
    @Override
    public String maskLink(final String url) {
      // Mask the Google Meet URL by applying specific masking logic
      return maskMeetUrl(url);
    }
  },

  YOUTUBE(youtubeLive(), liveStreamLink()) {

    /**
     * Masks the given YouTube URL by hiding parts of the video ID.
     *
     * @param url the original YouTube URL to be masked
     * @return the masked YouTube URL with part of the video ID replaced by "***"
     * @throws IllegalArgumentException if the provided URL is not a valid YouTube URL
     */
    @Override
    public String maskLink(final String url) {
      // Mask the YouTube URL by applying specific masking logic
      return maskYouTubeUrl(url);
    }
  };

  @Setter
  private String value;
  private final String baseUrl;
  public abstract String maskLink(String url);

  MaskedStreamLinkUri(final String value, final String baseUrl) {
    this.value = value;
    this.baseUrl = baseUrl;
  }

  @Override
  public String toString() {
    return value;
  }

  /**
   * Creates a MaskedStreamLinkUri instance based on the provided stream source and value.
   *
   * <p>
   * This method determines whether the stream source is Google Meet or YouTube Live
   * and applies the appropriate link masking. For Google Meet links, the method will
   * return a MaskedStreamLinkUri instance with the link masked according to Google Meet rules.
   * Similarly, for YouTube Live links, it will return a MaskedStreamLinkUri instance
   * with the link masked according to YouTube rules.</p>
   *
   * @param value  the original stream link (Google Meet or YouTube)
   * @param source the stream source (Google Meet or YouTube Live)
   * @return a MaskedStreamLinkUri instance with the masked link based on the source
   */
  public static MaskedStreamLinkUri of(final String value, final StreamSource source) {
    // Initialize with default Google Meet masked stream link
    MaskedStreamLinkUri maskedStreamLinkUri = MaskedStreamLinkUri.GOOGLE_MEET;

    // Check if the source is Google Meet and apply Google Meet-specific link masking
    if (isGoogleMeet(source)) {
      final String maskedLink = maskedStreamLinkUri.maskLink(value);
      maskedStreamLinkUri.setValue(maskedLink);
    }
    // Check if the source is YouTube Live and apply YouTube-specific link masking
    else if (isYouTubeLive(source)) {
      maskedStreamLinkUri = MaskedStreamLinkUri.YOUTUBE;
      final String maskedLink = maskedStreamLinkUri.maskLink(value);
      maskedStreamLinkUri.setValue(maskedLink);
    }
    // Return the masked link URI based on the source
    return maskedStreamLinkUri;
  }

  /**
   * Masks a Google Meet URL, obscuring parts of the meeting code while keeping the base URL visible.
   *
   * @param url the Google Meet URL to be masked.
   * @return the masked Google Meet URL.
   * @throws IllegalArgumentException if the URL is invalid.
   */
  private static String maskMeetUrl(final String url) {
    // Validate if the URL is null or does not start with the base Google Meet URL
    if (isNull(url) || !url.startsWith(GOOGLE_MEET.baseUrl)) {
      throw new IllegalArgumentException("Invalid Google Meet URL");
    }

    // Extract the meeting code from the URL by removing the base Google Meet URL part
    final String meetingCode = url.substring(GOOGLE_MEET.baseUrl.length());
    // Split the meeting code by hyphens to identify its parts
    final String[] codeParts = meetingCode.split("-");

    // If the meeting code does not contain the expected three parts, return the original URL
    if (codeParts.length != 3) {
      return url; // Return original URL if the format is unexpected
    }

    // Mask the first part of the meeting code, leaving the first three characters visible
    final String maskedCode = codeParts[0].substring(0, 3) + "-***-***";
    // Return the masked URL by appending the masked code to the base Google Meet URL
    return GOOGLE_MEET.baseUrl + maskedCode;
  }

  /**
   * Masks a YouTube URL, obscuring parts of the video ID while keeping the base URL visible.
   *
   * @param url the YouTube URL to be masked.
   * @return the masked YouTube URL.
   * @throws IllegalArgumentException if the URL is invalid.
   */
  private static String maskYouTubeUrl(final String url) {
    // Validate if the URL is null or does not start with the base YouTube URL
    if (isNull(url) || !url.startsWith(YOUTUBE.baseUrl)) {
      throw new IllegalArgumentException("Invalid YouTube URL");
    }

    // Extract the video ID from the URL by removing the base YouTube URL part
    final String maskedId = getMaskedIdFromVideoId(url);
    // Return the masked URL by appending the masked ID to the base YouTube URL
    return YOUTUBE.baseUrl + maskedId;
  }

  /**
   * Extracts and masks the video ID from a YouTube URL.
   *
   * <p>The method takes a YouTube video URL, extracts the video ID, and then masks the ID
   * by retaining the first three characters and replacing the rest with "***".
   * Throws an IllegalArgumentException if the video ID is missing.</p>
   *
   * @param url the YouTube video URL
   * @return a masked version of the video ID in the format "xxx-***"
   * @throws IllegalArgumentException if the video ID is missing
   */
  private static String getMaskedIdFromVideoId(final String url) {
    final String videoId = url.substring(YOUTUBE.baseUrl.length());
    // Check if the video ID is empty and throw an exception if it is missing
    if (videoId.isEmpty()) {
      throw new IllegalArgumentException("Video ID is missing");
    }

    // Mask the video ID by keeping the first three characters and replacing the rest with "***"
    return videoId.substring(0, Math.min(videoId.length(), 3)) + "-***";
  }

}

