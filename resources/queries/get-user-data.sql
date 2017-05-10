-- Select a single user from the DB.
select * from users where steam_id = :steam_id limit 1
