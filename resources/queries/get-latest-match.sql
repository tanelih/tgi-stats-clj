-- Select the most recent match for the given Steam ID.
select * from matches
  inner join players on
    players.match_id = matches.match_id and
    players.steam_id = :steam_id
  order by start_time desc limit 1
