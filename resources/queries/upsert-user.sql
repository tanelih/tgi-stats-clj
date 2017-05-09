-- Upsert an user into the database.
insert into users (steam_id, avatar_url, display_name)
  values (:steam_id, :avatar_url, :display_name)
  on conflict (steam_id)
  do update set
    (steam_id, avatar_url, display_name) = (:steam_id, :avatar_url, :display_name)
    where users.steam_id = :steam_id;

