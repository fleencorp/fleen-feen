package com.fleencorp.feen.mfa.model.info;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.mfa.constant.MfaType;
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
  "mfa_type",
  "mfa_type_text"
})
public class MfaTypeInfo {

  @JsonFormat(shape = STRING)
  @JsonProperty("mfa_type")
  private MfaType mfaType;

  @JsonProperty("mfa_type_text")
  private String mfaTypeText;

  public static MfaTypeInfo of(final MfaType mfaType, final String mfaTypeText) {
    return new MfaTypeInfo(mfaType, mfaTypeText);
  }
}
