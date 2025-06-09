package com.fleencorp.feen.user.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfilePhotoDto {

  @URL(message = "{user.profilePhoto.URL}")
  @Size(max = 500, message = "{user.profilePhoto.Size}")
  @JsonProperty("profile_photo")
  private String profilePhoto;
}
