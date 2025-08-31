package com.fleencorp.feen.calendar.mapper;

import com.fleencorp.feen.calendar.constant.CalendarStatus;
import com.fleencorp.feen.calendar.model.domain.Calendar;
import com.fleencorp.feen.calendar.model.info.CalendarStatusInfo;
import com.fleencorp.feen.calendar.model.response.base.CalendarResponse;
import com.fleencorp.feen.mapper.impl.BaseMapper;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.*;

import static java.util.Objects.nonNull;

@Component
public class CalendarMapperImpl extends BaseMapper implements CalendarMapper {

  public CalendarMapperImpl(final MessageSource messageSource) {
    super(messageSource);
  }

  /**
   * Converts a {@link Calendar} entity into a {@link CalendarResponse} DTO.
   *
   * <p>This method maps the fields of the given calendar entry into a response object
   * that can be returned to clients. If the input is {@code null}, the method
   * returns {@code null}.</p>
   *
   * @param entry the {@link Calendar} entity to be converted
   * @return a {@link CalendarResponse} populated with the entity's data, or {@code null} if {@code entry} is null
   */
  @Override
  public CalendarResponse toCalendarResponse(final Calendar entry) {
    if (nonNull(entry)) {
      final CalendarResponse response = new CalendarResponse();
      response.setId(entry.getCalendarId());
      response.setTitle(entry.getTitle());
      response.setDescription(entry.getDescription());
      response.setTimezone(entry.getTimezone());
      response.setCode(entry.getCode());
      
      final CalendarStatus status = entry.getStatus();
      final CalendarStatusInfo calendarStatusInfo = toCalendarStatusInfo(status);
      response.setStatusInfo(calendarStatusInfo);

      response.setCreatedOn(entry.getCreatedOn());
      response.setUpdatedOn(entry.getUpdatedOn());

      return response;
    }

    return null;
  }

  /**
   * Converts a list of {@link Calendar} entities into a collection of {@link CalendarResponse} DTOs.
   *
   * <p>This method safely handles {@code null} input by returning an empty collection.
   * Non-null entries are mapped using {@link #toCalendarResponse(Calendar)}.</p>
   *
   * @param entries the list of {@link Calendar} entities to be converted, may be {@code null}
   * @return a collection of {@link CalendarResponse} objects, never {@code null}
   */
  @Override
  public Collection<CalendarResponse> toCalendarResponses(final List<Calendar> entries) {
    return Optional.ofNullable(entries)
      .orElseGet(Collections::emptyList)
      .stream()
      .filter(Objects::nonNull)
      .map(this::toCalendarResponse)
      .toList();
  }

  private CalendarStatusInfo toCalendarStatusInfo(final CalendarStatus status) {
    return CalendarStatusInfo.of(status, translate(status.getMessageCode()), translate(status.getMessageCode2()));
  }

}
