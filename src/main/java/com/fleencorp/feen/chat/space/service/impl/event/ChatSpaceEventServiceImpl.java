package com.fleencorp.feen.chat.space.service.impl.event;

import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.base.model.view.search.SearchResult;
import com.fleencorp.feen.calendar.exception.core.CalendarNotFoundException;
import com.fleencorp.feen.calendar.model.domain.Calendar;
import com.fleencorp.feen.calendar.model.request.event.create.CreateCalendarEventRequest;
import com.fleencorp.feen.chat.space.exception.core.ChatSpaceNotFoundException;
import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.chat.space.model.search.event.ChatSpaceEventSearchResult;
import com.fleencorp.feen.chat.space.service.core.ChatSpaceOperationsService;
import com.fleencorp.feen.chat.space.service.event.ChatSpaceEventService;
import com.fleencorp.feen.chat.space.service.member.ChatSpaceMemberService;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.common.service.misc.MiscService;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.stream.constant.core.StreamType;
import com.fleencorp.feen.stream.mapper.StreamUnifiedMapper;
import com.fleencorp.feen.stream.mapper.stream.StreamMapper;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.stream.model.dto.event.CreateChatSpaceEventDto;
import com.fleencorp.feen.stream.model.info.core.StreamTypeInfo;
import com.fleencorp.feen.stream.model.response.StreamResponse;
import com.fleencorp.feen.stream.model.response.base.CreateStreamResponse;
import com.fleencorp.feen.stream.service.common.StreamOperationsService;
import com.fleencorp.feen.stream.service.event.EventOperationsService;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.fleencorp.base.util.FleenUtil.toSearchResult;

@Service
public class ChatSpaceEventServiceImpl implements ChatSpaceEventService {

  private final String delegatedAuthorityEmail;
  private final ChatSpaceMemberService chatSpaceMemberService;
  private final ChatSpaceOperationsService chatSpaceOperationsService;
  private final EventOperationsService eventOperationsService;
  private final MiscService miscService;
  private final StreamOperationsService streamOperationsService;
  private final StreamMapper streamMapper;
  private final StreamUnifiedMapper streamUnifiedMapper;
  private final Localizer localizer;

  public ChatSpaceEventServiceImpl(
      @Value("${google.delegated.authority.email}") final String delegatedAuthorityEmail,
      final ChatSpaceMemberService chatSpaceMemberService,
      final ChatSpaceOperationsService chatSpaceOperationsService,
      final EventOperationsService eventOperationsService,
      final MiscService miscService,
      final StreamOperationsService streamOperationsService,
      final StreamMapper streamMapper,
      final StreamUnifiedMapper streamUnifiedMapper,
      final Localizer localizer) {
    this.delegatedAuthorityEmail = delegatedAuthorityEmail;
    this.chatSpaceMemberService = chatSpaceMemberService;
    this.chatSpaceOperationsService = chatSpaceOperationsService;
    this.miscService = miscService;
    this.streamOperationsService = streamOperationsService;
    this.eventOperationsService = eventOperationsService;
    this.streamMapper = streamMapper;
    this.streamUnifiedMapper = streamUnifiedMapper;
    this.localizer = localizer;
  }

  /**
   * Retrieves a paginated list of events (streams) within a specific chat space.
   *
   * <p>This method searches for streams associated with the provided {@code chatSpaceId}
   * and converts them into {@link StreamResponse} views.</p>
   *
   * @param chatSpaceId the ID of the chat space to find events for.
   * @param searchRequest the search request containing pagination details.
   * @param user the current user performing the search.
   * @return a {@link ChatSpaceEventSearchResult} containing the list of event responses
   *         and pagination metadata.
   */
  @Override
  public ChatSpaceEventSearchResult findChatSpaceEvents(final Long chatSpaceId, final SearchRequest searchRequest, final RegisteredUser user) {
    final Page<FleenStream> page = streamOperationsService.findByChatSpaceId(chatSpaceId, searchRequest.getPage());
    final List<StreamResponse> streamResponses = streamUnifiedMapper.toStreamResponses(page.getContent());

    streamOperationsService.processOtherStreamDetails(streamResponses, user.toMember());

    final StreamTypeInfo streamTypeInfo = streamUnifiedMapper.toStreamTypeInfo(StreamType.EVENT);
    final SearchResult searchResult = toSearchResult(streamResponses, page);
    final ChatSpaceEventSearchResult chatSpaceEventSearchResult = ChatSpaceEventSearchResult.of(searchResult, streamTypeInfo);
    return localizer.of(chatSpaceEventSearchResult);
  }

  /**
   * Creates an event within a chat space and announces it.
   *
   * <p>This method finds the associated chat space and calendar, creates a Google Calendar event,
   * updates the FleenStream entity with event details, and announces the event in the chat space.</p>
   *
   * @param chatSpaceId The ID of the chat space where the event will be created.
   * @param createChatSpaceEventDto DTO containing the event details.
   * @param user The user creating the event.
   * @return A response with details of the created event.
   * @throws ChatSpaceNotFoundException if the chat space is not found
   * @throws CalendarNotFoundException if the calendar cannot be found
   * @throws FailedOperationException if there is an invalid input
   */
  @Override
  @Transactional
  public CreateStreamResponse createChatSpaceEvent(final Long chatSpaceId, final CreateChatSpaceEventDto createChatSpaceEventDto, final RegisteredUser user)
      throws ChatSpaceNotFoundException, CalendarNotFoundException, FailedOperationException {
    final ChatSpace chatSpace = chatSpaceOperationsService.findChatSpace(chatSpaceId);

    chatSpaceMemberService.findByChatSpaceAndMember(chatSpace, user.toMember());
    final Calendar calendar = miscService.findCalendar(user.getCountry());
    final CreateCalendarEventRequest createCalendarEventRequest = CreateCalendarEventRequest.bySuper(createChatSpaceEventDto);
    createCalendarEventRequest.update(calendar.getExternalId(), delegatedAuthorityEmail, user.getEmailAddress(), chatSpace.getMetadata());

    FleenStream stream = createChatSpaceEventDto.toStream(user.toMember(), chatSpace);
    final String organizerAliasOrDisplayName = createChatSpaceEventDto.getOrganizerAlias(user.getFullName());

    stream.update(organizerAliasOrDisplayName, user.getEmailAddress(), user.getPhoneNumber());
    stream = streamOperationsService.save(stream);

    streamOperationsService.increaseTotalAttendeesOrGuests(stream);
    streamOperationsService.registerAndApproveOrganizerOfStreamAsAnAttendee(stream, user);
    createEventExternally(stream, createCalendarEventRequest);

    final StreamResponse streamResponse = streamUnifiedMapper.toStreamResponseByAdminUpdate(stream);
    final StreamTypeInfo streamTypeInfo = streamUnifiedMapper.toStreamTypeInfo(stream.getStreamType());
    final CreateStreamResponse createStreamResponse = CreateStreamResponse.of(stream.getStreamId(), streamTypeInfo, streamResponse);
    return localizer.of(createStreamResponse);
  }

  /**
   * Creates a calendar event for the given stream using an external service.
   *
   * <p>This method delegates the operation to the {@code eventOperationsService}, which handles
   * the creation of the event in Google Calendar and announces it in the chat space.
   *
   * @param stream the stream for which the calendar event is being created
   * @param createCalendarEventRequest the request payload containing event details
   */
  private void createEventExternally(final FleenStream stream, final CreateCalendarEventRequest createCalendarEventRequest) {
    eventOperationsService.createEventInGoogleCalendarAndAnnounceInSpace(stream, createCalendarEventRequest);
  }

}
