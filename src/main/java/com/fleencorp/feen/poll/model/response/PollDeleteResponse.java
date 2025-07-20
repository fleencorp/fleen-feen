package com.fleencorp.feen.poll.model.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.common.model.info.IsDeletedInfo;
import com.fleencorp.localizer.model.response.LocalizedResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "poll_id",
  "is_deleted_info"
})
public class PollDeleteResponse extends LocalizedResponse {

  @JsonProperty("poll_id")
  private Long pollId;

  @JsonProperty("is_deleted_info")
  private IsDeletedInfo deletedInfo;

  @Override
  @JsonIgnore
  public String getMessageCode() {
    return "poll.delete";
  }

  public static PollDeleteResponse of(final Long pollId, final IsDeletedInfo deletedInfo) {
    return new PollDeleteResponse(pollId, deletedInfo);
  }
}