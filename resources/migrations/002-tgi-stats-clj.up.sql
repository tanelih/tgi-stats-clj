-- 11.5.2017
--
-- Make sure players are deleted if a match is deleted.

alter table players
  drop constraint players_match_id_fkey,
  add constraint players_match_id_fkey
    foreign key (match_id) references matches(match_id) on delete cascade;

