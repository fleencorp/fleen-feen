package com.fleencorp.feen.common.service.misc;

import com.fleencorp.feen.model.dto.aws.CreateSignedUrlDto;
import com.fleencorp.feen.model.response.external.aws.SignedUrlsResponse;

import java.util.HashMap;
import java.util.Map;

public interface ObjectService {

  SignedUrlsResponse createSignedUrls(CreateSignedUrlDto createSignedUrlDto);

  Map<String, String> generateAvatarUrl(String avatarId);

  static Map<String, String> getAvatarUrls(final String avatarName) {
    final Map<String, String> urls = new HashMap<>();
    urls.put("png", String.format(avatarName + ".png", avatarName));
    urls.put("jpg", String.format(avatarName + ".jpg", avatarName));

    return urls;
  }

}
