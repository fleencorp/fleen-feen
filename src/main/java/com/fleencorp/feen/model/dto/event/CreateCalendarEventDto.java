package com.fleencorp.feen.model.dto.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.validator.ValidEmail;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.dto.stream.CreateStreamDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

import static com.fleencorp.feen.constant.stream.StreamCreationType.SCHEDULED;
import static com.fleencorp.feen.constant.stream.StreamStatus.ACTIVE;
import static com.fleencorp.feen.converter.impl.common.ToTitleCaseConverter.toTitleCase;
import static java.lang.Boolean.parseBoolean;
import static java.util.Objects.nonNull;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCalendarEventDto extends CreateStreamDto {

  @Size(min = 3, max = 100, message = "{event.organizerAliasOrDisplayName.Size}")
  @JsonProperty("organizer_alias_or_display_name")
  private String organizerAliasOrDisplayName;

  @Valid
  @Size(min = 1, max = 5, message = "{event.eventAttendeesOrGuests.Size}")
  @JsonProperty("event_attendees_or_guests")
  private List<EventAttendeeOrGuest> eventAttendeesOrGuests;

  @JsonIgnore
  public String getOrganizerAlias(final String defaultOrganizerAlias) {
    if (nonNull(organizerAliasOrDisplayName) && !organizerAliasOrDisplayName.trim().isBlank()) {
      return organizerAliasOrDisplayName;
    }
    else {
      return defaultOrganizerAlias;
    }
  }

  @Builder
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

    public EventAttendeeOrGuest of(final String emailAddress, final String aliasOrDisplayName) {
      return of(emailAddress, aliasOrDisplayName, true);
    }

    public EventAttendeeOrGuest of(final String emailAddress, final String aliasOrDisplayName, final Boolean isOrganizer) {
      return EventAttendeeOrGuest.builder()
        .emailAddress(emailAddress)
        .aliasOrDisplayName(aliasOrDisplayName)
        .isOrganizer(isOrganizer)
        .build();
    }
  }

  public FleenStream toFleenStream(final Member member) {
    final FleenStream fleenStream = toFleenStream();
    fleenStream.setMember(member);
    return fleenStream;
  }

  public FleenStream toFleenStream() {
    return FleenStream.builder()
            .title(title)
            .description(description)
            .tags(tags)
            .location(location)
            .timezone(toTitleCase(timezone))
            .scheduledStartDate(getActualStartDateTime())
            .scheduledEndDate(getActualEndDateTime())
            .streamType(getActualType())
            .streamVisibility(getActualVisibility())
            .streamCreationType(SCHEDULED)
            .streamStatus(ACTIVE)
            .forKids(parseBoolean(isForKids))
            .isDeleted(false)
            .build();
  }

}
