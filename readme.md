# TGI Stats

Clojure implementation of an older project. For fun and practice.

## Development

```
docker-compose up -d; lein figwheel
```
Remember to create the `.lein-env` file in EDN notation. Like so:
```
{:port          "foo"
 :database-url  "bar"
 :steam-api-key "baz"
 :steam-id-list "foo,bar,baz"}
```

## Database Access

The local development database can be accessed as so:

```
$ docker exec -it <container> bash
# psql -U <user> <database>
```

## Migrations

```
lein repl
=> (require 'ragtime.repl)
=> (ragtime.repl/migrate tgi-stats-clj.server.db.config/migration)
```
Rollback can be performed with `ragtime.repl/rollback`.

## REPL in Heroku

More often than not, Heroku doesn't like us running the REPL using the official
`heroku run lein repl` way. Because memory. Instead this seems to be a bit more
easy on the dynos:

```
heroku run lein trampoline run -m clojure.main
```

This will run a ghetto-repl that will get the job done for migrations and other
similar stuff.

