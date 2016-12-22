# TGI Stats

Clojure implementation of an older project. For fun and practice.

## Development

```
docker-compose up -d; lein figwheel
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
=> (ragtime.repl/migrate tgi-stats-clj.server.db.schema/migration-config)
```

Rollback can be performed with `ragtime.repl/rollback ...`.
