# --- Create table with tags

# --- !Ups
CREATE TABLE tags(
  track_id INTEGER REFERENCES track(id) ON DELETE CASCADE,
  key VARCHAR(40),
  value VARCHAR(40)
);

# --- !Downs
DROP TABLE tags;