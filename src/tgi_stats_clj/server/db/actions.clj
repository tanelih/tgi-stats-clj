(ns tgi-stats-clj.server.db.actions
  (:require [yesql.core                     :as sql]
            [clojure.java.jdbc              :as jdbc]
            [tgi-stats-clj.server.db.config :as config]))

(def options {:connection config/db})

(sql/defquery q-upsert-user!     "queries/upsert-user.sql"      options)
(sql/defquery q-insert-match!    "queries/insert-match.sql"     options)
(sql/defquery q-insert-player!   "queries/insert-player.sql"    options)
(sql/defquery q-get-users        "queries/get-users.sql"        options)
(sql/defquery q-get-match-data   "queries/get-match-data.sql"   options)
(sql/defquery q-get-latest-match "queries/get-latest-match.sql" options)

(defn update-users
  [users]
  (jdbc/with-db-transaction [conn config/db]
    (doseq [user users]
      (q-upsert-user! user {:connection conn}))))

(defn get-match-data
  [params]
  (q-get-match-data params))

(defn set-match-data
  [matches]
  (jdbc/with-db-transaction [conn config/db]
    (doseq [match matches]
      (q-insert-match! match {:connection conn})
      (doseq [player (:players match)]
        (q-insert-player! player {:connection conn})))))

(defn get-users
  []
  (q-get-users))

(defn get-latest-match
  []
  (q-get-latest-match))
