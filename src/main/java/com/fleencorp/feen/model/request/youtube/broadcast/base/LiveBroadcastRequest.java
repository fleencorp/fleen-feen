package com.fleencorp.feen.model.request.youtube.broadcast.base;

import com.fleencorp.feen.constant.external.google.youtube.base.YouTubeLiveBroadcastVisibility;
import com.fleencorp.feen.constant.stream.StreamVisibility;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LiveBroadcastRequest {

  protected String accessTokenForHttpRequest;

  /**
   * Converts a visibility value from application-specific StreamVisibility to YouTubeLiveBroadcastVisibility format.
   *
   * <p>This method is useful for mapping visibility settings between different systems, ensuring consistent handling
   * of privacy settings across platforms.</p>
   *
   * @param visibility the visibility value to convert
   * @return the corresponding YouTube visibility value
   */
  public static String getVisibility(final String visibility) {
    if (StreamVisibility.PRIVATE.getValue().equalsIgnoreCase(visibility)) {
      return YouTubeLiveBroadcastVisibility.PRIVATE.getValue();
    } else if (StreamVisibility.PROTECTED.getValue().equalsIgnoreCase(visibility)) {
      return YouTubeLiveBroadcastVisibility.UNLISTED.getValue();
    } else if (StreamVisibility.PUBLIC.getValue().equalsIgnoreCase(visibility)) {
      return visibility;
    }
    return StreamVisibility.PUBLIC.getValue();
  }
}
