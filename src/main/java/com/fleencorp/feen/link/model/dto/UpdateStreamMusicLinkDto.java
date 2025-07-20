package com.fleencorp.feen.link.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.validator.OneOf;
import com.fleencorp.feen.constant.common.MusicLinkType;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.link.exception.core.InvalidLinkException;
import com.fleencorp.feen.link.exception.core.UnsupportedMusicLinkFormatException;
import com.fleencorp.feen.link.model.dto.base.BaseLinkDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import static java.util.Objects.isNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStreamMusicLinkDto extends BaseLinkDto {

  @NotBlank(message = "{link.musicLink.NotBlank}")
  @URL(message = "{link.musicLink.URL}")
  @Size(max = 1000, message = "{link.musicLink.Size}")
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

  /**
   * <p>Validates that the music link is correctly formed and corresponds to an expected link type.</p>
   *
   * <p>The method first checks that the URL and link type are not null or blank.
   * It then attempts to match the URL against known format prefixes associated with {@code MusicLinkType}.
   * If a matching format is found, the link type is checked for consistency with the detected type.</p>
   *
   * <p>If any of these checks fail, the method throws an appropriate exception.</p>
   *
   * @throws FailedOperationException if the URL or link type is null or blank
   * @throws UnsupportedMusicLinkFormatException if the URL does not match any known format
   * @throws InvalidLinkException if the link type does not match the format detected from the URL
   */
  public void checkLinkIsValid() {
    if (isNull(url) || isNull(linkType) || linkType.isBlank()) {
      throw FailedOperationException.of();
    }

    final MusicLinkType expectedType = MusicLinkType.fromUrl(url);
    if (expectedType == null) {
      throw UnsupportedMusicLinkFormatException.of();
    }

    if (!expectedType.name().equalsIgnoreCase(linkType)) {
      throw InvalidLinkException.of();
    }
  }

}
