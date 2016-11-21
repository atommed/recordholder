# -- Fix tags to tag

# ---!Ups
ALTER TABLE tags RENAME TO tag;

# ---!Downs
ALTER TABLE tag RENAME TO tags;