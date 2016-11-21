# ---Add has_cover field to track table

# ---!Ups
ALTER TABLE track ADD COLUMN has_cover BOOLEAN NOT NULL DEFAULT TRUE;

# ---!Downs
ALTER TABLE track DROP COLUMN has_cover;