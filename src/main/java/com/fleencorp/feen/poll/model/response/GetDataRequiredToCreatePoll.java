package com.fleencorp.feen.poll.model.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.poll.constant.core.PollVisibility;
import com.fleencorp.feen.poll.model.form.PollFormField;
import com.fleencorp.feen.poll.model.form.field.PollFormFieldGuide;
import com.fleencorp.feen.poll.model.info.PollVisibilityInfo;
import com.fleencorp.localizer.model.response.LocalizedResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
  "message",
  "visibility_types",
  "form_fields_guide",
})
public class GetDataRequiredToCreatePoll extends LocalizedResponse {

  @JsonProperty("visibility_types")
  private Map<PollVisibility, PollVisibilityInfo> visibilityTypes;

  @JsonProperty("form_fields_guide")
  private Map<PollFormField, PollFormFieldGuide> formFieldsGuide;

  @Override
  @JsonIgnore
  public String getMessageCode() {
    return "get.data.required.to.create.poll";
  }

  public static GetDataRequiredToCreatePoll of(final Map<PollVisibility, PollVisibilityInfo> visibilityTypes, final Map<PollFormField, PollFormFieldGuide> formFieldsGuide) {
    return new GetDataRequiredToCreatePoll(visibilityTypes, formFieldsGuide);
  }
}
