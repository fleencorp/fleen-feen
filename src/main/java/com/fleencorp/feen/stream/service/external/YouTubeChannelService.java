package com.fleencorp.feen.stream.service.external;

import com.fleencorp.feen.model.response.external.google.youtube.category.YouTubeCategoriesResponse;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;

public interface YouTubeChannelService {

  YouTubeCategoriesResponse listAssignableCategories();

  Channel getChannel(YouTube youTube);

  ChannelListResponse getChannels(YouTube youTube);
}
