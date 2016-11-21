# --- Creates user table

# --- !Ups
CREATE TABLE app_user(
  id SERIAL,
  name VARCHAR(40)
);

# --- !Downs
DROP TABLE app_user;