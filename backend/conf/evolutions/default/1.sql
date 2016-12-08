#--Starting schema

#---!Ups
CREATE TABLE users (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(100)
);
CREATE TABLE own_auths(
  user_id BIGINT NOT NULL REFERENCES users(id),
  login VARCHAR(100) PRIMARY KEY,
  password_hash BYTEA,
  salt BYTEA
);


CREATE TABLE artists(
  id BIGSERIAL PRIMARY KEY,
  name TEXT,
  description TEXT
);

CREATE TABLE albums(
  id BIGSERIAL PRIMARY KEY,
  artist_id BIGINT REFERENCES artists(id),
  name TEXT,
  description TEXT
);

CREATE TABLE tracks(
  id BIGSERIAL PRIMARY KEY,
  title TEXT,
  original_name TEXT,
  extension VARCHAR(10),
  length REAL,
  bitrate INTEGER,
  has_cover BOOLEAN DEFAULT FALSE ,
  artist_id BIGINT REFERENCES artists(id),
  album_id BIGINT REFERENCES albums(id)
);

CREATE TABLE tags(
  track_id BIGINT NOT NULL REFERENCES tracks(id),
  tag TEXT,
  value TEXT
);

CREATE TABLE collection_tracks(
  user_id BIGINT NOT NULL REFERENCES users(id),
  track_id BIGINT NOT NULL REFERENCES tracks(id)
);

CREATE TABLE playlists(
  id BIGSERIAL PRIMARY KEY,
  name TEXT
);

CREATE TABLE user_playlists(
  user_id BIGINT NOT NULL REFERENCES users(id),
  playlist_id BIGINT NOT NULL REFERENCES playlists(id)
);

CREATE TABLE playlist_tracks(
  id SERIAL PRIMARY KEY,
  playlist_id BIGINT NOT NULL REFERENCES playlists(id),
  track_id BIGINT NOT NULL REFERENCES tracks(id),
  after BIGINT NOT NULL REFERENCES playlist_tracks(id)
);

#--!Downs
DROP TABLE users CASCADE;
DROP TABLE own_auths;
DROP TABLE artists CASCADE;
DROP TABLE albums CASCADE;
DROP TABLE tracks CASCADE;
DROP TABLE tags;
DROP TABLE collection_tracks;
DROP TABLE playlists CASCADE;
DROP TABLE user_playlists;
DROP TABLE playlist_tracks;