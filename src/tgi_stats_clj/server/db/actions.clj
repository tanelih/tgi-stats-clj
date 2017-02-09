(ns tgi-stats-clj.server.db.actions
  (:require [yesql.core                     :as sql]
            [tgi-stats-clj.server.db.config :as config]))

(sql/defquery get-matches-by-week
  "queries/get-matches-by-week.sql"
  {:connection config/db})

(defn get-match-data
  "Get matches starting from the date set by :year and :week."
  [year week]
  (get-matches-by-week {:year year :week week}))

