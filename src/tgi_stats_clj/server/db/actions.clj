(ns tgi-stats-clj.server.db.actions
  (:require [yesql.core                     :as sql]
            [tgi-stats-clj.server.db.config :as config]))

(def options {:connection config/db})

(sql/defquery q-upsert-user!   "queries/upsert-user.sql"    options)
(sql/defquery q-get-match-data "queries/get-match-data.sql" options)

(defn upsert-user!
  [params]
  (q-upsert-user! params))

(defn get-match-data
  [params]
  (q-get-match-data params))

