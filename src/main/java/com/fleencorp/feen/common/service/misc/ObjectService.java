package com.fleencorp.feen.common.service.misc;

import com.fleencorp.feen.model.dto.aws.CreateSignedUrlDto;
import com.fleencorp.feen.model.response.external.aws.SignedUrlsResponse;

public interface ObjectService {

  SignedUrlsResponse createSignedUrls(CreateSignedUrlDto createSignedUrlDto);
}
