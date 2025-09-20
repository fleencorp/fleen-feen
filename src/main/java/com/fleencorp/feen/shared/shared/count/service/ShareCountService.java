package com.fleencorp.feen.shared.shared.count.service;

import com.fleencorp.feen.shared.shared.count.model.dto.ShareDto;
import com.fleencorp.feen.shared.shared.count.model.response.ShareResponse;

public interface ShareCountService {

  ShareResponse share(ShareDto shareDto);
}
