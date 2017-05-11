-- 11.5.2017
--
-- Revert the cascading constraint on players.

alter table players
  drop constraint players_match_id_fkey,
  add constraint players_match_id_fkey
    foreign key (match_id) references matches(match_id);

