package com.fleencorp.feen.user.model.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fleencorp.localizer.model.response.LocalizedResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RemoveProfilePhotoResponse extends LocalizedResponse {

  @Override
  @JsonIgnore
  public String getMessageCode() {
    return "remove.profile.photo";
  }

  public static RemoveProfilePhotoResponse of() {
    return new RemoveProfilePhotoResponse();
  }
}
