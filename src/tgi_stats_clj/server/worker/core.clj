(ns tgi-stats-clj.server.worker.core
  (:gen-class)
  (:require [environ.core :refer [env]]
            [chime                             :as c]
            [clj-time.core                     :as t]
            [clj-time.periodic                 :as p]
            [clojure.tools.logging             :as log]
            [clojure.string                    :refer [split]]
            [tgi-stats-clj.utils               :refer [parse-int]]
            [tgi-stats-clj.server.utils        :as utils]
            [tgi-stats-clj.server.worker.user  :as user-worker]
            [tgi-stats-clj.server.worker.match :as match-worker]))

(def api-key       (env :steam-api-key))
(def steam-id-list (map parse-int (split (env :steam-id-list) #",")))

(defn- create-log-token
  [key]
  (str "[" key "][" (str (java.util.UUID/randomUUID) "]\n")))

(defn do-user-work
  [time]
  (let [token (create-log-token "do-user-work")]
    (log/info token "Starting...")
    (try
      (user-worker/fetch-users api-key steam-id-list)
      (catch Exception e
        (log/error token e))
      (finally (log/info token "Done.")))))

(defn do-match-work
  [time]
  (let [token (create-log-token "do-match-work")]
    (log/info token "Starting...")
    (try
      (let [match-ids (match-worker/fetch-match-ids api-key)]
        (match-worker/fetch-match-details api-key match-ids))
      (catch Exception e
        (log/error token e))
      (finally (log/info token "Done.")))))

(defn do-clean-work
  [time]
  (let [token (create-log-token "do-clean-work")]
    (log/info token "Starting...")
    (log/info token "Done.")))

(def user-work-schedule
  (p/periodic-seq (t/now) (t/minutes 60)))

(def match-work-schedule
  (p/periodic-seq (t/now) (t/minutes 20)))

(def clean-work-schedule
  (p/periodic-seq (t/now) (t/minutes 60)))

(defn -main
  []
  (do-user-work  (t/now))
  (do-match-work (t/now))
  (do-clean-work (t/now))
  (c/chime-at user-work-schedule  do-user-work)
  (c/chime-at match-work-schedule do-match-work)
  (c/chime-at clean-work-schedule do-clean-work)
  @(promise))
