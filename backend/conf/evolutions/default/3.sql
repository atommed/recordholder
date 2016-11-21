# --- Add primary keys

# --- !Ups
ALTER TABLE track ADD PRIMARY KEY (id);
ALTER TABLE app_user ADD PRIMARY KEY (id);
# --- !Downs
ALTER TABLE track DROP CONSTRAINT track_pkey;
ALTER TABLE app_user DROP CONSTRAINT app_user_pkey;