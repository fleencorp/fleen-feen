package com.fleencorp.feen.common.service.misc;

import com.fleencorp.feen.model.dto.aws.CreateSignedUrlDto;
import com.fleencorp.feen.model.response.external.aws.SignedUrlsResponse;

import java.util.Map;

public interface ObjectService {

  SignedUrlsResponse createSignedUrls(CreateSignedUrlDto createSignedUrlDto);

  Map<String, String> getAvatarBaseName(String avatarId);

  Map<String, String> getAvatarUrls(String avatarId);
}
