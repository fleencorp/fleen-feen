package com.fleencorp.feen.notification.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.notification.constant.NotificationStatus;
import com.fleencorp.feen.notification.constant.NotificationType;
import com.fleencorp.feen.notification.model.domain.Notification;
import com.fleencorp.feen.common.model.response.core.FleenFeenResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static java.util.Objects.nonNull;

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
      final NotificationResponse response = new NotificationResponse();
      response.setId(entry.getNotificationId());
      response.setType(entry.getNotificationType());
      response.setStatus(entry.getNotificationStatus());
      response.setIdOrLinkOrUrl(entry.getIdOrLinkOrUrl());

      return response;
    }
    return null;
  }
}
