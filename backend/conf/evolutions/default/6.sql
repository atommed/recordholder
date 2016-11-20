#--Fix

# --!Ups
ALTER TABLE track RENAME COLUMN bitrait to bitrate;
# --!Downs
ALTER TABLE track RENAME COLUMN bitrate to bitrait;