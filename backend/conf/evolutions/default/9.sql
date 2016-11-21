# ---Add extension field to track table

# ---!Ups
ALTER TABLE track ADD COLUMN extension char(10);

# ---!Downs
ALTER TABLE track DROP COLUMN extension;