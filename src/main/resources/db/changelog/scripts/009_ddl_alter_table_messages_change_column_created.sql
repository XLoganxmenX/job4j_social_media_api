ALTER TABLE messages DROP COLUMN created;
ALTER TABLE messages ADD COLUMN created TIMESTAMP WITHOUT TIME ZONE DEFAULT now();