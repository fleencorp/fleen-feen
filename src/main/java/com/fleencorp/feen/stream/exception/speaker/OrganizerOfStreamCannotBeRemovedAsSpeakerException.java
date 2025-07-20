package com.fleencorp.feen.stream.exception.speaker;

import com.fleencorp.localizer.model.exception.LocalizedException;

import java.util.function.Supplier;

public class OrganizerOfStreamCannotBeRemovedAsSpeakerException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "organizer.of.stream.cannot.be.removed.as.speaker";
  }

  public OrganizerOfStreamCannotBeRemovedAsSpeakerException(final Object...params) {
    super(params);
  }

  public static Supplier<OrganizerOfStreamCannotBeRemovedAsSpeakerException> of(final Object attendeeId) {
    return () -> new OrganizerOfStreamCannotBeRemovedAsSpeakerException(attendeeId);
  }
}
