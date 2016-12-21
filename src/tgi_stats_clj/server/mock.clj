(ns tgi-stats-clj.server.mock
  (:require [clj-uuid        :as uuid]
            [clj-time.core   :as time]
            [clj-time.coerce :as coerce]))

(defn gen-unix-time
  "Generate a random unix timestamp for the given year and week."
  [year week]
  (let [hours   (rand-int 168)
        minutes (rand-int 60)]
    (-> (time/plus
          (time/date-time year)
          (time/weeks week)
          (time/hours hours)
          (time/minutes minutes))
        (coerce/to-long)
        (/ 1000))))

(defn gen-mock-match
  "Generate mock data for a single match in the given :year and :week."
  [year week]
  (let [id         (uuid/v4)
        created-at (gen-unix-time year week)]
    {:id         id
     :year       year
     :week       week
     :created-at created-at}))

(defn gen-mock-data
  "Generate mock data for a set of matches in the given :year and :week."
  [year week]
  [(gen-mock-match year week)])
