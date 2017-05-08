(ns tgi-stats-clj.server.worker.match
  (:require [tgi-stats-clj.server.utils      :as utils]
            [tgi-stats-clj.server.db.actions :as db]))

(def status-ok 1)

(defn- fetch-match-history
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
      (throw (Exception. (:error response))))))

(defn fetch-match-ids
  [api-key account-ids]
  (distinct
    (flatten
      (map #(fetch-match-history api-key % nil) account-ids))))
