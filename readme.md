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
