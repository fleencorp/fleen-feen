package com.fleencorp.feen.model.response.user.profile;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.info.contact.ContactRequestEligibilityInfo;
import com.fleencorp.feen.model.info.user.profile.*;
import com.fleencorp.feen.model.response.user.UserResponse;
import com.fleencorp.feen.model.search.chat.space.mutual.MutualChatSpaceMembershipSearchResult;
import com.fleencorp.feen.model.search.stream.common.UserCreatedStreamsSearchResult;
import com.fleencorp.feen.model.search.stream.mutual.MutualStreamAttendanceSearchResult;
import com.fleencorp.localizer.model.response.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "user_detail",
  "user_created_streams_search_result",
  "mutual_stream_attendance_search_result",
  "mutual_chat_space_membership_search_result",
  "contact_request_eligibility_info",
  "is_blocked_info",
  "is_following_info",
  "is_followed_info",
  "total_following_info",
  "total_followed_info"
})
public class UserProfileResponse extends ApiResponse {

  @JsonProperty("user_detail")
  private UserResponse user;

  @JsonProperty("user_created_streams_search_result")
  private UserCreatedStreamsSearchResult userCreatedStreamsSearchResult;

  @JsonProperty("mutual_stream_attendance_search_result")
  private MutualStreamAttendanceSearchResult mutualStreamAttendanceSearchResult;

  @JsonProperty("mutual_chat_space_membership_search_result")
  private MutualChatSpaceMembershipSearchResult mutualChatSpaceMembershipSearchResult;

  @JsonProperty("contact_request_eligibility_info")
  private ContactRequestEligibilityInfo contactRequestEligibilityInfo;

  @JsonProperty("is_blocked_info")
  private IsBlockedInfo isBlockedInfo;

  @JsonProperty("is_following_info")
  private IsFollowingInfo isFollowingInfo;

  @JsonProperty("is_followed_info")
  private IsFollowedInfo isFollowedInfo;

  @JsonProperty("total_following_info")
  private TotalFollowingInfo totalFollowingInfo;

  @JsonProperty("total_followed_info")
  private TotalFollowedInfo totalFollowedInfo;

  @Override
  public String getMessageCode() {
    return "user.profile.public";
  }

  public static UserProfileResponse of() {
    return new UserProfileResponse();
  }
}

