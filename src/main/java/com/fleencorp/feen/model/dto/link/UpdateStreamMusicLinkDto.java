package com.fleencorp.feen.model.dto.link;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.validator.OneOf;
import com.fleencorp.feen.constant.common.MusicLinkType;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.model.dto.link.base.BaseLinkDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

  @NotBlank(message = "{link.musicLink.NotNull}")
  @URL(message = "{link.musicLink.URL}")
  @JsonProperty("url")
  private String url;

  @NotNull(message = "{link.linkType.NotNull}")
  @OneOf(enumClass = MusicLinkType.class, message = "{link.linkType.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("link_type")
  private String linkType;

  public String getMusicLink() {
    return url;
  }

  public void checkLinkIsValid() {
    if (nonNull(url) && MusicLinkType.isValid(url)) {
      return;
    }
    throw FailedOperationException.of();
  }
}
