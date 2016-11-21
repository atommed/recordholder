# --- Add length to track

# --- !Ups
ALTER TABLE track ADD length REAL;

# --- !Downs
ALTER TABLE track DROP COLUMN length;