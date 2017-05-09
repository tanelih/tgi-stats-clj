(ns tgi-stats-clj.server.worker.match
  (:require [tgi-stats-clj.server.utils      :as utils]
            [tgi-stats-clj.server.db.actions :as db]))

(def status-ok 1)

(defn is-user
  [account-ids player]
  (some #(= (:account_id player)) account-ids))

(defn map-player-data
  [match player]
  ;; TODO Make these SteamIDS and AccountIDs into Integers or something.
  {:match_id       (:match_id match)
   :steam_id       (utils/to-steam-id (str (:account_id player)))
   :side           (utils/to-side (:player_slot player))
   :hero           (:hero_id player)
   :stat_kills     (:kills player)
   :stat_deaths    (:deaths player)
   :stat_assists   (:assists player)
   :stat_xpm       (:xp_per_min player)
   :stat_gpm       (:gold_per_min player)
   :stat_last_hits (:last_hits player)
   :stat_denies    (:denies player)})

(defn map-match-data
  [match account-ids]
  {:match_id   (:match_id match)
   :year       (utils/to-year (:start_time match))
   :week       (utils/to-week (:start_time match))
   :start_time (utils/to-date (:start_time match))
   :players    (map #(map-player-data % match) (filter #(is-user account-ids %) (:players match)))})

(defn fetch-match-history
  [api-key account-id start-at-match-id]
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
          []
          (concat
            (butlast matches)
            (if (> remaining 0)
              (fetch-match-history api-key account-id (:match_id (last matches)))
              [(last matches)]))))
      (throw (ex-info (:error response) response)))))

(defn fetch-match-details'
  [api-key match-id]
  (let [options {:query-params {:key      api-key
                                :match_id match-id}}
        endpoint "https://api.steampowered.com/IDOTA2Match_570/GetMatchDetails/V001"
        response (utils/fetch endpoint options)]
    (if (nil? (:error response))
      (get-in response [:body :result])
      (throw (ex-info (:error response) response)))))

(defn fetch-match-ids
  [api-key]
  (let [account-ids     (map (comp utils/to-account-id :steam_id) (db/get-users))
        latest-match-id (:match_id (db/get-latest-match))]
    (distinct
      (flatten
        (map (comp
               #(map :match_id %)
               #(fetch-match-history api-key % latest-match-id))
             account-ids)))))

(defn fetch-match-details
  [api-key match-ids]
  (let [account-ids (map (comp utils/to-account-id :steam_id) (db/get-users))
        matches     (map (comp
                           #(map-match-data % account-ids)
                           #(fetch-match-details' api-key %))
                         match-ids)]
    (db/set-match-data matches)))
