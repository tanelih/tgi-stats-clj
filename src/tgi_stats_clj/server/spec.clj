(ns tgi-stats-clj.server.spec
  (:require [clojure.spec  :as s]
            [clj-time.core :as t]))

(s/def ::week (s/and integer? #(>= % 0)))
(s/def ::year (s/and integer? #(>= % 1980)))

(defn is-valid-week [week] (s/valid? ::week week))
(defn is-valid-year [year] (s/valid? ::year year))

(defn is-valid-date
  [year week]
  (and
    (is-valid-year year)
    (is-valid-week week)
    (t/before?
      (t/plus (t/date-time year) (t/weeks week))
      (t/now))))
