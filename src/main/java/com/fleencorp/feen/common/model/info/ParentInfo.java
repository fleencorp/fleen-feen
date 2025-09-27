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
  "other_parent_id",
  "parent_summary",
})
public class ParentInfo {

  @JsonProperty("parent_id")
  private Long parentId;

  @JsonProperty("other_parent_id")
  private Long otherParentId;

  @JsonProperty("parent_summary")
  private String parentSummary;

  public static ParentInfo of(final Long parentId, final Long otherParentId, final String parentSummary) {
    final ParentInfo parentInfo = of(parentId, parentSummary);
    parentInfo.setOtherParentId(otherParentId);

    return parentInfo;
  }

  public static ParentInfo of(final Long parentId, final String parentSummary) {
    return new ParentInfo(parentId, null, parentSummary);
  }
}
