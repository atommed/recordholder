#--Starting schema

#---!Ups
CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  name VARCHAR(100)
);
CREATE TABLE own_auths(
  user_id SERIAL REFERENCES users(id),
  login VARCHAR(100) PRIMARY KEY,
  password_hash BYTEA,
  salt BYTEA
);
#---!Downs
DROP TABLE users CASCADE;
DROP TABLE own_auths;