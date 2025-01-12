package com.fleencorp.feen.model.dto.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.validator.ValidEmail;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.dto.stream.base.CreateStreamDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

import static com.fleencorp.feen.constant.stream.StreamSource.GOOGLE_MEET;
import static com.fleencorp.feen.constant.stream.StreamType.EVENT;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCalendarEventDto extends CreateStreamDto {

  @Valid
  @NotNull(message = "{event.eventAttendeesOrGuests.NotNull}")
  @Size(max = 5, message = "{event.eventAttendeesOrGuests.Size}")
  @JsonProperty("event_attendees_or_guests")
  private List<EventAttendeeOrGuest> eventAttendeesOrGuests;

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class EventAttendeeOrGuest {

    @NotBlank(message = "{event.eventAttendeesOrGuests.emailAddress.NotBlank}")
    @Size(min = 6, max = 50, message = "{event.eventAttendeesOrGuests.emailAddress.Size}")
    @ValidEmail
    @JsonProperty("email_address")
    private String emailAddress;

    @Size(min = 3, max = 100, message = "{event.eventAttendeesOrGuests.aliasOrDisplayName.Size}")
    @JsonProperty("alias_or_display_name")
    private String aliasOrDisplayName;

    @JsonIgnore
    private Boolean isOrganizer;

    public static EventAttendeeOrGuest of(final String emailAddress, final String aliasOrDisplayName) {
      return of(emailAddress, aliasOrDisplayName, true);
    }

    public static EventAttendeeOrGuest of(final String emailAddress, final String aliasOrDisplayName, final Boolean isOrganizer) {
      return new EventAttendeeOrGuest(emailAddress, aliasOrDisplayName, isOrganizer);
    }
  }

  public FleenStream toFleenStream(final Member member) {
    final FleenStream fleenStream = toFleenStream();
    fleenStream.setMember(member);
    fleenStream.setStreamType(EVENT);
    fleenStream.setStreamSource(GOOGLE_MEET);
    return fleenStream;
  }

}
