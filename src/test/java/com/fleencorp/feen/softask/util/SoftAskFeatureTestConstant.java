package com.fleencorp.feen.softask.util;

public class SoftAskFeatureTestConstant {

  public static class SoftAskDtoDefaultTestConstants {
    public static final String QUESTION = "I'm excited to be here, what about you?";

    public static final String PARENT_ID = "1";
    public static final String PARENT_TYPE_CHAT_SPACE = "CHAT_SPACE";
    public static final String PARENT_TYPE_POLL = "POLL";
    public static final String PARENT_TYPE_STREAM = "STREAM";

    public static final String NULL_TITLE = "";
    public static final String PARENT_TITLE = "Java Conf 2025";
  }

  public static class SoftAskReplyDtoDefaultTestConstants {
    public static final String CONTENT = "I'm excited to be here too";

  }

  public static class SoftAskVoteDtoDefaultTestConstants {
    public static final String PARENT_ID = "1";
    public static final String VOTED = "VOTED";
    public static final String NOT_VOTED = "NOT_VOTED";

    public static final String PARENT_TYPE_SOFT_ASK = "SOFT_ASK";
    public static final String PARENT_TYPE_SOFT_ASK_REPLY = "SOFT_ASK_REPLY";

  }

  public static class UserOtherDtoDefaultTestConstants {
    public static final Double LONGITUDE = 9.292;
    public static final Double LATITUDE = -9.292;

    public static final String MOOD = "HAPPY";
  }

  public static class SoftAskDefaultTestConstants {

    public static final Long ID_1 = 1L;
    public static final Long ID_2 = 2L;
    public static final Long ID_3 = 3L;
    public static final Long ID_4 = 4L;
    public static final Long ID_5 = 5L;
    public static final Long ID_6 = 6L;

    public static final String ID_1_Str = String.valueOf(ID_1);
  }

  public static class SoftAskReplyDefaultTestConstants {

    public static final Long ID_1 = 1L;
    public static final Long ID_2 = 2L;
    public static final Long ID_3 = 3L;
    public static final Long ID_4 = 4L;
    public static final Long ID_5 = 5L;
    public static final Long ID_6 = 6L;

    public static final String ID_1_Str = String.valueOf(ID_1);
  }

  public static class SoftAskVoteDefaultTestConstants {
    public static final Long ID_1 = 1L;
    public static final Long ID_2 = 2L;


  }

  public static class SoftAskResponseTestConstants {

    public static final Long ID_1 = 1L;
    public static final Long ID_2 = 2L;
    public static final Long ID_3 = 3L;
    public static final Long ID_4 = 4L;
    public static final Long ID_5 = 5L;
    public static final Long ID_6 = 6L;
  }

  public static class RegisteredUserDefaultTestConstants {

    public static final Long ID_1 = 1L;
    public static final Long ID_2 = 2L;
    public static final Long ID_3 = 3L;
    public static final Long ID_4 = 4L;
    public static final Long ID_5 = 5L;
    public static final Long ID_6 = 6L;
  }

  public static class IsAMemberTestConstants {

    public static final Long ID_1 = 1L;
    public static final Long ID_2 = 2L;
    public static final Long ID_3 = 3L;
    public static final Long ID_4 = 4L;
    public static final Long ID_5 = 5L;
    public static final Long ID_6 = 6L;

    public static final String USERNAME = "john123";
    public static final String EMAIL_ADDRESS = "john@example.com";
    public static final String PASSWORD = "00000";
  }

  public static class OtherTestConstants {

    public static final Integer PARENT_TOTAL_VOTES_1 = 1;
    public static final Integer PARENT_TOTAL_VOTES_0 = 0;
  }
}
