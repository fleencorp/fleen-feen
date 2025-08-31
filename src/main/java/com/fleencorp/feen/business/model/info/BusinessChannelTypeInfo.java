package com.fleencorp.feen.business.model.info;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.business.constant.BusinessChannelType;
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
  "channel_type",
  "channel_type_text",
  "channel_type_text_2"
})
public class BusinessChannelTypeInfo {

  @JsonFormat(shape = STRING)
  @JsonProperty("channel_type")
  private BusinessChannelType channelType;

  @JsonProperty("channel_type_text")
  private String channelTypeText;

  @JsonProperty("channel_type_text_2")
  private String channelTypeText2;

  public static BusinessChannelTypeInfo of(final BusinessChannelType channelType, final String channelTypeText, final String channelTypeText2) {
    return new BusinessChannelTypeInfo(channelType, channelTypeText, channelTypeText2);
  }
}
