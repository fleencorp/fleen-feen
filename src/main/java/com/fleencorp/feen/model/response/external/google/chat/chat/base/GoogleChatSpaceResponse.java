package com.fleencorp.feen.model.response.external.google.chat.chat.base;

import com.fleencorp.feen.constant.external.google.chat.space.ChatSpaceField;
import com.google.gson.Gson;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.fleencorp.base.util.FleenUtil.isValidNumber;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GoogleChatSpaceResponse {

  private String name;
  private String displayName;
  private String description;
  private String guidelinesOrRules;
  private String externalId;
  private String type;
  private String spaceType;
  private String spaceHistoryState;
  private String spaceThreadingState;
  private LocalDateTime createTime;
  private MembershipCount membershipCount;
  private String spaceUri;

  @Builder
  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @ToString
  public static class MembershipCount {
    private Integer joinedDirectHumanUserCount;
    private Integer joinedGroupCount;

    /**
     * Creates a {@link MembershipCount} instance from a map of fields.
     *
     * <p>This static method initializes a {@link MembershipCount} object by extracting
     * relevant counts from the provided fields map. It checks for the existence of
     * the membership count data, parses it from JSON format, and sets the values
     * for the joined direct human user count and joined group count if valid.</p>
     *
     * @param fields a map containing the fields from which to construct the
     *               {@link MembershipCount}. This map must contain valid
     *               membership count information under the key specified by
     *               {@link ChatSpaceField#membershipCount()}.
     *
     * @return a {@link MembershipCount} object populated with the extracted counts,
     *         or an empty {@link MembershipCount} if no valid data is found.
     */
    public static MembershipCount of(final Map<String, Object> fields) {
      // Create a new MembershipCount instance
      final MembershipCount membershipCount = new MembershipCount();

      // Return early if fields map is null
      if (fields == null) {
        return membershipCount;
      }

      // Get the membership count object from the fields map
      final Object membershipCountObj = fields.get(ChatSpaceField.membershipCount());

      // Return early if membership count object is null
      if (membershipCountObj == null) {
        return membershipCount;
      }

      // Parse the membership count object to a map
      final Gson gson = new Gson();
      final Map<String, Object> map = gson.fromJson(membershipCountObj.toString(), Map.class);

      // Extract counts and set them if valid
      setValidCount(map.get(ChatSpaceField.joinedDirectHumanUserCount()), membershipCount::setJoinedDirectHumanUserCount);
      setValidCount(map.get(ChatSpaceField.joinedGroupCount()), membershipCount::setJoinedGroupCount);

      // Return the populated MembershipCount object
      return membershipCount;
    }

    private static void setValidCount(final Object countObj, final Consumer<Integer> setter) {
      if (countObj != null && isValidNumber(countObj.toString())) {
        setter.accept((Integer) countObj);
      }
    }


    /**
     * Parses the Google Chat space response from the input string.
     *
     * <p>This method extracts the membership count information, including the joined group count
     * and the joined direct human user count, using regular expression patterns. It then constructs
     * a {@code GoogleChatSpaceResponse} object, which contains the extracted membership count data.</p>
     *
     * @param input the input string containing the Google Chat space response data
     * @return a {@code GoogleChatSpaceResponse} object with the parsed membership count information
     **/
    public static GoogleChatSpaceResponse parseChatSpaceResponse(final String input) {
      final GoogleChatSpaceResponse response = new GoogleChatSpaceResponse();

      // Extract joinedGroupCount from input using a regex pattern
      final Pattern groupCountPattern = Pattern.compile("20:\\s*\\{\\s*4:\\s*(\\d+)\\s*}");
      final Integer joinedGroupCount = getJoinedGroupCount(input, groupCountPattern);

      // Extract joinedDirectHumanUserCount from input using another regex pattern
      final Pattern directUserCountPattern = Pattern.compile("23:\\s*\\{\\s*1:\\s*(\\d+)\\s*}");
      final MembershipCount membershipCount = getMembershipCount(input, directUserCountPattern, joinedGroupCount);

      response.setMembershipCount(membershipCount);
      return response;
    }

    /**
     * Extracts the joined group count from the input string using a regular expression pattern.
     *
     * <p>This method searches the input string for a specific pattern that matches the joined group
     * count. If the pattern is found, the method returns the parsed integer value; otherwise, it
     * returns null.</p>
     *
     * @param input the input string that may contain the joined group count
     * @param groupCountPattern the regex pattern used to extract the group count
     * @return the parsed integer joined group count, or null if not found
     **/
    public static Integer getJoinedGroupCount(final String input, final Pattern groupCountPattern) {
      // Create a matcher for the input string using the provided regex pattern
      final Matcher groupCountMatcher = groupCountPattern.matcher(input);
      Integer joinedGroupCount = null;

      // Find and parse the joined group count from the matcher
      if (groupCountMatcher.find()) {
        joinedGroupCount = Integer.parseInt(groupCountMatcher.group(1));
      }
      // Return the parsed joined group count, or null if not found
      return joinedGroupCount;
    }

    /**
     * Retrieves the membership count by extracting the direct user count from the input string and combining it with the provided group count.
     *
     * <p>This method uses the provided regular expression pattern to search for the direct human user count
     * in the input string. It then constructs a {@code MembershipCount} object by setting both the parsed
     * direct user count and the supplied group count.</p>
     *
     * @param input the input string that may contain the direct human user count
     * @param directUserCountPattern the regex pattern used to extract the direct human user count
     * @param joinedGroupCount the previously extracted joined group count
     * @return a {@code MembershipCount} object containing both direct user count and group count
     **/
    public static MembershipCount getMembershipCount(final String input, final Pattern directUserCountPattern, final Integer joinedGroupCount) {
      // Create a matcher for the input string using the provided regex pattern
      final Matcher directUserCountMatcher = directUserCountPattern.matcher(input);
      Integer joinedDirectHumanUserCount = null;

      // Find and parse the joined direct human user count from the matcher
      if (directUserCountMatcher.find()) {
        joinedDirectHumanUserCount = Integer.parseInt(directUserCountMatcher.group(1));
      }

      // Construct and set values in the MembershipCount object
      return new MembershipCount(joinedDirectHumanUserCount, joinedGroupCount);
    }
  }
}
