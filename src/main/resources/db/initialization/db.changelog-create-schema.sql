--liquibase formatted sql


--changeset alamu:create_table_member

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'member';

CREATE TABLE member (
  member_id BIGSERIAL PRIMARY KEY,
  first_name VARCHAR(100) NOT NULL,
  last_name VARCHAR(100) NOT NULL,
  email_address VARCHAR(50) NOT NULL,
  phone_number VARCHAR(20) NOT NULL,
  username VARCHAR(50) NULL,
  password_hash VARCHAR(500) NOT NULL,
  profile_photo_url VARCHAR(1000),
  country VARCHAR(50),
  email_address_verified BOOLEAN DEFAULT FALSE,
  phone_number_verified BOOLEAN DEFAULT FALSE,
  mfa_enabled BOOLEAN DEFAULT FALSE,
  mfa_secret VARCHAR(1000),
  is_internal BOOLEAN DEFAULT FALSE,

  mfa_type VARCHAR(255) DEFAULT 'NONE'
    NOT NULL CHECK (mfa_type IN ('AUTHENTICATOR', 'EMAIL', 'PHONE', 'NONE')),
  verification_status VARCHAR(255) DEFAULT 'PENDING'
    NOT NULL CHECK (verification_status IN ('APPROVED', 'DISAPPROVED', 'IN_PROGRESS', 'PENDING')),
  profile_status VARCHAR(255) DEFAULT 'ACTIVE'
    NOT NULL CHECK (profile_status IN ('ACTIVE', 'BANNED', 'DISABLED', 'INACTIVE')),
  profile_type VARCHAR(255) DEFAULT 'USER'
    NOT NULL CHECK (profile_type IN ('ADMIN', 'USER')),

  created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

--rollback DROP TABLE IF EXISTS `member`;



--changeset alamu:create_table_profile_token

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'profile_token';

CREATE TABLE profile_token (
  profile_token_id BIGSERIAL PRIMARY KEY,
  reset_password_token VARCHAR(500),
  reset_password_token_expiry_date TIMESTAMP,

  member_id BIGINT NOT NULL,

  created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

  CONSTRAINT profile_token_fk_member
    FOREIGN KEY (member_id)
      REFERENCES member (member_id)
        ON DELETE CASCADE
);

--rollback DROP TABLE IF EXISTS `profile_token`;



--changeset alamu:create_table_role

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'role';

CREATE TABLE role (
  role_id SERIAL PRIMARY KEY,
  title VARCHAR(100) NOT NULL,
  code VARCHAR(100) NOT NULL,
  description VARCHAR(1000),

  created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

--rollback DROP TABLE IF EXISTS `role`;



--changeset alamu:create_table_oauth2_authorization

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'oauth2_authorization';

CREATE TABLE oauth2_authorization (
  oauth2_authorization_id BIGSERIAL PRIMARY KEY,
  access_token VARCHAR(1000),
  refresh_token VARCHAR(1000),
  scope VARCHAR(1000),
  token_type VARCHAR(1000),
  token_expiration_time_in_milliseconds BIGINT,

  oauth2_service_type VARCHAR(255)
    NOT NULL CHECK (oauth2_service_type IN ('GOOGLE_CALENDAR', 'SPOTIFY', 'YOUTUBE')),
  oauth2_source VARCHAR(255)
    NOT NULL CHECK (oauth2_source IN ('GOOGLE', 'SPOTIFY')),

  member_id BIGINT NOT NULL,

  created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

  CONSTRAINT oauth2_authorization_fk_member
    FOREIGN KEY (member_id)
      REFERENCES member (member_id)
        ON DELETE CASCADE
);

--rollback DROP TABLE IF EXISTS `oauth2_authorization`;



-- changeset alamu:create_table_member_role

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'member_role';

CREATE TABLE member_role (
  member_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,

  CONSTRAINT member_role_fk_member
    FOREIGN KEY (member_id)
      REFERENCES member (member_id)
        ON DELETE CASCADE ,
  CONSTRAINT member_role_fk_role
    FOREIGN KEY (role_id)
      REFERENCES role (role_id)
        ON DELETE CASCADE
);

--rollback DROP TABLE IF EXISTS `member_role`;


--changeset alamu:create_table_chat_space

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'chat_space';

CREATE TABLE chat_space (
  chat_space_id BIGSERIAL PRIMARY KEY,
  external_id_or_name VARCHAR(1000),
  title VARCHAR(500) NOT NULL,
  description VARCHAR(3000) NOT NULL,
  summary VARCHAR(3000) NULL,
  tags VARCHAR(300) NULL,
  guidelines_or_rules VARCHAR(3000) NOT NULL,
  space_link VARCHAR(1000),
  slug VARCHAR(255) NOT NULL,

  is_active BOOLEAN DEFAULT TRUE NOT NULL,
  is_deleted BOOLEAN DEFAULT FALSE NOT NULL,
  total_members INTEGER DEFAULT 0 NOT NULL,

  bookmark_count INTEGER DEFAULT 0 NOT NULL,
  like_count INTEGER DEFAULT 0 NOT NULL,
  share_count INTEGER DEFAULT 0 NOT NULL,

  space_visibility VARCHAR(255) DEFAULT 'PUBLIC'
    NOT NULL CHECK (space_visibility IN ('PUBLIC', 'PRIVATE')),

  space_status VARCHAR(255) DEFAULT 'ACTIVE'
    NOT NULL CHECK (space_status IN ('ACTIVE', 'INACTIVE')),

  member_id BIGINT NOT NULL,

  created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

  CONSTRAINT chat_space_fk_member_id
    FOREIGN KEY (member_id)
      REFERENCES member (member_id)
      ON DELETE CASCADE
);

--rollback DROP TABLE IF EXISTS `chat_space`;



--changeset alamu:create_table_chat_space_member

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'chat_space_member';

CREATE TABLE chat_space_member (
  chat_space_member_id BIGSERIAL PRIMARY KEY,
  parent_external_id_or_name VARCHAR(1000),
  external_id_or_name VARCHAR(1000),
  member_comment VARCHAR(1000) NULL,
  space_admin_comment VARCHAR(1000) NULL,

  has_left BOOLEAN NOT NULL DEFAULT false,
  is_removed BOOLEAN NOT NULL DEFAULT false,

  role VARCHAR(255) DEFAULT 'MEMBER'
    NOT NULL CHECK (role IN ('MEMBER', 'ADMIN')),
  request_to_join_status VARCHAR(255) DEFAULT 'PENDING'
    NOT NULL CHECK (request_to_join_status IN ('APPROVED', 'DISAPPROVED', 'PENDING')),

  chat_space_id BIGINT NOT NULL,
  member_id BIGINT NOT NULL,

  created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

  CONSTRAINT chat_space_member_fk_chat_space_id
    FOREIGN KEY (chat_space_id)
      REFERENCES chat_space (chat_space_id)
        ON DELETE CASCADE,

  CONSTRAINT chat_space_member_fk_member_id
    FOREIGN KEY (member_id)
      REFERENCES member (member_id)
        ON DELETE CASCADE
);

--rollback DROP TABLE IF EXISTS `chat_space_member`;



-- changeset alamu:create_table_stream

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'stream';

CREATE TABLE stream (
  stream_id BIGSERIAL PRIMARY KEY,
  external_id VARCHAR(1000),
  title VARCHAR(500) NOT NULL,
  description VARCHAR(3000) NOT NULL,
  summary VARCHAR(3000) NULL,
  tags VARCHAR(300) NULL,
  location VARCHAR(100) NOT NULL,
  timezone VARCHAR(30) NOT NULL,
  organizer_name VARCHAR(100) NOT NULL,
  organizer_email VARCHAR(50) NOT NULL,
  organizer_phone VARCHAR(20) NOT NULL,
  stream_link VARCHAR(1000),
  thumbnail_link VARCHAR(1000),
  slug VARCHAR(255) NOT NULL,

  made_for_kids BOOLEAN NOT NULL DEFAULT false,
  is_deleted BOOLEAN NOT NULL DEFAULT false,

  total_attendees INTEGER DEFAULT 0 NOT NULL,
  total_speakers INTEGER DEFAULT 0 NOT NULL,

  bookmark_count INTEGER DEFAULT 0 NOT NULL,
  like_count INTEGER DEFAULT 0 NOT NULL,
  share_count INTEGER DEFAULT 0 NOT NULL,

  other_details VARCHAR(3000) NULL,
  other_link VARCHAR(1000) NULL,
  music_link VARCHAR(1000) NULL,
  group_or_organization_name VARCHAR(500) NULL,

  source VARCHAR(255) DEFAULT 'NONE'
    NOT NULL CHECK (source IN ('GOOGLE_MEET', 'GOOGLE_MEET_LIVESTREAM', 'NONE', 'YOUTUBE_LIVE', 'EMAIL', 'PHONE', 'NONE')),
  type VARCHAR(255)
    NOT NULL CHECK (type IN ('EVENT', 'LIVE_STREAM')),
  creation_type VARCHAR(255) DEFAULT 'INSTANT'
    NOT NULL CHECK (creation_type IN ('INSTANT', 'SCHEDULED')),
  visibility VARCHAR(255) DEFAULT 'PUBLIC'
    NOT NULL CHECK (visibility IN ('PRIVATE', 'PROTECTED', 'PUBLIC')),
  status VARCHAR(255) DEFAULT 'ACTIVE'
    NOT NULL CHECK (status IN ('ACTIVE', 'CANCELED')),
  scheduled_start_date TIMESTAMP NOT NULL,
  scheduled_end_date TIMESTAMP NOT NULL,

  member_id BIGINT NOT NULL,
  chat_space_id BIGINT NULL,

  created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

  CONSTRAINT stream_fk_member
    FOREIGN KEY (member_id)
      REFERENCES member (member_id)
        ON DELETE SET NULL,
  CONSTRAINT stream_fk_chat_space
    FOREIGN KEY (chat_space_id)
      REFERENCES chat_space (chat_space_id)
      ON DELETE SET NULL
);

--rollback DROP TABLE IF EXISTS `stream`;



-- changeset alamu:create_table_calendar

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'calendar';

CREATE TABLE calendar (
  calendar_id BIGSERIAL PRIMARY KEY,
  external_id VARCHAR(1000),
  title VARCHAR(300) NOT NULL,
  description VARCHAR(1000) NOT NULL,
  timezone VARCHAR(30) NOT NULL,
  code VARCHAR(100) NOT NULL,

  status VARCHAR(255) NOT NULL
    CHECK (status IN ('ACTIVE', 'INACTIVE')),

  created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

--rollback DROP TABLE IF EXISTS `calendar`;



-- changeset alamu:create_table_stream_attendee

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'stream_attendee';

CREATE TABLE stream_attendee (
  stream_attendee_id BIGSERIAL PRIMARY KEY,
  attendee_comment VARCHAR(1000) NULL,
  organizer_comment VARCHAR(1000) NULL,
  email_address VARCHAR(255) NOT NULL,
  is_attending BOOLEAN NOT NULL DEFAULT false,
  is_a_speaker BOOLEAN NOT NULL DEFAULT false,
  is_organizer BOOLEAN NOT NULL DEFAULT false,
  request_to_join_status VARCHAR(255) DEFAULT 'PENDING'
    NOT NULL CHECK (request_to_join_status IN ('APPROVED', 'DISAPPROVED', 'PENDING')),

  stream_id BIGINT NOT NULL,
  member_id BIGINT NOT NULL,

  created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

  CONSTRAINT stream_attendee_fk_member_id
    FOREIGN KEY (member_id)
      REFERENCES member (member_id)
        ON DELETE SET NULL,
  CONSTRAINT stream_attendee_fk_stream_id
    FOREIGN KEY (stream_id)
      REFERENCES stream (stream_id)
        ON DELETE SET NULL
);

--rollback DROP TABLE IF EXISTS `stream_attendee`;



-- changeset alamu:create_table_review

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'review';

CREATE TABLE review (
  review_id BIGSERIAL PRIMARY KEY,
  parent_id BIGINT NOT NULL,
  rating INTEGER NOT NULL,
  review VARCHAR(1000) NULL,
  parent_title varchar(1000) NULL,

  stream_id BIGINT NULL,
  chat_space_id BIGINT NULL,
  author_id BIGINT NOT NULL,

  bookmark_count INTEGER DEFAULT 0 NOT NULL,
  like_count INTEGER DEFAULT 0 NOT NULL,

  parent_type VARCHAR(255)
    NOT NULL CHECK (parent_type IN ('STREAM', 'CHAT_SPACE')),

  created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

  CONSTRAINT review_fk_author_id
    FOREIGN KEY (author_id)
      REFERENCES member (member_id)
        ON DELETE SET NULL,
  CONSTRAINT review_fk_stream_id
    FOREIGN KEY (stream_id)
      REFERENCES stream (stream_id)
        ON DELETE SET NULL,
  CONSTRAINT review_fk_chat_space_id
    FOREIGN KEY (chat_space_id)
      REFERENCES chat_space (chat_space_id)
      ON DELETE SET NULL
);

--rollback DROP TABLE IF EXISTS `review`;



-- changeset alamu:create_table_country

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'country';

CREATE TABLE country (
  country_id BIGSERIAL PRIMARY KEY,
  title VARCHAR(100) NOT NULL,
  code VARCHAR(100) NOT NULL,
  timezone VARCHAR(100) NOT NULL,

  created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

--rollback DROP TABLE IF EXISTS `country`;



--changeset alamu:create_table_follower

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'follower';

CREATE TABLE follower (
  follower_id BIGSERIAL PRIMARY KEY,
  following_id BIGINT NOT NULL,
  followed_id BIGINT NOT NULL,

  created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

  CONSTRAINT follower_fk_following_id
    FOREIGN KEY (following_id)
      REFERENCES member (member_id)
        ON DELETE CASCADE,
  CONSTRAINT follower_fk_followed_id
    FOREIGN KEY (followed_id)
      REFERENCES member (member_id)
        ON DELETE CASCADE
);

--rollback DROP TABLE IF EXISTS `follower`;



--changeset alamu:create_table_stream_speaker

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'stream_speaker';

CREATE TABLE stream_speaker (
  stream_speaker_id BIGSERIAL PRIMARY KEY,
  stream_id BIGINT NOT NULL,
  attendee_id BIGINT NULL,
  member_id BIGINT NULL,
  full_name VARCHAR(100) NOT NULL,
  title VARCHAR(100) NULL,
  description VARCHAR(1000) NULL,

  created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

  CONSTRAINT stream_speaker_fk_stream_id
    FOREIGN KEY (stream_id)
      REFERENCES stream (stream_id)
        ON DELETE CASCADE,
  CONSTRAINT stream_speaker_fk_attendee_id
    FOREIGN KEY (attendee_id)
      REFERENCES stream_attendee (stream_attendee_id)
        ON DELETE CASCADE,
  CONSTRAINT stream_speaker_member_id
    FOREIGN KEY (member_id)
      REFERENCES member (member_id)
        ON DELETE CASCADE
);

--rollback DROP TABLE IF EXISTS `stream_speaker`;



--changeset alamu:create_table_poll

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'poll';

CREATE TABLE poll (
  poll_id BIGSERIAL PRIMARY KEY,
  question VARCHAR(1000) NOT NULL,
  description VARCHAR(2000),
  summary VARCHAR(2000) NULL,
  expires_at TIMESTAMP,
  slug VARCHAR(255) NOT NULL,

  parent_id BIGINT,
  parent_title VARCHAR(1000),

  author_id BIGINT NOT NULL,
  stream_id BIGINT,
  chat_space_id BIGINT,

  is_anonymous BOOLEAN NOT NULL DEFAULT FALSE,
  is_multiple_choice BOOLEAN NOT NULL DEFAULT FALSE,
  deleted BOOLEAN NOT NULL DEFAULT FALSE,

  total_entries INTEGER NOT NULL DEFAULT 0,
  bookmark_count INTEGER NOT NULL DEFAULT 0,
  like_count INTEGER NOT NULL DEFAULT 0,
  share_count INTEGER NOT NULL DEFAULT 0,

  parent_type VARCHAR(255) NULL
    CHECK (parent_type IN ('CHAT_SPACE', 'STREAM')),
  visibility VARCHAR(255) NOT NULL
    CHECK (visibility IN ('PUBLIC', 'PRIVATE', 'FOLLOWERS_ONLY', 'MEMBERS_ONLY', 'ATTENDEES_ONLY')),

  created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

  CONSTRAINT poll_fk_author_id
    FOREIGN KEY (author_id)
      REFERENCES member (member_id)
      ON DELETE SET NULL,
  CONSTRAINT poll_fk_stream_id
    FOREIGN KEY (stream_id)
      REFERENCES stream (stream_id)
      ON DELETE SET NULL,
  CONSTRAINT poll_fk_chat_space_id
    FOREIGN KEY (chat_space_id)
      REFERENCES chat_space (chat_space_id)
      ON DELETE SET NULL
);

--rollback DROP TABLE IF EXISTS `poll`;



--changeset alamu:create_table_share_contact_request

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'share_contact_request';

CREATE TABLE share_contact_request (
  share_contact_request_id BIGSERIAL PRIMARY KEY,
  contact VARCHAR(1000),
  initiator_comment VARCHAR(1000),
  recipient_comment VARCHAR(1000),
  is_expected BOOLEAN NOT NULL DEFAULT false,

  share_contact_request_status VARCHAR(255)
    CHECK (share_contact_request_status IN ('CANCELED', 'ACCEPTED', 'REJECTED', 'SENT')),
  contact_type VARCHAR(255)
    CHECK (contact_type IN ('EMAIL', 'FACEBOOK', 'INSTAGRAM', 'LINKEDIN', 'PHONE_NUMBER',
      'SNAPCHAT', 'TELEGRAM', 'TIKTOK', 'TWITTER_OR_X', 'WECHAT', 'WHATSAPP')),

  initiator_id BIGINT NOT NULL,
  recipient_id BIGINT NOT NULL,

  created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

  CONSTRAINT share_contact_request_fk_initiator_id
    FOREIGN KEY (initiator_id)
      REFERENCES member (member_id)
        ON DELETE CASCADE,
  CONSTRAINT share_contact_request_fk_recipient_id
    FOREIGN KEY (recipient_id)
      REFERENCES member (member_id)
        ON DELETE CASCADE
);

--rollback DROP TABLE IF EXISTS `share_contact_request`;



--changeset alamu:create_table_block_user

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'block_user';

CREATE TABLE block_user (
  block_user_id BIGSERIAL PRIMARY KEY,
  initiator_id BIGINT NOT NULL,
  recipient_id BIGINT NOT NULL,
  block_status VARCHAR(255) DEFAULT 'BLOCKED'
    NOT NULL CHECK (block_status IN ('BLOCKED', 'UNBLOCKED')),

  created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

  CONSTRAINT block_user_fk_initiator_id
    FOREIGN KEY (initiator_id)
      REFERENCES member (member_id)
        ON DELETE CASCADE,
  CONSTRAINT block_user_fk_recipient_id
    FOREIGN KEY (recipient_id)
      REFERENCES member (member_id)
        ON DELETE CASCADE
);

--rollback DROP TABLE IF EXISTS `block_user`;



--changeset alamu:create_table_contact

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'contact';

CREATE TABLE contact (
  contact_id BIGSERIAL PRIMARY KEY,
  contact VARCHAR(1000),
  owner_id BIGINT NOT NULL,

  contact_type VARCHAR(255)
    NOT NULL CHECK (contact_type IN ('EMAIL', 'FACEBOOK', 'INSTAGRAM', 'LINKEDIN', 'PHONE_NUMBER',
      'SNAPCHAT', 'TELEGRAM', 'TIKTOK', 'TWITTER_OR_X', 'WECHAT', 'WHATSAPP')),

  created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

   CONSTRAINT contact_fk_owner_id
     FOREIGN KEY (owner_id)
       REFERENCES member (member_id)
         ON DELETE CASCADE
);

--rollback DROP TABLE IF EXISTS `contact`;



--changeset alamu:create_table_notification

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'notification';

CREATE TABLE notification (
  notification_id BIGSERIAL PRIMARY KEY,
  initiator_or_requester_name VARCHAR(1000),
  recipient_name VARCHAR(1000),
  message_key VARCHAR(1000) NOT NULL,
  other_comment VARCHAR(1000) NULL,
  is_read BOOLEAN NOT NULL,
  id_or_link_or_url VARCHAR(1000) NOT NULL,
  notification_read_on TIMESTAMP NULL,
  share_contact_request_id BIGINT,
  stream_title VARCHAR(1000),
  stream_attendee_name VARCHAR(1000),
  chat_space_member_name VARCHAR(1000),
  chat_space_title VARCHAR(1000),
  follower_name VARCHAR(1000),


  notification_status VARCHAR(255)
    NOT NULL CHECK (notification.notification_status IN ('READ', 'UNREAD')),
  contact_type VARCHAR(255)
    CHECK (contact_type IN ('EMAIL', 'FACEBOOK', 'INSTAGRAM', 'LINKEDIN', 'PHONE_NUMBER'
      'SNAPCHAT', 'TELEGRAM', 'TIKTOK', 'TWITTER_OR_X', 'WECHAT', 'WHATSAPP')),
  notification_type VARCHAR(255) NOT NULL CHECK (notification_type IN (
    'REQUEST_TO_JOIN_CHAT_SPACE_APPROVED',
    'REQUEST_TO_JOIN_CHAT_SPACE_DISAPPROVED',
    'REQUEST_TO_JOIN_CHAT_SPACE_RECEIVED',
    'REQUEST_TO_JOIN_EVENT_APPROVED',
    'REQUEST_TO_JOIN_EVENT_DISAPPROVED',
    'REQUEST_TO_JOIN_EVENT_RECEIVED',
    'REQUEST_TO_JOIN_LIVE_BROADCAST_APPROVED',
    'REQUEST_TO_JOIN_LIVE_BROADCAST_DISAPPROVED',
    'REQUEST_TO_JOIN_LIVE_BROADCAST_RECEIVED',
    'SHARE_CONTACT_REQUEST_APPROVED',
    'SHARE_CONTACT_REQUEST_DISAPPROVED',
    'SHARE_CONTACT_REQUEST_RECEIVED',
    'USER_FOLLOWING'
  )),

  chat_space_id BIGINT,
  chat_space_member_id BIGINT,
  initiator_or_requester_id BIGINT,
  stream_id BIGINT,
  follower_id BIGINT,
  receiver_id BIGINT NOT NULL,
  recipient_id BIGINT,
  stream_attendee_id BIGINT,

  created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

  CONSTRAINT notification_fk_receiver_id
    FOREIGN KEY (receiver_id)
      REFERENCES member (member_id)
        ON DELETE CASCADE,

  CONSTRAINT notification_fk_initiator_or_requester_id
    FOREIGN KEY (initiator_or_requester_id)
      REFERENCES member (member_id)
        ON DELETE CASCADE,

  CONSTRAINT notification_fk_recipient_id
    FOREIGN KEY (recipient_id)
      REFERENCES member (member_id)
        ON DELETE CASCADE,

  CONSTRAINT notification_fk_share_contact_request_id
    FOREIGN KEY (share_contact_request_id)
      REFERENCES share_contact_request (share_contact_request_id)
        ON DELETE SET NULL,

  CONSTRAINT notification_fk_stream_id
    FOREIGN KEY (stream_id)
      REFERENCES stream (stream_id)
        ON DELETE SET NULL,

  CONSTRAINT notification_fk_stream_attendee_id
    FOREIGN KEY (stream_attendee_id)
      REFERENCES stream_attendee (stream_attendee_id)
        ON DELETE SET NULL,

  CONSTRAINT notification_fk_chat_space_id
    FOREIGN KEY (chat_space_id)
      REFERENCES chat_space (chat_space_id)
        ON DELETE SET NULL,

  CONSTRAINT notification_fk_chat_space_member_id
    FOREIGN KEY (chat_space_member_id)
      REFERENCES chat_space_member (chat_space_member_id)
        ON DELETE SET NULL,

  CONSTRAINT notification_fk_follower_id
    FOREIGN KEY (follower_id)
      REFERENCES follower (follower_id)
        ON DELETE SET NULL
);

--rollback DROP TABLE IF EXISTS `notification`;



--changeset alamu:create_table_adjectives

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'adjectives';

CREATE TABLE adjectives (
  id BIGSERIAL PRIMARY KEY,
  word VARCHAR(255)
);

--rollback DROP TABLE IF EXISTS `adjectives`;



--changeset alamu:create_table_nouns

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'nouns';

CREATE TABLE nouns (
  id BIGSERIAL PRIMARY KEY,
  word VARCHAR(255)
);

--rollback DROP TABLE IF EXISTS `nouns`;



--changeset alamu:create_table_link

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'link';

CREATE TABLE link (
  link_id BIGSERIAL PRIMARY KEY,
  parent_id BIGINT NOT NULL,
  url VARCHAR(1000),

  link_parent_type VARCHAR(255)
    NOT NULL CHECK (link_parent_type IN ('BUSINESS', 'CHAT_SPACE', 'POLL', 'STREAM', 'USER')),

  link_type VARCHAR(255) NOT NULL CHECK (link_type IN (
    'EMAIL',
    'DISCORD',
    'FACEBOOK',
    'INSTAGRAM',
    'LINKEDIN',
    'PHONE_NUMBER',
    'SLACK',
    'SNAPCHAT',
    'TWITTER_OR_X',
    'TELEGRAM',
    'WHATSAPP',
    'OTHER'
    )),

  business_id BIGINT,
  chat_space_id BIGINT,
  stream_id BIGINT,

  member_id BIGINT,

  CONSTRAINT link_fk_business_id
    FOREIGN KEY (business_id)
      REFERENCES business (business_id)
      ON DELETE SET NULL,

  CONSTRAINT link_fk_chat_space_id
    FOREIGN KEY (chat_space_id)
      REFERENCES chat_space (chat_space_id)
      ON DELETE SET NULL,

  CONSTRAINT link_fk_stream_id
    FOREIGN KEY (stream_id)
      REFERENCES stream (stream_id)
      ON DELETE SET NULL,

  CONSTRAINT link_fk_member_id
    FOREIGN KEY (member_id)
      REFERENCES member (member_id)
      ON DELETE SET NULL

);

--rollback DROP TABLE IF EXISTS `link`;



-- changeset alamu:create_table_likes

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'likes';

CREATE TABLE likes (
  like_id BIGSERIAL PRIMARY KEY,
  parent_id BIGINT NOT NULL,
  parent_title varchar(1000) NULL,

  chat_space_id BIGINT NULL,
  poll_id BIGINT NULL,
  review_id BIGINT NULL,
  stream_id BIGINT NULL,
  member_id BIGINT NOT NULL,

  like_parent_type VARCHAR(255)
    NOT NULL CHECK (like_parent_type IN ('BUSINESS', 'CHAT_SPACE', 'JOB_OPPORTUNITY', 'POLL', 'REVIEW', 'STREAM')),

  like_type VARCHAR(255)
    NOT NULL CHECK (like_type IN ('LIKE', 'UNLIKE')),

  created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

  CONSTRAINT like_fk_member_id
    FOREIGN KEY (member_id)
      REFERENCES member (member_id)
        ON DELETE SET NULL,

  CONSTRAINT like_fk_chat_space_id
    FOREIGN KEY (chat_space_id)
      REFERENCES stream (stream_id)
        ON DELETE SET NULL,

  CONSTRAINT like_fk_poll_id
    FOREIGN KEY (poll)
      REFERENCES poll (poll_id)
      ON DELETE SET NULL,

  CONSTRAINT like_fk_review_id
    FOREIGN KEY (review_id)
      REFERENCES review (review_id)
        ON DELETE SET NULL,

  CONSTRAINT like_fk_stream_id
    FOREIGN KEY (stream_id)
      REFERENCES stream (stream_id)
        ON DELETE SET NULL
);

--rollback DROP TABLE IF EXISTS `likes`;


--changeset alamu:create_table_poll_option

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'poll_option';

CREATE TABLE poll_option (
  poll_option_id BIGSERIAL PRIMARY KEY,
  poll_id BIGINT NOT NULL,
  option_text VARCHAR(1000) NOT NULL,
  vote_count INTEGER NOT NULL DEFAULT 0,
  created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

  CONSTRAINT poll_option_fk_poll_id
    FOREIGN KEY (poll_id)
      REFERENCES poll (poll_id)
      ON DELETE CASCADE
);

--rollback DROP TABLE IF EXISTS `poll_option`;



--changeset alamu:create_table_poll_vote

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'poll_vote';

CREATE TABLE poll_vote (
  vote_id BIGSERIAL PRIMARY KEY,
  poll_id BIGINT NOT NULL,
  option_id BIGINT NOT NULL,
  member_id BIGINT NOT NULL,
  created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

  CONSTRAINT poll_vote_fk_poll_id
    FOREIGN KEY (poll_id)
      REFERENCES poll (poll_id)
      ON DELETE CASCADE,
  CONSTRAINT poll_vote_fk_option_id
    FOREIGN KEY (option_id)
      REFERENCES poll_option (poll_option_id)
      ON DELETE CASCADE,
  CONSTRAINT poll_vote_fk_member_id
    FOREIGN KEY (member_id)
      REFERENCES member (member_id)
      ON DELETE SET NULL,
  CONSTRAINT poll_vote_unique_poll_member_option
    UNIQUE (poll_id, member_id, option_id)
);

--rollback DROP TABLE IF EXISTS `poll_vote`;



--changeset alamu:create_table_soft_ask

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'soft_ask';

CREATE TABLE soft_ask (
  soft_ask_id BIGSERIAL PRIMARY KEY,
  title VARCHAR(500) NOT NULL,
  description VARCHAR(2000) NOT NULL,
  tags VARCHAR(500),
  link VARCHAR(1000),
  slug VARCHAR(255) NOT NULL,

  parent_id BIGINT,
  parent_title VARCHAR(500),

  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
  is_visible BOOLEAN NOT NULL DEFAULT TRUE,

  bookmark_count INTEGER DEFAULT 0 NOT NULL,
  reply_count INTEGER NOT NULL DEFAULT 0,
  share_count INTEGER NOT NULL DEFAULT 0,
  vote_count INTEGER NOT NULL DEFAULT 0,

  latitude DECIMAL(10, 8),
  longitude DECIMAL(11, 8),

  geohash VARCHAR(9) NULL,
  geohash_prefix VARCHAR(5) NULL,
  location geography(Point, 4326),

  chat_space_id BIGINT,
  poll_id BIGINT,
  stream_id BIGINT,
  author_id BIGINT NOT NULL,

  parent_type VARCHAR(255) NULL
    CHECK (parent_type IN ('CHAT_SPACE', 'POLL', 'STREAM')),
  status VARCHAR(255) NOT NULL
    CHECK (status IN ('ANONYMOUS', 'NON_ANONYMOUS')),
  visibility VARCHAR(255) NOT NULL
    CHECK (visibility IN ('PUBLIC', 'PRIVATE')),
  location_visibility VARCHAR(255) NOT NULL
    CHECK (location_visibility IN ('COUNTRY', 'GLOBAL', 'LOCAL', 'NEARBY', 'PRIVATE', 'REGION')),
  moderation_status VARCHAR(255) NOT NULL
    CHECK (moderation_status IN ('ABUSE', 'CLEAN', 'FLAGGED', 'HIDDEN', 'SPAM')),
  mood_tag VARCHAR(255) NULL
    CHECK (mood_tag IN ('HAPPY','SAD','EXCITED','ANGRY','THOUGHTFUL','CURIOUS','BORED','GRATEFUL','HOPEFUL','CONFUSED','RELAXED','INSPIRED')),

  created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

  CONSTRAINT soft_ask_fk_chat_space_id
    FOREIGN KEY (chat_space_id)
      REFERENCES chat_space (chat_space_id)
        ON DELETE SET NULL,

  CONSTRAINT soft_ask_fk_poll_id
    FOREIGN KEY (poll_id)
      REFERENCES poll (poll_id)
      ON DELETE SET NULL,

  CONSTRAINT soft_ask_fk_stream_id
    FOREIGN KEY (stream_id)
      REFERENCES stream (stream_id)
        ON DELETE SET NULL,

  CONSTRAINT soft_ask_fk_author_id
    FOREIGN KEY (author_id)
      REFERENCES member (member_id)
        ON DELETE SET NULL
);

--rollback DROP TABLE IF EXISTS `soft_ask`;



--changeset alamu:create_table_soft_ask_reply

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'soft_ask_reply';

CREATE TABLE soft_ask_reply (
  soft_ask_reply_id BIGSERIAL PRIMARY KEY,
  content VARCHAR(3000) NOT NULL,
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
  is_visible BOOLEAN NOT NULL DEFAULT TRUE,
  slug VARCHAR(255) NOT NULL,

  bookmark_count INTEGER NOT NULL DEFAULT 0,
  child_reply_count INTEGER NOT NULL DEFAULT 0,
  share_count INTEGER NOT NULL DEFAULT 0,
  vote_count INTEGER NOT NULL DEFAULT 0,

  latitude DECIMAL(10, 8),
  longitude DECIMAL(11, 8),

  geohash VARCHAR(9) NULL,
  geohash_prefix VARCHAR(5) NULL,

  author_id BIGINT NOT NULL,
  parent_reply_id BIGINT,
  soft_ask_id BIGINT NOT NULL,

  location_visibility VARCHAR(255) NOT NULL
    CHECK (location_visibility IN ('COUNTRY', 'GLOBAL', 'LOCAL', 'NEARBY', 'PRIVATE', 'REGION')),
  moderation_status VARCHAR(255) NOT NULL
    CHECK (moderation_status IN ('ABUSE', 'CLEAN', 'FLAGGED', 'HIDDEN', 'SPAM')),
  mood_tag VARCHAR(255) NULL
    CHECK (mood_tag IN ('HAPPY','SAD','EXCITED','ANGRY','THOUGHTFUL','CURIOUS','BORED','GRATEFUL','HOPEFUL','CONFUSED','RELAXED','INSPIRED')),

  created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

  CONSTRAINT soft_ask_reply_fk_soft_ask
    FOREIGN KEY (soft_ask_id)
      REFERENCES soft_ask (soft_ask_id)
        ON DELETE CASCADE,

  CONSTRAINT soft_ask_reply_fk_author
    FOREIGN KEY (author_id)
      REFERENCES member (member_id)
        ON DELETE CASCADE,

  CONSTRAINT soft_ask_reply_fk_parent_reply
    FOREIGN KEY (parent_reply_id)
      REFERENCES soft_ask_reply (soft_ask_reply_id)
        ON DELETE CASCADE
);

--rollback DROP TABLE IF EXISTS `soft_ask_reply`;



--changeset alamu:create_table_soft_ask_votes

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'soft_ask_votes';

CREATE TABLE soft_ask_votes (
  vote_id BIGSERIAL PRIMARY KEY,
  parent_id BIGINT,
  parent_title VARCHAR(500),
  parent_summary VARCHAR(500),

  member_id BIGINT NOT NULL,
  soft_ask_id BIGINT,
  soft_ask_reply_id BIGINT,

  parent_type VARCHAR(255) NOT NULL
    CHECK (parent_type IN ('SOFT_ASK', 'SOFT_ASK_REPLY')),
  type VARCHAR(255) NOT NULL
    CHECK (type IN ('NOT_VOTED', 'VOTED')),

  created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

  CONSTRAINT soft_ask_vote_fk_soft_ask_id
    FOREIGN KEY (soft_ask_id)
      REFERENCES soft_ask (soft_ask_id)
        ON DELETE CASCADE,

  CONSTRAINT soft_ask_vote_fk_reply_id
    FOREIGN KEY (soft_ask_reply_id)
      REFERENCES soft_ask_reply (soft_ask_reply_id)
        ON DELETE CASCADE,

  CONSTRAINT soft_ask_vote_fk_member_id
    FOREIGN KEY (member_id)
      REFERENCES member (member_id)
        ON DELETE SET NULL
);

--rollback DROP TABLE IF EXISTS `soft_ask_votes`;



--changeset alamu:create_table_soft_ask_participant_detail

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'soft_ask_participant_detail';

CREATE TABLE soft_ask_participant_detail (
  id BIGSERIAL PRIMARY KEY,

  soft_ask_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,

  username VARCHAR(100) NOT NULL,
  display_name VARCHAR(100) NOT NULL,
  avatar VARCHAR(1000) NOT NULL,

  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_soft_ask_participant_detail_soft_ask
    FOREIGN KEY (soft_ask_id)
      REFERENCES soft_ask (soft_ask_id)
        ON DELETE CASCADE,

  CONSTRAINT fk_soft_ask_participant_detail_author
    FOREIGN KEY (user_id)
      REFERENCES member (member_id)
        ON DELETE CASCADE
);

--rollback DROP TABLE IF EXISTS `soft_ask_participant_detail`;



--changeset alamu:create_table_bookmarks

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'bookmarks';

CREATE TABLE bookmarks (
  bookmark_id BIGSERIAL PRIMARY KEY,
  parent_id BIGINT,
  parent_summary VARCHAR(255),

  type VARCHAR(255) NOT NULL
    CHECK (type IN ('BOOKMARK', 'UNBOOKMARK')),

  parent_type VARCHAR(255) NOT NULL
    CHECK (parent_type IN ('BUSINESS', 'CHAT_SPACE', 'JOB_OPPORTUNITY', 'POLL', 'REVIEW', 'SOFT_ASK', 'SOFT_ASK_REPLY', 'STREAM')),

  chat_space_id BIGINT,
  poll_id BIGINT,
  review_id BIGINT,
  stream_id BIGINT,
  soft_ask_id BIGINT,
  soft_ask_reply_id BIGINT,
  other_id BIGINT,
  member_id BIGINT NOT NULL,

  created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

  CONSTRAINT bookmarks_fk_chat_space
    FOREIGN KEY (chat_space_id)
      REFERENCES chat_space (chat_space_id)
        ON DELETE CASCADE,

  CONSTRAINT bookmarks_fk_poll
    FOREIGN KEY (poll_id)
      REFERENCES poll (poll_id)
        ON DELETE CASCADE,

  CONSTRAINT bookmarks_fk_stream
    FOREIGN KEY (stream_id)
      REFERENCES stream (stream_id)
        ON DELETE CASCADE,

  CONSTRAINT bookmarks_fk_review
    FOREIGN KEY (review_id)
      REFERENCES review (review_id)
        ON DELETE CASCADE,

  CONSTRAINT bookmarks_fk_soft_ask
    FOREIGN KEY (soft_ask_id)
      REFERENCES soft_ask (soft_ask_id)
        ON DELETE CASCADE,

  CONSTRAINT bookmarks_fk_soft_ask_reply
    FOREIGN KEY (soft_ask_reply_id)
      REFERENCES soft_ask_reply (soft_ask_reply_id)
        ON DELETE CASCADE,

  CONSTRAINT bookmarks_fk_member
    FOREIGN KEY (member_id)
      REFERENCES member (member_id)
        ON DELETE CASCADE
);

--rollback DROP TABLE IF EXISTS `bookmarks`;



--changeset alamu:create_table_business

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'business';

CREATE TABLE business (
  business_id BIGSERIAL PRIMARY KEY,
  title VARCHAR(300) NOT NULL,
  description VARCHAR(3000) NOT NULL,
  slug VARCHAR(255) NOT NULL,

  motto VARCHAR(500),
  other_details VARCHAR(3000),
  address VARCHAR(500),
  country VARCHAR(300),
  logo_url VARCHAR(2000),
  founding_year INTEGER,

  deleted BOOLEAN NOT NULL DEFAULT FALSE,
  share_count INT NOT NULL DEFAULT 0,

  channel_type VARCHAR(255) NOT NULL
    CHECK (channel_type IN ('OFFLINE', 'ONLINE', 'OFFLINE_AND_ONLINE')),
  status VARCHAR(255) NOT NULL
    CHECK (status IN ('ACTIVE', 'INACTIVE')),

  owner_id BIGINT NOT NULL,

  created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

  CONSTRAINT business_fk_owner
    FOREIGN KEY (owner_id)
      REFERENCES member (member_id)
      ON DELETE CASCADE
);

--rollback DROP TABLE IF EXISTS `business`;