--liquibase formatted sql


--changeset alamu:1

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM role;

INSERT INTO role (title, code) VALUES
  ('Super Administrator', 'SUPER_ADMINISTRATOR'),
  ('Administrator', 'ADMINISTRATOR'),
  ('Contributor', 'CONTRIBUTOR'),
  ('User', 'USER'),
  ('Pre Authenticated User', 'PRE_AUTHENTICATED_USER'),
  ('Refresh Token', 'REFRESH_TOKEN'),
  ('Pre Verified User', 'PRE_VERIFIED_USER');

--rollback DELETE FROM role WHERE code IN ('SUPER_ADMINISTRATOR', 'ADMINISTRATOR', 'CONTRIBUTOR', 'USER', 'PRE_AUTHENTICATED_USER', 'REFRESH_TOKEN', 'PRE_VERIFIED_USER');