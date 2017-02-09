-- 09.02.2017
--
-- Insert dummies into the database for development purposes.

insert into users (
  steam_id,
  avatar_url,
  display_name
) values (
  '1234ABCD',
  'https://placecage.com/256/256',
  'Nicholas Cage'
);

-- ;;

insert into matches (
  match_id,
  year,
  week,
  start_time
) values (
  '1234ABCD',
  2017,
  6,
  now()
);

-- ;;

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
  '1234ABCD',
  '1234ABCD',
  'dire',
  16,
  3,
  2,
  2,
  420,
  420,
  420,
  88
);

