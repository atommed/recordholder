# --- Add bitrait to track

# --- !Ups
ALTER TABLE track add bitrait integer;

# --- !Downs
ALTER TABLE track DROP COLUMN bitrait;