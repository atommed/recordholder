# --- Add length to track

# --- !Ups
ALTER TABLE track add length REAL;

# --- !Downs
ALTER TABLE track DROP COLUMN length;