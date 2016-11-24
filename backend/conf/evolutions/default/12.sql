# ---Replace tag size from 40 to 300

# ---!Ups
ALTER TABLE tag ALTER COLUMN value TYPE VARCHAR(300);
# ---!Downs
ALTER TABLE tag ALTER COLUMN value TYPE VARCHAR(40);