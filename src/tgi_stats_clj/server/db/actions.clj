(ns tgi-stats-clj.server.db.actions
  (:require [yesql.core                     :as sql]
            [clojure.java.jdbc              :as jdbc]
            [tgi-stats-clj.server.db.config :as config]))

(def options {:connection config/db})

(sql/defquery q-upsert-user!   "queries/upsert-user.sql"    options)
(sql/defquery q-get-match-data "queries/get-match-data.sql" options)

(defn update-users
  [users]
  (jdbc/with-db-transaction [connection config/db]
    (doseq [user users]
      (q-upsert-user! user {:connection connection}))))

(defn get-match-data
  [params]
  (q-get-match-data params))
