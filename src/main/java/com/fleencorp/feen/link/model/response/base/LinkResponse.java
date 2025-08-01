package com.fleencorp.feen.link.model.response.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.link.model.info.LinkTypeInfo;
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
  "url",
  "link_type",
  "is_updatable"
})
public class LinkResponse implements Updatable {

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
