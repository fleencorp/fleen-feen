--liquibase formatted sql


--changeset alamu:1

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'member';

CREATE TABLE member (
  member_id SERIAL PRIMARY KEY,
  first_name VARCHAR(100) NOT NULL,
  last_name VARCHAR(100) NOT NULL,
  email_address VARCHAR(150) NOT NULL,
  phone_number VARCHAR(15) NOT NULL,
  password_hash VARCHAR(500) NOT NULL,
  profile_photo VARCHAR(1000),
  email_address_verified BOOLEAN DEFAULT FALSE,
  phone_number_verified BOOLEAN DEFAULT FALSE,
  mfa_enabled BOOLEAN DEFAULT FALSE,
  mfa_secret VARCHAR(1000),

  mfa_type VARCHAR(255) DEFAULT 'NONE' NOT NULL CHECK (mfa_type IN ('PHONE', 'EMAIL', 'AUTHENTICATOR', 'NONE')),
  verification_status VARCHAR(255) DEFAULT 'PENDING' NOT NULL CHECK (verification_status IN ('PENDING', 'IN_PROGRESS', 'DISAPPROVED', 'APPROVED')),
  member_status VARCHAR(255) DEFAULT 'ACTIVE' NOT NULL CHECK (member_status IN ('ACTIVE', 'INACTIVE', 'DISABLED', 'BANNED')),
  profile_type VARCHAR(255) DEFAULT 'USER' NOT NULL CHECK (profile_type IN ('CONTRIBUTOR', 'USER', 'ADMIN')),

  created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL

);

--rollback DROP TABLE IF EXISTS `member`;



--changeset alamu:2

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'profile_token';

CREATE TABLE profile_token (
  profile_token_id SERIAL PRIMARY KEY,
  reset_password_token VARCHAR(500),
  reset_password_token_expiry_date TIMESTAMP,
  member_id BIGINT NOT NULL,

  created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

  FOREIGN KEY (member_id) REFERENCES member (member_id)
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
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'google_oauth_authorization';

CREATE TABLE google_oauth_authorization (
  google_oauth_authorization_id SERIAL PRIMARY KEY,
  access_token VARCHAR(1000),
  refresh_token VARCHAR(1000),
  authorization_scope VARCHAR(1000),
  token_type VARCHAR(1000),
  token_expiration_time_in_milliseconds BIGINT,
  member_id BIGINT NOT NULL,

  created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

  FOREIGN KEY (member_id) REFERENCES member (member_id)
);

--rollback DROP TABLE IF EXISTS `google_oauth_authorization`;


-- changeset alamu:5

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'member_role';

CREATE TABLE member_role (
  member_id BIGINT NOT NULL,
  role_id INT NOT NULL,

  FOREIGN KEY (member_id) REFERENCES member (member_id),
  FOREIGN KEY (role_id) REFERENCES role (role_id)
);

--rollback DROP TABLE IF EXISTS `member_role`;