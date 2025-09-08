package com.fleencorp.feen.shared.poll.query.constant;

public final class PollQueryConstant {

  private PollQueryConstant() {}

  public static final String FIND_POLLS = """
    SELECT
      p.poll_id       AS pollId,
      p.question      AS question,
      p.description   AS description,
      p.author_id     AS authorId,
      p.parent_id     AS parentId,
      p.parent_title  AS parentTitle,
      p.stream_id     AS streamId,
      p.chat_space_id AS chatSpaceId
    FROM poll p
    WHERE p.deleted = FALSE
    """;
}

