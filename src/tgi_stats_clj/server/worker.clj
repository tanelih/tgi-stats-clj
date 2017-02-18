(ns tgi-stats-clj.server.worker
  "Contains a set of operations to fetch data from the Steam API in a tight
   schedule."
  (:require [environ.core :refer [env]]
            [chime                           :as c]
            [clj-time.core                   :as t]
            [clj-time.periodic               :as p]
            [clojure.tools.logging           :as log]
            [tgi-stats-clj.server.utils      :as utils]
            [tgi-stats-clj.server.db.actions :as db]))

(def status-ok 1)

(def api-key       (env :steam-api-key))
(def steam-id-list (env :steam-id-list))

(defn- insert-player
  [player]
  (let [user {:steam_id     (:steamid player)
              :avatar_url   (:avatar player)
              :display_name (:personaname player)}]
    (try
      (db/upsert-user! user)
      (catch Exception e
        (log/error (.getMessage e))))))

;; TODO Since the Steam API 'date_min' parameter does not work, we need to
;;      provide the 'latest' match per account here, so we can check for each
;;      batch of results, whether we need to fetch more...
(defn- fetch-match-history
  [account-id start-at-match-id]
  (let [options {:query-params {:key               api-key
                                :account_id        account-id
                                :start_at_match_id start-at-match-id}}
        endpoint "https://api.steampowered.com/IDOTA2Match_570/GetMatchHistory/V001"
        response (utils/fetch endpoint options)]
    (if (nil? (:error response))
      (let [status    (get-in response [:body :result :status])
            matches   (get-in response [:body :result :matches])
            remaining (get-in response [:body :result :results_remaining])]
        (if-not (= status status-ok)
          (do
            (log/info (str "match history not enabled for ["account-id "]"))
            [])
          (concat
            (butlast matches)
            (if (> remaining 0)
              (fetch-match-history account-id (:match_id (last matches)))
              [(last matches)]))))
      (do
        (log/error (:error response))
        []))))

(defn- do-user-work
  "Keep configured user data in sync with steam."
  [time]
  (log/info "do-user-work")
  (let [options {:query-params {:key      api-key
                                :steamids steam-id-list}}
        endpoint "http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002"
        response (utils/fetch endpoint options)]
    (if (nil? (:error response))
      (do
        (doseq [player (get-in response [:response :players])]
          (insert-player player))
        (log/info "do-user-work :: done"))
      (log/error (:error response)))))

(defn- do-match-work
  "Fetch all available matches for configured users, as long as those matches
   do not yet exist in the database."
  [time]
  (log/info "do-match-work")
  (let [steam-ids   (clojure.string/split (env :steam-id-list) #",")
        account-ids (map utils/to-account-id steam-ids)
        match-ids   (map :match_id
                      (flatten
                        (map #(fetch-match-history % nil) account-ids)))]
    ;; TODO Do something with the 'distinct' match ids...
    (clojure.pprint/pprint (count (distinct match-ids)))
    (log/info "do-match-work :: done")))

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

