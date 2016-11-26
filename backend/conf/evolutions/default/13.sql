
# ---!Ups
CREATE TABLE db_auth(
  login VARCHAR(40) PRIMARY KEY ,
  passwordHash bytea,
  salt bytea
);

#---!Downs
DROP TABLE db_auth;