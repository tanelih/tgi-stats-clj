(ns tgi-stats-clj.server.db.config
  "Database configuration including migrations."
  (:require [environ.core     :refer [env]]
            [ragtime.jdbc     :refer [sql-database load-resources]]
            [to-jdbc-uri.core :refer [to-jdbc-uri]]))

(def database-url (env :database-url))

(def db {:connection-uri (to-jdbc-uri database-url)})

(def migration
  "Configuration for the Ragtime DB migration utility."
  {:datastore  (sql-database {:connection-uri (to-jdbc-uri database-url)})
   :migrations (load-resources "migrations")})

