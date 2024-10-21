package com.fleencorp.feen.model.response.notification;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.constant.notification.NotificationStatus;
import com.fleencorp.feen.constant.notification.NotificationType;
import com.fleencorp.feen.model.domain.notification.Notification;
import com.fleencorp.feen.model.response.base.FleenFeenResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static java.util.Objects.nonNull;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "id",
  "type",
  "message",
  "status",
  "id_or_link_or_url",
  "created_on",
  "updated_on"
})
public class NotificationResponse extends FleenFeenResponse {

  @JsonFormat(shape = STRING)
  @JsonProperty("type")
  private NotificationType type;

  @JsonProperty("message")
  private String message;

  @JsonFormat(shape = STRING)
  @JsonProperty("status")
  private NotificationStatus status;

  @JsonProperty("id_or_link_or_url")
  private String idOrLinkOrUrl;

  public static NotificationResponse toNotificationResponse(final Notification entry) {
    if (nonNull(entry)) {
      return NotificationResponse.builder()
        .id(entry.getNotificationId())
        .type(entry.getNotificationType())
        .status(entry.getNotificationStatus())
        .idOrLinkOrUrl(nonNull(entry.getIdOrLinkOrUrl()) ? entry.getIdOrLinkOrUrl().toString() : null)
        .build();
    }
    return null;
  }
}
