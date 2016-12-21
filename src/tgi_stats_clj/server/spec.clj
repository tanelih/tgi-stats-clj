(ns tgi-stats-clj.server.spec
  "Contains common validations and specifications for the application including utilities related
   to testing these validations."
  (:require [clojure.spec  :as s]
            [clj-time.core :as t]))

(s/def ::year (s/and integer? #(>= % 1980)))
(s/def ::week (s/and integer? #(>= % 1) #(<= % 53)))

(defn is-valid-year [year] (s/valid? ::year year))
(defn is-valid-week [week] (s/valid? ::week week))

(defn is-valid-date
  "Check that both the year and week form a date that is both _valid_ and that the date formed by
   these two is before the current moment. E.g. eventually some dates will become valid, so this is
   quite the side-effect of a function."
  [year week]
  (and
    (is-valid-year year)
    (is-valid-week week)
    (t/before?
      (t/plus (t/date-time year) (t/weeks (- week 1)))
      (t/now))))
