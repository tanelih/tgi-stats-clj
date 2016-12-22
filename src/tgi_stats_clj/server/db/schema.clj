(ns tgi-stats-clj.server.db.schema
  "Database configuration and models in a single place."
  (:require [clojure.string   :refer [replace-first]]
            [environ.core     :refer [env]]
            [ragtime.jdbc     :refer [sql-database load-resources]]
            [to-jdbc-uri.core :refer [to-jdbc-uri]]
            [korma.db         :refer [defdb postgres]]
            [korma.core       :as    korma]))

(def database-url (env :database-url))

;; utilities for managing and parsing the above :database-url

(defn- create-uri
  [url]
  (java.net.URI. url))

(defn- parse-address
  [uri]
  {:db   (replace-first (.getPath uri) "/" "")
   :host (.getHost uri)
   :port (.getPort uri)})

(defn- parse-credentials
  [uri]
  (let [credentials (clojure.string/split (.getUserInfo uri) #":")]
    {:user     (nth credentials 0)
     :password (nth credentials 1)}))

;; configuration for both migrations and the database

(def migration-config
  "Configuration for the Ragtime DB migration utility."
  {:datastore  (sql-database {:connection-uri (to-jdbc-uri database-url)})
   :migrations (load-resources "migrations")})

(def database-opts
  "The database options are parsed from the :database-url environment variable, which is assumed to
   take the format of: `<protocol>://<username>:<password>@<host>/<dbname>`."
  (let [uri         (create-uri database-url)
        address     (parse-address uri)
        credentials (parse-credentials uri)]
    (merge address credentials)))

;; database definition and entities / models

(defdb db (postgres database-opts))

(korma/defentity players
  (korma/entity-fields :side :hero :account-id))

(korma/defentity matches
  (korma/has-many players)
  (korma/entity-fields :start-time))
