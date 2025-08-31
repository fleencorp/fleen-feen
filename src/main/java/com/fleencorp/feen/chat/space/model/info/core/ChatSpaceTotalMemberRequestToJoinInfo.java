package com.fleencorp.feen.chat.space.model.info.core;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
  "total",
  "total_member_text"
})
public class ChatSpaceTotalMemberRequestToJoinInfo {

  @JsonFormat(shape = STRING)
  @JsonProperty("total")
  private Integer totalMember;

  @JsonProperty("total_member_text")
  private String totalMemberText;

  public static ChatSpaceTotalMemberRequestToJoinInfo of(final Integer totalMember, final String totalMemberText) {
    return new ChatSpaceTotalMemberRequestToJoinInfo(totalMember, totalMemberText);
  }
}
