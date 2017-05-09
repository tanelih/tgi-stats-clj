-- Insert a match into the database.
insert into matches (match_id, year, week, start_time)
  values (:match_id, :year, :week, :start_time)
