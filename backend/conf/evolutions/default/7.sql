# --- Create table with tags

# --- !Ups
create table tags(
  track_id INTEGER REFERENCES track(id),
  key VARCHAR(40),
  value VARCHAR(40)
);

# --- !Downs
drop table tags;