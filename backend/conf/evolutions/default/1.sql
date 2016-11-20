# --- Creates user table

# --- !Ups
create table app_user (
  id SERIAL,
  name VARCHAR(40)
);

# --- !Downs
drop table app_user;