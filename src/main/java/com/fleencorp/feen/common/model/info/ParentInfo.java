package com.fleencorp.feen.common.model.info;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
  "parent_id",
  "parent_title",
  "parent_content"
})
public class ParentInfo {

  @JsonProperty("parent_id")
  private Long parentId;

  @JsonProperty("parent_title")
  private String parentTitle;

  @JsonProperty("parent_content")
  private String parentContent;

  public static ParentInfo of(final Long parentId, final String parentTitle) {
    return new ParentInfo(parentId, parentTitle, null);
  }

  public static ParentInfo of(final Long parentId, final String parentTitle, final String parentContent) {
    return new ParentInfo(parentId, parentTitle, parentContent);
  }

  public static ParentInfo of(final Long parentId) {
    return new ParentInfo(parentId, null, null);
  }
}
