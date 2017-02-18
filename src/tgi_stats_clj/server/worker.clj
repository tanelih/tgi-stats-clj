(ns tgi-stats-clj.server.worker
  "Contains a set of operations to fetch data from the Steam API in a tight
   schedule."
  (:require [chime                           :as c]
            [clj-time.core                   :as t]
            [clj-time.periodic               :as p]
            [org.httpkit.client              :as http]
            [clojure.tools.logging           :as log]
            [tgi-stats-clj.server.db.actions :as db]
            [environ.core                    :refer [env]]
            [cheshire.core                   :refer [parse-string]]
            [clojure.string                  :refer [split]]))


(defn- insert-player
  [player]
  (let [user {:steam_id     (:steamid player)
              :avatar_url   (:avatar player)
              :display_name (:personaname player)}]
    (try
      (db/upsert-user! user)
      (catch Exception e
        (log/error (.getMessage e))))))

(defn- do-user-work
  "Keep configured user data in sync with steam."
  [time]
  (log/info "do-user-work")
  (let [options {:query-params {:key      (env :steam-api-key)
                                :steamids (env :steam-id-list)}}
        endpoint "http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002"
        response @(http/get endpoint options)]
    (if (:error response)
      (log/error (:error response))
      (try
        (let [json (parse-string (:body response) true)]
          (doseq [player (get-in json [:response :players])]
            (insert-player player))
          (log/info "do-user-work :: done"))
        (catch Exception e
          (log/error (.getMessage e)))))))

(defn- do-match-work
  "Fetch all available matches for configured users, as long as those matches
   do not yet exist in the database."
  [time]
  (log/info "do-match-work"))

(defn- do-clean-work
  "Cleanup old matches from the database."
  [time]
  (log/info "do-clean-work"))

(def user-work-schedule
  (p/periodic-seq (t/now) (t/seconds 10)))

(def match-work-schedule
  (p/periodic-seq (t/now) (t/seconds 5)))

(def clean-work-schedule
  (p/periodic-seq (t/now) (t/seconds 20)))

(defn work
  "Starts the schedule and the scheduled jobs. Returns a function that can be
   invoked to cancel the scheduled jobs."
  []
  (do-user-work (t/now))
  (do-clean-work (t/now))
  (let [cancel-user-worker  (c/chime-at user-work-schedule  do-user-work)
        cancel-match-worker (c/chime-at match-work-schedule do-match-work)
        cancel-clean-worker (c/chime-at clean-work-schedule do-clean-work)]
    (fn []
      (cancel-user-worker)
      (cancel-match-worker)
      (cancel-clean-worker))))

