package com.fleencorp.feen.mapper;

import com.fleencorp.feen.constant.security.mfa.MfaType;
import com.fleencorp.feen.constant.security.verification.VerificationType;
import com.fleencorp.feen.constant.social.ShareContactRequestStatus;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.info.security.IsMfaEnabledInfo;
import com.fleencorp.feen.model.info.security.MfaTypeInfo;
import com.fleencorp.feen.model.info.share.contact.request.ShareContactRequestStatusInfo;
import com.fleencorp.feen.model.response.auth.SignInResponse;
import com.fleencorp.feen.model.response.auth.SignUpResponse;
import com.fleencorp.feen.model.response.stream.StreamResponse;
import com.fleencorp.feen.model.response.stream.attendance.NotAttendingStreamResponse;
import com.fleencorp.feen.model.response.stream.attendance.ProcessAttendeeRequestToJoinStreamResponse;

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
