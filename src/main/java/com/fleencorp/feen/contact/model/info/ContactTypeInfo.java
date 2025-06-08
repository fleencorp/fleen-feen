package com.fleencorp.feen.contact.model.info;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.contact.constant.ContactType;
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
  "type",
  "label",
  "format"
})
public class ContactTypeInfo {

  @JsonProperty("type")
  private ContactType type;

  @JsonProperty("label")
  private String label;

  @JsonProperty("format")
  private String format;

  public static ContactTypeInfo of(final ContactType type, final String label, final String format) {
    return new ContactTypeInfo(type, label, format);
  }
}
