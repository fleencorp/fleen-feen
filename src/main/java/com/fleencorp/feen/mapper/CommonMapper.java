package com.fleencorp.feen.mapper;

import com.fleencorp.feen.constant.security.mfa.IsMfaEnabled;
import com.fleencorp.feen.constant.security.mfa.MfaType;
import com.fleencorp.feen.constant.security.verification.VerificationType;
import com.fleencorp.feen.constant.social.ShareContactRequestStatus;
import com.fleencorp.feen.constant.stream.JoinStatus;
import com.fleencorp.feen.constant.stream.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.info.JoinStatusInfo;
import com.fleencorp.feen.model.info.security.IsMfaEnabledInfo;
import com.fleencorp.feen.model.info.security.MfaTypeInfo;
import com.fleencorp.feen.model.info.security.VerificationTypeInfo;
import com.fleencorp.feen.model.info.share.contact.request.ShareContactRequestStatusInfo;
import com.fleencorp.feen.model.info.stream.attendee.StreamAttendeeRequestToJoinStatusInfo;
import com.fleencorp.feen.model.response.auth.SignInResponse;
import com.fleencorp.feen.model.response.auth.SignUpResponse;
import com.fleencorp.feen.model.response.broadcast.NotAttendingStreamResponse;
import com.fleencorp.feen.model.response.broadcast.ProcessAttendeeRequestToJoinStreamResponse;
import com.fleencorp.feen.model.response.event.ProcessAttendeeRequestToJoinEventResponse;
import com.fleencorp.feen.model.response.stream.base.FleenStreamResponse;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Optional;

import static java.util.Objects.nonNull;

/**
 * A utility class that provides common mapping functionality for the application.
 * This class is responsible for mapping stream-related data and performing
 * translation of message codes to localized messages based on the current locale.
 *
 * <p>The class makes use of {@link FleenStreamMapper} for handling stream-related mappings
 * and {@link MessageSource} for message translations.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Component
public class CommonMapper {

  private final FleenStreamMapper streamMapper;
  private final MessageSource messageSource;;

  /**
   * Constructs a new instance of {@link CommonMapper} with the specified dependencies.
   *
   * <p>This constructor initializes the {@link CommonMapper} class with the provided {@link FleenStreamMapper} and
   * {@link MessageSource} instances, which are used for mapping stream-related data and translating messages, respectively.</p>
   *
   * @param streamMapper The {@link FleenStreamMapper} instance to be used for mapping stream-related data.
   * @param messageSource The {@link MessageSource} instance for message translation based on locale.
   */
  public CommonMapper(
      final FleenStreamMapper streamMapper,
      final MessageSource messageSource) {
    this.messageSource = messageSource;
    this.streamMapper = streamMapper;
  }

  /**
   * Translates the provided message code into a localized message based on the current locale.
   *
   * <p>This method retrieves the current locale from the {@link LocaleContextHolder}, and then uses the
   * {@link MessageSource} to resolve the message corresponding to the provided {@code messageCode}.
   * The method returns the translated message string for the current locale. If the message code cannot be found,
   * the method may return the default message or throw an exception based on the configuration of the {@link MessageSource}.</p>
   *
   * @param messageCode The code representing the message to be translated.
   * @return The localized message corresponding to the {@code messageCode} for the current locale.
   */
  public String translate(final String messageCode) {
    final Locale locale = LocaleContextHolder.getLocale();
    return messageSource.getMessage(messageCode, null, locale);
  }

  /**
   * Converts the provided {@link ShareContactRequestStatus} into a {@link ShareContactRequestStatusInfo} object.
   *
   * <p>This method checks if the provided {@code requestStatus} is non-null. If it is, it returns a {@link ShareContactRequestStatusInfo}
   * object containing the {@code requestStatus}, along with two translated message codes corresponding to the status.
   * If {@code requestStatus} is null, the method returns {@code null}.</p>
   *
   * @param requestStatus The status of a share contact request, represented by the {@link ShareContactRequestStatus}.
   * @return A {@link ShareContactRequestStatusInfo} object with the status and translated message codes, or {@code null} if {@code requestStatus} is null.
   */
  public ShareContactRequestStatusInfo toShareContactRequestStatusInfo(final ShareContactRequestStatus requestStatus) {
    if (nonNull(requestStatus)) {
      return ShareContactRequestStatusInfo.of(requestStatus, translate(requestStatus.getMessageCode()), translate(requestStatus.getMessageCode2()));
    }
    return null;
  }

  /**
   * Retrieves the MFA (Multi-Factor Authentication) type information based on the provided {@code mfaType}.
   *
   * <p>This method checks if the provided {@code mfaType} value is non-null. If it is, it returns an {@link MfaTypeInfo}
   * object containing the MFA type and a translated message code corresponding to that type.
   * If {@code mfaType} is null, the method returns {@code null}.</p>
   *
   * @param mfaType A {@link MfaType} enumeration representing the type of multi-factor authentication.
   * @return An {@link MfaTypeInfo} object if {@code mfaType} is non-null, or {@code null} if the value is null.
   */
  public MfaTypeInfo toMfaTypeInfo(final MfaType mfaType) {
    if (nonNull(mfaType)) {
      return MfaTypeInfo.of(mfaType, translate(mfaType.getMessageCode()));
    }
    return null;
  }

  /**
   * Retrieves the MFA (Multi-Factor Authentication) enabled information based on the provided status.
   *
   * <p>This method checks if the provided {@code mfaEnabled} value is non-null. If it is, it returns an {@link IsMfaEnabledInfo}
   * object containing the MFA status (enabled or not) and a translated message code corresponding to that status.
   * If {@code mfaEnabled} is null, the method returns {@code null}.</p>
   *
   * @param mfaEnabled A {@link Boolean} indicating whether MFA is enabled or not.
   * @return An {@link IsMfaEnabledInfo} object if {@code mfaEnabled} is non-null, or {@code null} if the value is null.
   */
  public IsMfaEnabledInfo toIsMfaEnabledInfo(final Boolean mfaEnabled) {
    if (nonNull(mfaEnabled)) {
      return IsMfaEnabledInfo.of(mfaEnabled, translate(IsMfaEnabled.by(mfaEnabled).getMessageCode()));
    }
    return null;
  }

  /**
   * Sets the MFA (Multi-Factor Authentication) type for the given sign-in response.
   *
   * <p>This method assigns the MFA type to the provided {@link SignInResponse}.
   * It checks if both the sign-in response and the MFA type are non-null,
   * and if so, sets the MFA type information in the sign-in response,
   * using the corresponding message code for the MFA type.</p>
   *
   * @param signInResponse The {@link SignInResponse} to which the MFA type will be added.
   * @param mfaType The {@link MfaType} that indicates the type of MFA enabled.
   */
  public void setMfaType(final SignInResponse signInResponse, final MfaType mfaType) {
    if (nonNull(signInResponse) && nonNull(mfaType)) {
      signInResponse.setMfaTypeInfo(MfaTypeInfo.of(mfaType, translate(mfaType.getMessageCode())));
    }
  }

  /**
   * Sets the MFA (Multi-Factor Authentication) status for the given sign-in response.
   *
   * <p>This method assigns the MFA enabled status to the provided {@link SignInResponse}.
   * It checks if both the sign-in response and the MFA status are non-null,
   * and if so, sets the MFA enabled status information in the sign-in response,
   * using the corresponding message code for the MFA status.</p>
   *
   * @param signInResponse The {@link SignInResponse} to which the MFA status will be added.
   * @param isMfaEnabled A Boolean value indicating whether MFA is enabled.
   */
  public void setMfaEnabled(final SignInResponse signInResponse, final Boolean isMfaEnabled) {
    if (nonNull(signInResponse) && nonNull(isMfaEnabled)) {
      signInResponse.setIsMfaEnabledInfo(IsMfaEnabledInfo.of(isMfaEnabled, translate(IsMfaEnabled.by(isMfaEnabled).getMessageCode())));
    }
  }

  /**
   * Sets the MFA (Multi-Factor Authentication) status and type in the sign-in response.
   *
   * <p>This method updates the {@code signInResponse} with the provided MFA type and
   * enables or disables MFA based on the {@code isMfaEnabled} flag.</p>
   *
   * @param signInResponse the response object that will be updated with the MFA status and type
   * @param isMfaEnabled a flag indicating whether MFA is enabled
   * @param mfaType the type of MFA to be set (e.g., SMS, email, etc.)
   */
  public void setMfaEnabledAndMfaType(final SignInResponse signInResponse, final Boolean isMfaEnabled, final MfaType mfaType) {
    setMfaType(signInResponse, mfaType);
    setMfaEnabled(signInResponse, isMfaEnabled);
  }

  /**
   * Sets the verification type for the given sign-up response.
   *
   * <p>This method assigns a {@link VerificationType} to the provided {@link SignUpResponse}.
   * It checks if both the sign-up response and verification type are non-null,
   * and if so, sets the verification type information in the sign-up response,
   * translating the message code associated with the verification type.</p>
   *
   * @param signUpResponse The {@link SignUpResponse} to which the verification type will be added.
   * @param verificationType The {@link VerificationType} to be set for the sign-up response.
   */
  public void setVerificationType(final SignUpResponse signUpResponse, final VerificationType verificationType) {
    if (nonNull(signUpResponse) && nonNull(verificationType)) {
      signUpResponse.setVerificationTypeInfo(VerificationTypeInfo.of(verificationType, translate(verificationType.getMessageCode())));
    }
  }

  /**
   * Processes an attendee's request to join an event stream.
   *
   * <p>This method handles the logic for processing the request of an attendee
   * who is seeking to join a stream. It checks if the attendee already exists
   * and retrieves their current request status. A response is generated
   * containing the stream information and the current request status.</p>
   *
   * If the attendee does not exist, the method returns null.
   *
   * @param stream The {@link FleenStreamResponse} containing the stream details.
   * @param existingAttendee An {@link Optional} containing the existing {@link StreamAttendee},
   *                         or empty if no attendee exists for this request.
   *
   * @return A {@link ProcessAttendeeRequestToJoinEventResponse} populated with stream details
   *         and the request to join status if the attendee exists, or null if no attendee is found.
   */
  public ProcessAttendeeRequestToJoinStreamResponse processAttendeeRequestToJoinStream(final FleenStreamResponse stream, final Optional<StreamAttendee> existingAttendee) {
    return existingAttendee
      .map(attendee -> {
        final StreamAttendeeRequestToJoinStatus requestToJoinStatus = attendee.getRequestToJoinStatus();
        final ProcessAttendeeRequestToJoinStreamResponse response = ProcessAttendeeRequestToJoinStreamResponse.of(stream.getNumberId(), stream);
        response.setRequestToJoinStatusInfo(StreamAttendeeRequestToJoinStatusInfo.of(requestToJoinStatus, translate(requestToJoinStatus.getMessageCode())));
        return response;
      })
      .orElse(null);
  }


  /**
   * Processes an attendee's request to join an event stream.
   *
   * <p>This method handles the logic for processing the request of an attendee
   * who is seeking to join a stream. It checks if the attendee already exists
   * and retrieves their current request status. A response is generated
   * containing the stream information and the current request status.</p>
   *
   * If the attendee does not exist, the method returns null.
   *
   * @param stream The {@link FleenStreamResponse} containing the stream details.
   * @param existingAttendee An {@link Optional} containing the existing {@link StreamAttendee},
   *                         or empty if no attendee exists for this request.
   *
   * @return A {@link ProcessAttendeeRequestToJoinEventResponse} populated with stream details
   *         and the request to join status if the attendee exists, or null if no attendee is found.
   */
  public ProcessAttendeeRequestToJoinEventResponse processAttendeeRequestToJoinEvent(final FleenStreamResponse stream, final Optional<StreamAttendee> existingAttendee) {
    return existingAttendee
      .map(attendee -> {
        // Get the request-to-join status of the attendee
        final StreamAttendeeRequestToJoinStatus requestToJoinStatus = attendee.getRequestToJoinStatus();
        // Create a response object with the stream ID and stream details
        final ProcessAttendeeRequestToJoinEventResponse response = ProcessAttendeeRequestToJoinEventResponse.of(stream.getNumberId(), stream);
        // Set the request-to-join status information with localized messages
        response.setRequestToJoinStatusInfo(StreamAttendeeRequestToJoinStatusInfo.of(requestToJoinStatus, translate(requestToJoinStatus.getMessageCode())));
        // Return the response
        return response;
      })
      // Return null if no existing attendee is found
      .orElse(null);
  }


  /**
   * Generates a response for a stream where the user is not attending.
   *
   * <p>This method creates a {@link NotAttendingStreamResponse} object, sets its
   * attendance status to "not attending", and populates the join status
   * information using the provided {@link JoinStatus}.</p>
   *
   * @return A {@link NotAttendingStreamResponse} indicating that the user
   *         is not attending the stream.
   */
  public NotAttendingStreamResponse notAttendingStream() {
    // Create a new NotAttendingStreamResponse instance
    final NotAttendingStreamResponse notAttendingStreamResponse = NotAttendingStreamResponse.of();

    // Set the join status to 'not attending'
    final JoinStatus joinStatus = JoinStatus.notAttending();
    // Set the 'is attending' info to false, using the translated message
    notAttendingStreamResponse.setIsAttendingInfo(streamMapper.toIsAttendingInfo(false));

    // Set the join status info with translated messages for the join status
    notAttendingStreamResponse.setJoinStatusInfo(JoinStatusInfo.of(joinStatus, translate(joinStatus.getMessageCode()), translate(joinStatus.getMessageCode2())));
    // Return the fully populated NotAttendingStreamResponse
    return notAttendingStreamResponse;
  }

}
