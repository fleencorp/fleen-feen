package com.fleencorp.feen.shared.stream.service;

import com.fleencorp.feen.shared.stream.IsAStream;

import java.util.Optional;

public interface StreamQueryService {

  Optional<IsAStream> findStreamById(Long streamId);
}
