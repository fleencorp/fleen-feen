package com.fleencorp.feen.business.model.info;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.business.constant.BusinessStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "status",
  "status_text",
  "status_text_2"
})
public class BusinessStatusInfo {

  @JsonFormat(shape = STRING)
  @JsonProperty("status")
  private BusinessStatus businessStatus;

  @JsonProperty("status_text")
  private String statusText;

  @JsonProperty("status_text_2")
  private String statusText2;

  public static BusinessStatusInfo of(final BusinessStatus status, final String statusText, final String statusText2) {
    return new BusinessStatusInfo(status, statusText, statusText2);
  }
}
