package com.fleencorp.feen.mapper;

import com.fleencorp.feen.constant.social.ShareContactRequestStatus;
import com.fleencorp.feen.mfa.constant.MfaType;
import com.fleencorp.feen.mfa.model.info.IsMfaEnabledInfo;
import com.fleencorp.feen.mfa.model.info.MfaTypeInfo;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.info.share.contact.request.ShareContactRequestStatusInfo;
import com.fleencorp.feen.model.response.authentication.SignInResponse;
import com.fleencorp.feen.model.response.authentication.SignUpResponse;
import com.fleencorp.feen.model.response.stream.StreamResponse;
import com.fleencorp.feen.model.response.stream.attendance.NotAttendingStreamResponse;
import com.fleencorp.feen.model.response.stream.attendance.ProcessAttendeeRequestToJoinStreamResponse;
import com.fleencorp.feen.verification.constant.VerificationType;

public interface CommonMapper {

  ShareContactRequestStatusInfo toShareContactRequestStatusInfo(ShareContactRequestStatus requestStatus);

  MfaTypeInfo toMfaTypeInfo(MfaType mfaType);

  IsMfaEnabledInfo toIsMfaEnabledInfo(Boolean mfaEnabled);

  void setMfaEnabled(SignInResponse signInResponse, Boolean isMfaEnabled);

  void setMfaEnabledAndMfaType(SignInResponse signInResponse, Boolean isMfaEnabled, MfaType mfaType);

  void setVerificationType(SignUpResponse signUpResponse, VerificationType verificationType);

  ProcessAttendeeRequestToJoinStreamResponse processAttendeeRequestToJoinStream(StreamResponse stream, StreamAttendee existingAttendee);

  NotAttendingStreamResponse notAttendingStream();
}
