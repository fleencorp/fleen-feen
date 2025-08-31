--liquibase formatted sql



--changeset alamu:add_constraints_member

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.table_constraints WHERE table_name = 'member' AND constraint_name = 'unique_email_address' AND constraint_type = 'UNIQUE';
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.table_constraints WHERE table_name = 'member' AND constraint_name = 'unique_phone_number' AND constraint_type = 'UNIQUE';
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.table_constraints WHERE table_name = 'member' AND constraint_name = 'unique_username' AND constraint_type = 'UNIQUE';

ALTER TABLE member ADD CONSTRAINT unique_email_address UNIQUE (email_address);
ALTER TABLE member ADD CONSTRAINT unique_phone_number UNIQUE (phone_number);
ALTER TABLE member ADD CONSTRAINT unique_username UNIQUE (username);

--rollback ALTER TABLE member DROP CONSTRAINT IF EXISTS unique_email_address;
--rollback ALTER TABLE member DROP CONSTRAINT IF EXISTS unique_phone_number;
--rollback ALTER TABLE member DROP CONSTRAINT IF EXISTS unique_username;



--changeset alamu:add_constraints_role

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.table_constraints WHERE table_name = 'role' AND constraint_name = 'unique_role_code' AND constraint_type = 'UNIQUE';

ALTER TABLE role ADD CONSTRAINT unique_role_code UNIQUE (code);

--rollback ALTER TABLE role DROP CONSTRAINT IF EXISTS unique_role_code;



--changeset alamu:add_constraints_calendar

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.table_constraints WHERE table_name = 'calendar' AND constraint_name = 'unique_calendar_code' AND constraint_type = 'UNIQUE';

ALTER TABLE calendar ADD CONSTRAINT unique_calendar_code UNIQUE (code);

--rollback ALTER TABLE calendar DROP CONSTRAINT IF EXISTS unique_calendar_code;



--changeset alamu:add_constraints_country

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.table_constraints WHERE table_name = 'country' AND constraint_name = 'unique_country_code' AND constraint_type = 'UNIQUE';

ALTER TABLE country ADD CONSTRAINT unique_country_code UNIQUE (code);

--rollback ALTER TABLE country DROP CONSTRAINT IF EXISTS unique_country_code;



--changeset alamu:add_constraints_adjectives

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.table_constraints WHERE table_name = 'adjectives' AND constraint_name = 'unique_adjective_word' AND constraint_type = 'UNIQUE';

ALTER TABLE adjectives ADD CONSTRAINT unique_adjective_word UNIQUE (word);

--rollback ALTER TABLE adjectives DROP CONSTRAINT IF EXISTS unique_adjective_word;



--changeset alamu:add_constraints_nouns

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.table_constraints WHERE table_name = 'nouns' AND constraint_name = 'unique_noun_word' AND constraint_type = 'UNIQUE';

ALTER TABLE nouns ADD CONSTRAINT unique_noun_word UNIQUE (word);

--rollback ALTER TABLE nouns DROP CONSTRAINT IF EXISTS unique_noun_word;



--changeset alamu:add_constraints_soft_ask_votes_unique_vote

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.table_constraints WHERE table_name = 'soft_ask_votes' AND constraint_name = 'unique_soft_ask_vote_per_item' AND constraint_type = 'UNIQUE';

ALTER TABLE soft_ask_votes ADD CONSTRAINT unique_soft_ask_vote_per_item UNIQUE (member_id, vote_parent_type, parent_id);

--rollback ALTER TABLE soft_ask_votes DROP CONSTRAINT IF EXISTS unique_soft_ask_vote_per_item;



--changeset alamu:add_constraint_soft_ask_username_unique_softask_username

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.table_constraints WHERE table_name = 'soft_ask_username' AND constraint_name = 'unique_soft_ask_username' AND constraint_type = 'UNIQUE';

ALTER TABLE soft_ask_username
  ADD CONSTRAINT unique_soft_ask_username UNIQUE (soft_ask_id, username);

--rollback ALTER TABLE soft_ask_username DROP CONSTRAINT IF EXISTS unique_soft_ask_username;



--changeset alamu:add_constraint_soft_ask_username_unique_softask_user

--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.table_constraints WHERE table_name = 'soft_ask_username' AND constraint_name = 'uq_soft_ask_username_softask_user' AND constraint_type = 'UNIQUE';

ALTER TABLE soft_ask_username
  ADD CONSTRAINT uq_soft_ask_username_softask_user UNIQUE (soft_ask_id, user_id);

--rollback ALTER TABLE soft_ask_username DROP CONSTRAINT IF EXISTS uq_soft_ask_username_softask_user;