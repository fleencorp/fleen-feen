package com.fleencorp.feen.exception.stream.speaker;

import com.fleencorp.localizer.model.exception.ApiException;

import java.util.function.Supplier;

public class OrganizerOfStreamCannotBeRemovedAsSpeakerException extends ApiException {

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
