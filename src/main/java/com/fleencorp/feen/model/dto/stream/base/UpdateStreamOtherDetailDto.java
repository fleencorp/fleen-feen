package com.fleencorp.feen.model.dto.stream.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.validator.OneOf;
import com.fleencorp.feen.constant.stream.StreamType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStreamOtherDetailDto {

  @Size(min = 10, max = 1000, message = "{stream.otherDetails.Size}")
  @JsonProperty("other_details")
  private String otherDetails;

  @URL(message = "{stream.otherLink.URL}")
  @JsonProperty("other_link")
  private String otherLink;

  @Size(min = 10, max = 500, message = "{stream.groupOrOrganizationName.Size}")
  @JsonProperty("group_or_organization_name")
  protected String groupOrOrganizationName;

  @NotNull(message = "{stream.streamType.NotNull}")
  @OneOf(enumClass = StreamType.class, message = "{stream.streamType.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("stream_type")
  protected String streamType;

  public StreamType getStreamType() {
    return StreamType.of(streamType);
  }
}
