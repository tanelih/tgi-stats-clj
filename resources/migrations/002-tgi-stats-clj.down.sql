-- 09.02.2017
--
-- Remove dummy data from the database.

delete from players
  where steam_id = '1234ABCD' and match_id = '1234ABCD';

-- ;;

delete from matches
  where match_id = '1234ABCD';

-- ;;

delete from users
  where steam_id = '1234ABCD';

