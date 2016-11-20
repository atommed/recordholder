# -- Create tracks table

# --- !Ups
create table track(
  id SERIAL
);

# --- !Downs
DROP TABLE track;