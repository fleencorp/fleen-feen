package com.fleencorp.feen.contact.model.response.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.common.model.response.core.FleenFeenResponse;
import com.fleencorp.feen.contact.model.info.ContactTypeInfo;
import com.fleencorp.feen.model.contract.Updatable;
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
  "id",
  "contact",
  "contact_type_info",
  "is_updatable",
  "created_on",
  "updated_on"
})
public class ContactResponse extends FleenFeenResponse
  implements Updatable {

  @JsonProperty("contact")
  private String contact;

  @JsonProperty("contact_type_info")
  private ContactTypeInfo contactTypeInfo;

  @JsonProperty("is_updatable")
  private Boolean isUpdatable;

  @JsonIgnore
  private Long authorId;

  @JsonIgnore
  private Long organizerId;

  @Override
  public void setIsUpdatable(final boolean isUpdatable) {
    this.isUpdatable = isUpdatable;
  }

  @Override
  public void markAsUpdatable() {
    setIsUpdatable(true);
  }
}
