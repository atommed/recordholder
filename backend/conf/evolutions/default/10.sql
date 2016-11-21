# ---Add original_name field to track table

# ---!Ups
ALTER TABLE track ADD COLUMN original_name char(100);

# ---!Downs
ALTER TABLE track DROP COLUMN original_name;