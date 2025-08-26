package com.fleencorp.feen.stream.service.core;

import com.fleencorp.feen.stream.constant.core.StreamVisibility;
import com.fleencorp.feen.stream.model.request.external.ExternalStreamRequest;

public interface ExternalStreamRequestService {

  void deleteStreamExternally(ExternalStreamRequest deleteStreamRequest);

  void cancelStreamExternally(ExternalStreamRequest cancelStreamRequest);

  void rescheduleStreamExternally(ExternalStreamRequest rescheduleStreamRequest);

  void patchStreamExternally(ExternalStreamRequest patchStreamRequest);

  void updateStreamVisibilityExternally(ExternalStreamRequest updateStreamVisibilityRequest, StreamVisibility previousStreamVisibility);
}
