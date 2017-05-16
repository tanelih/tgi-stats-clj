(ns tgi-stats-clj.server.db.actions
  (:require [yesql.core                     :as sql]
            [clojure.java.jdbc              :as jdbc]
            [tgi-stats-clj.server.db.config :as config]))

(def options {:connection config/db})

(sql/defquery q-upsert-user!     "queries/upsert-user.sql"      options)
(sql/defquery q-insert-match!    "queries/insert-match.sql"     options)
(sql/defquery q-insert-player!   "queries/insert-player.sql"    options)
(sql/defquery q-get-user-data    "queries/get-user-data.sql"    options)
(sql/defquery q-get-player-data  "queries/get-player-data.sql"  options)
(sql/defquery q-get-match-data   "queries/get-match-data.sql"   options)
(sql/defquery q-get-users        "queries/get-users.sql"        options)
(sql/defquery q-get-latest-match "queries/get-latest-match.sql" options)

(defn create-db-ex
  [ex action]
  (ex-info (.getMessage ex) {:type :db :action action} ex))

(defn get-match-data
  [params]
  (try
    (map
      (fn [match]
        (assoc match
          :players
          (map
            (fn [player]
              (assoc player
                :user
                (q-get-user-data {:steam_id (:steam_id player)} {:result-set-fn first})))
            (q-get-player-data {:match_id (:match_id match)}))))
      (q-get-match-data params))
    (catch Exception e
      (throw (create-db-ex e :get-match-data)))))

(defn set-match-data
  [matches]
  (try
    (jdbc/with-db-transaction [conn config/db]
      (doseq [match matches]
        (q-insert-match! match {:connection conn})
        (doseq [player (:players match)]
          (q-insert-player! player {:connection conn}))))
    (catch Exception e
      (throw (create-db-ex e :set-match-data)))))

(defn update-users
  [users]
  (try
    (jdbc/with-db-transaction [conn config/db]
      (doseq [user users]
        (q-upsert-user! user {:connection conn})))
    (catch Exception e
      (throw (create-db-ex e :update-users)))))

(defn get-users
  []
  (try
    (q-get-users)
    (catch Exception e
      (throw (create-db-ex e :get-users)))))

(defn get-latest-match
  [steam-id]
  (try
    (q-get-latest-match {:steam_id steam-id} {:result-set-fn first})
    (catch Exception e
      (throw (create-db-ex e :get-latest-match)))))
