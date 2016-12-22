-- 22.12.2016

do $$
begin
  if not exists (select * from pg_type where typname = 'side') then
    create type side as enum ('dire', 'radiant');
  end if;
end
$$;

--;;

create table if not exists users (
  steam_id varchar(32) primary key,

  -- properties
  avatar_url   text        not null,
  display_name varchar(20) not null
);

--;;

create table if not exists matches (
  match_id varchar(32) primary key,

  -- properties
  year       int       not null,
  week       int       not null,
  start_time timestamp not null
);

--;;

create table if not exists players (
  account_id varchar(32) primary key,

  -- relations
  steam_id varchar(32) references users,
  match_id varchar(32) references matches,

  -- properties
  side           side not null,
  hero           int  not null,
  stat_kills     int  not null,
  stat_deaths    int  not null,
  stat_assists   int  not null,
  stat_xpm       int  not null,
  stat_gpm       int  not null,
  stat_last_hits int  not null,
  stat_denies    int  not null
);
