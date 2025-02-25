--liquibase formatted sql


--changeset alamu:1

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'member';

CREATE TABLE member (
  member_id BIGSERIAL PRIMARY KEY,
  first_name VARCHAR(100) NOT NULL,
  last_name VARCHAR(100) NOT NULL,
  email_address VARCHAR(50) NOT NULL,
  phone_number VARCHAR(20) NOT NULL,
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



--changeset alamu:2

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



--changeset alamu:3

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



--changeset alamu:4

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'oauth2_authorization';

CREATE TABLE oauth2_authorization (
  oauth2_authorization_id BIGSERIAL PRIMARY KEY,
  access_token VARCHAR(1000),
  refresh_token VARCHAR(1000),
  scope VARCHAR(1000),
  token_type VARCHAR(1000),
  token_expiration_time_in_milliseconds BIGINT,
  member_id BIGINT NOT NULL,

  oauth2_service_type VARCHAR(255)
      NOT NULL CHECK (oauth2_service_type IN ('GOOGLE_CALENDAR', 'YOUTUBE')),
  oauth2_source VARCHAR(255)
      NOT NULL CHECK (oauth2_source IN ('GOOGLE')),

  created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

  CONSTRAINT oauth2_authorization_fk_member
    FOREIGN KEY (member_id)
      REFERENCES member (member_id)
        ON DELETE CASCADE
);

--rollback DROP TABLE IF EXISTS `oauth2_authorization`;



-- changeset alamu:5

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


--changeset alamu:6

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'chat_space';

CREATE TABLE chat_space (
  chat_space_id BIGSERIAL PRIMARY KEY,
  external_id_or_name VARCHAR(1000),
  title VARCHAR(500) NOT NULL,
  description VARCHAR(3000) NOT NULL,
  tags VARCHAR(300) NULL,
  guidelines_or_rules VARCHAR(3000) NOT NULL,
  space_link VARCHAR(1000),

  member_id BIGINT NOT NULL,
  space_visibility VARCHAR(255) DEFAULT 'PUBLIC'
    NOT NULL CHECK (space_visibility IN ('PUBLIC', 'PRIVATE')),
  is_active BOOLEAN DEFAULT TRUE NOT NULL,
  is_deleted BOOLEAN DEFAULT FALSE NOT NULL,
  total_members BIGINT DEFAULT 0 NOT NULL,

  created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

  CONSTRAINT chat_space_fk_member_id
    FOREIGN KEY (member_id)
      REFERENCES member (member_id)
      ON DELETE CASCADE
);

--rollback DROP TABLE IF EXISTS `chat_space`;



--changeset alamu:7

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'chat_space_member';

CREATE TABLE chat_space_member (
  chat_space_member_id BIGSERIAL PRIMARY KEY,
  parent_external_id_or_name VARCHAR(1000),
  external_id_or_name VARCHAR(1000),
  chat_space_id BIGINT NOT NULL,
  member_id BIGINT NOT NULL,
  role VARCHAR(255) DEFAULT 'MEMBER'
    NOT NULL CHECK (role IN ('MEMBER', 'ADMIN')),
  request_to_join_status VARCHAR(255) DEFAULT 'PENDING'
    NOT NULL CHECK (request_to_join_status IN ('APPROVED', 'DISAPPROVED', 'PENDING')),
  member_comment VARCHAR(1000) NULL,
  space_admin_comment VARCHAR(1000) NULL,

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



-- changeset alamu:8

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'fleen_stream';

CREATE TABLE fleen_stream (
  fleen_stream_id BIGSERIAL PRIMARY KEY,
  external_id VARCHAR(1000),
  title VARCHAR(500) NOT NULL,
  description VARCHAR(3000) NOT NULL,
  tags VARCHAR(300) NULL,
  location VARCHAR(100) NOT NULL,
  timezone VARCHAR(30) NOT NULL,
  organizer_name VARCHAR(100) NOT NULL,
  organizer_email VARCHAR(50) NOT NULL,
  organizer_phone VARCHAR(20) NOT NULL,
  stream_link VARCHAR(1000),
  thumbnail_link VARCHAR(1000),

  made_for_kids BOOLEAN NOT NULL DEFAULT false,
  is_deleted BOOLEAN NOT NULL DEFAULT false,
  total_attendees BIGINT DEFAULT 0 NOT NULL,

  other_details VARCHAR(3000) NULL,
  other_link VARCHAR(1000) NULL,
  group_or_organization_name VARCHAR(500) NULL,


  stream_source VARCHAR(255) DEFAULT 'NONE'
    NOT NULL CHECK (stream_source IN ('GOOGLE_MEET', 'GOOGLE_MEET_LIVESTREAM', 'NONE', 'YOUTUBE_LIVE', 'EMAIL', 'PHONE', 'NONE')),
  stream_creation_type VARCHAR(255) DEFAULT 'INSTANT'
    NOT NULL CHECK (stream_creation_type IN ('INSTANT', 'SCHEDULED')),
  stream_visibility VARCHAR(255) DEFAULT 'PUBLIC'
    NOT NULL CHECK (stream_visibility IN ('PRIVATE', 'PROTECTED', 'PUBLIC')),
  stream_status VARCHAR(255) DEFAULT 'ACTIVE'
    NOT NULL CHECK (stream_status IN ('ACTIVE', 'CANCELED')),
  scheduled_start_date TIMESTAMP NOT NULL,
  scheduled_end_date TIMESTAMP NOT NULL,

  member_id BIGINT NOT NULL,
  chat_space_id BIGINT NULL,

  created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

  CONSTRAINT fleen_stream_fk_member
    FOREIGN KEY (member_id)
      REFERENCES member (member_id)
        ON DELETE SET NULL,
  CONSTRAINT fleen_stream_fk_chat_space
    FOREIGN KEY (chat_space_id)
      REFERENCES chat_space (chat_space_id)
      ON DELETE SET NULL
);

--rollback DROP TABLE IF EXISTS `fleen_stream`;



-- changeset alamu:9

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'calendar';

CREATE TABLE calendar (
  calendar_id BIGSERIAL PRIMARY KEY,
  external_id VARCHAR(1000),
  title VARCHAR(300) NOT NULL,
  description VARCHAR(1000) NOT NULL,
  timezone VARCHAR(30) NOT NULL,
  is_active BOOLEAN NOT NULL,
  code VARCHAR(100) NOT NULL,

  created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

--rollback DROP TABLE IF EXISTS `calendar`;



-- changeset alamu:10

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'stream_attendee';

CREATE TABLE stream_attendee (
  stream_attendee_id BIGSERIAL PRIMARY KEY,
  attendee_comment VARCHAR(1000) NULL,
  organizer_comment VARCHAR(1000) NULL,
  is_attending BOOLEAN NOT NULL DEFAULT false,
  request_to_join_status VARCHAR(255) DEFAULT 'PENDING'
    NOT NULL CHECK (request_to_join_status IN ('APPROVED', 'DISAPPROVED', 'PENDING')),

  fleen_stream_id BIGINT NOT NULL,
  member_id BIGINT NOT NULL,

  created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

  CONSTRAINT stream_attendee_fk_member_id
    FOREIGN KEY (member_id)
      REFERENCES member (member_id)
        ON DELETE SET NULL,
  CONSTRAINT stream_attendee_fk_fleen_stream_id
    FOREIGN KEY (fleen_stream_id)
      REFERENCES fleen_stream (fleen_stream_id)
        ON DELETE SET NULL
);

--rollback DROP TABLE IF EXISTS `stream_attendee`;



-- changeset alamu:11

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'stream_review';

CREATE TABLE stream_review (
  stream_review_id BIGSERIAL PRIMARY KEY,
  rating INT NOT NULL,
  review VARCHAR(1000) NULL,

  fleen_stream_id BIGINT NOT NULL,
  member_id BIGINT NOT NULL,

  created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

  CONSTRAINT stream_review_fk_member_id
    FOREIGN KEY (member_id)
      REFERENCES member (member_id)
        ON DELETE SET NULL,
  CONSTRAINT stream_review_fk_fleen_stream_id
    FOREIGN KEY (fleen_stream_id)
      REFERENCES fleen_stream (fleen_stream_id)
        ON DELETE SET NULL
);

--rollback DROP TABLE IF EXISTS `stream_review`;



-- changeset alamu:12

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



--changeset alamu:13

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



--changeset alamu:14

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'stream_speaker';

CREATE TABLE stream_speaker (
  stream_speaker_id BIGSERIAL PRIMARY KEY,
  fleen_stream_id BIGINT NOT NULL,
  member_id BIGINT NULL,
  full_name VARCHAR(100) NOT NULL,
  title VARCHAR(100) NULL,
  description VARCHAR(1000) NULL,

  created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

  CONSTRAINT stream_speaker_fk_fleen_stream_id
    FOREIGN KEY (fleen_stream_id)
      REFERENCES fleen_stream (fleen_stream_id)
        ON DELETE CASCADE,
  CONSTRAINT stream_speaker_fk_member_id
    FOREIGN KEY (member_id)
      REFERENCES member (member_id)
        ON DELETE CASCADE
);

--rollback DROP TABLE IF EXISTS `stream_speaker`;



--changeset alamu:15

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'share_contact_request';

CREATE TABLE share_contact_request (
  share_contact_request_id BIGSERIAL PRIMARY KEY,
  contact VARCHAR(1000),
  initiator_id BIGINT NOT NULL,
  recipient_id BIGINT NOT NULL,
  initiator_comment VARCHAR(1000),
  recipient_comment VARCHAR(1000),
  is_expected BOOLEAN NOT NULL DEFAULT false,
  share_contact_request_status VARCHAR(255)
    CHECK (share_contact_request_status IN ('CANCELED', 'ACCEPTED', 'REJECTED', 'SENT')),
  contact_type VARCHAR(255)
    CHECK (contact_type IN ('EMAIL', 'FACEBOOK', 'INSTAGRAM', 'LINKEDIN', 'PHONE_NUMBER'
      'SNAPCHAT', 'TELEGRAM', 'TIKTOK', 'TWITTER_OR_X', 'WECHAT', 'WHATSAPP')),

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



--changeset alamu:16

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



--changeset alamu:17

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'contact';

CREATE TABLE contact (
  contact_id BIGSERIAL PRIMARY KEY,
  contact VARCHAR(1000),
  owner_id BIGINT NOT NULL,
  contact_type VARCHAR(255)
    NOT NULL CHECK (contact_type IN ('EMAIL', 'FACEBOOK', 'INSTAGRAM', 'LINKEDIN', 'PHONE_NUMBER'
      'SNAPCHAT', 'TELEGRAM', 'TIKTOK', 'TWITTER_OR_X', 'WECHAT', 'WHATSAPP')),

  created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

   CONSTRAINT contact_fk_owner_id
     FOREIGN KEY (owner_id)
       REFERENCES member (member_id)
         ON DELETE CASCADE
);

--rollback DROP TABLE IF EXISTS `contact`;



--changeset alamu:18

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'notification';

CREATE TABLE notification (
  notification_id BIGSERIAL PRIMARY KEY,
  notification_type VARCHAR(255) NOT NULL CHECK (notification_type IN (
    'REQUEST_TO_JOIN_CHAT_SPACE_APPROVED', 'REQUEST_TO_JOIN_CHAT_SPACE_DISAPPROVED', 'REQUEST_TO_JOIN_CHAT_SPACE_RECEIVED',
    'REQUEST_TO_JOIN_EVENT_APPROVED', 'REQUEST_TO_JOIN_EVENT_DISAPPROVED', 'REQUEST_TO_JOIN_EVENT_RECEIVED',
    'REQUEST_TO_JOIN_LIVE_BROADCAST_APPROVED', 'REQUEST_TO_JOIN_LIVE_BROADCAST_DISAPPROVED', 'REQUEST_TO_JOIN_LIVE_BROADCAST_RECEIVED',
    'SHARE_CONTACT_REQUEST_APPROVED', 'SHARE_CONTACT_REQUEST_DISAPPROVED', 'SHARE_CONTACT_REQUEST_RECEIVED',
    'USER_FOLLOWING'
  )),
  notification_status VARCHAR(255) NOT NULL CHECK (notification.notification_status IN ('READ', 'UNREAD')),
  contact_type VARCHAR(255) CHECK (contact_type IN ('EMAIL', 'FACEBOOK', 'INSTAGRAM', 'LINKEDIN', 'PHONE_NUMBER'
    'SNAPCHAT', 'TELEGRAM', 'TIKTOK', 'TWITTER_OR_X', 'WECHAT', 'WHATSAPP')),
  receiver_id BIGINT NOT NULL,
  initiator_or_requester_id BIGINT,
  initiator_or_requester_name VARCHAR(1000),
  recipient_id BIGINT,
  recipient_name VARCHAR(1000),
  message_key VARCHAR(1000) NOT NULL,
  other_comment VARCHAR(1000) NULL,
  is_read BOOLEAN NOT NULL,
  id_or_link_or_url VARCHAR(1000) NOT NULL,
  notification_read_on TIMESTAMP NULL,
  share_contact_request_id BIGINT,
  fleen_stream_id BIGINT,
  fleen_stream_title VARCHAR(1000),
  stream_attendee_id BIGINT,
  stream_attendee_name VARCHAR(1000),
  chat_space_id BIGINT,
  chat_space_title VARCHAR(1000),
  chat_space_member_id BIGINT,
  chat_space_member_name VARCHAR(1000),
  follower_id BIGINT,
  follower_name VARCHAR(1000),
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

  CONSTRAINT notification_fk_fleen_stream_id
    FOREIGN KEY (fleen_stream_id)
      REFERENCES fleen_stream (fleen_stream_id)
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

