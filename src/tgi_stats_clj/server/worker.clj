(ns tgi-stats-clj.server.worker
  "Contains a set of operations to fetch data from the Steam API in a tight schedule."
  (:require [timely.core :as t]))

(defn- fetch-matches
  "Fetch match history data for the configured players."
  []
  (println "fetch-matches"))

(defn- fetch-players
  "Fetch player and user data for the configured players."
  []
  (println "fetch-players"))

(defn work
  "Starts the schedule and the scheduled jobs."
  []
  (t/start-scheduler)
  (t/start-schedule (t/scheduled-item (t/every 2 :minutes) fetch-matches))
  (t/start-schedule (t/scheduled-item (t/every 3 :minutes) fetch-players)))
