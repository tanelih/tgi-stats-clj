-- 16.5.2017
--
-- Revert creating a combined unique index on players.

alter table players
  drop constraint players_match_id_steam_id_uniq;
