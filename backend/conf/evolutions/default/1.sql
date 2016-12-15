#--Starting schema

#---!Ups
CREATE TABLE app_user (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(100)
);
CREATE TABLE own_auth (
  user_id BIGINT NOT NULL REFERENCES app_user(id),
  login VARCHAR(100) PRIMARY KEY,
  password_hash BYTEA,
  salt BYTEA
);


CREATE TABLE artist (
  id BIGSERIAL PRIMARY KEY,
  name TEXT,
  description TEXT
);

CREATE TABLE user_artists(
  user_id BIGINT NOT NULL REFERENCES app_user(id),
  artist_id BIGINT NOT NULL REFERENCES artist (id),
  UNIQUE (user_id, artist_id)
);

CREATE TABLE album (
  id BIGSERIAL PRIMARY KEY,
  artist_id BIGINT REFERENCES artist(id),
  name TEXT,
  description TEXT
);

CREATE TABLE user_albums(
  user_id BIGINT NOT NULL REFERENCES app_user(id),
  album_id BIGINT NOT NULL REFERENCES album (id),
  UNIQUE (user_id, album_id)
);

CREATE TABLE track (
  id BIGSERIAL PRIMARY KEY,
  title TEXT,
  original_name TEXT,
  extension VARCHAR(10),
  length REAL,
  bitrate INTEGER,
  has_cover BOOLEAN DEFAULT FALSE ,
  artist_id BIGINT REFERENCES artist(id),
  album_id BIGINT REFERENCES album(id)
);

CREATE TABLE tag (
  track_id BIGINT NOT NULL REFERENCES track(id),
  tag TEXT,
  value TEXT
);

CREATE TABLE user_tracks (
  user_id BIGINT NOT NULL REFERENCES app_user (id),
  track_id BIGINT NOT NULL REFERENCES track(id)
);

CREATE TABLE playlist(
  id BIGSERIAL PRIMARY KEY,
  name TEXT
);

CREATE TABLE user_playlists(
  user_id BIGINT NOT NULL REFERENCES app_user(id),
  playlist_id BIGINT NOT NULL REFERENCES playlist(id),
  UNIQUE (user_id, playlist_id)
);

CREATE TABLE playlist_tracks(
  id SERIAL PRIMARY KEY,
  playlist_id BIGINT NOT NULL REFERENCES playlist(id),
  track_id BIGINT NOT NULL REFERENCES track(id),
  after BIGINT REFERENCES playlist_tracks(id)
);

#--!Downs
DROP TABLE app_user CASCADE;
DROP TABLE own_auth;
DROP TABLE artist CASCADE;
DROP TABLE user_artists;
DROP TABLE album CASCADE;
DROP TABLE user_albums;
DROP TABLE track CASCADE;
DROP TABLE tag;
DROP TABLE user_tracks;
DROP TABLE playlist CASCADE;
DROP TABLE user_playlists;
DROP TABLE playlist_tracks;