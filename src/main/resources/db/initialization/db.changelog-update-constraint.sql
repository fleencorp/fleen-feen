--liquibase formatted sql


--changeset alamu:1

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'member';

ALTER TABLE member ADD CONSTRAINT unique_email_address UNIQUE (email_address);
ALTER TABLE member ADD CONSTRAINT unique_phone_number UNIQUE (phone_number);

--rollback DROP INDEX IF EXISTS `unique_email_address`;
--rollback DROP INDEX IF EXISTS `unique_phone_number`;



--changeset alamu:2

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'role';

ALTER TABLE role ADD CONSTRAINT unique_role_code UNIQUE (code);

--rollback DROP INDEX IF EXISTS `unique_role_code`;



--changeset alamu:3

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'calendar';

ALTER TABLE calendar ADD CONSTRAINT unique_calendar_code UNIQUE (code);

--rollback DROP INDEX IF EXISTS `unique_calendar_code`;



--changeset alamu:4

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'country';

ALTER TABLE country ADD CONSTRAINT unique_country_code UNIQUE (code);

--rollback DROP INDEX IF EXISTS `unique_country_code`;
