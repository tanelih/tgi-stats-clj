(ns tgi-stats-clj.server.db.config
  (:require [clj-time.jdbc]
            [environ.core     :refer [env]]
            [ragtime.jdbc     :refer [sql-database load-resources]]
            [to-jdbc-uri.core :refer [to-jdbc-uri]]))

(def database-url (env :database-url))

(def db {:connection-uri (to-jdbc-uri database-url)})

(def migration
  {:datastore  (sql-database {:connection-uri (to-jdbc-uri database-url)})
   :migrations (load-resources "migrations")})
