-- Insert a player into the database.
insert into players (
  steam_id,
  match_id,
  side,
  hero,
  stat_kills,
  stat_deaths,
  stat_assists,
  stat_xpm,
  stat_gpm,
  stat_last_hits,
  stat_denies
) values (
  :steam_id,
  :match_id,
  :side,
  :hero,
  :stat_kills,
  :stat_deaths,
  :stat_assists,
  :stat_xpm,
  :stat_gpm,
  :stat_last_hits,
  :stat_denies
) on conflict (steam_id, match_id) do nothing
