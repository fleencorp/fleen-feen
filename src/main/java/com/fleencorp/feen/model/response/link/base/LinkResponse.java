package com.fleencorp.feen.model.response.link.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.contract.SetIsUpdatable;
import com.fleencorp.feen.model.info.link.LinkTypeInfo;
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
  "url",
  "link_type",
  "is_updatable"
})
public class LinkResponse implements SetIsUpdatable {

  @JsonProperty("url")
  private String url;

  @JsonProperty("link_type")
  private LinkTypeInfo linkType;

  @JsonProperty("is_updatable")
  private Boolean isUpdatable;

  @Override
  public void setIsUpdatable(final boolean isUpdatable) {
    this.isUpdatable = isUpdatable;
  }

  @Override
  public void markAsUpdatable() {
    setIsUpdatable(true);
  }
}
