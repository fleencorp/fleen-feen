package com.fleencorp.feen.chat.space.model.request.external.message;

import com.fleencorp.feen.chat.space.constant.message.CalendarEventChatMessageField;
import com.fleencorp.feen.stream.model.other.Schedule;
import com.fleencorp.feen.stream.model.response.StreamResponse;
import lombok.*;

import java.util.List;

import static com.fleencorp.feen.chat.space.constant.message.CalendarEventChatMessageField.*;
import static com.fleencorp.feen.common.util.external.google.GoogleApiUtil.getChatSpaceIdOrNameRequiredPattern;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleChatSpaceMessageRequest {

  private String spaceIdOrName;
  private String title;
  private String descriptionOrSubtitle;
  private String imageUrl;
  private String sectionHeader;
  private int unCollapsibleWidgetsCount;
  private List<DecoratedText> decoratedTexts;
  private AccessoryButton accessoryButton;

  /**
   * Retrieves the first decorated text from the list of decorated texts.
   *
   * <p>This method returns the first {@code DecoratedText} object from the list,
   * if the list is not null or empty. If the list is null or empty, it returns {@code null}.</p>
   *
   * @return the first {@code DecoratedText} object, or {@code null} if none exists.
   */
  public DecoratedText getFirstDecoratedText() {
    return (decoratedTexts != null && !decoratedTexts.isEmpty()) ? decoratedTexts.getFirst() : null;
  }


  /**
   * Retrieves the second decorated text from the list of decorated texts.
   *
   * <p>This method returns the second {@code DecoratedText} object from the list,
   * if the list contains at least two elements. If the list is null, empty, or contains
   * fewer than two elements, it returns {@code null}.</p>
   *
   * @return the second {@code DecoratedText} object, or {@code null} if none exists.
   */
  public DecoratedText getSecondDecoratedText() {
    return (decoratedTexts != null && decoratedTexts.size() > 1) ? decoratedTexts.get(1) : null;
  }

  /**
   * Creates a new instance of {@link GoogleChatSpaceMessageRequest} based on the provided
   * space name and stream details.
   *
   * <p>This method is used to construct a message request for Google Chat, incorporating
   * information such as the space ID or name, title, description, and additional details
   * related to the stream. It sets a default image URL and configures the message with
   * specific widgets and buttons for enhanced interaction.</p>
   *
   * @param spaceName the name or ID of the Google Chat space where the message will be sent.
   *                  Must conform to the required pattern for space identifiers.
   * @param stream    the {@link StreamResponse} object containing details about the stream,
   *                  including title, description, schedule, and stream link.
   * @return a {@link GoogleChatSpaceMessageRequest} object populated with the provided stream details
   *         and additional metadata for sending a message to the specified chat space.
   */
  public static GoogleChatSpaceMessageRequest ofEventOrStream(final String spaceName, final StreamResponse stream) {
    return GoogleChatSpaceMessageRequest.builder()
      .spaceIdOrName(getChatSpaceIdOrNameRequiredPattern(spaceName))
      .title(stream.getTitle())
      .descriptionOrSubtitle(stream.getDescription())
      .imageUrl(robotImage())
      .sectionHeader(CalendarEventChatMessageField.eventDetails())
      .unCollapsibleWidgetsCount(5)
      .decoratedTexts(List.of(DecoratedText.ofDateAndTime(stream.getSchedule()), DecoratedText.ofTimezone(stream.getSchedule())))
      .accessoryButton(AccessoryButton.ofEventOrStream(stream.getStreamLinkNotMasked()))
      .build();
  }

  @Builder
  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class DecoratedText {
    private String text;
    private String icon;

    /**
     * Creates a {@code DecoratedText} representation for the date and time of the given schedule.
     *
     * <p>This method constructs a {@code DecoratedText} object that includes a bold "Date:" label
     * followed by the date from the provided {@code Schedule}. It also adds a clock icon to visually
     * represent the date and time information.</p>
     *
     * <p>The date is formatted using HTML-style bold tags for the "Date" label, and the actual date
     * value is dynamically retrieved from the {@code Schedule}'s {@code getDate()} method.</p>
     *
     * @param schedule the {@code Schedule} object from which the date information is retrieved.
     * @return a {@code DecoratedText} object displaying the formatted date with a clock icon.
     */
    public static DecoratedText ofDateAndTime(final Schedule schedule) {
      return DecoratedText.builder()
        .text(String.format("<b>Date:</b> %s", schedule.getDate()))
        .icon(clockIcon())
        .build();
    }

    /**
     * Creates a {@code DecoratedText} representation for the timezone of the given schedule.
     *
     * <p>This method constructs a {@code DecoratedText} object that includes a bold "Timezone:" label
     * followed by the formatted timezone from the provided {@code Schedule}. It also adds a bookmark
     * icon as a visual indicator.</p>
     *
     * <p>The text is formatted using HTML-style bold tags for the "Timezone" label, and the timezone
     * value is dynamically retrieved from the {@code Schedule}'s {@code getFormattedTimezone()} method.</p>
     *
     * @param schedule the {@code Schedule} object from which the timezone information is retrieved.
     * @return a {@code DecoratedText} object displaying the formatted timezone with a bookmark icon.
     */
    public static DecoratedText ofTimezone(final Schedule schedule) {
      return DecoratedText.builder()
        .text(String.format("<b>Timezone:</b> %s", schedule.getFormattedTimezone()))
        .icon(bookmarkIcon())
        .build();
    }
  }

  @Builder
  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class AccessoryButton {
    private String text;
    private String url;
    private Color color;

    public static AccessoryButton ofEventOrStream(final String linkOrUrl) {
      return AccessoryButton.builder()
        .text(CalendarEventChatMessageField.joinEvent())
        .url(linkOrUrl)
        .color(Color.ofEventOrStream())
        .build();
    }
  }

  @Builder
  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Color {
    private float red;
    private float green;
    private float blue;
    private float alpha;

    public static Color ofEventOrStream() {
      return Color.builder()
        .red(0)
        .green(0.5f)
        .blue(1)
        .alpha(1)
        .build();
    }
  }
}

