(ns tgi-stats-clj.server.worker.core
  "Contains a set of operations to fetch data from the Steam API in a tight
   schedule. Ran as an independent worker process."
  (:gen-class)
  (:require [environ.core :refer [env]]
            [chime                             :as c]
            [clj-time.core                     :as t]
            [clj-time.periodic                 :as p]
            [clojure.tools.logging             :as log]
            [tgi-stats-clj.server.utils        :as utils]
            [tgi-stats-clj.server.worker.user  :as user-worker]
            [tgi-stats-clj.server.worker.match :as match-worker]))

(def api-key       (env :steam-api-key))
(def steam-id-list (env :steam-id-list))

(defn- create-log-token
  [key]
  (str "[" key "][" (str (java.util.UUID/randomUUID) "]\n")))

(defn- do-user-work
  [time]
  (let [token (create-log-token "do-user-work")]
    (log/info token "Starting...")
    (try
      (user-worker/fetch-users api-key steam-id-list)
      (catch Exception e (log/error token e))
      (finally (log/info token "Done.")))))

(defn do-match-work
  [time]
  (let [token (create-log-token "do-match-work")]
    (log/info token "Starting...")
    (try
      (let [steam-ids   (clojure.string/split (env :steam-id-list) #",")
            account-ids (map utils/to-account-id steam-ids)
            match-ids   (match-worker/fetch-match-ids api-key account-ids)]
        (clojure.pprint/pprint match-ids))
      (catch Exception e (log/error token e))
      (finally (log/info token "Done.")))))

(defn- do-clean-work
  [time]
  (let [token (create-log-token "do-clean-work")]
    (log/info token "Starting...")
    (log/info token "Done.")))

(def user-work-schedule
  (p/periodic-seq (t/now) (t/seconds 10)))

(def match-work-schedule
  (p/periodic-seq (t/now) (t/seconds 5)))

(def clean-work-schedule
  (p/periodic-seq (t/now) (t/seconds 20)))

(defn -main
  []
  (do-user-work (t/now))
  (do-clean-work (t/now))
  (c/chime-at user-work-schedule  do-user-work)
  (c/chime-at match-work-schedule do-match-work)
  (c/chime-at clean-work-schedule do-clean-work)
  @(promise))
