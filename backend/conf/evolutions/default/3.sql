# --- Add primary keys

# --- !Ups
ALTER TABLE track add PRIMARY KEY (id);
ALTER TABLE app_user add PRIMARY KEY (id);
# --- !Downs
ALTER table track drop CONSTRAINT track_pkey;
ALTER table app_user drop CONSTRAINT app_user_pkey;