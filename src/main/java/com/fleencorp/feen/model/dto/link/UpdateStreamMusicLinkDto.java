package com.fleencorp.feen.model.dto.link;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.feen.constant.common.MusicLinkType;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.model.dto.link.base.BaseLinkDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStreamMusicLinkDto extends BaseLinkDto {

  @URL(message = "{user.profilePhoto.URL}")
  @JsonProperty("music_link")
  private String musicLink;

  public void checkLinkIsValid() {
    if (nonNull(musicLink) && MusicLinkType.isValid(musicLink)) {
      return;
    }
    throw FailedOperationException.of();
  }
}
