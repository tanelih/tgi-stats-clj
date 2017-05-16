-- 16.5.2017
--
-- Create a combined unique index on players.

alter table players
  add constraint players_match_id_steam_id_uniq unique(match_id, steam_id);
