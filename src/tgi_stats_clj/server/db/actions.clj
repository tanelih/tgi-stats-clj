(ns tgi-stats-clj.server.db.actions
  (:require [korma.core                     :as korma]
            [tgi-stats-clj.server.db.schema :as schema]))

(defn get-match-data
  "Get matches starting from the date set by :year and :week."
  [year week]
  (vector))
