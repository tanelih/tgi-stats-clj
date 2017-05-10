(ns tgi-stats-clj.server.worker.match
  (:require [tgi-stats-clj.server.utils      :as utils]
            [tgi-stats-clj.server.db.actions :as db]))

(def api-endpoint {:match-history "https://api.steampowered.com/IDOTA2Match_570/GetMatchHistory/V001"
                   :match-details "https://api.steampowered.com/IDOTA2Match_570/GetMatchDetails/V001"})

(def status-ok 1)

(defn is-user
  [account-ids player]
  (some #(= (:account_id player) %) account-ids))

(defn map-player-data
  [match player]
  {:match_id       (:match_id match)
   :steam_id       (utils/to-steam-id (:account_id player))
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
   :players    (map #(map-player-data match %) (filter #(is-user account-ids %) (:players match)))})

(defn fetch-match-history
  [api-key account-id start-at-match-id]
  (let [options {:query-params {:key               api-key
                                :account_id        account-id
                                :start_at_match_id start-at-match-id}}
        response  (utils/fetch (:match-history api-endpoint) options)
        status    (get-in response [:body :result :status])
        matches   (get-in response [:body :result :matches])
        remaining (get-in response [:body :result :results_remaining])]
    (if-not (= status status-ok)
      []
      (concat
        (butlast matches)
        (if (> remaining 0)
          (fetch-match-history api-key account-id (:match_id (last matches)))
          [(last matches)])))))

(defn fetch-match-ids
  [api-key]
  (let [account-ids  (map (comp utils/to-account-id :steam_id) (db/get-users))
        latest-match (db/get-latest-match)]
    (distinct
      (map
        :match_id
        (filter
          (fn [match]
            (let [start-time (utils/to-date (:start_time match))]
              (or
                (nil? latest-match)
                (clj-time.core/after? start-time (:start_time latest-match)))))
          (flatten
            (map #(fetch-match-history api-key % (:match_id latest-match))
                 account-ids)))))))

(defn fetch-single-match-details
  [api-key match-id]
  (let [options {:query-params {:key      api-key
                                :match_id match-id}}
        response (utils/fetch (:match-details api-endpoint) options)]
    (get-in response [:body :result])))

(defn fetch-match-details
  [api-key match-ids]
  (let [account-ids (map (comp utils/to-account-id :steam_id) (db/get-users))
        matches     (map (comp
                           #(map-match-data % account-ids)
                           #(fetch-single-match-details api-key %))
                         match-ids)]
    (db/set-match-data matches)))
